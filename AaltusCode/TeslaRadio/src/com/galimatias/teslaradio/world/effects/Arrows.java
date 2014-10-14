/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;

/**
 *
 * @author Christian
 */
public class Arrows extends Node{
    private float baseScale;
    private float cumulatedTime;
    private String name;
    private Quaternion rot;
    private Geometry imageGeom;
    private float fadingTime;
    private float currentFadingTime;  
    private boolean showImage = true;  
    
    /**
     * 
     * @param name
     * @param mat
     * @param baseScale 
     */
    public Arrows(String name, AssetManager assetManager, float baseScale, float fadingTime)
    {
        this.fadingTime = fadingTime;
        currentFadingTime = fadingTime;
        
        if (name.equals("touch")){
            imageGeom = new Geometry(name ,new Quad(1f, 1f));
        }
        else{
            imageGeom = new Geometry(name ,new Box(1f,Float.MIN_VALUE,1f));
        }
        Material imageMat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        imageMat.setTexture("ColorMap", assetManager.loadTexture("Textures/"+name+".png"));
        imageMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        imageGeom.setQueueBucket(RenderQueue.Bucket.Transparent);
        imageGeom.setMaterial(imageMat);
        this.attachChild(imageGeom);
        
        this.name = name;
        this.baseScale = baseScale;
        this.setLocalScale(baseScale);
        //this.setQueueBucket(RenderQueue.Bucket.Transparent);
        if (name.equals("rotation")){
            this.rot = new Quaternion();
        }
    }
    
    /**
     * Set the show image bool that will create a fade in/ fade out.
     * @param showImage the showImage to set
     */
    public void setShowImage(boolean showImage) {
        this.showImage = showImage;
    }
    
    public void simpleUpdate(float tpf)
    {
        cumulatedTime+=tpf;
        float movement = 0.5f*(float)Math.sin(cumulatedTime*3);
        
        if (this.name.equals("move")){
            this.setLocalScale(baseScale+movement);
        }
        else if (this.name.equals("rotation")){
            this.setLocalRotation(this.rot.fromAngleAxis(movement, Vector3f.UNIT_Y));
        }
        else if (this.name.equals("move")){
            
        }
    }
    
}
