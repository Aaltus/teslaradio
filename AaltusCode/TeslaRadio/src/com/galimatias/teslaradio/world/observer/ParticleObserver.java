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
public interface ParticleObserver {
	public void onParticleEndOfLife(Spatial spatial);
        public void onParticleReachingReceiver(Spatial spatial);
}
