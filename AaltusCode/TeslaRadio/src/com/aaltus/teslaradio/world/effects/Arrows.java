/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaltus.teslaradio.world.effects;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;

/**
 *
 * @author Christian
 */
public class Arrows extends AbstractControl{
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
    public Arrows(String name, AssetManager assetManager, float baseScale)
    {        
        if (name.equals("touch")){
            imageGeom = new Geometry(name ,new Box(0.5f, 0.5f, Float.MIN_VALUE));
            imageGeom.move(Vector3f.UNIT_Y.divide(1.7f));
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
        imageMat.getAdditionalRenderState().setDepthWrite(false);
        
        imageGeom.setMaterial(imageMat);
        
        
        
        this.name      = name;
        this.baseScale = baseScale;
        
        if (name.equals("rotation")){
            this.rot = new Quaternion();
        }
    }
    
    public void resetTimeLastTouch(){
        timeLastTouch = 0f;
    }
    
    @Override
    public void setSpatial(Spatial spatial){
        super.setSpatial(spatial);
        FadeControl fade = new FadeControl(hintFadingTime);
        this.spatial.addControl(fade);
        ((Node)this.spatial).attachChild(imageGeom);
        this.spatial.setLocalScale(baseScale);
    }

    @Override
    protected void controlUpdate(float tpf) {
        timeLastTouch += tpf;
        cumulatedTime += tpf;

        if (timeLastTouch >= maxTimeRefreshHint)
        {
            this.spatial.getControl(FadeControl.class).setShowImage(true);
        }
                
        float movement = 0.5f*(float)Math.sin(cumulatedTime*3);
        if (this.name.equals("move")){
            this.spatial.setLocalScale(baseScale+movement);
        }
        else if (this.name.equals("rotation")){
            this.spatial.setLocalRotation(this.rot.fromAngleAxis(movement, Vector3f.UNIT_Y));
        }
        else if (this.name.equals("touch")){
            this.spatial.setLocalScale(baseScale+movement/3);            
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
