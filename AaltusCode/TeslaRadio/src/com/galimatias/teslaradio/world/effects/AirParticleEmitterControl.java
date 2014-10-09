/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.ar4android.vuforiaJME.AppGetter;
import com.galimatias.teslaradio.world.observer.ParticleObserver;
import com.jme3.cinematic.MotionPath;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.SkyFactory;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hugo
 */
public class AirParticleEmitterControl extends ParticleEmitterControl{
    
    
    public enum AreaType
    {
        SPHERE,
        DOME
    }

    public void setAreaType(AreaType areaType) {
        this.areaType = areaType;
    }
    
    private Spatial destinationHandle;
    private float radius;
    private Material material;
    private AreaType areaType = AreaType.SPHERE;
    
    
    
    public AirParticleEmitterControl(Spatial destinationHandle, float speed, float maxScale, Material material)
    {
        this(destinationHandle, speed, maxScale, material, AreaType.SPHERE);
    }
    
    public AirParticleEmitterControl(Spatial destinationHandle, float speed, float maxScale, Material material, AreaType areaType)
    {
        spatialToSendBuffer = new ArrayList();
        //path = new MotionPath();
        
        this.speed = speed;
        this.radius = maxScale;
        this.destinationHandle = destinationHandle;
        this.material = material;
        setAreaType(areaType);
        
    }

    // notification from particle when they reach their goal.
    @Override
    public void onParticleEndOfLife(Spatial toBeDeletedSpatial) {
        
        // deconnect particle from this particle emitter
        toBeDeletedSpatial.removeControl(ScalingSignalControl.class);
        toBeDeletedSpatial.removeControl(SignalControl.class);
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
        
        emitParticle(spatialToSend, this.radius*spatialToSend.getLocalScale().length());
    }
    
    private void emitParticle(Spatial spatialToSend, float scale) {
        
        Material materialClone = material.clone();
        
        Spatial scalingSignalNode = null;
        switch(areaType){
        
            case SPHERE:
                scalingSignalNode = this.factoryScalingSphere(materialClone, scale);
                break;
           case DOME:
                scalingSignalNode = this.factoryScalingDome(materialClone, scale);
                break;     
           
        }
        
        
        
        //Configure a scaling signal control
        ScalingSignalControl sigControl = new ScalingSignalControl(speed/scale,spatialToSend,destinationHandle,materialClone);
        //We register our emitter to receive update and we add our DomeSignalControl
        sigControl.registerObserver(this);
        scalingSignalNode.addControl(sigControl);
     
        spatialToSendBuffer.add(scalingSignalNode);
        
        
        // Generate little flying particles
        // TODO: put all this in thread !!!
        float[] temp2 = {0, 0, 0.31416f};
        Quaternion rotQuat2 = new Quaternion(temp2);
        Vector3f path_vector_flat = new Vector3f(scale,0,0);
        for(int axe_flat = 0; axe_flat < 5; axe_flat++)
        {        
            
            float[] temp = {0, 6.2832f/(10-axe_flat), 0};
            Quaternion rotQuat = new Quaternion(temp);
            
            Vector3f path_vector = path_vector_flat.clone();
            for(int axe_a = 0; axe_a < 10-axe_flat; axe_a++)
            {
               Spatial spatial_clone = spatialToSend.clone();
               spatial_clone.setMaterial(materialClone);
               MotionPath path = new MotionPath();
               path.addWayPoint(Vector3f.ZERO);
               path.addWayPoint(path_vector);
               path_vector = rotQuat.mult(path_vector);
               SignalControl sigControl2 = new SignalControl(path,speed,cam,0);
               sigControl2.registerObserver(this);
               spatial_clone.addControl(sigControl2);
               //spatial_clone.addControl(new LookAtCameraControl(Camera));
               spatialToSendBuffer.add(spatial_clone);
            }
            
            path_vector_flat = rotQuat2.mult(path_vector_flat);
        }
    }
    
    private Spatial factoryScalingDome(Material material, float scaleAndRadius){
        
        //if this value is bigger the dome will be more precise but will require more triangles to draw
        final int numberOrRadialAndPlanes = 12;
        //We have two dome, one that can be seen from inner and one that can be seen from outise of the dome.
        Dome outsideDome = new Dome( new Vector3f(), numberOrRadialAndPlanes, numberOrRadialAndPlanes, scaleAndRadius, false);
        Geometry outsideDomeGeom = new Geometry("OutsideDome", outsideDome);
        Dome insideDome = new Dome( new Vector3f(), numberOrRadialAndPlanes, numberOrRadialAndPlanes, scaleAndRadius, true);
        Geometry insideDomeGeom = new Geometry("InsideDome", insideDome);
        
        outsideDomeGeom.setMaterial(material);
        insideDomeGeom.setMaterial(material);
        
        //Both node are attached to the same node that is in the transparent bucket
        Node scalingSignalNode = new Node();
        scalingSignalNode.attachChild(outsideDomeGeom);
        scalingSignalNode.attachChild(insideDomeGeom);
        scalingSignalNode.setQueueBucket(RenderQueue.Bucket.Transparent);
        
        return scalingSignalNode;
        
    }
    
    private Spatial factoryScalingSphere(Material material, float scaleAndRadius){
        
        //if this value is bigger the dome will be more precise but will require more triangles to draw
        final int numberOrRadialAndPlanes = 12;
        //We have two dome, one that can be seen from inner and one that can be seen from outise of the dome.
        Sphere outsideDome = new Sphere(numberOrRadialAndPlanes, numberOrRadialAndPlanes, scaleAndRadius, true, true);
        Geometry outsideDomeGeom = new Geometry("OutsideDome", outsideDome);
        Sphere insideDome = new Sphere(numberOrRadialAndPlanes, numberOrRadialAndPlanes, scaleAndRadius, true, false);
        Geometry insideDomeGeom = new Geometry("InsideDome", insideDome);
        
        outsideDomeGeom.setMaterial(material);
        insideDomeGeom.setMaterial(material);
        
        //Both node are attached to the same node that is in the transparent bucket
        Node scalingSignalNode = new Node();
        scalingSignalNode.attachChild(outsideDomeGeom);
        scalingSignalNode.attachChild(insideDomeGeom);
        scalingSignalNode.setQueueBucket(RenderQueue.Bucket.Transparent);
        
        return scalingSignalNode;
        
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
            
            ((Node) this.spatial).attachChild(spatialToAttach);
        }
        spatialToSendBuffer.clear();
    }

    

}
