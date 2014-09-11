package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.TextBox;
import com.jme3.asset.AssetManager;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * Created by Batcave on 2014-09-09.
 */
public class Modulation extends Scenario {

    private final static String TAG = "Modulation";
    
    // Enum to hold the different possible modulation frequencies
    public enum modulationFrequencies {
        FM1061, FM977, FM952,
        AM697, AM498, AM707
    }
    
    private modulationFrequencies frequency;
    
    // Values displayed on the digital screen of the PCB 3D object
    private final String sFM1061 = "106.1 FM";
    private final String sFM977 = "97.7 FM";
    private final String sFM952 = "95.2 FM";
    private final String sAM697 = "697 AM";
    private final String sAM498 = "498 AM";
    private final String sAM707 = "707 AM";
    
    // 3D objects of the scene
    private Spatial pcb;
    private Spatial button;
    
    // TextBox of the scene
    private TextBox titleTextBox;
    private TextBox digitalDisplay;
    
    // Default text to be seen when scenario starts
    private String titleText = "La Modulation";
    private float titleTextSize = 0.5f;
    private ColorRGBA defaultTextColor = new ColorRGBA(1f, 0f, 1f, 1f);
    
    //CHANGE THIS VALUE CHANGE THE PARTICULE BEHAVIOUR 
    //Setting the direction norms and the speed displacement to the trajectories
    private float VecDirectionNorms = 80f;
    private float SoundParticles_Speed = 50f;
    
    public Modulation(AssetManager assetManager, com.jme3.renderer.Camera Camera /*, ScenarioObserver observer*/) {

        super(assetManager, Camera);

        loadUnmovableObjects();
        loadMovableObjects();
    }
    
    public Modulation(AssetManager assetManager) {
        this(assetManager,null /*, null */);
    }

    @Override
    protected void loadUnmovableObjects() {

        scene = (Node) assetManager.loadModel("Models/SoundCapture/micro.j3o");
        scene.setName("SoundCapture");
        this.attachChild(scene);
        scene.scale(10.0f,10.0f,10.0f);
    }

    @Override
    public void loadMovableObjects() {


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

    }
    
    private void initTunerButton() {
        
    }
    
    private void turnTunerButton(float angle, Boolean AM) {
        
        //TODO add the verification of the angle
        if (angle >= 2*Math.PI/3 && AM) {
            changeModulation(frequency.AM498);
        }
    }
    
    private void switchModulationAM_FM(Boolean AM) {
        
        if (AM)
        {
            
        }
        else // Not AM, so FM modulation...
        {
            
        }
        
    }
    
    private void changeModulation(modulationFrequencies frequency) {
        
        switch(frequency)
        {
            case FM1061:
                digitalDisplay.simpleUpdate(sFM1061, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                changeElectricalParticles();
                break;
            case FM977:
                digitalDisplay.simpleUpdate(sFM977, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                changeElectricalParticles();
                break;
            case FM952:
                digitalDisplay.simpleUpdate(sFM952, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                changeElectricalParticles();
                break;
            case AM697:
                digitalDisplay.simpleUpdate(sAM697, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                changeElectricalParticles();
                break;
            case AM498:
                digitalDisplay.simpleUpdate(sAM498, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                changeElectricalParticles();
                break;
            case AM707:
                digitalDisplay.simpleUpdate(sAM707, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                changeElectricalParticles();
                break;
            default :
                digitalDisplay.simpleUpdate(sFM1061, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                break;
        }
        
    }
    
    private void changeElectricalParticles() {
        
    }
}
