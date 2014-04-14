/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 *
 * @author David
 */
public class Halo extends Geometry {
    
    private float cumulatedTime;
    
    public Halo(String name, Box rect, Material halo_mat) {
        super("halo",rect);
        
        this.setMaterial(halo_mat);
        this.setQueueBucket(RenderQueue.Bucket.Translucent);
    }
    
    public void simpleUpdate(float tpf) {
    cumulatedTime+=tpf;
        this.setLocalScale((float) (0.9+0.1f*Math.sin(cumulatedTime*3)));
    }
    
}
