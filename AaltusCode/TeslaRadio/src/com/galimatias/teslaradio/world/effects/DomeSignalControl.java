/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.galimatias.teslaradio.world.observer.ParticleObservable;
import com.galimatias.teslaradio.world.observer.ParticleObserver;
import com.jme3.cinematic.MotionPath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author jimbojd72
 */
public class DomeSignalControl extends AbstractControl implements ParticleObservable{
    
    private float currentTotalScale;
    private float speed;
    private float maxScale;
    private Spatial particle;
    private Spatial destinationSpatial;
    private boolean reachingDestination = false;
    // for end of path notification
    private ParticleObserver observer;
    private boolean startScaling = false;
    
    public DomeSignalControl(float speed, Spatial particle, Spatial destinationSpatial, float maxScale){
        this.speed = speed;
        this.enabled = false;
        this.particle = particle;
        this.destinationSpatial = destinationSpatial;
        this.maxScale = maxScale;
        this.currentTotalScale = 0;
    }
    
    

    @Override
    protected void controlUpdate(float tpf) {
        
        if(!startScaling){
            this.spatial.setLocalScale(0);
            startScaling = true;
        }
        
        float deltaScale = tpf*speed;
        currentTotalScale += deltaScale;
        this.spatial.setLocalScale(currentTotalScale);
        
        //When the distance == 0, we know we are inside the dome because there is no distance between both.
        float distanceFromEdge = this.spatial.getWorldBound().distanceToEdge(destinationSpatial.getWorldTranslation());
        if(!reachingDestination && distanceFromEdge == 0)
        {
            reachingDestination = true;
            this.observer.onParticleReachingReceiver(spatial);
        }
        
        // check if it is the end of the path and notify
        if(currentTotalScale >= 1)
        {
            if(this.observer != null){
                this.observer.onParticleEndOfLife(spatial);
            }
            return;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void registerObserver(ParticleObserver observer) {
        this.observer = observer;
    }

    @Override
    public void removeObserver(ParticleObserver observer) {
        this.observer = null;
    }

   
    
}
