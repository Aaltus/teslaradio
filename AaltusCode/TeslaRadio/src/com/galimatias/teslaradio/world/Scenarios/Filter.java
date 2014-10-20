/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.DynamicWireParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.ParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.PatternGeneratorControl;
import com.galimatias.teslaradio.world.effects.TextBox;
import com.galimatias.teslaradio.world.observer.AutoGenObserver;
import com.galimatias.teslaradio.world.observer.EmitterObserver;
import com.jme3.font.BitmapFont;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Batcave
 */
public class Filter extends Scenario implements EmitterObserver, AutoGenObserver {

    private float trackableAngle = 0;
    private float direction = 1;
    
    private Spatial filterWheel;
    
    private String titleText = "Le filtrage";
    
    private boolean isFM = true;
    private boolean hasTurned = false;
    private int lastFrequency = 1;
    
    //Pattern Geometry
    private Node micTapParticle;
    private Node cubeSignal;
    private Node pyramidSignal;
    private Node dodecagoneSignal;
    
    private Node inputEmitter = new Node();
    private Node outFilterEmitter = new Node();
    private Node outputEmitter = new Node();
    
    private Quaternion initAngleWheel = new Quaternion();
    private Quaternion endAngleWheel = new Quaternion();
    
    private int frequency = 0;
    
    private float tpfCumul = 0;
    
    Filter(Camera cam, Spatial destinationHandle) {
        
        super(cam, destinationHandle);
        
        this.needAutoGenIfMain = true;
        
        loadUnmovableObjects();
        loadMovableObjects();
    }
    
    @Override
    protected void loadUnmovableObjects() { 
        scene = (Node) assetManager.loadModel("Models/Filter/Filtre.j3o");
        scene.setName("Filter");
        this.attachChild(scene);
        
        scene.setLocalRotation(new Quaternion().fromAngleAxis(-pi/2f, Vector3f.UNIT_Y));
        
        // Get the handles of the emitters
        Spatial pathInHandle = scene.getChild("Handle.In");
        Spatial pathOutFilterHandle = scene.getChild("Handle.Filtre.In");
        Spatial outputHandle = scene.getChild("Handle.Out");
        
        // Get the different paths
        Node wirePcb_node = (Node) scene.getChild("Path.In.Object");
        Geometry pathIn = (Geometry) wirePcb_node.getChild("Path.In.Nurbs");
        Node output_node = (Node) scene.getChild("Path.Out.Object");
        Geometry pathOut = (Geometry) output_node.getChild("Path.Out.Nurbs");
        
        initTitleBox();
        
        initStaticParticlesEmitter(inputEmitter, pathInHandle, pathIn, null);
        initStaticParticlesEmitter(outFilterEmitter, pathOutFilterHandle, pathOut, null);
        
        inputEmitter.setName("Input");
        outFilterEmitter.setName("Output Filter");
        outputEmitter.setName("Output");
        
        initPatternGenerator();
        
        scene.attachChild(outputEmitter);
        outputEmitter.setLocalTranslation(outputHandle.getLocalTranslation());
        
        if(destinationHandle != null) {
            outputEmitter.addControl(new DynamicWireParticleEmitterControl(this.destinationHandle, 3.5f, null));
            inputEmitter.getControl(ParticleEmitterControl.class).registerObserver(this);
            outFilterEmitter.getControl(ParticleEmitterControl.class).registerObserver(outputEmitter.getControl(ParticleEmitterControl.class));
            outputEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);
            outputEmitter.getControl(ParticleEmitterControl.class).registerObserver(this.destinationHandle.getControl(ParticleEmitterControl.class));
        }
    }

    @Override
    protected void loadMovableObjects() {
        filterWheel = scene.getChild("Circle");
        
        initAngleWheel.fromAngleAxis(0f, Vector3f.UNIT_Y);
        endAngleWheel.fromAngleAxis(2f*pi/3f, Vector3f.UNIT_Y);
    }

    @Override
    public void restartScenario() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onScenarioTouch(String name, TouchEvent touchEvent, float v) {
        
    }

    @Override
    protected boolean simpleUpdate(float tpf) {
        tpfCumul += tpf;
        
        if (this.DEBUG_ANGLE) { //In Scenario class !!
            trackableAngle += direction * (pi / 9) * tpf;

            if (trackableAngle >= 2 * pi || trackableAngle <= 0) {
                //trackableAngle = 0;
                direction *= -1;
            }
        } else {
            //trackableAngle = 0;
            trackableAngle = this.getUserData("angleX");
        }
        
        checkTrackableAngle(trackableAngle);
                
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
    protected Spatial getInputHandle() {
        return inputEmitter;
    }

    @Override
    protected void initTitleBox() {
        TextBox titleTextBox = new TextBox(assetManager, 
                                    titleText, 
                                    TEXTSIZE,
                                    TEXTCOLOR, 
                                    TEXTBOXCOLOR,
                                    TITLEWIDTH, 
                                    TITLEHEIGHT, 
                                    "titleText", 
                                    BitmapFont.Align.Center, 
                                    SHOWTEXTDEBUG, 
                                    TEXTLOOKATCAMERA);

        //move the text on the ground without moving
        Vector3f titleTextPosition = new Vector3f(0f, 0.25f, 6f);
        titleTextBox.rotate((float) -Math.PI / 2, 0, 0);

        titleTextBox.move(titleTextPosition);
        this.attachChild(titleTextBox);
    }

    @Override
    protected void initPatternGenerator() {        
        Spatial baseGeom = ScenariosCommon.initBaseGeneratorParticle();
        Spatial[] carrier = ScenariosCommon.initCarrierGeometries();
              
        this.cubeSignal = new Node();
        this.cubeSignal.attachChild(carrier[0].clone());
        ScenariosCommon.modulateFMorAM(this.cubeSignal, baseGeom, isFM);
        this.cubeSignal.attachChild(baseGeom.clone());
        this.cubeSignal.setUserData("CarrierShape", this.cubeSignal.getChild(0).getName());
        this.cubeSignal.setUserData("isFM", isFM);
        this.cubeSignal.setUserData("Frequency", frequency);
        
        this.pyramidSignal = new Node();
        this.pyramidSignal.attachChild(carrier[0].clone());
        ScenariosCommon.modulateFMorAM(this.pyramidSignal, baseGeom, isFM);
        this.pyramidSignal.attachChild(baseGeom.clone());
        this.pyramidSignal.setUserData("CarrierShape", this.pyramidSignal.getChild(0).getName());
        this.pyramidSignal.setUserData("isFM", isFM);
        this.pyramidSignal.setUserData("Frequency", frequency);
       
        this.dodecagoneSignal = new Node();
        this.dodecagoneSignal.attachChild(carrier[0].clone());
        ScenariosCommon.modulateFMorAM(this.dodecagoneSignal, baseGeom, isFM);
        this.dodecagoneSignal.attachChild(baseGeom.clone());
        this.dodecagoneSignal.setUserData("CarrierShape", this.dodecagoneSignal.getChild(0).getName());
        this.dodecagoneSignal.setUserData("isFM", isFM);
        this.dodecagoneSignal.setUserData("Frequency", frequency);
        
        this.micTapParticle = this.cubeSignal;
        
        this.getInputHandle().addControl(new PatternGeneratorControl(0.5f, micTapParticle.clone(), 7, ScenariosCommon.minBaseParticleScale, 
                                                                     ScenariosCommon.maxBaseParticleScale, true));

        this.inputEmitter.addControl(new PatternGeneratorControl(0.5f, micTapParticle, 10, ScenariosCommon.minBaseParticleScale,
                ScenariosCommon.maxBaseParticleScale, true));    
    }
    
    @Override
    public void autoGenObserverUpdate(Spatial newCarrier, boolean isFm) {
        this.isFM = isFm;
        this.initPatternGenerator();
        if(newCarrier.getName().equals("CubeCarrier")){
             this.getInputHandle().getControl(PatternGeneratorControl.class).setBaseParticle(this.cubeSignal);
        }
        else if(newCarrier.getName().equals("PyramidCarrier")){
            this.getInputHandle().getControl(PatternGeneratorControl.class).setBaseParticle(this.pyramidSignal);
        }
        else if(newCarrier.getName().equals("DodecagoneCarrier")){
            this.getInputHandle().getControl(PatternGeneratorControl.class).setBaseParticle(this.dodecagoneSignal);
            
        }
    }

    @Override
    public void signalEndOfPath(Geometry caller, float magnitude) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void checkTrackableAngle(float trackableAngle) {

        float stepRange = pi / 3;

        if (trackableAngle >= 0 && trackableAngle < stepRange) {
            frequency = 1;
        } else if (trackableAngle >= stepRange && trackableAngle < 2 * stepRange) {
            frequency = 2;
        } else if (trackableAngle >= 2 * stepRange && trackableAngle < 3 * stepRange) {
            frequency = 3;
        } else if (trackableAngle >= 3 * stepRange && trackableAngle < 4 * stepRange) {
            frequency = 4;
        } else if (trackableAngle >= 4 * stepRange && trackableAngle < 5 * stepRange) {
            frequency = 5;
        } else if (trackableAngle >= 5 * stepRange && trackableAngle < 6 * stepRange) {
            frequency = 6;
        }
        
        turnTunerButton(stepRange);
    }
    
    private void turnTunerButton(float stepRange) {

        Quaternion rot = new Quaternion();
        
        switch(frequency) {
            case 1:
                initAngleWheel.fromAngleAxis(0f, Vector3f.UNIT_Y);
                endAngleWheel.fromAngleAxis(stepRange, Vector3f.UNIT_Y);
                break;
            case 2:
                initAngleWheel.fromAngleAxis(stepRange, Vector3f.UNIT_Y);
                endAngleWheel.fromAngleAxis(2*stepRange, Vector3f.UNIT_Y);
                break;
            case 3:
                initAngleWheel.fromAngleAxis(2*stepRange, Vector3f.UNIT_Y);
                endAngleWheel.fromAngleAxis(3*stepRange, Vector3f.UNIT_Y);
                break;
            case 4:
                initAngleWheel.fromAngleAxis(3*stepRange, Vector3f.UNIT_Y);
                endAngleWheel.fromAngleAxis(4*stepRange, Vector3f.UNIT_Y);
                break;
            case 5:
                initAngleWheel.fromAngleAxis(4*stepRange, Vector3f.UNIT_Y);
                endAngleWheel.fromAngleAxis(5*stepRange, Vector3f.UNIT_Y);
                break;
            case 6:
                initAngleWheel.fromAngleAxis(5*stepRange, Vector3f.UNIT_Y);
                endAngleWheel.fromAngleAxis(6*stepRange, Vector3f.UNIT_Y);
                break;    
            default:
                initAngleWheel.fromAngleAxis(0f, Vector3f.UNIT_Y);
                endAngleWheel.fromAngleAxis(stepRange/2f, Vector3f.UNIT_Y);
                break;       
        }
        
        if (lastFrequency != frequency) {
            filterWheel.setLocalRotation(rot.slerp(initAngleWheel, endAngleWheel, tpfCumul));
        }
        
        lastFrequency = frequency;
    }
    
    private void filter(int frequency, Spatial spatial) {
        
        if (frequency != this.frequency) {
            
        } else {
            ((Node)spatial).detachChild(((Node)spatial).getChild(0));
        }
    }

    @Override
    public void emitterObserverUpdate(Spatial spatial, String notifierId) {
        if (notifierId.equals("Input")) {      
            filter((int)spatial.getUserData("Frequency"), spatial);
        }
    }
}
