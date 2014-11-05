/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.*;
import com.galimatias.teslaradio.world.observer.AutoGenObserver;
import com.galimatias.teslaradio.world.observer.EmitterObserver;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.*;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.utils.AppLogger;

/**
 *
 * @author Batcave
 */
public abstract class ModulationCommon extends Scenario implements EmitterObserver, AutoGenObserver {

    // Values displayed on the digital screen of the PCB 3D object
    protected final String sFM1061 = "FM 106.1MHz";
    protected final String sFM969 = "FM 96.9MHz";
    protected final String sFM1027 = "FM 102.7MHz";
    protected final String sAM600 = "AM 600kHz";
    protected final String sAM800 = "AM 800kHz";
    protected final String sAM1500 = "AM 1500kHz";
    protected Boolean isFM = false;
    protected Boolean switchIsToggled = false;
    
    // 3D objects of the scene
    private Spatial turnButton;
    private Spatial actionSwitch;
    
    // TextBox of the scene
    private TextBox digitalDisplay;
    protected TextBox titleTextBox;

    protected float titleTextSize = 0.5f;
    protected float digitalTextSize = 0.4f;
    protected ColorRGBA digitalTextColor = ColorRGBA.Green;

    // Signals emitters
    protected Node wirePcbEmitter = new Node();
    protected Node carrierEmitter = new Node();
    protected Node pcbAmpEmitter = new Node();
    protected Node outputEmitter = new Node();

    // Geometry of the carrier signals
    private Spatial cubeCarrier;
    private Spatial pyramidCarrier;
    private Spatial dodecagoneCarrier; // Really...

    // Current carrier signal and his associated output
    protected Spatial selectedCarrier;
    protected Node outputSignal;

    //Pattern Geometry
    protected Spatial micTapParticle;

    //Angle for test purposes
    private float trackableAngle = 0;
    private int direction = 1;

    //Variable for switch
    private Quaternion initAngleSwitch = new Quaternion();
    private Quaternion endAngleSwitch = new Quaternion();
    private float stepAngleSwitch = 0;
    private float tpfCumulSwitch = 0;
    private float tpfCumul = 0;
    private Quaternion rotationXSwitch = new Quaternion();

    //Arrows
    private Node rotationArrow;
    private Node switchArrow;
    
    protected int frequency = 1;
    
    protected boolean lastFm = false;
    private int lastFrequency = 0;
    
    ModulationCommon(ScenarioCommon sc, Camera cam, Spatial destinationHandle) {
        
        super(sc, cam, destinationHandle);
        
        this.needAutoGenIfMain = true;
    }
    
    ModulationCommon(ScenarioCommon sc, Camera cam, Spatial destinationHandle, String bgm) {
        
        super(sc, cam, destinationHandle, bgm);
        
        this.needAutoGenIfMain = true;
    }
    
    @Override
    protected void loadUnmovableObjects() {
        scene = (Node) assetManager.loadModel("Models/Modulation_Demodulation/modulation.j3o");
        scene.setName("Modulation_Demodulation");
        this.attachChild(scene);

        scene.setLocalTranslation(new Vector3f(2.5f, 0.0f, 0.5f));

        // Get the handles of the emitters
        Spatial pathInHandle = scene.getChild("Handle.Module.In");
        Spatial pathCarrierHandle = scene.getChild("Handle.Generator");
        Spatial pathOutChipHandle = scene.getChild("Handle.Chip.Out");
        Spatial outputHandle = scene.getChild("Handle.Module.Out");

        // Get the different paths
        Node wirePcb_node = (Node) scene.getChild("Path.In.Object");
        Geometry pathIn = (Geometry) wirePcb_node.getChild("Path.In.Nurbs");
        Node carrier_node = (Node) scene.getChild("Path.Generator.Object");
        Geometry pathCarrier = (Geometry) carrier_node.getChild("Path.Generator.Nurbs");
        Node pcbAmp_node = (Node) scene.getChild("Path.Out.Object");
        Geometry pathOut = (Geometry) pcbAmp_node.getChild("Path.Out.Nurbs");

        initDigitalDisplay();
        initTitleBox();

        initStaticParticlesEmitter(wirePcbEmitter, pathInHandle, pathIn, cam);
        initStaticParticlesEmitter(carrierEmitter, pathCarrierHandle, pathCarrier, null);
        initStaticParticlesEmitter(pcbAmpEmitter, pathOutChipHandle, pathOut, null);
        initPatternGenerator();

        scene.attachChild(outputEmitter);
        outputEmitter.setLocalTranslation(outputHandle.getLocalTranslation()); // TO DO: utiliser le object handle blender pour position
        //System.out.println("translation " + outputHandle.getLocalTranslation());

        // Set names for the emitters
        wirePcbEmitter.setName("WirePCBEmitter");
        carrierEmitter.setName("CarrierEmitter");
        pcbAmpEmitter.setName("PCBAmpEmitter");

        if(destinationHandle != null){
            outputEmitter.addControl(new DynamicWireParticleEmitterControl((Node)this.destinationHandle, 3.5f, null, true));
            outputEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);
            carrierEmitter.getControl(ParticleEmitterControl.class).registerObserver(this);
            wirePcbEmitter.getControl(ParticleEmitterControl.class).registerObserver(this);
            outputEmitter.getControl(ParticleEmitterControl.class).registerObserver(((Node)this.destinationHandle).getControl(ParticleEmitterControl.class));
            pcbAmpEmitter.getControl(ParticleEmitterControl.class).registerObserver(outputEmitter.getControl(ParticleEmitterControl.class));
        }
    }

    @Override
    protected void loadMovableObjects() {
        turnButton = scene.getChild("Button");
        actionSwitch = scene.getChild("Switch");

        initAngleSwitch.fromAngleAxis(0.45f, Vector3f.UNIT_X);
        endAngleSwitch.fromAngleAxis(-0.45f, Vector3f.UNIT_X);

        Spatial[] geom = ScenarioCommon.initCarrierGeometries();
        cubeCarrier = geom[0];
        pyramidCarrier = geom[1];
        dodecagoneCarrier = geom[2];
        selectedCarrier = cubeCarrier;

        carrierEmitter.getLocalTranslation().addLocal(new Vector3f(0.0f,cubeCarrier.getWorldScale().y,0.0f));
        pcbAmpEmitter.getLocalTranslation().addLocal(new Vector3f(0.0f,cubeCarrier.getWorldScale().y,0.0f));

        initOutputSignals();
        
        this.spotlight = ScenarioCommon.spotlightFactory();

        //Assign touchable
        touchable = new Node();//(Node) scene.getParent().getChild("Touchable")
        touchable.attachChild(actionSwitch);
        scene.attachChild(touchable);
    }

    protected void initOutputSignals() {

        this.outputSignal = new Node();

        //cubeOutputSignal = cubeCarrier.clone();

        // Default value of the outputSignal
        this.outputSignal.attachChild(cubeCarrier.clone());

        //pyramidOutputSignal = pyramidCarrier.clone();

        //dodecagoneOutputSignal = dodecagoneCarrier.clone();
    }

    private void initDigitalDisplay() {

        // Default configuration of the digital display
        digitalDisplay = new TextBox(assetManager,
                sFM1061,
                digitalTextSize,
                TEXTCOLOR,
                TEXTBOXCOLOR,
                3.5f,
                1.0f,
                "DigitalDisplay",
                BitmapFont.Align.Center.Center,
                false,
                false);

        // Get the digital display parameters
        Vector3f displayPosition = scene.getChild("Display").getLocalTranslation();
        // TODO Use addLocal... I tried but for some reasons, it doesn't work...
        displayPosition = displayPosition.add(-0.4f, 0.5f, 0.0f);

        digitalDisplay.setLocalTranslation(displayPosition);
        Quaternion rotY = new Quaternion();
        Quaternion rotZ = new Quaternion();
        rotY.fromAngleAxis(pi / 2, Vector3f.UNIT_Y);
        rotZ.fromAngleAxis(-pi / 3, Vector3f.UNIT_X);
        digitalDisplay.rotate(rotY);
        digitalDisplay.rotate(rotZ);
        scene.attachChild(digitalDisplay);
    }

    @Override
    protected void onFirstNodeActions() {
        super.onFirstNodeActions();
        
        turnButton.setLocalRotation(new Quaternion().fromAngleAxis(pi, Vector3f.UNIT_Y));
    }  
    

    //Dynamic move
    private void checkModulationMode(float tpf) {
        if (switchIsToggled) {
            tpfCumulSwitch += tpf;
            stepAngleSwitch = tpfCumulSwitch/0.35f; //permet de donner la vitesse
            switchRotation(isFM, stepAngleSwitch);
            if (stepAngleSwitch >= 1) {
                switchIsToggled = false;
                tpfCumulSwitch = 0;
            }
           
        }
    }

    public void toggleModulationMode() {
        removeHintImages();
        if (!switchIsToggled) {
            isFM = !isFM;
            switchIsToggled = true;
        }
    }

    private void turnTunerButton(float ZXangle) {

        Quaternion rot = new Quaternion();
        rot.fromAngleAxis(ZXangle, Vector3f.UNIT_Y);
        turnButton.setLocalRotation(rot);
    }

    private void changeModulation(int frequency, Boolean isFM) {
        
        if (isFM) {
            switch (frequency) {
                case 1:
                    digitalDisplay.simpleUpdate(sFM1061,
                                                digitalTextSize, 
                                                digitalTextColor, 
                                                Camera, 
                                                Vector3f.UNIT_X);
                    break;
                case 2:
                    digitalDisplay.simpleUpdate(sFM969, 
                                                digitalTextSize, 
                                                digitalTextColor, 
                                                Camera, 
                                                Vector3f.UNIT_X);
                    break;
                case 3:
                    digitalDisplay.simpleUpdate(sFM1027,
                            digitalTextSize,
                            digitalTextColor,
                            Camera,
                            Vector3f.UNIT_X);
                    break;
            }
        } else if (!isFM) {
            switch (frequency) {
                case 1:
                    digitalDisplay.simpleUpdate(sAM600, 
                                                digitalTextSize, 
                                                digitalTextColor, 
                                                Camera, 
                                                Vector3f.UNIT_X);
                    break;
                case 2:
                    digitalDisplay.simpleUpdate(sAM800, 
                                                digitalTextSize, 
                                                digitalTextColor, 
                                                Camera, 
                                                Vector3f.UNIT_X);
                    break;
                case 3:
                    digitalDisplay.simpleUpdate(sAM1500, 
                                                digitalTextSize, 
                                                digitalTextColor, 
                                                Camera, 
                                                Vector3f.UNIT_X);
                    break;
            }
        }
        
        changeCarrierParticles(frequency);
    }

    protected void changeOuputParticles(Spatial spatial, String emitterId) {

        if (spatial != null && emitterId.equals("CarrierEmitter")) {
            outputSignal.detachAllChildren();
            outputSignal.attachChild(spatial);
        }
    }

    private void changeCarrierParticles(int frequency) {

        this.frequency = frequency;
        
        Spatial lastCarrier = selectedCarrier;
        switch (frequency) {
            case 1:
                selectedCarrier = cubeCarrier;
                break;
            case 2:
                selectedCarrier = pyramidCarrier;
                break;
            case 3:
                selectedCarrier = dodecagoneCarrier;
                break;
        }
        if(this.getName().equals("Modulation") && (lastCarrier != selectedCarrier || lastFm != isFM)){
            scenarioCommon.notifyObservers(selectedCarrier, this.isFM);
        }

        lastFm = isFM;
    }

    private void checkTrackableAngle(float trackableAngle) {

        float stepRange = 2f * pi / 3;
        int frequency = 0;

        if (trackableAngle >= 0 && trackableAngle < stepRange) {
            frequency = 1;
        } else if (trackableAngle >= stepRange && trackableAngle < 2 * stepRange) {
            frequency = 2;
        } else if (trackableAngle >= 2 * stepRange && trackableAngle < 3 * stepRange) {
            frequency = 3;
        }
        
        if (lastFrequency != frequency) {
            changeModulation(frequency, isFM);
        }
        
        turnTunerButton(trackableAngle);

        lastFrequency = frequency;
    }


    /**
     * Switches the FM/AM switch dynamically
     *
     * @param isFM
     * @param tpfCumul
     */
    private void switchRotation(boolean isFM, float tpfCumul) {
        Quaternion currRotation = new Quaternion();
        if (isFM) {
            actionSwitch.setLocalRotation(currRotation.slerp(initAngleSwitch, endAngleSwitch, tpfCumul));

        } else {
            actionSwitch.setLocalRotation(currRotation.slerp(endAngleSwitch, initAngleSwitch, tpfCumul));
        }
    }

    @Override
    public void onScenarioTouch(String name, TouchEvent touchEvent, float v) {
        switch (touchEvent.getType()) {

            //Checking for down event is very responsive
            case DOWN:

                //case TAP:
                if (name.equals("Touch")) {
                    // 1. Reset results list.
                    CollisionResults results = new CollisionResults();

                    // 2. Mode 1: user touch location.
                    //Vector2f click2d = inputManager.getCursorPosition();

                    Vector2f click2d = new Vector2f(touchEvent.getX(), touchEvent.getY());
                    Vector3f click3d = Camera.getWorldCoordinates(
                            new Vector2f(click2d.x, click2d.y), 0f).clone();
                    Vector3f dir = Camera.getWorldCoordinates(
                            new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
                    Ray ray = new Ray(click3d, dir);

                    // 3. Collect intersections between Ray and Shootables in results list.
                    touchable.collideWith(ray, results);

                    // 4. Print the results
                    //Log.d(TAG, "----- Collisions? " + results.size() + "-----");
                    for (int i = 0; i < results.size(); i++) {
                        // For each hit, we know distance, impact point, name of geometry.
                        float dist = results.getCollision(i).getDistance();
                        Vector3f pt = results.getCollision(i).getContactPoint();
                        String hit = results.getCollision(i).getGeometry().getName();

                        //Log.e(TAG, "  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
                    }

                    // 5. Use the results (we mark the hit object)
                    if (results.size() > 0) {

                        // The closest collision point is what was truly hit:
                        CollisionResult closest = results.getClosestCollision();

                        Spatial touchedGeometry = closest.getGeometry();
                        String nameToCompare = touchedGeometry.getParent().getName();

                        if (nameToCompare.equals(this.getChild("Switch").getName())) {
                            toggleModulationMode();
                        }
                        else if (nameToCompare.equals(titleTextBox.getName()))
                        {
                            showInformativeMenu = true;
                            break;
                        }
                    }
                }
                break;
        }
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

        if (this.emphasisChange) {
            objectEmphasis();
            this.emphasisChange = false;
        }
        
        //switchArrow.simpleUpdate(tpf);
        //rotationArrow.simpleUpdate(tpf);

        checkTrackableAngle(trackableAngle);
        invRotScenario(trackableAngle + (pi / 2));
        checkModulationMode(tpf);
        
        if (carrierEmitter != null && tpfCumul >= 1.0f) {
            AppLogger.getInstance().d("TAG","Chat dans");
            carrierEmitter.getControl(ParticleEmitterControl.class).emitParticle(selectedCarrier.clone());
            tpfCumul = 0;
        }

        return false;
    }

    @Override
    protected Spatial getInputHandle() {
        return wirePcbEmitter;
    }

    @Override
    public void signalEndOfPath(Geometry caller, float magnitude) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    protected void loadArrows() {
        
        switchArrow = new Node();
        switchArrow.move(actionSwitch.getLocalTranslation());
        switchArrow.addControl(new Arrows("touch", assetManager, 1));
        LookAtCameraControl control = new LookAtCameraControl(Camera);
        switchArrow.addControl(control);
        scene.attachChild(switchArrow);

        rotationArrow = new Node();
        rotationArrow.addControl(new Arrows("rotation", assetManager, 10));
        this.attachChild(rotationArrow);
    }

    @Override
    protected void setAutoGenerationParticle(Spatial particle){
        this.micTapParticle = (Node) particle;
        this.wirePcbEmitter.getControl(PatternGeneratorControl.class).
                setBaseParticle(this.micTapParticle);

    }

    /**
     * Remove hints, is called after touch occurs
     */
    public void removeHintImages()
    {
        switchArrow.getControl(FadeControl.class).setShowImage(false);
        switchArrow.getControl(Arrows.class).resetTimeLastTouch();
    }
    
    @Override
    protected void objectEmphasis() {
        if (this.spotlight != null) {            
            switch(this.currentObjectToEmphasisOn) {
                // Attach on modulator
                case 0:
                    this.spotlight.setLocalTranslation(scene.getChild("Modulator").getLocalTranslation().add(0.0f,-scene.getChild("Modulator").getLocalTranslation().y,0.0f));
                    this.spotlight.setLocalScale(new Vector3f(2.0f,20.0f,2.0f));
                    scene.attachChild(this.spotlight);
                    break;
                // Attach on frequency generator    
                case 1:
                    this.spotlight.setLocalTranslation(scene.getChild("Display").getLocalTranslation().add(0.0f,-scene.getChild("Display").getLocalTranslation().y,0.0f));
                    this.spotlight.setLocalScale(new Vector3f(2.0f,20.0f,2.0f));
                    scene.attachChild(this.spotlight);
                    break;
                // Attach on the switch    
                case 2:
                    this.spotlight.setLocalTranslation(scene.getChild("Switch").getLocalTranslation().add(0.0f,-scene.getChild("Switch").getLocalTranslation().y,0.0f));
                    this.spotlight.setLocalScale(new Vector3f(2.0f,20.0f,2.0f));
                    scene.attachChild(this.spotlight);
                    break;    
                default:
                    scene.detachChild(this.spotlight);
                    break;
            }
        }
    }
}
