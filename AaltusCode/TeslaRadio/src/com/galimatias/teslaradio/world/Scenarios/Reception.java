/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.ParticleEmitterControl;
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
    
    
    // Signals emitters 
    private Node inputAntenneRx = new Node();
    
    
    
    
    
    public Reception(com.jme3.renderer.Camera Camera, Spatial destinationHandle) {
        super(Camera, destinationHandle);

        this.cam = Camera;
        this.destinationHandle = destinationHandle;

        loadUnmovableObjects();
        loadMovableObjects();
    }

    @Override
    protected void loadUnmovableObjects() {
        scene = (Node) assetManager.loadModel("Models/Amplification/Antenne_Rx.j3o");
        scene.setName("Reception");
        this.attachChild(scene);
        
        //implement touchable
     /*   
        //scene rotation
        scene.setLocalTranslation(new Vector3f(0.5f, 0.0f, 1.7f));
        Quaternion rot = new Quaternion();
        rot.fromAngleAxis(-pi / 2, Vector3f.UNIT_Y);
        scene.setLocalRotation(rot);
        
        */
        // Get the handles of the emitters
        inputAntenneRx = scene.getChild("Handle.In");
   /*
        
          // Get the different paths
        Node wireInAmpli_node = (Node) scene.getChild("Path.In.Amp.Object");
        inputAmpPath = (Geometry) wireInAmpli_node.getChild("Path.In.Amp.Nurbs2");
        Node wireOutAmpli_node = (Node) scene.getChild("Path.Out.Amp.Object");
        outputAmpPath = (Geometry) wireOutAmpli_node.getChild("Path.Out.Amp.Nurbs");
     
        initParticlesEmitter(inputWireAmpli, pathInputAmpli, inputAmpPath, cam);
        initParticlesEmitter(outputWireAmpli, pathOutputAmpli, outputAmpPath, null);
     
        // Set names for the emitters  // VOir si utile dans ce module
        inputWireAmpli.setName("InputWireAmpli");
        outputWireAmpli.setName("OutputWireAmpli");
      
        outputWireAmpli.getControl(ParticleEmitterControl.class).registerObserver(this);
        inputWireAmpli.getControl(ParticleEmitterControl.class).registerObserver(outputWireAmpli.getControl(ParticleEmitterControl.class));
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean simpleUpdate(float tpf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void signalEndOfPath(Geometry caller, float magnitude) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void emitterObserverUpdate(Spatial spatial, String notifierId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
