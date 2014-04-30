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
import com.jme3.scene.shape.Quad;

/**
 * This class provides a floating 2D image for the 3D world. 
 * @author Emilien Boisvert
 */
public class ImageBox extends Node{
    
    private Quad imageRect;
    private Geometry imageGeom;
    private float fadingTime = 0f;
    private float currentFadingTime = 0f;
    private boolean showImage = true;
    
    /**
     * Constructor with all variables
     * @param width
     * @param height
     * @param assetManager
     * @param imageBoxName
     * @param imagePath 
     */
    public ImageBox(float width, float height, AssetManager assetManager, String imageBoxName, String imagePath, float fadingTime)
    {
        this.fadingTime = fadingTime;
        currentFadingTime = fadingTime;
        init(width, height, assetManager, imageBoxName, imagePath);
        
    }
    
    /**
     * Set the show image bool that will create a fade in/ fade out.
     * @param showImage the showImage to set
     */
    public void setShowImage(boolean showImage) {
        this.showImage = showImage;
    }
    
    /**
     * Initialize the 3D box and textures
     * @param width
     * @param height
     * @param assetManager
     * @param imageBoxName
     * @param imagePath 
     */
    private void init(float width, float height, AssetManager assetManager, String imageBoxName, String imagePath)
    {
        imageRect = new Quad(width, height);
        imageGeom = new Geometry(imageBoxName ,imageRect);
        Material imageBoxMat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        //Texture imageTexture = assetManager.loadTexture(imagePath);
        //imageTexture.set
        imageBoxMat.setTexture("ColorMap", assetManager.loadTexture(imagePath));
        imageBoxMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        imageGeom.setQueueBucket(RenderQueue.Bucket.Transparent);
        imageGeom.setMaterial(imageBoxMat);
        this.attachChild(imageGeom);
        
    }
 
    /**
     * This function rotates the image to face the camera. It's also managign the texture fading
     * 
     * @param updatedText
     * @param updatedSize
     * @param updatedColor
     * @param cam
     * @param scenarioUpVector
     */
    public void simpleUpdate(float tpf, Camera cam, Vector3f scenarioUpVector)
    {
        this.lookAt(cam.getLocation(), scenarioUpVector);

        if (showImage && currentFadingTime < fadingTime)
        {
            currentFadingTime += tpf;
            imageGeom.getMaterial().setColor("Color", new ColorRGBA(1f ,1f ,1f,currentFadingTime / fadingTime));
            System.out.println("Fade In Time " + currentFadingTime);
        }
        else if(!showImage && currentFadingTime > 0)
        {
            currentFadingTime -= tpf;
            imageGeom.getMaterial().setColor("Color", new ColorRGBA(1f ,1f ,1f,currentFadingTime / fadingTime));
            System.out.println("Fade Out Time " + currentFadingTime);
        }
        
    }
    
}

/* Will be useful later
    public void simpleUpdate(Camera cam, Vector3f scenarioUpVector, float backDistance)
    {
        this.lookAt(cam.getLocation(), scenarioUpVector);
        Vector3f moveVector = ((this.getLocalTranslation().subtract(cam.getLocation())).normalize()).mult(new Vector3f(backDistance ,0f, backDistance));
        this.setLocalTranslation(initPosition.add(moveVector));
    }
    */