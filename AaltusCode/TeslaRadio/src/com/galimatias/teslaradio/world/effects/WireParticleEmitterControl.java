/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.jme3.cinematic.MotionPath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hugo
 */
public class WireParticleEmitterControl extends ParticleEmitterControl {
    
    private MotionPath path;
    private float speed;
    private List<Spatial> spatialAliveFifo;

    public WireParticleEmitterControl()
    {
        spatialToSendFifo = new ArrayList();
        spatialAliveFifo = new ArrayList();
    }
    
    @Override
    public void prepareEmission(Spatial spatialToSend) {
        
        spatialToSend.removeFromParent();
        spatialToSend.addControl(new SignalControl(path,speed));
        spatialToSendFifo.add(spatialToSend);
        spatialAliveFifo.add(spatialToSend);
    }

    
    // notification from particle when they reach their goal.
    @Override
    public void observerUpdate(Spatial spatial) {
        Spatial toBeDeletedSpatial;
        toBeDeletedSpatial = spatialAliveFifo.get(0);
        spatialAliveFifo.remove(0);
        toBeDeletedSpatial.removeControl(SignalControl.class);
        toBeDeletedSpatial.removeFromParent();
        
        // notify Registered observers of the ParticleEmitter
        this.notifyObservers(toBeDeletedSpatial);
    }

    @Override
    public void updatePath() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
