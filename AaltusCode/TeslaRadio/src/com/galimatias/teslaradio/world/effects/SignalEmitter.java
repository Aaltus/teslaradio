/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.galimatias.teslaradio.world.observer.EmitterObserver;
import com.galimatias.teslaradio.world.observer.Observable;
import com.galimatias.teslaradio.world.observer.Observer;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.LinkedList;

/**
 *
 * @author David
 */
public class SignalEmitter extends Node implements EmitterObserver, Observable
{
    
    private Vector<Vector3f> paths = new Vector<Vector3f>();
    private Geometry mainParticle;
    private Geometry secondaryParticle;
    private float particlesSpeed;
    private SignalType signalType;
    private float capturePathLength = -1;
    private List<EmitterObserver> observers = new ArrayList<EmitterObserver>();
    
    private ArrayList<Float> waveMagnitudes;
    private float wavePeriod;
    private float cumulatedPeriod = 0;
    private int waveIndex;
    private boolean readyForEmission = false;
    private boolean areWavesEnabled = false;
    
    public SignalEmitter(Vector<Vector3f> paths, Geometry mainParticle, Geometry secondaryParticle, float particlesSpeed, SignalType signalType) {
        this.paths = paths;
        this.mainParticle = mainParticle;
        this.secondaryParticle = secondaryParticle;
        this.particlesSpeed = particlesSpeed;
        this.signalType = signalType;
    }
    
    public SignalEmitter(Vector<Vector3f> paths, float capturePathLength, Geometry mainParticle, Geometry secondaryParticle, float particlesSpeed, SignalType signalType) {
        this.paths = paths;
        this.mainParticle = mainParticle;
        this.secondaryParticle = secondaryParticle;
        this.particlesSpeed = particlesSpeed;
        this.signalType = signalType;
        this.capturePathLength = capturePathLength;
    }    
    
    public void simpleUpdate(float tpf) {    
        
        Signal liveSignal;
        
        for (Spatial waveNode : (this.getChildren())) {
            for (Spatial signal : ((Node)waveNode).getChildren()) {
                liveSignal = (Signal)signal;
                liveSignal.updatePosition(tpf);
            }
        } 
        
        if (areWavesEnabled) {
            if (readyForEmission) {
                float magnitude = waveMagnitudes.get((waveIndex++)%waveMagnitudes.size());
                System.out.println(magnitude);
                emitParticles( magnitude );
                readyForEmission=false;
            }
                
            cumulatedPeriod += tpf;
            readyForEmission = (cumulatedPeriod>wavePeriod && waveIndex<waveMagnitudes.size()) ? true:false;
            cumulatedPeriod %= wavePeriod;
            
        }
    }

    public void emitParticles(float magnitude) {
        
        Node waveNode = new Node();
        
        if(signalType == SignalType.Air)
        {
            emitAirParticles(waveNode, magnitude);
        }
        else if(signalType == SignalType.Wire)
        {
            emitCurWireParticles(waveNode, magnitude);
        }

    }
    
    public void emitWaves() {
    
        this.waveIndex = 0;
        this.areWavesEnabled = true;
        this.simpleUpdate(0f);

    }
    
    public void setWaves(ArrayList<Float> magnitudes, float period) {
        this.waveMagnitudes = magnitudes;
        this.wavePeriod = period;
        this.waveIndex = 0;
        this.areWavesEnabled = false;
    }
    
    private void emitAirParticles(Node waveNode, float magnitude)
    {
        
        System.out.println("Wave magnitude is: "+magnitude);
        
        for (Vector3f path : paths) {
            
            Signal mySignal;
            int a = paths.indexOf(path);
            if (paths.indexOf(path)==0)
                mySignal = new Signal(mainParticle, path, particlesSpeed, magnitude, capturePathLength);
            else {
                mySignal = new Signal(secondaryParticle, path, particlesSpeed, magnitude);
            }       
            
            waveNode.attachChild(mySignal);
            this.attachChild(waveNode);
        }        
    }
    
    private void emitCurWireParticles(Node waveNode, float magnitude){
        
        Signal myCurvedSignal = new Signal(mainParticle, paths, particlesSpeed, magnitude);
        waveNode.attachChild(myCurvedSignal);
        this.attachChild(waveNode);
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
        emitParticles(1.0f); //TODO: PASS THE CORRECT MAGNITUDE
    }

    public void registerObserver(EmitterObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Object caller) {
        for (EmitterObserver observer: observers)
        {
            if (caller.getClass()==Signal.class)
                observer.observerUpdate(((Signal)caller).getLocalScale().x);
        }
    }

    public void observerUpdate(float magnitude) {
        emitParticles(magnitude); //TODO: PASS THE CORRECT MAGNITUDE
    }

    public void registerObserver(Observer observer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

