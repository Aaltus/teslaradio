/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.galimatias.teslaradio.observer.Observable;
import com.galimatias.teslaradio.observer.Observer;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
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
public class SignalEmitter extends Node implements Observer, Observable
{
    
    private Vector<Vector3f> paths = new Vector<Vector3f>();
    private Geometry mainParticle;
    private Geometry secondaryParticle;
    private float particlesSpeed;
    private ColorRGBA baseColor;
    private SignalType signalType;
    private float capturePathLength = -1;
    private List<Observer> observers = new ArrayList<Observer>();
    
    
    public SignalEmitter(Vector<Vector3f> paths, Geometry mainParticle, Geometry secondaryParticle, float particlesSpeed, SignalType signalType) {
        this.paths = paths;
        this.mainParticle = mainParticle;
        this.secondaryParticle = secondaryParticle;
        this.particlesSpeed = particlesSpeed;
        this.baseColor = baseColor;
        this.signalType = signalType;
    }
    
    public SignalEmitter(Vector<Vector3f> paths, float capturePathLength, Geometry mainParticle, Geometry secondaryParticle, float particlesSpeed, SignalType signalType) {
        this.paths = paths;
        this.mainParticle = mainParticle;
        this.secondaryParticle = secondaryParticle;
        this.particlesSpeed = particlesSpeed;
        this.baseColor = baseColor;
        this.signalType = signalType;
        this.capturePathLength = capturePathLength;
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
        
        for (Vector3f path : paths) {
            
            Signal mySignal;
            int a = paths.indexOf(path);
            System.out.println(a);
            if (paths.indexOf(path)==0)
                mySignal = new Signal(mainParticle, path, particlesSpeed, capturePathLength);
            else {
                mySignal = new Signal(secondaryParticle, path, particlesSpeed);
            }
            
            this.attachChild(mySignal);
        }        
    }
    
    private void emitCurWireParticles(){
        
        Signal myCurvedSignal = new Signal(mainParticle, paths, particlesSpeed);
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
    
    public float getCapturePathLength()
    {
        return capturePathLength;
    }
    
    public void setCapturePathLength(float capturePathLength)
    {
        this.capturePathLength = capturePathLength;
    }

    public void observerUpdate() {
        emitParticles();
    }

    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (Observer observer: observers)
        {
            observer.observerUpdate();
        }
    }
}

