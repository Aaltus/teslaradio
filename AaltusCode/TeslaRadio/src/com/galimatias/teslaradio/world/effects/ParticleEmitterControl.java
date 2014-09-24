/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;
import com.galimatias.teslaradio.world.observer.EmitterObservable;
import com.galimatias.teslaradio.world.observer.EmitterObserver;
import com.galimatias.teslaradio.world.observer.Observer;
import com.jme3.material.Material;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hugo
 */
public abstract class ParticleEmitterControl extends AbstractControl implements Observer, EmitterObservable {
    
    // speed of particle
    protected float speed;
    
    // material of particle to send
    protected Material material;
    
    // list of particle to 
    protected List<Spatial> spatialToSendBuffer;
    
    // register an observer for end of path notification
    protected ArrayList<EmitterObserver> observerList = new ArrayList();
    
    // initialise particles to be send and put them in FIFO
    public abstract void emitParticle(Spatial spatialToSend);
    
    // attach particle to the node and activate their control(they start moving)
    // this is done in controlUpdate to be synch with frames
    @Override
    protected void controlUpdate(float tpf) {
        for(Spatial spatialToAttach : spatialToSendBuffer)
        {
            spatialToAttach.getControl(SignalControl.class).setEnabled(true);
            ((Node) this.spatial).attachChild(spatialToAttach);
        }
        spatialToSendBuffer.clear();
        
        // update dynamic path
        this.pathUpdate();
    }
    
    // this function should do nothing if the path is not a dynamic one
    protected abstract void pathUpdate();
    
    
    // this method define a material to be set to each particle before emission
    // if not define the emitter wont edit the geom material aready in place.
    public void setDefaultMaterial(Material mat)
    {
        this.material = mat;
    }
    public void clearDefaultMaterial(Material mat)
    {
        this.material = null;
    }    
    
    @Override
    public void registerObserver(EmitterObserver observer) {
        this.observerList.add(observer);
    }

    @Override
    public void removeObserver(EmitterObserver observer) {
        this.observerList.remove(observer);
    }
    
    // observable method to notify whoever wants to know that a particle as ended his path
    @Override
    public void notifyObservers(Spatial spatial, String notifierId) {
        for(EmitterObserver observer : this.observerList)
        {
            observer.observerUpdate(spatial, notifierId);
        }        
    }
}