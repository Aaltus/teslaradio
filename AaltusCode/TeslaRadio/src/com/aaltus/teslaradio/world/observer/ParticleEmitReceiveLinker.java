package com.aaltus.teslaradio.world.observer;

import com.aaltus.teslaradio.world.Scenarios.Scenario;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

/**
 * Created by Greenwood0 on 2014-09-16.
 */
public interface ParticleEmitReceiveLinker {
    public Vector3f GetEmitterDestinationPaths(Scenario caller);

    public void sendSignalToNextScenario(Scenario caller, Geometry newSignal, float magnitude);
}
