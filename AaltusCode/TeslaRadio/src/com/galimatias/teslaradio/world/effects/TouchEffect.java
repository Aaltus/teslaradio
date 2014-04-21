/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Alexandre
 */
public class TouchEffect extends Geometry {

    private float scaleGradient;
    private float maxScale;
    private Vector3f path;
    private float scaling;
    private float baseScale;
    private Vector3f newPath;
    
    public TouchEffect(float scaleGradient, float baseScale, float maxScale, Vector3f path, Geometry effectToScale) {
        
        this.scaleGradient = scaleGradient;
        this.maxScale = maxScale;
        this.baseScale = baseScale;
        this.path = path;
        this.setMesh(effectToScale.getMesh());
        this.setMaterial(effectToScale.getMaterial());
        this.setLocalScale(baseScale);
    }
    
    public void updateScaling(float tpf) {
        
        float scalingFactor = tpf*scaleGradient;
        scaling += scalingFactor;
        newPath = path.mult(scaling);
        
        if(this.getLocalScale().length() > maxScale) {
            this.removeFromParent();
            scaling = baseScale;
            newPath = path;
        }
        else
            this.setLocalScale(newPath);
    }
    
}
