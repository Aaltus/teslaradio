/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import android.util.Log;
import com.galimatias.teslaradio.world.Scenarios.ScenarioManager;
import com.galimatias.teslaradio.world.observer.Observable;
import com.galimatias.teslaradio.world.observer.Observer;
import com.galimatias.teslaradio.world.observer.SignalObserver;
import com.jme3.math.Quaternion;
import com.jme3.math.Spline;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.utils.AppLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author David
 */
public class SignalEmitter extends Node implements SignalObserver, Observable
{
    
    private Vector<Vector3f> paths = new Vector<Vector3f>();
    private Geometry plainParticle;
    private Geometry translucentParticle;
    private float particlesSpeed;
    private SignalType signalType;
    private float capturePathLength = -1;
    private List<Observer> observers = new ArrayList<Observer>();
    
    private ArrayList<Float> waveMagnitudes;
    private float wavePeriod;
    private float cumulatedPeriod = 0;
    private int waveIndex;
    private boolean readyForEmission = false;
    private boolean areWavesEnabled = false;
    
	private Spline curveSpline;


    /**
     * Deprecated
     * @param paths
     * @param mainParticle
     * @param secondaryParticle
     * @param particlesSpeed
     * @param signalType
     */
    @Deprecated
    public SignalEmitter(Vector<Vector3f> paths, Geometry mainParticle, Geometry secondaryParticle, float particlesSpeed, SignalType signalType) {
        this.paths = paths;
        this.plainParticle = mainParticle;
        //this.secondaryParticle = secondaryParticle;
        this.particlesSpeed = particlesSpeed;
        this.signalType = signalType;
    }

    /**
     * Deprecated
     * @param paths
     * @param mainParticle
     * @param secondaryParticle
     * @param particlesSpeed
     * @param signalType
     */
    @Deprecated
    public SignalEmitter(Vector<Vector3f> paths, float capturePathLength, Geometry mainParticle, Geometry secondaryParticle, float particlesSpeed, SignalType signalType) {
        this.paths = paths;
        this.plainParticle = mainParticle;
        //this.secondaryParticle = secondaryParticle;
        this.particlesSpeed = particlesSpeed;
        this.signalType = signalType;
        this.capturePathLength = capturePathLength;
    }

    /**
     * Deprecated
     * @param paths
     * @param mainParticle
     * @param secondaryParticle
     * @param particlesSpeed
     * @param signalType
     */
    @Deprecated
    public SignalEmitter(Spline paths, Geometry mainParticle, Geometry secondaryParticle, float particlesSpeed, SignalType signalType) {
        this.curveSpline = paths;
        this.plainParticle = mainParticle;
        //this.secondaryParticle = secondaryParticle;
        this.particlesSpeed = particlesSpeed;
        this.signalType = signalType;
    }


    /**
     * New constructor
     * @param observer
     */
    public SignalEmitter(Observer observer) {
        this.registerObserver(observer);
    }

    /**
     * This method generates the signals when they are ready to be sent and call the Signals update
     * @param tpf
     * @param cam
     */
    public void simpleUpdate(float tpf, Camera cam) {
        
        Signal liveSignal;
        
        for (Spatial waveNode : (this.getChildren())) {
            for (Spatial signal : ((Node)waveNode).getChildren()) {
                liveSignal = (Signal)signal;
                liveSignal.updatePosition(tpf, cam);
            }
        } 
        
        if (areWavesEnabled) {
            if (readyForEmission) {
                float magnitude = waveMagnitudes.get((waveIndex++)%waveMagnitudes.size());
                emitParticles( magnitude );
                readyForEmission=false;
            }
                
            cumulatedPeriod += tpf;
            readyForEmission = (cumulatedPeriod>wavePeriod && waveIndex<waveMagnitudes.size());// ? true:false;
            cumulatedPeriod %= wavePeriod;
            
        }
    }

    /**
     * TODO: Implement this new method
     * @param receiverHandlePosition
     */
    public void prepareEmitParticles(Vector3f receiverHandlePosition)
    {
        // If the vector we recieve is empty, we stop right here.
        if (receiverHandlePosition == null || receiverHandlePosition == Vector3f.NAN){
            return;
        }

        Vector3f direction = receiverHandlePosition.subtract(this.getWorldTranslation());
        Quaternion worldInverseTranslation = this.getWorldRotation().inverse();

        direction = worldInverseTranslation.toRotationMatrix().mult(direction);

        direction = direction.divide(ScenarioManager.ANDROID_SCALE_DEFAULT);

        this.capturePathLength = direction.length();

        int totalNbDirections = 10;
        int nbXYDirections = 2;


        Log.d("Aaltus", Float.toString(direction.length()));
        AppLogger.getInstance().d("Aaltus", "Lenght of vector is: " + Float.toString(direction.length()));
        SignalTrajectories directionFactory = new SignalTrajectories(totalNbDirections, nbXYDirections);
        directionFactory.setTrajectories(direction, direction.length());

        this.paths = directionFactory.getTrajectories();

        // Enabling Wave emission! Shhhhrroroohhhhh!
        this.waveIndex = 0;
        this.areWavesEnabled = true;
    }

    /**
     * Deprecated
     * @param magnitude
     */
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

    }

    @Deprecated
    public void setWaves(ArrayList<Float> magnitudes, float period) {
        this.waveMagnitudes = magnitudes;
        this.wavePeriod = period;
        this.waveIndex = 0;
        this.areWavesEnabled = false;
    }

    public void setWaves(ArrayList<Float> magnitudes, Geometry plainParticle, Geometry translucentParticle, float period, float particlesSpeed, SignalType signalType){
        this.waveMagnitudes = magnitudes;
        this.wavePeriod = period;
        this.waveIndex = 0;
        this.areWavesEnabled = false;
        this.plainParticle = plainParticle;
        this.translucentParticle = translucentParticle;
        this.particlesSpeed = particlesSpeed;
        this.signalType = signalType;
    }
    
    private void emitAirParticles(Node waveNode, float magnitude)
    {        
        for (Vector3f path : paths) {
            
            Signal mySignal;
            int a = paths.indexOf(path);
            if (paths.indexOf(path)==0)
                mySignal = new Signal(plainParticle, path, particlesSpeed, magnitude, capturePathLength, this);
            else {
                mySignal = new Signal(translucentParticle, path, particlesSpeed, magnitude, this);
            }
            
            waveNode.attachChild(mySignal);
            this.attachChild(waveNode);
        }        
    }


    private void emitCurWireParticles(Node waveNode, float magnitude){
        
        Signal myCurvedSignal = new Signal(plainParticle, curveSpline, particlesSpeed, magnitude, this);
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

//    public void signalEndOfPath() {
//        emitParticles(1.0f); //TODO: PASS THE CORRECT MAGNITUDE
//    }

    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Object caller) {
        for (Observer observer: observers)
        {

        }
    }

//    @Override
//    public void signalEndOfPath() {
//
//    }

    @Override
    public void signalEndOfPath(Signal caller) {
        Log.d("HEHE", "HAAAAAAAA!!");
    }
}

