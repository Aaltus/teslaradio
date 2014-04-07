/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import java.util.Vector;



/**
 *
 * @author David
 */
public class Signal extends Geometry {
    
    private Vector3f path;
    
    private Vector<Vector3f> curvedPath;
    private boolean isCurved;
    private int index = 0;
    
    private Vector3f startPoint;
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
    public Signal(Geometry particle, Vector<Vector3f> curvedPath, float speed)
    {
        this.setMesh(particle.getMesh());
        this.setMaterial(particle.getMaterial());
        this.curvedPath = curvedPath;
        this.speed = speed;
        this.isCurved = true;
    }
    
      
    private void updateCurvedPosition(float tpf)
    {
        Vector3f currentPath = curvedPath.get(index);
              
        Vector3f currentPos = this.getLocalTranslation();
        float displacement = tpf*speed;
        Vector3f newPos = currentPos.add(currentPath.normalize().mult(displacement));
        distanceTraveled += displacement;
        
        //Deletion of the object if its at the end of its path.
        if (distanceTraveled>currentPath.length()) {
          //  this.setCullHint(CullHint.Always);
            
            index ++;
            //this.setLocalTranslation(currentPos.subtract(currentPath.normalize().mult(distanceTraveled-displacement)));
            distanceTraveled = 0;
            int listSize = curvedPath.size();
            
            if(index >= listSize)
            {
                this.removeFromParent();
            }

        }
        else {
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
        if (distanceTraveled>path.length() || (distanceTraveled>capturePathLength && capturePathLength!= -1)) {
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
