/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaltus.teslaradio.world.effects;

import com.ar4android.vuforiaJME.AppGetter;
import com.jme3.cinematic.MotionPath;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Cylinder;

import java.util.ArrayList;

/**
 *
 * @author Hugo
 */
public class DynamicWireParticleEmitterControl extends ParticleEmitterControl {
    
    private MotionPath path;
    private Spatial destinationHandle;
    private Node dummyRootNodeScaled = new Node();

    // dynamic wire
    private Node wireGeomNode = null;
    private Vector3f pathDirection = new Vector3f();
    private Quaternion wireRotQuat = new Quaternion();

    
    public DynamicWireParticleEmitterControl(Spatial destinationHandle, float speed)
    {
        this(destinationHandle, speed, null);
    }
    
    public DynamicWireParticleEmitterControl(Spatial destinationHandle, float speed, Camera cam){
        this(destinationHandle, speed, cam, false);
    }
               
    public DynamicWireParticleEmitterControl(Spatial destinationHandle, float speed, Camera cam, boolean wireIsVisible)
    {
        spatialToSendBuffer = new ArrayList();
        path = new MotionPath();
        
        this.speed = speed;
        this.destinationHandle = destinationHandle;
        this.cam = cam;

        // create a wire geom and attach the dynamic wire control to it
        if(wireIsVisible){

            // create the wire geom
            Geometry wireGeom  = new Geometry();
            wireGeom.setMesh(new Cylinder(4, 4, 0.04f, 1, true));
            Material wireMat = new Material(AppGetter.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            wireMat.setColor("Color", new ColorRGBA(0, 0, 0, 1));
            wireGeom.setMaterial(wireMat);
            wireGeom.setLocalTranslation(0, 0, 0.5f);

            this.wireGeomNode = new Node();
            this.wireGeomNode.attachChild(wireGeom);
        }

    }
    
    protected void pathUpdate() {
        
        // validate that the handle is valid
        //TODO: Maybe do something more bulletproof than getting the rootnode from AppGetter
        if(AppGetter.hasRootNodeAsAncestor(this.destinationHandle))
        {
            // create new path at each frame
            this.path.clearWayPoints();
            this.path.addWayPoint(Vector3f.ZERO);
            this.pathDirection = this.spatial.getWorldRotation().inverse().mult((this.destinationHandle.getWorldTranslation().subtract(this.spatial.getWorldTranslation())).divide(this.spatial.getWorldScale().x));
            this.path.addWayPoint(this.pathDirection);
            
            // update the wire position
            if(this.wireGeomNode != null){
                // update wire length
                this.wireGeomNode.setLocalScale(1, 1, this.path.getLength());

                // update wire rotation
                this.wireGeomNode.setLocalRotation(findRotQuaternion(Vector3f.UNIT_Z,pathDirection,wireRotQuat));
            }
        }
        else
        {
            // delete path if there is no destination
            this.path.clearWayPoints();
            
            if(this.wireGeomNode != null){
                this.wireGeomNode.setLocalScale(1, 1, 0);
            }
        }

    }

    private Quaternion findRotQuaternion(Vector3f v1, Vector3f v2, Quaternion returnQuat)
    {
        Vector3f rotAxis = v1.cross(v2);
        returnQuat.fromAngleAxis(v1.normalizeLocal().angleBetween(v2.normalizeLocal()),rotAxis.normalizeLocal());
        return returnQuat;
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
        SignalControl sigControl = new SignalControl(path,speed,cam,this.spatial);
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
        toBeDeletedSpatial.setLocalTranslation(0, 0, 0);

        // notify Registered observers of the ParticleEmitter
        if(this.path.getLength() != 0f){this.notifyObservers(toBeDeletedSpatial, this.spatial.getName());}
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
        
        // set the emitter handle to the wire control
        if(this.wireGeomNode != null) {
            ((Node) this.spatial).attachChild(this.wireGeomNode);
        }
            
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
            
            spatialToAttach.setLocalTranslation(Vector3f.ZERO);
            ((Node) this.spatial).attachChild(spatialToAttach);
        }
        spatialToSendBuffer.clear();
        
        // update dynamic path
        this.pathUpdate();
    }

}
