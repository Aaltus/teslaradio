/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaltus.teslaradio.world.effects;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author jimbojd72
 */
public class LookAtCameraControl extends AbstractControl {

    
    private Camera camera;

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    
    public LookAtCameraControl(Camera cam) 
    {
        
        setCamera(cam);
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        
        //this.spatial.lookAt(camera.getLocation(), upVector);
        this.spatial.lookAt(camera.getLocation(), camera.getUp());
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
