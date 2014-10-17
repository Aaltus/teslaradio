/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;
import com.ar4android.vuforiaJME.AppGetter;
import com.galimatias.teslaradio.world.observer.EmitterObservable;
import com.galimatias.teslaradio.world.observer.EmitterObserver;
import com.galimatias.teslaradio.world.observer.ParticleObserver;
import com.jme3.material.Material;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hugo
 */
public abstract class ParticleEmitterControl extends AbstractControl implements ParticleObserver, EmitterObservable, EmitterObserver {
    
    // speed of particle
    protected float speed;
    
    // material of particle to send
    protected Material material;
    
    // list of particle to 
    protected List<Spatial> spatialToSendBuffer;
    
    // register an observer for end of path notification
    protected ArrayList<EmitterObserver> observerList = new ArrayList();
    
    // The foreground camera of the scenarios
    protected Camera cam;
    
    // initialise particles to be send and put them in FIFO
    public abstract void emitParticle(Spatial spatialToSend);
    
    // attach particle to the node and activate their control(they start moving)
    // this is done in controlUpdate to be synch with frames
    @Override
    protected abstract void controlUpdate(float tpf);
       
    // this method define a material to be set to each particle before emission
    // if not define the emitter wont edit the geom material already in place.
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
            observer.emitterObserverUpdate(spatial, notifierId);
        }        
    }
    
    // trigger an emit particle if the emitter observe another emitter
    @Override
    public void emitterObserverUpdate(Spatial spatial, String notifierId)
    {
        this.emitParticle(spatial);
    }

}