/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.*;
import com.galimatias.teslaradio.world.observer.AutoGenObserver;
import com.galimatias.teslaradio.world.observer.EmitterObserver;
import com.jme3.font.BitmapFont;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.List;

/**
 *
 * @author Batcave
 */
public class Filter extends Scenario implements EmitterObserver, AutoGenObserver {

    private float trackableAngle = 0;
    private float direction = 1;
    
    private Spatial filterWheel;
    
    private String titleText = "Le Filtrage";
    
    private boolean isFM = true;
    
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

    private Node rotationArrow;
    
    private int frequency = 1;
    private String carrier = "CubeCarrier";
    
    public Filter(ScenarioCommon sc, Camera cam, Spatial destinationHandle) {
        
        super(sc, cam, destinationHandle);
        this.setName("Filter");
        this.needAutoGenIfMain = true; 
        scenarioCommon.registerObserver(this);
        
        loadUnmovableObjects();
        loadMovableObjects();
        loadArrows();
    }
    
    @Override
    protected void loadUnmovableObjects() { 
        scene = (Node) assetManager.loadModel("Models/Filter/Filtre.j3o");
        scene.setName("Filter");
        this.attachChild(scene);

        scene.setLocalRotation(new Quaternion().fromAngleAxis(-pi/2f, Vector3f.UNIT_Y));
        scene.setLocalScale(1.5f);
        
        // Get the handles of the emitters
        Spatial pathInHandle = scene.getChild("Handle.In");
        Spatial pathOutFilterHandle = scene.getChild("Handle.Filtre.In");
        Spatial outputHandle = scene.getChild("Handle.Out");
        
        // Get the different paths
        Node wirePcb_node = (Node) scene.getChild("Path.In.Object");
        Geometry pathIn = (Geometry) wirePcb_node.getChild("Path.In.Nurbs");
        Node output_node = (Node) scene.getChild("Path.Out.Object");
        Geometry pathOut = (Geometry) output_node.getChild("Path.Out.Nurbs");
        
        //initTitleBox();
        
        initStaticParticlesEmitter(inputEmitter, pathInHandle, pathIn, null);
        initStaticParticlesEmitter(outFilterEmitter, pathOutFilterHandle, pathOut, null);
        
        inputEmitter.setName("Input");
        outFilterEmitter.setName("Output Filter");
        outputEmitter.setName("Output");
        
        inputEmitter.getControl(ParticleEmitterControl.class).registerObserver(this);
        
        initPatternGenerator();
        
        scene.attachChild(outputEmitter);
        outputEmitter.setLocalTranslation(outputHandle.getLocalTranslation());
        
        if(destinationHandle != null) {
            outputEmitter.addControl(new DynamicWireParticleEmitterControl(this.destinationHandle, 3.5f, null, true));
            outFilterEmitter.getControl(ParticleEmitterControl.class).registerObserver(outputEmitter.getControl(ParticleEmitterControl.class));
            outputEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);
            outputEmitter.getControl(ParticleEmitterControl.class).registerObserver(this.destinationHandle.getControl(ParticleEmitterControl.class));
        }
    }

    @Override
    protected void loadMovableObjects() {
        filterWheel = scene.getChild("Circle");
        filterWheel.setLocalRotation(new Quaternion().fromAngleAxis(pi/3f, Vector3f.UNIT_Y));
        
        initAngleWheel.fromAngleAxis(0f, Vector3f.UNIT_Y);
        endAngleWheel.fromAngleAxis(pi/3f, Vector3f.UNIT_Y);
        
        this.spotlight = ScenarioCommon.spotlightFactory();
    }

    private void loadArrows()
    {
        rotationArrow = new Node();
        rotationArrow.addControl(new Arrows("rotation", assetManager, 10));
        scene.attachChild(rotationArrow);
    }

    @Override
    public void restartScenario() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onScenarioTouch(String name, TouchEvent touchEvent, float v) {
        // ...Does nothing in this scenario
    }

    @Override
    protected boolean simpleUpdate(float tpf) {
        
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
        
        if (this.emphasisChange) {
            objectEmphasis();
            this.emphasisChange = false;
        }
        
        checkTrackableAngle(trackableAngle);
        invRotScenario(trackableAngle + (pi / 2));
                
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
        Spatial baseGeom = scenarioCommon.initBaseGeneratorParticle();
        Spatial[] carrier = ScenarioCommon.initCarrierGeometries();
              
        this.cubeSignal = new Node();
        this.cubeSignal.attachChild(carrier[0].clone());
        scenarioCommon.modulateFMorAM(this.cubeSignal, baseGeom, isFM);
        this.cubeSignal.attachChild(baseGeom.clone());
        this.cubeSignal.setUserData("CarrierShape", this.cubeSignal.getChild(0).getName());
        this.cubeSignal.setUserData("isFM", isFM);
        
        this.pyramidSignal = new Node();
        this.pyramidSignal.attachChild(carrier[0].clone());
        scenarioCommon.modulateFMorAM(this.pyramidSignal, baseGeom, isFM);
        this.pyramidSignal.attachChild(baseGeom.clone());
        this.pyramidSignal.setUserData("CarrierShape", this.pyramidSignal.getChild(0).getName());
        this.pyramidSignal.setUserData("isFM", isFM);
       
        this.dodecagoneSignal = new Node();
        this.dodecagoneSignal.attachChild(carrier[0].clone());
        scenarioCommon.modulateFMorAM(this.dodecagoneSignal, baseGeom, isFM);
        this.dodecagoneSignal.attachChild(baseGeom.clone());
        this.dodecagoneSignal.setUserData("CarrierShape", this.dodecagoneSignal.getChild(0).getName());
        this.dodecagoneSignal.setUserData("isFM", isFM);
        
        this.micTapParticle = this.cubeSignal;
        
        this.getInputHandle().addControl(new PatternGeneratorControl(0.5f, micTapParticle.clone(), 7, scenarioCommon.minBaseParticleScale, 
                                                                     scenarioCommon.maxBaseParticleScale, true));    
    }
    
    @Override
    public void autoGenObserverUpdate(Spatial newCarrier, boolean isFm) {
        this.isFM = isFm;
        Node node = new Node();
        Spatial baseGeom = scenarioCommon.initBaseGeneratorParticle();
        node.attachChild(newCarrier.clone());
        List<Spatial> lst = scenarioCommon.generateModulatedWaves(
               node , baseGeom, isFm, 10,scenarioCommon.minBaseParticleScale ,scenarioCommon.maxBaseParticleScale);
        
        this.getInputHandle().getControl(PatternGeneratorControl.class).setParticleList(lst);
    }

    @Override
    public void signalEndOfPath(Geometry caller, float magnitude) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void checkTrackableAngle(float trackableAngle) {

        float stepRange = 2f*pi / 3f;
        float deltaAngle = stepRange*0.25f/2f;

        if (trackableAngle <= stepRange + deltaAngle && trackableAngle >= stepRange - deltaAngle) {
            frequency = 1;
            this.carrier = "CubeCarrier";
        } else if (trackableAngle <= 2 * stepRange + deltaAngle && trackableAngle >= 2 * stepRange - deltaAngle) {
            frequency = 2;
            this.carrier = "PyramidCarrier";
        } else if (trackableAngle <= 3 * stepRange + deltaAngle && trackableAngle >= 3 * stepRange - deltaAngle) {
            frequency = 3;
            this.carrier = "DodecagoneCarrier";
        } else {
            frequency = 4;
            this.carrier = "NOPE, CHUCK TESTA!";
        }

        turnTunerButton(frequency,stepRange);
    }
    
    private void turnTunerButton(int frequency, float stepAngle) {

        Quaternion rot = new Quaternion();

        switch(frequency) {
            case 1:
                filterWheel.setLocalRotation(rot.fromAngleAxis(stepAngle,Vector3f.UNIT_Y));
                break;
            case 2:
                filterWheel.setLocalRotation(rot.fromAngleAxis(2*stepAngle,Vector3f.UNIT_Y));
                break;
            case 3:
                filterWheel.setLocalRotation(rot.fromAngleAxis(3*stepAngle,Vector3f.UNIT_Y));
                break;
            case 4:
                filterWheel.setLocalRotation(rot.fromAngleAxis(trackableAngle,Vector3f.UNIT_Y));
                break;
        }


    }
    
    private void filter(String carrier, Spatial spatial) {
        if (carrier.equals(this.carrier)) {
            if (outFilterEmitter != null) {
                outFilterEmitter.getControl(ParticleEmitterControl.class).emitParticle(spatial);
            }
            if(!this.isFirst){
                this.updateVolume(1);
            }
        }
         else{
            this.updateVolume(0);
        }
    }

    @Override
    public void emitterObserverUpdate(Spatial spatial, String notifierId) {
        if (notifierId.equals("Input")) {
            filter(((Node)spatial).getChild(0).getName(), spatial);
        }
    }

    @Override
    protected void objectEmphasis() {
        if (this.spotlight != null) {            
            switch(this.currentObjectToEmphasisOn) {
                // Attach on microphone
                case 0:
                    this.spotlight.setLocalTranslation(scene.getChild("Circle").getLocalTranslation().add(0.0f,-scene.getChild("Circle").getLocalTranslation().y,0.0f));
                    this.spotlight.setLocalScale(new Vector3f(3.0f,30.0f,3.0f));
                    scene.attachChild(this.spotlight);
                    break;  
                default:
                    scene.detachChild(this.spotlight);
                    break;
            }
        }
    }
    
    
}
