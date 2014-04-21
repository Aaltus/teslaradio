/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.jme3.material.RenderState;
import com.jme3.math.Spline;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import java.util.List;


/**
 *
 * @author David
 */
public class Signal extends Geometry {
    
    private Vector3f path;
    
    private Spline curvedPath;
    private List<Float> curvePath_segmentLength;
    
    private boolean isCurved;
    
    private float speed;
    private float startScale;
    private float distanceTraveled;
    private float capturePathLength = -1;
    
    private int lineIndex = 0;
    private float currentLength = 0;
    
    // Linear path Particle
    public Signal(Geometry particle, Vector3f path, float speed, float startScale) {
            this.setMesh(particle.getMesh());
            this.setMaterial(particle.getMaterial());
            this.speed = speed;
            this.path = path;
            this.isCurved = false;
            this.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            this.setQueueBucket(Bucket.Transparent);
            this.capturePathLength = -1;
            this.startScale = startScale;
            this.setLocalScale(startScale);
    }
 
    // Linear path Particle with capture
    public Signal(Geometry particle, Vector3f path, float speed, float startScale, float capturePathLength) {
            this.setMesh(particle.getMesh());
            this.setMaterial(particle.getMaterial());
            this.speed = speed;
            this.path = path;
            this.isCurved = false;
            this.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            this.setQueueBucket(Bucket.Transparent);
            this.capturePathLength = capturePathLength;
            this.startScale = startScale;
            this.setLocalScale(startScale);
    }
    
    // Curved path Particle
    public Signal(Geometry particle, Spline curvedPath, float speed, float startScale)
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

    }
    
      
    private void updateCurvedPosition(float tpf, Camera cam)
    {         
        float displacement = tpf*speed;
        distanceTraveled += displacement;
        
        //Deletion of the object if its at the end of its path.
        if (distanceTraveled> curvedPath.getTotalLength()) 
        {
            //((SignalEmitter) this.getParent()).notifyObservers();
            lineIndex = 0;
            this.removeFromParent();
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
        
        //Deletion of the object if its at the end of its path.
        if(distanceTraveled>capturePathLength && capturePathLength!= -1)
        {
            ((SignalEmitter) this.getParent().getParent()).notifyObservers(this);
            this.removeFromParent();
            
        }
        else if (distanceTraveled>path.length()) {
          //  this.setCullHint(CullHint.Always);
            this.removeFromParent();
        }
        else {
            // set scaling
            this.setLocalScale(startScale*(1-0.5f*(distanceTraveled/path.length())));
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
    
    public void SetPropagationSpeed(){
        
    }
    
    public void SetRotationSpeed(){
        
    }
    
    public void SetAmplitude(){
        
    }
    
    public void SetAmplitudeValueQueue(int[] amplitudeArray) {
        
    }
        
}
