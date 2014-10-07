/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.ar4android.vuforiaJME.AppGetter;
import com.jme3.cinematic.MotionPath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;

/**
 *
 * @author Hugo
 */
public class DynamicWireParticleEmitterControl extends ParticleEmitterControl {
    
    private MotionPath path;
    private Spatial destinationHandle;
    private Quaternion localRotMem = new Quaternion();
    private Node dummyRootNodeScaled = new Node();

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
            this.path.addWayPoint(this.spatial.getWorldTranslation().divide(this.spatial.getWorldScale().x));
            this.path.addWayPoint(this.destinationHandle.getWorldTranslation().divide(this.spatial.getWorldScale().x));
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

        // disconnect particle from this particle emitter
        toBeDeletedSpatial.removeControl(SignalControl.class);
        toBeDeletedSpatial.removeFromParent();
        
        // notify Registered observers of the ParticleEmitter
        this.notifyObservers(toBeDeletedSpatial, this.spatial.getName());
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    @Override
    public void onParticleReachingReceiver(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSpatial(Spatial spatial) {
        AppGetter.attachToRootNode(this.dummyRootNodeScaled);
        this.dummyRootNodeScaled.scale(AppGetter.getWorldScalingDefault());
        super.setSpatial(spatial);
    }

    @Override
    protected void controlUpdate(float tpf) {

        for(Spatial spatialToAttach : spatialToSendBuffer)
        {
            
            AbstractControl control = spatialToAttach.getControl(SignalControl.class);
            if(control != null){
                control.setEnabled(true);
            }
            
            control = spatialToAttach.getControl(ScalingSignalControl.class);
            if(control != null){
                control.setEnabled(true);
            }
            
            this.dummyRootNodeScaled.attachChild(spatialToAttach);
            //((Node) this.spatial).attachChild(spatialToAttach);
        }
        spatialToSendBuffer.clear();
        
        // update dynamic path
        this.pathUpdate();
    }

}
