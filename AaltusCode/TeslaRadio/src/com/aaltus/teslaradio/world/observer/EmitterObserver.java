/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaltus.teslaradio.world.observer;

import com.jme3.scene.Spatial;

/**
 *
 * @author David
 */
public interface EmitterObserver {
    public void emitterObserverUpdate(Spatial spatial, String notifierId);
}
