/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.ar4android.vuforiaJME.AppGetter;
import com.galimatias.teslaradio.world.observer.ParticleObserver;
import com.jme3.cinematic.MotionPath;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Dome;
import com.jme3.util.SkyFactory;
import java.util.ArrayList;

/**
 *
 * @author Hugo
 */
public class AirParticleEmitterControl extends ParticleEmitterControl{
    
    private Spatial destinationHandle;
    private float maxScale;
    private Material material;
    
    public AirParticleEmitterControl(Spatial destinationHandle, float speed, float maxScale, Material material)
    {
        spatialToSendBuffer = new ArrayList();
        //path = new MotionPath();
        
        this.speed = speed;
        this.maxScale = maxScale;
        this.destinationHandle = destinationHandle;
        this.material = material;
        
    }

    // notification from particle when they reach their goal.
    @Override
    public void onParticleEndOfLife(Spatial toBeDeletedSpatial) {
        
        // deconnect particle from this particle emitter
        toBeDeletedSpatial.removeControl(DomeSignalControl.class);
        toBeDeletedSpatial.removeFromParent();
        
        
    }
    @Override
    public void onParticleReachingReceiver(Spatial toBeDeletedSpatial) {
        
        // notify Registered observers of the ParticleEmitter
        this.notifyObservers(toBeDeletedSpatial,this.spatial.getName());
    }


    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void emitParticle(Spatial spatialToSend) {
        
        Dome box1 = new Dome( new Vector3f(), 32, 32, this.maxScale, false);
        Geometry geom1 = new Geometry("Test", box1);
        Dome box2 = new Dome( new Vector3f(), 32, 32, this.maxScale, true);
        Geometry geom2 = new Geometry("Test", box2);
        Material materialClone = this.material.clone();
        geom1.setMaterial(materialClone);
        geom2.setMaterial(materialClone);
        Spatial particle = geom1;
        DomeSignalControl sigControl = new DomeSignalControl(speed,spatialToSend,destinationHandle,materialClone);
        
        Node testNode = new Node();
        testNode.attachChild(geom1);
        testNode.attachChild(geom2);
        testNode.setQueueBucket(RenderQueue.Bucket.Transparent);
        
        sigControl.registerObserver(this);
        //sigControl2.registerObserver(this);
        testNode.addControl(sigControl);
        //geom2.addControl(sigControl2);
        spatialToSendBuffer.add(testNode);
        //spatialToSendBuffer.add(geom2);
    }

    @Override
    protected void pathUpdate() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    

}
