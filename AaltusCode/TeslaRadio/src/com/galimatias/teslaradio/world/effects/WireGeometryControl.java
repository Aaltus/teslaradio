/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.ar4android.vuforiaJME.AppGetter;
import com.jme3.cinematic.MotionPath;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Cylinder;

/**
 *
 * @author Hugo
 */
public class WireGeometryControl extends AbstractControl {

    private Vector3f pathDirection = new Vector3f();
    private Quaternion wireRotQuat = new Quaternion();
    
    // reference to position updated in dynamic emitter
    private Spatial destinationHandle;
    private Spatial emitterHandle = null;
    private MotionPath path;
    private Vector3f emitterPos = new Vector3f();
    
    // wire geometry
    private Geometry wireGeom = new Geometry();
    
    public WireGeometryControl(MotionPath path, Spatial destinationHandle)
    {
        this.path = path;
        this.destinationHandle = destinationHandle;
        
        // create the wire geom
        wireGeom.setMesh(new Cylinder(4, 4, 0.04f, 1, true));
        Material wireMat = new Material(AppGetter.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        wireMat.setColor("Color", new ColorRGBA(0, 0, 0, 1));
        wireGeom.setMaterial(wireMat);
        wireGeom.setLocalTranslation(0, 0, 0.5f);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        ((Node) spatial).attachChild(this.wireGeom);
        super.setSpatial(spatial);
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        
        if(this.emitterHandle != null){
            // move wire dynamically
            if(AppGetter.hasRootNodeAsAncestor(this.destinationHandle) && AppGetter.hasRootNodeAsAncestor(this.emitterHandle)){
                // attach wire if not attach
                if(!this.wireGeom.hasAncestor((Node) this.spatial))
                {
                    ((Node) this.spatial).attachChild(this.wireGeom);
                }
            }
            else{
                // detach if attached and emitter is not active
                if(this.wireGeom.hasAncestor((Node) this.spatial))
                {
                    this.wireGeom.removeFromParent();
                }
            }
        }
    }
    
    // called by emitter after his own update
    public void wirePositionUpdate(float tpf)
    {
        if(this.emitterHandle != null && AppGetter.hasRootNodeAsAncestor(this.destinationHandle) && AppGetter.hasRootNodeAsAncestor(this.emitterHandle)){
            // get the new position of the emitter in world
            emitterPos = emitterHandle.getWorldTranslation().divide(emitterHandle.getWorldScale());

            // update wire position
            this.spatial.setLocalScale(1, 1, this.path.getLength());
            this.spatial.setLocalTranslation(emitterPos);
            pathDirection = this.destinationHandle.getWorldTranslation().divide(emitterHandle.getWorldScale()).subtract(emitterPos);
            this.spatial.setLocalRotation(findRotQuaternion(Vector3f.UNIT_Z,pathDirection,wireRotQuat));       
        }
    }
 
    private Quaternion findRotQuaternion(Vector3f v1, Vector3f v2, Quaternion returnQuat)
    {
        Vector3f rotAxis = v1.cross(v2);
        returnQuat.fromAngleAxis(v1.normalizeLocal().angleBetween(v2.normalizeLocal()),rotAxis.normalizeLocal());
        return returnQuat;
    }
    
    public void setEmitterHandle(Spatial emitterHandle){
        this.emitterHandle = emitterHandle;
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
       
    }
    
}
