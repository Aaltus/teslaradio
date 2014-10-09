/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;


/**
 *
 * @author jimbojd72
 */
public class FadeControl extends AbstractControl {

    private float fadingTime = 0f;
    private float currentFadingTime = 0f;
    private boolean showImage = true;
    
    /**
     * Set the show image bool that will create a fade in/ fade out.
     * @param showImage the showImage to set
     */
    public void setShowImage(boolean showImage) {
        this.showImage = showImage;
    }
    
    public FadeControl(float fadingTime){
        
        this.fadingTime = fadingTime;
        this.currentFadingTime = fadingTime;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
        if (showImage && currentFadingTime < fadingTime)
        {
            currentFadingTime += tpf;
            //floatfloat deltaScale = tpf*fadeSpeed;
            float deltaScale = tpf*(1/fadingTime);
            
            
            updateMaterial(this.spatial, deltaScale);
            //imageGeom.getMaterial().setColor("Color", new ColorRGBA(1f ,1f ,1f,currentFadingTime / fadingTime));
            //AppLogger.getInstance().d("ImageBox.java", "Fade In Time " + Float.toString(currentFadingTime));
        }
        else if(!showImage && currentFadingTime > 0)
        {
            currentFadingTime -= tpf;
            //float deltaScale = tpf*fadeSpeed;
            float deltaScale = -tpf*(1/fadingTime);
            updateMaterial(this.spatial, deltaScale);
            //imageGeom.getMaterial().setColor("Color", new ColorRGBA(1f ,1f ,1f,currentFadingTime / fadingTime));
            //AppLogger.getInstance().d("ImageBox.java", "Fade In Time " + Float.toString(currentFadingTime));
        }
    }
    
    
    private void fadeInMaterial(Material material, float deltaScale){
        
            //currentFadingTime += tpf;
            ColorRGBA color;
            try{
                 color = (ColorRGBA)material.getParam("Color").getValue();
            }
            catch (NullPointerException ex){
                
                AppLogger.getInstance().d(FadeControl.class.getSimpleName(), "No color for material, we define one.");
                color = new ColorRGBA(1,1,1, deltaScale < 0 ? 1 : 0 );
            
            }
            
            //By adding fading time
            ColorRGBA newColor = color.add(new ColorRGBA(0,0,0,deltaScale/*currentFadingTime / fadingTime/*deltaScale*1/*originalColorAlphaValue*/));
            material.setColor("Color", newColor);
            //ColorRGBA newColor = color.add(new ColorRGBA(0,0,0,currentFadingTime / fadingTime/*deltaScale*1/*originalColorAlphaValue*/));
            //material.setColor("Color", newColor);
            
            
            
            //material.getMaterial().setColor("Color", new ColorRGBA(1f ,1f ,1f,currentFadingTime / fadingTime));
            //AppLogger.getInstance().d("ImageBox.java", "Fade In Time " + Float.toString(currentFadingTime));
        
    }

    private void updateMaterial(Spatial spatial, float deltaScale) {
        try {
            if (spatial instanceof Node) {
                Node node = (Node) spatial;
                for (int i = 0; i < node.getQuantity(); i++) {
                    Spatial child = node.getChild(i);
                    updateMaterial(child, deltaScale);
                }
            } else if (spatial instanceof Geometry) {
                Geometry geo = (Geometry) spatial;
                fadeInMaterial(geo.getMaterial(), deltaScale); // YEA
            }
        } catch (Exception err) {
        System.out.println("Exception ExtNode::updateMaterial() err: " + err + " | " + err.getLocalizedMessage() + " | " + err.getMessage() + " | " + err.toString());
        }
    }
    
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
