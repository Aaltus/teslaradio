/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;
import com.galimatias.teslaradio.world.observer.Observable;
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
public abstract class ParticleEmitterControl extends AbstractControl implements Observer, Observable {
    
    // speed of particle
    protected float speed;
    
    // material of particle to send
    protected Material material;
    
    // list of particle to 
    protected List<Spatial> spatialToSendFifo;
    
    // register an observer for end of path notification
    protected ArrayList<Observer> observerList = new ArrayList();
    
    // initialise particles to be send and put them in FIFO
    public abstract void emitParticle(Spatial spatialToSend);
    
    // attach particle to the node and activate their control(they start moving)
    // this is done in controlUpdate to be synch with frames
    @Override
    protected void controlUpdate(float tpf) {
        for(Spatial spatialToAttach : spatialToSendFifo)
        {
            spatialToAttach.getControl(SignalControl.class).setEnabled(true);
            ((Node) this.spatial).attachChild(spatialToAttach);
        }
        spatialToSendFifo.clear();
        
        // update dynamic path
        this.pathUpdate();
    }
    
    // this function should do nothing if the path is not a dynamic one
    protected abstract void pathUpdate();
    
    @Override
    public void registerObserver(Observer observer) {
        this.observerList.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        this.observerList.remove(observer);
    }
    
    // observable method to notify whoever wants to know that a particle as ended his path
    @Override
    public void notifyObservers(Spatial spatial) {
        for(Observer observer : this.observerList)
        {
            observer.observerUpdate(spatial);
        }
    }
}