/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaltus.teslaradio.world.Scenarios;

import com.aaltus.teslaradio.world.effects.WireGeometryControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Hugo
 */
public class DestinationHandleGetUpdateControl extends AbstractControl {

    private WireGeometryControl toNotify;
    
    public DestinationHandleGetUpdateControl(WireGeometryControl toNotify){
        this.toNotify = toNotify;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        this.toNotify.wireRotationUpdate();
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
    
    
    
}
