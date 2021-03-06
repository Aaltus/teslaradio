/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaltus.teslaradio.world.effects;

import com.ar4android.vuforiaJME.AppGetter;
import com.aaltus.teslaradio.world.observer.ParticleObservable;
import com.aaltus.teslaradio.world.observer.ParticleObserver;
import com.jme3.cinematic.MotionPath;
import com.jme3.math.Spline;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Hugo
 */
public class SignalControl extends AbstractControl implements ParticleObservable{

    // moving data
    private MotionPath path;
    private Spline splinePath;
    private float distanceTraveled;
    private float speed;
    private Vector3f currentPosition;
    private Camera cam;
    private float endScaleRatio;
    private float startScaleRatio;
    
    private Spatial refNodeDetachAttach;
    
    // for end of path notification
    private ParticleObserver observer;
    
    public SignalControl(MotionPath path, float speed){
        this(path, speed, null);
    }
    
    public SignalControl(MotionPath path, float speed, Camera cam){
        this(path, speed, cam, -1, null);
    }
    
    public SignalControl(MotionPath path, float speed, Camera cam, Spatial refNodeDetachAttach){
        this(path, speed, cam, -1,refNodeDetachAttach);
    }
      
    public SignalControl(MotionPath path, float speed, Camera cam, float endScaleRatio){
       this(path,speed,cam,endScaleRatio,null);
    }
    
    public SignalControl(MotionPath path, float speed, Camera cam, float endScaleRatio, Spatial refNodeDetachAttach){
        this.path = path;
        this.speed = speed;
        this.enabled = false;
        this.splinePath = path.getSpline();
        this.distanceTraveled = 0;
        this.currentPosition = new Vector3f();
        this.cam = cam;
        this.endScaleRatio = endScaleRatio;
        this.refNodeDetachAttach = refNodeDetachAttach;
    }
    
    @Override
    protected void controlUpdate(float tpf) {

        // compute the new distance traveled in the wire
        distanceTraveled += tpf*speed;
        
        // check if it is the end of the path and notify
        if(distanceTraveled >= path.getLength())
        {
            this.observer.onParticleEndOfLife(this.spatial);
            return;
        }
        
        // from distance, find new position of the signal
        Vector2f wayPointIndex = new Vector2f();
        wayPointIndex.set(path.getWayPointIndexForDistance(distanceTraveled));
        splinePath.interpolate(wayPointIndex.y, (int) (wayPointIndex.x), this.currentPosition);
        this.spatial.setLocalTranslation(currentPosition);
        this.spatial.setLocalScale(this.startScaleRatio -((this.distanceTraveled/path.getLength())*(this.startScaleRatio-this.endScaleRatio)));

        //I have commented the look at because we don't have 2d particle now and ti seems that it is not good performance wise.
        //if (cam != null) {
        //    this.spatial.lookAt(cam.getLocation(), cam.getUp());
        //}
        
        // verify if the refNodeDetachAttach is still in focus or not
        if(refNodeDetachAttach != null)
        {
            // if the emitter node is detach then detach particles on dynamic path
            if(!AppGetter.hasRootNodeAsAncestor(refNodeDetachAttach))
            {
                this.spatial.removeFromParent();
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void registerObserver(ParticleObserver observer) {
        this.observer = observer;
    }

    @Override
    public void removeObserver(ParticleObserver observer) {
        this.observer = null;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial); //To change body of generated methods, choose Tools | Templates.
        if(spatial != null) {
            this.startScaleRatio = spatial.getLocalScale().x;
            if(this.endScaleRatio == -1){
                this.endScaleRatio = this.startScaleRatio;
            }
        }
    }
    
}
