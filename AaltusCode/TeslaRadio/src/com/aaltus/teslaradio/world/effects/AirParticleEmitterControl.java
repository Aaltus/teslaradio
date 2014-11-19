/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaltus.teslaradio.world.effects;

import com.ar4android.vuforiaJME.AppGetter;
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
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Sphere;
import com.utils.AppLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hugo
 */
public class AirParticleEmitterControl extends ParticleEmitterControl{
    private final static String TAG = AirParticleEmitterControl.class.getSimpleName();
    private final static int MAX_WAVE_NB = 3;

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
    
     //The future that is used to check the execution status:
    Future future = null;
 
    private float threadScale;
    private Spatial threadSpatialToSend;
    private Material threadMaterialClone;

    private List<List<Spatial>> waveParticlesList = new ArrayList<List<Spatial>>();
    
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
        
        // delete particle wave in the waveList when they reach the end of their path
        if(this.waveParticlesList.size() > 0 && this.waveParticlesList.get(0).contains(toBeDeletedSpatial)){
            this.waveParticlesList.remove(0);
        }
        
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
        emitParticle(spatialToSend, this.radius* spatial.getLocalScale().x);
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
        

        //start thread if no thread already running (else: do nothing)
        if(future == null){
            // save scale and particle for thread
            this.threadScale = scale;
            this.threadSpatialToSend = spatialToSend;
            this.threadMaterialClone = materialClone; 
            
            future = AppGetter.getThreadExecutor().submit(createMultiPathParticles);

            if(this.waveParticlesList.size() >= MAX_WAVE_NB){
                for( Spatial particle : this.waveParticlesList.get(0)){
                    Node dummy = particle.getParent();
                    dummy.detachChild(particle);
                }
                this.waveParticlesList.remove(0);
            }
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

        material.getAdditionalRenderState().setDepthWrite(false);
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
        
        // send particles when thread finish creating them
        if(future != null)
        {
            if(future.isDone()){                
                try {
                    List<Spatial> multiPathSpatial = ((List<Spatial>) future.get());
                    List<Spatial> currentParticleList = new ArrayList<Spatial>();
                    for(Spatial spatialToAttach : multiPathSpatial)
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
                        currentParticleList.add(spatialToAttach);
                    }
                    this.waveParticlesList.add(currentParticleList);
                }
                catch (InterruptedException ex)
                {
                    AppLogger.getInstance().e(TAG, ex.getMessage());
                }
                catch (ExecutionException ex)
                {
                    AppLogger.getInstance().e(TAG, ex.getMessage());
                }

                future = null;
            }
        }

        
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

    
    private Callable<List<Spatial>> createMultiPathParticles = new Callable<List<Spatial>>() {

        @Override
        public List<Spatial> call() throws Exception {
            
            List<Spatial> multiPathParticlesList = new ArrayList(); 
            // Generate little flying particles
            // TODO: put all this in thread !!!
            int nbDirection = 6;
            Quaternion rotQuat2 = new Quaternion();
            rotQuat2.fromAngleAxis(3.1416f/nbDirection, Vector3f.UNIT_Z);
            Vector3f path_vector_flat = new Vector3f(threadScale,0,0);
            for(int axe_flat = 0; axe_flat < (nbDirection/2); axe_flat++)
            {        
                Quaternion rotQuat = new Quaternion();
                rotQuat.fromAngleAxis(6.2832f/(nbDirection-axe_flat), Vector3f.UNIT_Y);

                Vector3f path_vector = path_vector_flat.clone();
                for(int axe_a = 0; axe_a < nbDirection-axe_flat; axe_a++)
                {
                   Spatial spatial_clone = threadSpatialToSend.clone();
                   MotionPath path = new MotionPath();
                   path.addWayPoint(Vector3f.ZERO);
                   path.addWayPoint(path_vector);
                   path_vector = rotQuat.mult(path_vector);
                   SignalControl sigControl2 = new SignalControl(path,speed,cam,0);
                   sigControl2.registerObserver(spatial.getControl(ParticleEmitterControl.class));
                   spatial_clone.addControl(sigControl2);
                   //spatial_clone.addControl(new LookAtCameraControl(Camera));
                   multiPathParticlesList.add(spatial_clone);
                }

                path_vector_flat = rotQuat2.mult(path_vector_flat);
            }            

            return multiPathParticlesList;
        }
          
    };
}
