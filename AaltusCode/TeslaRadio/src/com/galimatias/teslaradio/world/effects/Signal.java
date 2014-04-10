/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.jme3.material.RenderState;
import com.jme3.math.Spline;
import com.jme3.math.Spline.SplineType;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Curve;
import java.util.List;

import java.util.Vector;



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
    private float distanceTraveled;
    private float capturePathLength = -1;
    
    // Linear path Particle
    public Signal(Geometry particle, Vector3f path, float speed) {
            this.setMesh(particle.getMesh());
            this.setMaterial(particle.getMaterial());
            this.speed = speed;
            this.path = path;
            this.isCurved = false;
            this.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            this.setQueueBucket(Bucket.Translucent);
            this.capturePathLength = -1;
    }
 
    // Linear path Particle with capture
    public Signal(Geometry particle, Vector3f path, float speed, float capturePathLength) {
            this.setMesh(particle.getMesh());
            this.setMaterial(particle.getMaterial());
            this.speed = speed;
            this.path = path;
            this.isCurved = false;
            this.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            this.setQueueBucket(Bucket.Translucent);
            this.capturePathLength = capturePathLength;
    }
    
    // Curved path Particle
    public Signal(Geometry particle, Spline curvedPath, float speed)
    {
        this.setMesh(particle.getMesh());
        this.setMaterial(particle.getMaterial());
        this.curvedPath = curvedPath;
        this.speed = speed;
        this.isCurved = true;
        
        this.curvePath_segmentLength = this.curvedPath.getSegmentsLength();
    }
    
      
    private void updateCurvedPosition(float tpf)
    {         
        float displacement = tpf*speed;
        distanceTraveled += displacement;
        
        //Deletion of the object if its at the end of its path.
        if (distanceTraveled> curvedPath.getTotalLength()) 
        {
            //((SignalEmitter) this.getParent()).notifyObservers();
            this.removeFromParent();
        }
        else 
        {
            
            // calculate position
            int lineIndex = 0;
            float currentLength = curvePath_segmentLength.get(lineIndex);
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
        } 
  
    }
    private void updateLinearPosition(float tpf)
    {
            
        Vector3f currentPos = this.getLocalTranslation();
        float displacement = tpf*speed;
        Vector3f newPos = currentPos.add(path.normalize().mult(displacement));
        distanceTraveled += displacement;
        
        //Deletion of the object if its at the end of its path.
        if(distanceTraveled>capturePathLength && capturePathLength!= -1)
        {
            ((SignalEmitter) this.getParent()).notifyObservers();
            this.removeFromParent();
            
        }
        else if (distanceTraveled>path.length()) {
          //  this.setCullHint(CullHint.Always);
            this.removeFromParent();
        }
        else {
            // set scaling
            this.setLocalScale(1-(distanceTraveled/path.length()));
            // set position
            this.setLocalTranslation(newPos);
        }    
        
    }
    
    public void updatePosition(float tpf) {

        if(isCurved)
        {
            updateCurvedPosition(tpf);
        }
        else
        {
            updateLinearPosition(tpf);
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
