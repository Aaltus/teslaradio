/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.jme3.cinematic.MotionPath;
import com.jme3.math.Spline;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.mesh.IndexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
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
        spatialToSendFifo = new ArrayList();
        path = new MotionPath();
        
        this.speed = speed;
        this.destinationHandle = destinationHandle;
        
        this.path.addWayPoint(new Vector3f(0,0,0));
        this.path.addWayPoint(this.destinationHandle.getLocalTranslation());

    }
    
    @Override
    protected void pathUpdate() {
        if(this.destinationHandle != null)
        {
            this.path.clearWayPoints();
            this.path.addWayPoint(this.spatial.getLocalTranslation());
            this.path.addWayPoint(this.destinationHandle.getLocalTranslation()); 
        }
    }    
        
    @Override
    public void emitParticle(Spatial spatialToSend) {
        
        SignalControl sigControl = new SignalControl(path,speed);
        sigControl.registerObserver(this);
        spatialToSend.addControl(sigControl);
        spatialToSendFifo.add(spatialToSend);
    }

    // notification from particle when they reach their goal.
    @Override
    public void observerUpdate(Spatial toBeDeletedSpatial) {
        
        // deconnect particle from this particle emitter
        toBeDeletedSpatial.removeControl(SignalControl.class);
        toBeDeletedSpatial.removeFromParent();
        
        // notify Registered observers of the ParticleEmitter
        this.notifyObservers(toBeDeletedSpatial);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }

}
