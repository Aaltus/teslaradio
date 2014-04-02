/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.jme3.asset.AssetManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author David
 */
public class SignalEmitter extends Node{

    private Vector<Vector3f> paths = new Vector<Vector3f>();
    private Geometry particle;
    private float particlesSpeed;


    public SignalEmitter(Vector<Vector3f> paths, Geometry particle, float particlesSpeed) {
        this.paths = paths;
        this.particle = particle;
        this.particlesSpeed = particlesSpeed;

    }

    public void simpleUpdate(float tpf) {

        Signal liveSignal;

        for (Spatial signal : (this.getChildren())) {
            liveSignal = (Signal)signal;
            liveSignal.updatePosition(tpf);
        }
    }

    public void emitParticles() {
        for (Vector3f path : paths) {
            Signal mySignal = new Signal(particle, path, particlesSpeed);
            this.attachChild(mySignal);
        }
    }

}
