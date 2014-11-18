/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaltus.teslaradio.world.effects;

import com.jme3.material.Material;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 *
 * @author David
 */
public class Halo extends Geometry {
    
    private float cumulatedTime;
    private float baseScale;
    
    public Halo(String name, Box rect, Material halo_mat, float baseScale)
    {
        super("halo",rect);
        
        this.baseScale = baseScale;
        this.setMaterial(halo_mat);
        this.setQueueBucket(RenderQueue.Bucket.Transparent);
    }
    
    public void simpleUpdate(float tpf)
    {
        cumulatedTime+=tpf;
        this.setLocalScale((float) (baseScale+0.1f*Math.sin(cumulatedTime*3)));
    }
    
}