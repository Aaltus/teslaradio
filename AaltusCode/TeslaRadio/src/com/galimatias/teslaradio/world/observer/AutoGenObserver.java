/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.observer;

import com.jme3.scene.Spatial;

/**
 *
 * @author Jean-Christophe
 */
public interface AutoGenObserver {
    
    public void autoGenObserverUpdate(Spatial newCarrier, boolean isFm);
}
