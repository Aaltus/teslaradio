/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.ar4android.vuforiaJME.AppGetter;
import com.jme3.cinematic.MotionPath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import java.util.ArrayList;

/**
 *
 * @author Hugo
 */
public class DynamicWireParticleEmitterControl extends ParticleEmitterControl {
    
    private MotionPath path;
    private Spatial destinationHandle;

    public DynamicWireParticleEmitterControl(Spatial destinationHandle, float speed)
    {
        this(destinationHandle, speed, null);
    }
    
    public DynamicWireParticleEmitterControl(Spatial destinationHandle, float speed, Camera cam)
    {
        spatialToSendBuffer = new ArrayList();
        path = new MotionPath();
        
        this.speed = speed;
        this.destinationHandle = destinationHandle;
        this.cam = cam;
    }
    
    @Override
    protected void pathUpdate() {
        // validate that the handle is valid
        //TODO: Maybe do something more bulletproof than getting the rootnode from AppGetter
        if(AppGetter.hasRootNodeAsAncestor(this.destinationHandle))
        {
            this.path.clearWayPoints();
            this.path.addWayPoint(new Vector3f(0,0,0));
            this.path.addWayPoint((this.destinationHandle.getWorldTranslation().subtract(this.spatial.getWorldTranslation())).divide(this.spatial.getWorldScale())); 
        }
        else
        {
            this.path.clearWayPoints();
        }
    }    
        
    @Override
    public void emitParticle(Spatial spatialToSend) {
        // if there is specific material to be use by the emitter apply it
        // if not dont change the already in place material
        if(this.material != null)
        {
            spatialToSend.setMaterial(this.material);
        }
        
        // create the signal control and put the signal in the send buffer
        SignalControl sigControl = new SignalControl(path,speed,cam);
        sigControl.registerObserver(this);
        spatialToSend.addControl(sigControl);
        spatialToSendBuffer.add(spatialToSend);
    }

    // notification from particle when they reach their goal.
    @Override
    public void onParticleEndOfLife(Spatial toBeDeletedSpatial) {
        
        // deconnect particle from this particle emitter
        toBeDeletedSpatial.removeControl(SignalControl.class);
        toBeDeletedSpatial.removeFromParent();
        
        // notify Registered observers of the ParticleEmitter
        this.notifyObservers(toBeDeletedSpatial,this.spatial.getName());
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    @Override
    public void onParticleReachingReceiver(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
