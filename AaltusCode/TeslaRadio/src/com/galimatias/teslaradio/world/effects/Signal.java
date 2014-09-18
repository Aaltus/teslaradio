/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.galimatias.teslaradio.world.observer.SignalObservable;
import com.galimatias.teslaradio.world.observer.SignalObserver;
import com.jme3.material.RenderState;
import com.jme3.math.Spline;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author David
 */
public class Signal extends Geometry implements SignalObservable {
    
    private Vector3f path;
    
    private Spline curvedPath;
    private List<Float> curvePath_segmentLength;
    
    private boolean isCurved;
    private List<SignalObserver> signalObservers = new ArrayList<SignalObserver>();
    private float speed;
    private float startScale;
    private float distanceTraveled;
    private float pathLenghtNotification = -1f;
    
    private int lineIndex = 0;
    private float currentLength = 0;

    // Linear path Particle
    public Signal(Geometry particle, Vector3f path, float speed, float startScale, SignalObserver observer) {
        this.setMesh(particle.getMesh());
        this.setMaterial(particle.getMaterial());
        this.speed = speed;
        this.path = path;
        this.isCurved = false;
        this.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        this.setQueueBucket(Bucket.Transparent);
        this.startScale = startScale;
        this.setLocalScale(startScale);

        if (observer != null){
            this.signalObservers.add(observer);
        }
    }
    
    // Curved path Particle
    public Signal(Geometry particle, Spline curvedPath, float speed, float startScale, SignalObserver observer)
    {
        this.setMesh(particle.getMesh());
        this.setMaterial(particle.getMaterial());
        this.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        this.setQueueBucket(Bucket.Transparent);
        this.curvedPath = curvedPath;
        this.speed = speed;
        this.isCurved = true;

        
        this.curvePath_segmentLength = this.curvedPath.getSegmentsLength();

        this.setLocalScale(startScale);
        this.startScale = startScale;

        if (observer != null){
            this.signalObservers.add(observer);
        }

    }
    
      
    private void updateCurvedPosition(float tpf, Camera cam)
    {         
        float displacement = tpf*speed;
        distanceTraveled += displacement;
        
        //Deletion of the object if its at the end of its path.
        if (distanceTraveled> curvedPath.getTotalLength()) 
        {
            lineIndex = 0;
            this.notifyEndOfPath();
        }
        else 
        {
            
            // calculate position
            lineIndex = lineIndex;
            currentLength = currentLength + curvePath_segmentLength.get(lineIndex);
            
            while(distanceTraveled > currentLength)
            {
                lineIndex++;            
                currentLength = currentLength + curvePath_segmentLength.get(lineIndex);

            }
            currentLength = currentLength - curvePath_segmentLength.get(lineIndex);

            float lengthToInterpolate = distanceTraveled - currentLength;
            float percentToInterpolate = lengthToInterpolate/curvePath_segmentLength.get(lineIndex);

            Vector3f newPos = new Vector3f();
            curvedPath.interpolate(percentToInterpolate, lineIndex, newPos);
            
            // set position
            this.setLocalTranslation(newPos);
            
            //Adjust orientation
            if (cam!=null)
                this.lookAt(cam.getLocation(), cam.getUp());
        } 
  
    }
    private void updateLinearPosition(float tpf, Camera cam)
    {
            
        Vector3f currentPos = this.getLocalTranslation();
        float displacement = tpf*speed;
        Vector3f newPos = currentPos.add(path.normalize().mult(displacement));
        distanceTraveled += displacement;
        
        if ((distanceTraveled>pathLenghtNotification) && (pathLenghtNotification > 0)) {
            this.notifyEndOfPath();
        }
        else if(distanceTraveled > path.length()){
            this.removeFromParent();
        }
        else {
            // set scaling
            this.setLocalScale(startScale*(1-0.9f*(distanceTraveled/path.length())));
            // set position
            this.setLocalTranslation(newPos);
            
            //Adjust orientation
            if (cam!=null)
                this.lookAt(cam.getLocation(), cam.getUp());
        }    
        
    }
    
    public void updatePosition(float tpf, Camera cam) {

        if(isCurved)
        {
            updateCurvedPosition(tpf, cam);
        }
        else
        {
            updateLinearPosition(tpf, cam);
        }
    }

    @Override
    public void notifyEndOfPath() {
        for (SignalObserver observer: signalObservers){
            observer.signalEndOfPath(this, this.getLocalScale().x);
            this.removeFromParent();
        }
    }

    public void setPathLenghtNotification(float lenght)
    {
        this.pathLenghtNotification = lenght;
    }
//    //TODO: Useful to removed ?!
//    public void SetPropagationSpeed(){
//
//    }
//
//    //TODO: Useful to removed ?!
//    public void SetRotationSpeed(){
//
//    }
//
//    //TODO: Useful to removed ?!
//    public void SetAmplitude(){
//
//    }
//
//    //TODO: Useful to removed ?!
//    public void SetAmplitudeValueQueue(int[] amplitudeArray) {
//
//    }
        
}
