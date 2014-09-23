/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;
import com.galimatias.teslaradio.world.observer.Observable;
import com.galimatias.teslaradio.world.observer.Observer;
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
    
    // 
    protected List<Spatial> spatialToSendFifo;
    
    // register an observer for end of path notification
    protected ArrayList<Observer> observerList = new ArrayList();
    
    // initialise particles to be send and put them in FIFO
    public abstract void prepareEmission(Spatial spatialToSend);
    
    // attach particle to the node and activate their control(they start moving)
    // this should be call by simpleUpdate
    @Override
    protected void controlUpdate(float tpf) {
        emitParticle();
        
    }
    public void emitParticle()
    {
        for(Spatial spatialToAttach : spatialToSendFifo)
        {
            spatialToAttach.getControl(SignalControl.class).setEnabled(true);
            ((Node) this.spatial).attachChild(spatialToAttach);
        }
        spatialToSendFifo.clear();
    }
    
    public abstract void updatePath();
    
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