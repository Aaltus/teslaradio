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
    private float       baseScale;
    private float       cumulatedTime;
    private String      name;
    private Quaternion  rot;
    private Geometry    imageGeom;
    
    // Refresh hint values
    private float maxTimeRefreshHint = 10f;
    private float timeLastTouch = maxTimeRefreshHint;
    private float hintFadingTime = 1.5f;
    
    /**
     * 
     * @param name
     * @param mat
     * @param baseScale 
     */
    public Arrows(String name, Vector3f location, AssetManager assetManager, float baseScale)
    {        
        if (name.equals("touch")){
            imageGeom = new Geometry(name ,new Box(0.5f, 0.5f, Float.MIN_VALUE));
            imageGeom.move(Vector3f.UNIT_Y);
            imageGeom.setQueueBucket(RenderQueue.Bucket.Translucent);
        }
        else{
            imageGeom = new Geometry(name ,new Box(1f,Float.MIN_VALUE,1f));
            imageGeom.move(new Vector3f(0f, -0.01f, 0f));
            imageGeom.setQueueBucket(RenderQueue.Bucket.Opaque);
        }
        
        Material imageMat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        imageMat.setTexture("ColorMap", assetManager.loadTexture("Textures/"+name+".png"));
        imageMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        FadeControl fade = new FadeControl(hintFadingTime);
        if (location != null){
            this.move(location);
        }
        imageGeom.setMaterial(imageMat);
        this.addControl(fade);
        this.attachChild(imageGeom);
        this.setLocalScale(baseScale);
        
        this.name      = name;
        this.baseScale = baseScale;
        
        if (name.equals("rotation")){
            this.rot = new Quaternion();
        }
    }
    
    public void resetTimeLastTouch(){
        timeLastTouch = 0f;
    }
    
    public void simpleUpdate(float tpf)
    {
        timeLastTouch += tpf;
        cumulatedTime += tpf;

        if (timeLastTouch >= maxTimeRefreshHint)
        {
            this.getControl(FadeControl.class).setShowImage(true);
        }
                
        float movement = 0.5f*(float)Math.sin(cumulatedTime*3);
        if (this.name.equals("move")){
            this.setLocalScale(baseScale+movement);
        }
        else if (this.name.equals("rotation")){
            this.setLocalRotation(this.rot.fromAngleAxis(movement, Vector3f.UNIT_Y));
        }
        else if (this.name.equals("touch")){
            this.setLocalScale(baseScale+movement/3);            
        }
    }
    
}
