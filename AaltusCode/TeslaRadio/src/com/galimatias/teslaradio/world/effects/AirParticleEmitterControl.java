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
import java.util.List;

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
        toBeDeletedSpatial.removeControl(ScalingSignalControl.class);
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
        
        emitParticle(spatialToSend, this.maxScale);
    }
    
    private void emitParticle(Spatial spatialToSend, float scale) {
        //if this value is bigger the dome will be more precise but will require more triangles to draw
        final int numberOrRadialAndPlanes = 12;
        
        //We have two dome, one that can be seen from inner and one that can be seen from outise of the dome.
        Dome outsideDome = new Dome( new Vector3f(), numberOrRadialAndPlanes, numberOrRadialAndPlanes, scale, false);
        Geometry outsideDomeGeom = new Geometry("Test", outsideDome);
        Dome insideDome = new Dome( new Vector3f(), numberOrRadialAndPlanes, numberOrRadialAndPlanes, scale, true);
        Geometry insideDomeGeom = new Geometry("Test", insideDome);
        Material materialClone = this.material.clone();
        outsideDomeGeom.setMaterial(materialClone);
        insideDomeGeom.setMaterial(materialClone);
        Spatial particle = outsideDomeGeom;
        ScalingSignalControl sigControl = new ScalingSignalControl(speed,spatialToSend,destinationHandle,materialClone);
        
        //Both node are attached to the same node that is in the transparent bucket
        Node testNode = new Node();
        testNode.attachChild(outsideDomeGeom);
        testNode.attachChild(insideDomeGeom);
        testNode.setQueueBucket(RenderQueue.Bucket.Transparent);
        
        //We register our emitter to receive update and we add our DomeSignalControl
        sigControl.registerObserver(this);
        testNode.addControl(sigControl);
        spatialToSendBuffer.add(testNode);
    }
    
    

    @Override
    protected void pathUpdate() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    

}
