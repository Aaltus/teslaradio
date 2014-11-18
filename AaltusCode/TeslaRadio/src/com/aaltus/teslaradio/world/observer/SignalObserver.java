package com.aaltus.teslaradio.world.observer;

import com.jme3.scene.Geometry;

/**
 * Created by Greenwood0 on 2014-09-16.
 */
public interface SignalObserver {
    public void signalEndOfPath(Geometry caller, float magnitude);
}
