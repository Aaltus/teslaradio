/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.jme3.cinematic.MotionPath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;



/**
 *
 * @author David
 */
public class Signal extends Geometry {
    
    private Vector3f path;
    private Vector3f curvedPath;
    private Vector3f startPoint;
    private float speed;
    private float distanceTraveled;
    
    public Signal(Geometry particle, Vector3f path, float speed) {
            this.setMesh(particle.getMesh());
            this.setMaterial(particle.getMaterial());
            this.speed = speed;
            this.path = path;
    }
    
    public Signal(Geometry particle, Vector3f path)
    {
        this.setMesh(particle.getMesh());
        this.setMaterial(particle.getMaterial());
        this.curvedPath = path;
    }
    
    public void updatePosition(float tpf) {
        
        Vector3f currentPos = this.getLocalTranslation();
        float displacement = tpf*speed;
        Vector3f newPos = currentPos.add(path.divide(path.length()).mult(displacement));
        distanceTraveled += displacement;
        
        //Deletion of the object if its at the end of its path.
        if (distanceTraveled>path.length()) {
            this.setCullHint(CullHint.Always);
        }
        else {
            this.setLocalTranslation(newPos);
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
    
    public void SetPath(/*bezier*/) {
        
    }
    
}
