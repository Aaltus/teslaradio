/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.galimatias.teslaradio.world.observer.Observer;
import com.jme3.cinematic.MotionPath;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import java.util.ArrayList;

/**
 *
 * @author Hugo
 */
public class AirParticleEmitterControl extends ParticleEmitterControl{
    
    private MotionPath path;
    private Spatial destinationHandle;
    
    public AirParticleEmitterControl(Spatial destinationHandle, float speed)
    {
        
        //Material soundParticul_mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        //soundParticul_mat.setTexture("ColorMap", assetManager.loadTexture("Textures/Sound.png"));
        //destinationHandle.setMaterial(soundParticul_mat);
        
        spatialToSendBuffer = new ArrayList();
        path = new MotionPath();
        
        this.speed = speed;
        this.destinationHandle = destinationHandle;
    }

    @Override
    public void observerUpdate(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void notifyObservers(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void emitParticle(Spatial spatialToSend) {
        // if there is specific material to be use by the emitter apply it
        // if not dont change the already in place material
        if(this.material != null)
        {
            spatialToSend.setMaterial(this.material);
        }
        
        this.path.addWayPoint(spatialToSend.getLocalTranslation());
        this.path.addWayPoint(this.destinationHandle.getLocalTranslation());
        
        SignalControl sigControl = new SignalControl(path,speed);
        sigControl.registerObserver(this);
        spatialToSend.addControl(sigControl);
        spatialToSendBuffer.add(spatialToSend);
    }

    @Override
    protected void pathUpdate() {
        // Do nothing
    }

}
