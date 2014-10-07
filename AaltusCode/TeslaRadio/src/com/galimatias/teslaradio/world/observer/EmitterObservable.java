/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.observer;

import com.jme3.scene.Spatial;

/**
 *
 * @author Hugo
 */
public interface EmitterObservable {
    public void registerObserver(EmitterObserver observer);
    public void removeObserver(EmitterObserver observer);
    public void notifyObservers(Spatial spatial, String notifierId);
}
