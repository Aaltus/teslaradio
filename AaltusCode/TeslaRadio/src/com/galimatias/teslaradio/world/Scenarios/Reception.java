/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.ParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.StaticWireParticleEmitterControl;
import com.galimatias.teslaradio.world.observer.EmitterObserver;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Barliber
 */
public class Reception extends Scenario implements EmitterObserver  {
    
    private Camera cam;
    private Spatial destinationHandle;
    
     // this is PIIIIIII! (kick persian)
    private final float pi = (float) Math.PI;
    
    // Signals emitters 
    private Node inputAntenneRx = new Node();
    
    // Handles for the emitter positions
    private Spatial pathAntenneRx;
    
    
    // Paths
    private Geometry antenneRxPath;
    
    
    public Reception(com.jme3.renderer.Camera Camera, Spatial destinationHandle) {
        super(Camera, destinationHandle);

        this.cam = Camera;
        this.destinationHandle = destinationHandle;

        loadUnmovableObjects();
      //  loadMovableObjects();
    }

    @Override
    protected void loadUnmovableObjects() {
        scene = (Node) assetManager.loadModel("Models/Reception/Antenne_Rx.j3o");
        scene.setName("Reception");
        this.attachChild(scene);
        
        //implement touchable
    
        
        
        //scene rotation
        Quaternion rot = new Quaternion();
        rot.fromAngleAxis(-pi, Vector3f.UNIT_Y);
        scene.setLocalRotation(rot);
        
        pathAntenneRx = scene.getChild("Antenna.Handle.In");
        
         // Get the different paths
        Node wireAntenneRx_node = (Node) scene.getChild("Path.Sortie.001");
        antenneRxPath = (Geometry) wireAntenneRx_node.getChild("NurbsPath.005");
        
       
        initParticlesEmitter(inputAntenneRx, pathAntenneRx, antenneRxPath, null);
        
        // Set names for the emitters  // VOir si utile dans ce module
        inputAntenneRx.setName("InputAntenneRx");
        
        
        inputAntenneRx.getControl(ParticleEmitterControl.class).registerObserver(this);
        
        
        // Get the handles of the emitters
    }

    @Override
    protected void loadMovableObjects() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void restartScenario() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onScenarioTouch(String name, TouchEvent touchEvent, float v) {
      
    }

    @Override
    public boolean simpleUpdate(float tpf) {
        return false;
    }

    @Override
    public void setGlobalSpeed(float speed) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onAudioEvent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Spatial getInputHandle() {
        return inputAntenneRx;
    }

    @Override
    public void signalEndOfPath(Geometry caller, float magnitude) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void emitterObserverUpdate(Spatial spatial, String notifierId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
     private void initParticlesEmitter(Node signalEmitter, Spatial handle, Geometry path, Camera cam) {
        scene.attachChild(signalEmitter);
        System.out.println(signalEmitter);
        signalEmitter.setLocalTranslation(handle.getLocalTranslation()); // TO DO: utiliser le object handle blender pour position
        signalEmitter.addControl(new StaticWireParticleEmitterControl(path.getMesh(), 3.5f, cam));
        signalEmitter.getControl(ParticleEmitterControl.class).setEnabled(true); 
    }
}
