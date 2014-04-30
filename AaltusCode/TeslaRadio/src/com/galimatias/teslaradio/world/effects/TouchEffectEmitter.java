/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Alexandre
 */
public class TouchEffectEmitter extends Node {
    
    private boolean isTouched = false;
    private float baseScale;
    private float maxScale;
    private float scaleGradient;
    private Vector3f path;
    private Geometry effectToScale;
       
    public TouchEffectEmitter(String name, float baseScale, float maxScale, float scaleGradient, Geometry effectToScale, Vector3f path) {
        
        this.setName(name);
        this.path = path;
        this.baseScale = baseScale;
        this.maxScale = maxScale;
        this.scaleGradient = scaleGradient;
        this.effectToScale = effectToScale;
    }
    
    public void isTouched() {
        
        this.isTouched = true;
    }
    
    public void simpleUpdate(float tpf) {
        
        TouchEffect effect;
        
        for (Spatial scaledGeo : this.getChildren()) {            
            effect = (TouchEffect)scaledGeo;
            effect.updateScaling(tpf);
        }
        
        if(isTouched) {
                     
            TouchEffect myEffect = new TouchEffect(scaleGradient, baseScale, maxScale, path, effectToScale);
            this.attachChild(myEffect);
            isTouched = false;
        }
    }
    
}
