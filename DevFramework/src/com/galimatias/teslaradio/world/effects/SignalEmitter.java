/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.Vector;

/**
 *
 * @author David
 */
public class SignalEmitter extends Node{
    
    private Vector<Vector3f> paths = new Vector<Vector3f>();
    private Geometry particle;
    private float particlesSpeed;
    private ColorRGBA baseColor;
    private SignalType signalType;
    
    
    public SignalEmitter(Vector<Vector3f> paths, Geometry particle, float particlesSpeed, ColorRGBA baseColor, SignalType signalType) {
        this.paths = paths;
        this.particle = particle;
        this.particlesSpeed = particlesSpeed;
        this.baseColor = baseColor;
        this.signalType = signalType;
    }
    
    public void simpleUpdate(float tpf) {
        
        Signal liveSignal;
        
        for (Spatial signal : (this.getChildren())) {
            liveSignal = (Signal)signal;
            liveSignal.updatePosition(tpf);
        } 
    }

    public void emitParticles() {
        
        if(signalType == SignalType.Air)
        {
            emitAirParticles();
        }
        else if(signalType == SignalType.Wire)
        {
            emitCurWireParticles();
        }

    }
    
    private void emitAirParticles()
    {
        boolean setTransparency=false;
        
        Geometry translucentParticle = particle.clone();
        
        
        ColorRGBA translucentColor = new ColorRGBA();
        translucentColor.r=baseColor.r;
        translucentColor.g=baseColor.g;
        translucentColor.b=baseColor.b;
        translucentColor.a=0.15f;
        
        translucentParticle.getMaterial().setColor("Color", translucentColor);
        
        baseColor.a=1.0f;
        
        for (Vector3f path : paths) {
            
            Signal mySignal;
            int a = paths.indexOf(path);
            System.out.println(a);
            if (paths.indexOf(path)==0)
                mySignal = new Signal(particle, path, particlesSpeed, baseColor);
            else {
                mySignal = new Signal(translucentParticle, path, particlesSpeed, translucentColor);
            }
            
            this.attachChild(mySignal);
        }        
    }
    
    private void emitCurWireParticles(){
        
        Signal myCurvedSignal = new Signal(particle, paths, particlesSpeed);
        this.attachChild(myCurvedSignal);
    }
    
    public SignalType getSignalType()
    {
        return signalType;
    }
    
    public void setSignalType(SignalType signalType)
    {
        this.signalType = signalType;
    }
}

