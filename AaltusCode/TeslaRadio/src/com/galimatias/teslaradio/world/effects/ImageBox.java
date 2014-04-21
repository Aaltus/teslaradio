/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Greenwood0
 */
public class ImageBox extends Node{
    
    private Box imageRect;
    private Geometry imageGeom;
    private Vector3f initPosition;
    
    /**
     * 
     * @param width
     * @param height
     * @param assetManager
     * @param imageBoxName
     * @param imagePath 
     */
    public ImageBox(float width, float height, Vector3f initPosition, AssetManager assetManager, String imageBoxName, String imagePath)
    {
        this.initPosition = initPosition;
        init(width, height, assetManager, imageBoxName, imagePath);
        
    }
    /**
     * 
     * @param width
     * @param height
     * @param assetManager
     * @param imageBoxName
     * @param imagePath 
     */
    private void init(float width, float height, AssetManager assetManager, String imageBoxName, String imagePath)
    {
        imageRect = new Box(width, height, Float.MIN_VALUE);
        imageGeom = new Geometry(imageBoxName ,imageRect);
        Material imageBoxMat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        imageBoxMat.setTexture("ColorMap", assetManager.loadTexture(imagePath));
        imageBoxMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        imageGeom.setQueueBucket(RenderQueue.Bucket.Transparent);
        imageGeom.setMaterial(imageBoxMat);
        this.attachChild(imageGeom);
        this.move(initPosition);
    }
    
    
        /**
     * 
     * This function rotates the image to face the camera
     *
     * @param updatedText
     * @param updatedSize
     * @param updatedColor
     * @param cam
     * @param scenarioUpVector
     */
    public void simpleUpdate(Camera cam, Vector3f scenarioUpVector, float backDistance)
    {
        this.lookAt(cam.getLocation(), scenarioUpVector);
        Vector3f moveVector = ((this.getLocalTranslation().subtract(cam.getLocation())).normalize()).mult(new Vector3f(backDistance ,0f, backDistance));
        this.setLocalTranslation(initPosition.add(moveVector));
    }
    
}
