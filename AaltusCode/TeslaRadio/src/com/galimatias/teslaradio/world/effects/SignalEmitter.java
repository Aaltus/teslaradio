/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.galimatias.teslaradio.world.Scenarios.ScenarioManager;
import com.galimatias.teslaradio.world.Scenarios.SoundCapture;
import com.galimatias.teslaradio.world.observer.SignalObserver;
import com.jme3.math.Quaternion;
import com.jme3.math.Spline;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;


/**
 *
 * @author David
 */
public class SignalEmitter extends Node
{
    
    private ArrayList<Vector3f> paths;
    private LinkedList<Node> waveNodeQ;
    private Geometry plainParticle;
    private Geometry translucentParticle;
    private float particlesSpeed;
    private SignalType signalType;
    private float capturePathLength = -1;
    private SignalObserver signalObserver = null;
    
    private ArrayList<Float> waveMagnitudes;
    private float wavePeriod;
    private float cumulatedPeriod = 0;
    private int waveIndex;
    private int waveAttachIndex;
    private boolean readyForEmission = false;
    private boolean areWavesEnabled = false;
    private Spline curveSpline;
    
    /*
     * TODO: The executor should be in the AppStateInit and
     * all controls should have a certain access to it.
     * Proof of concept here.
     */
    private ScheduledThreadPoolExecutor executor;
    private LinkedList<Future> futureListQ;

    /**
     * New constructor
     * @param observer
     */
    public SignalEmitter(SignalObserver observer) {
        this.paths = new ArrayList<>();
        this.futureListQ = new LinkedList<>();

        if (observer != null){
            this.signalObserver = observer;
        }
        this.waveMagnitudes = new ArrayList<>();
        this.executor = new ScheduledThreadPoolExecutor(4); //10 waves?
    }

    /**
     * This method generates the signals when they are ready to be sent and call the Signals update
     * @param tpf
     * @param cam
     */
    public void simpleUpdate(float tpf, Camera cam) {
        
        for (Spatial waveNode : (this.getChildren())) {
            for (Spatial signal : ((Node)waveNode).getChildren()) {
                ((Signal)signal).updatePosition(tpf, cam);
            }
        } 
       
        if (areWavesEnabled) {
             if(this.parent.getClass() == SoundCapture.class)
             {
                System.out.println("test");
             }
            if(readyForEmission && this.futureListQ.size() > 0){
                Future future = this.futureListQ.getFirst();
                if(future.isDone()){
                   try{
                       this.attachChild(((Spatial)future.get()));
                       this.futureListQ.remove(future);
                       readyForEmission = false;
                   }catch(InterruptedException | ExecutionException e)
                   {
                       //TOODO add handling
                   }
                }    
            }
                       
            cumulatedPeriod += tpf;
            readyForEmission = (cumulatedPeriod>wavePeriod && waveAttachIndex++<waveMagnitudes.size());// ? true:false;
            cumulatedPeriod %= wavePeriod;
            
          
            
        }
       
    }

    /**
     * Method that prepares the emitter to send a signal to the receiver handle.
     * @param receiverHandlePosition
     */
    public void prepareEmitParticles(Vector3f receiverHandlePosition)
    {
        // If the vector we recieve is empty, we stop right here.
        if (receiverHandlePosition == null || receiverHandlePosition == Vector3f.NAN
                || this.futureListQ.size() > 2){
            return;
        }

        // We're getting the vector between the emitter and receiver!
        Vector3f direction = receiverHandlePosition.subtract(this.getWorldTranslation());
        Quaternion worldInverseTranslation = this.getWorldRotation().inverse();
        direction = worldInverseTranslation.toRotationMatrix().mult(direction);
        // THIS IS REALLY IMPORTANT! DO NOT FORGET
        direction.divideLocal(ScenarioManager.WORLD_SCALE_DEFAULT);

        // Signal Trajectory call to create all the paths
        int totalNbDirections = 10;
        int nbXYDirections = 2;
        SignalTrajectories directionFactory = new SignalTrajectories(totalNbDirections, nbXYDirections);
        directionFactory.setTrajectories(direction, direction.length());
        this.paths = directionFactory.getTrajectories();

        // Enabling Wave emission! Shhhhrroroohhhhh!
        this.waveIndex = 0;
        this.waveAttachIndex = 0;
        this.areWavesEnabled = true;
        Future future = this.executor.submit(emitParticles);
        this.futureListQ.add(future);
       
   
            
    }


    /**
     * Method that prepares the emitter to send a signal to the receiver handle.
     * @param newSignal
     */
    public void prepareEmitParticles(Geometry newSignal, float magnitude)
    {
        if (newSignal == null || this.futureListQ.size() > 2){
            return;
        }

        this.plainParticle = newSignal;
        this.translucentParticle = null;

        //clearing old wave magnitudes, since we'll probably get other new magnitudes.
        this.waveMagnitudes.clear();
        this.waveMagnitudes.add(magnitude);
        this.waveIndex = 0;
        this.waveAttachIndex = 0;

        // We need to remove it from the other scenario before binding it to the new one
        newSignal.removeFromParent();

        // Enabling Wave emission! Shhhhrroroohhhhh!
        this.areWavesEnabled = true;
        Future future = this.executor.submit(emitParticles);
        this.futureListQ.add(future);
      
    }

    /**
     * Deprecated
     * @param magnitude
     */
    private Callable<Node> emitParticles =  new Callable<Node>(){
        private int index = 0;
        @Override
        public Node call() {
        
        Node waveNode = new Node();
        float magnitude = waveMagnitudes.get((waveIndex++)%waveMagnitudes.size());
        if(signalType == SignalType.Air)
        {
            emitAirParticles(waveNode, magnitude);
        }
        else if(signalType == SignalType.Wire)
        {
            emitCurWireParticles(waveNode, magnitude);
        }
        
        return waveNode;
      
    }
    };

    /**
     * This method is for when you know in advance what kind of signal geom you're going to send
     * It's Mainly for the SoundEmission Scenario.
     * The signal type is AIR
     * @param magnitudes
     * @param plainParticle
     * @param translucentParticle
     * @param period
     * @param particlesSpeed
     */
    public void setWaves(ArrayList<Float> magnitudes, Geometry plainParticle, Geometry translucentParticle, float period, float particlesSpeed){
        this.waveMagnitudes = magnitudes;
        this.wavePeriod = period;
        this.waveIndex = 0;
        this.areWavesEnabled = false;
        this.plainParticle = plainParticle;
        this.translucentParticle = translucentParticle;
        this.particlesSpeed = particlesSpeed;
        this.signalType = SignalType.Air;
    }

    /**
     * This method is when you don't know the geoms you'll emit, but you know the mesh path it must take
     * The signal type is WIRE
     * @param meshToFollow
     * @param particlesSpeed
     */
    public void setWaves(Mesh meshToFollow, float period, float particlesSpeed){
        this.waveIndex = 0;
        this.areWavesEnabled = false;
        this.wavePeriod = period;
        this.particlesSpeed = particlesSpeed;
        this.signalType = SignalType.Wire;

        SignalTrajectories directionFactory = new SignalTrajectories();
        this.curveSpline = directionFactory.getCurvedPath(meshToFollow);
    }
    
    private void emitAirParticles(Node waveNode, float magnitude)
    {        
        for (Vector3f path : paths) {
            
            Signal mySignal;
            int a = paths.indexOf(path);
            if (paths.indexOf(path)==0)
                mySignal = new Signal(plainParticle, path, particlesSpeed, magnitude, true, signalObserver);
            else {
                mySignal = new Signal(translucentParticle, path, particlesSpeed, magnitude, false, signalObserver);
            }
            
            waveNode.attachChild(mySignal);
        } 
        //this.attachChild(waveNode);
    }

    private void emitCurWireParticles(Node waveNode, float magnitude){
        
        Signal myCurvedSignal = new Signal(plainParticle, curveSpline, particlesSpeed, magnitude, signalObserver);
        waveNode.attachChild(myCurvedSignal);
        //this.attachChild(waveNode);
    }
}

