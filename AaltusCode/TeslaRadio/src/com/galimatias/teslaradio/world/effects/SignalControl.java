/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.galimatias.teslaradio.world.observer.Observable;
import com.galimatias.teslaradio.world.observer.Observer;
import com.jme3.cinematic.MotionPath;
import com.jme3.math.Spline;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Hugo
 */
public class SignalControl extends AbstractControl implements Observable{

    // moving data
    private MotionPath path;
    private Spline splinePath;
    private float distanceTraveled;
    private float speed;
    private Vector3f currentPosition;
    
    // for end of path notification
    private Observer observer;
    
    public SignalControl(MotionPath path, float speed){
        this.path = path;
        this.speed = speed;
        this.enabled = false;
        this.splinePath = path.getSpline();
        this.distanceTraveled = 0;
        this.currentPosition = new Vector3f();
    }

    SignalControl() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    protected void controlUpdate(float tpf) {

        // compute the new distance traveled in the wire
        distanceTraveled += tpf*speed;
        
        // check if it is the end of the path and notify
        if(distanceTraveled >= path.getLength())
        {
            this.notifyObservers(this.spatial);
            return;
        }
        
        // from distance, find new position of the signal
        Vector2f wayPointIndex = new Vector2f();
        wayPointIndex.set(path.getWayPointIndexForDistance(distanceTraveled));
        splinePath.interpolate(wayPointIndex.y, (int) (wayPointIndex.x), this.currentPosition);
        this.spatial.setLocalTranslation(currentPosition);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void registerObserver(Observer observer) {
        this.observer = observer;
    }

    @Override
    public void removeObserver(Observer observer) {
        this.observer = null;
    }

    // make sure that emitter is register to each Signal send
    @Override
    public void notifyObservers(Spatial spatial) {
        this.observer.observerUpdate(spatial);
    }
    
}
