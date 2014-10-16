package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.Arrows;
import com.galimatias.teslaradio.world.effects.DynamicWireParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.FadeControl;
import com.galimatias.teslaradio.world.effects.ImageBox;
import com.galimatias.teslaradio.world.effects.LookAtCameraControl;
import com.galimatias.teslaradio.world.effects.ParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.PatternGeneratorControl;
import com.galimatias.teslaradio.world.effects.StaticWireParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.TextBox;
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

/**
 * Created by Batcave on 2014-09-09.
 */
public final class Modulation extends Scenario implements EmitterObserver {
    
    private final static String TAG = "Modulation";
    
    // Values displayed on the digital screen of the PCB 3D object
    private final String sFM1061 = "FM 106.1MHz";
    private final String sFM969 = "FM 96.9MHz";
    private final String sFM1027 = "FM 102.7MHz";
    private final String sAM600 = "AM 600kHz";
    private final String sAM800 = "AM 800kHz";
    private final String sAM1500 = "AM 1500kHz";
    private Boolean isFM = true;
    private Boolean switchIsToggled = false;
    
    // 3D objects of the scene
    private Spatial turnButton;
    private Spatial actionSwitch;
    
    // TextBox of the scene
    private TextBox digitalDisplay;
    
    // Default text to be seen when scenario starts
    private String titleText = "La Modulation";
    private float titleTextSize = 0.5f;
    private ColorRGBA defaultTextColor = ColorRGBA.Green;
    
    // Signals emitters 
    private Node wirePcbEmitter = new Node();
    private Node carrierEmitter = new Node();
    private Node pcbAmpEmitter = new Node();
    private Node outputEmitter = new Node();
    
    // Geometry of the carrier signals
    private Spatial cubeCarrier;
    private Spatial pyramidCarrier;
    private Spatial dodecagoneCarrier; // Really...
    
    // Current carrier signal and his associated output
    private Spatial selectedCarrier;
    private Node outputSignal;
    
    //Pattern Geometry
    private Geometry micTapParticle;
    
    // this is PIIIIIII! (kick persian)
    private final float pi = (float) Math.PI;
    
    //Angle for test purposes
    private float trackableAngle = 0;
    private int direction = 1;
    
    //Variable for switch
    private float initAngleSwitch;
    private float tpfCumulSwitch = 0;
    private float tpfCumul = 0;
    private Quaternion rotationXSwitch = new Quaternion();   
    
    //Arrows
    private Arrows rotationArrow;
    private Arrows switchArrow;

    
    public Modulation(com.jme3.renderer.Camera Camera, Spatial destinationHandle) {
        
        super(Camera, destinationHandle);
        
        this.cam = Camera;
        this.destinationHandle = destinationHandle;
        this.needAutoGenIfMain = true;
        
        loadUnmovableObjects();
        loadMovableObjects();
        loadArrows();
    }
    
    @Override
    protected void loadUnmovableObjects() {
        
        scene = (Node) assetManager.loadModel("Models/Modulation/modulation.j3o");
        scene.setName("Modulation");
        this.attachChild(scene);
        
        scene.setLocalTranslation(new Vector3f(2.5f, 0.0f, 0.5f));
//        Quaternion rot = new Quaternion();
//        rot.fromAngleAxis(-pi / 2, Vector3f.UNIT_Y);
//        scene.setLocalRotation(rot);

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
        
        initParticlesEmitter(wirePcbEmitter, pathInHandle, pathIn, cam);
        initParticlesEmitter(carrierEmitter, pathCarrierHandle, pathCarrier, null);
        initParticlesEmitter(pcbAmpEmitter, pathOutChipHandle, pathOut, null);
        initPatternGenerator();
        
        scene.attachChild(outputEmitter);
        outputEmitter.setLocalTranslation(outputHandle.getLocalTranslation()); // TO DO: utiliser le object handle blender pour position
        //System.out.println("translation " + outputHandle.getLocalTranslation());
        outputEmitter.addControl(new DynamicWireParticleEmitterControl(this.destinationHandle, 3.5f, null));
        outputEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);

        // Set names for the emitters
        wirePcbEmitter.setName("WirePCBEmitter");
        carrierEmitter.setName("CarrierEmitter");
        pcbAmpEmitter.setName("PCBAmpEmitter");
        
        carrierEmitter.getControl(ParticleEmitterControl.class).registerObserver(this);
        wirePcbEmitter.getControl(ParticleEmitterControl.class).registerObserver(this);
        outputEmitter.getControl(ParticleEmitterControl.class).registerObserver(this.destinationHandle.getControl(ParticleEmitterControl.class));
        pcbAmpEmitter.getControl(ParticleEmitterControl.class).registerObserver(outputEmitter.getControl(ParticleEmitterControl.class));
    }
    
    @Override
    protected void loadMovableObjects() {
        turnButton = scene.getChild("Button");
        actionSwitch = scene.getChild("Switch");
        initAngleSwitch = actionSwitch.getLocalRotation().toAngleAxis(Vector3f.UNIT_X);
        
        Spatial[] geom = ModulationCommon.initCarrierGeometries();
        cubeCarrier = geom[0];
        pyramidCarrier = geom[1];
        dodecagoneCarrier = geom[2];
        
        carrierEmitter.getLocalTranslation().addLocal(new Vector3f(0.0f,cubeCarrier.getWorldScale().y,0.0f));
        pcbAmpEmitter.getLocalTranslation().addLocal(new Vector3f(0.0f,cubeCarrier.getWorldScale().y,0.0f));
                
        initOutputSignals();

        //Assign touchable
        touchable = new Node();//(Node) scene.getParent().getChild("Touchable")
        touchable.attachChild(actionSwitch);
        scene.attachChild(touchable);
        
    }
    
    // TODO Add the real output signals with a pattern generator
    private void initOutputSignals() {
        
        this.outputSignal = new Node();
        
        //cubeOutputSignal = cubeCarrier.clone();
        
        // Default value of the outputSignal
        this.outputSignal.attachChild(cubeCarrier.clone());

        //pyramidOutputSignal = pyramidCarrier.clone();

        //dodecagoneOutputSignal = dodecagoneCarrier.clone();
    }
    
    private void initParticlesEmitter(Node signalEmitter, Spatial handle, Geometry path, Camera cam) {
        
        scene.attachChild(signalEmitter);
        signalEmitter.setLocalTranslation(handle.getLocalTranslation()); // TO DO: utiliser le object handle blender pour position
        signalEmitter.addControl(new StaticWireParticleEmitterControl(path.getMesh(), 3.5f, cam));
        signalEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);
    }
    
    private void initPatternGenerator(){
        
        micTapParticle = ModulationCommon.initBaseGeneratorParticle();
        
        this.wirePcbEmitter.addControl(new PatternGeneratorControl(0.5f, micTapParticle, 10, ModulationCommon.minBaseParticleScale, 
                                                                   ModulationCommon.maxBaseParticleScale, true));
        this.waveTime = 1;
        this.particlePerWave = 4;
    }
    @Override
    protected void initTitleBox() {
        
        boolean lookAtCamera = false;
        boolean showDebugBox = false;
        float textBoxWidth = 5.2f;
        float textBoxHeight = 0.8f;
        
        ColorRGBA titleTextColor = new ColorRGBA(1f, 1f, 1f, 1f);
        ColorRGBA titleBackColor = new ColorRGBA(0.1f, 0.1f, 0.1f, 0.5f);
        TextBox titleTextBox = new TextBox(assetManager,
                titleText,
                titleTextSize,
                titleTextColor,
                titleBackColor,
                textBoxWidth,
                textBoxHeight,
                "titleText",
                BitmapFont.Align.Center.Center,
                showDebugBox,
                lookAtCamera);

        //move the text on the ground without moving
        Vector3f titleTextPosition = new Vector3f(0f, 0.25f, 6f);
        titleTextBox.rotate((float) -Math.PI / 2, 0, 0);
        
        titleTextBox.move(titleTextPosition);
        this.attachChild(titleTextBox);
    }
    
    private void initDigitalDisplay() {

        // Default configuration of the digital display
        digitalDisplay = new TextBox(assetManager,
                sFM1061,
                titleTextSize,
                defaultTextColor,
                new ColorRGBA(0.1f, 0.1f, 0.1f, 0.0f),
                3.5f, 1.0f,
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
        rotZ.fromAngleAxis(-pi / 2, Vector3f.UNIT_X);
        digitalDisplay.rotate(rotY);
        digitalDisplay.rotate(rotZ);
        scene.attachChild(digitalDisplay);
    }
    
    @Override
    public void restartScenario() {
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
    public void signalEndOfPath(Geometry caller, float magnitude) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //Dynamic move
    private void checkModulationMode(float tpf) {
        if (switchIsToggled) {
            tpfCumulSwitch += 3 * tpf;
            switchRotation(isFM, tpfCumulSwitch);
            float currAngle = actionSwitch.getLocalRotation().toAngleAxis(Vector3f.UNIT_X);
            if (currAngle >= initAngleSwitch && currAngle <= (2 * pi - initAngleSwitch)) {
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
    
    private void changeModulation(int frequency, Boolean isFM, float tpf) {
        
        if (isFM) {
            switch (frequency) {
                case 1:
                    digitalDisplay.simpleUpdate(sFM1061, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    // System.out.println(sFM1061);
                    changeCarrierParticles(1, tpf);
                    break;
                case 2:
                    digitalDisplay.simpleUpdate(sFM969, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    // System.out.println(sFM977);
                    changeCarrierParticles(2, tpf);
                    break;
                case 3:
                    digitalDisplay.simpleUpdate(sFM1027, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    // System.out.println(sFM952);
                    changeCarrierParticles(3, tpf);
                    break;
                default:
                    digitalDisplay.simpleUpdate(sFM1061, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    // System.out.println(sFM1061);
                    changeCarrierParticles(1, tpf);
                    break;
            }
        } else {
            switch (frequency) {
                case 1:
                    digitalDisplay.simpleUpdate(sAM600, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    //  System.out.println(sAM697);
                    changeCarrierParticles(1, tpf);
                    break;
                case 2:
                    digitalDisplay.simpleUpdate(sAM800, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    //  System.out.println(sAM498);
                    changeCarrierParticles(2, tpf);
                    break;
                case 3:
                    digitalDisplay.simpleUpdate(sAM1500, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    //  System.out.println(sAM707);
                    changeCarrierParticles(3, tpf);
                    break;
                default:
                    digitalDisplay.simpleUpdate(sAM600, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    // System.out.println(sAM697);
                    changeCarrierParticles(1, tpf);
                    break;
            }
        }
    }
    
    private void changeOuputParticles(Spatial spatial, String emitterId) {
        
        if (spatial != null && emitterId.equals("CarrierEmitter")) {
            
            outputSignal.detachAllChildren();
            outputSignal.attachChild(spatial);
        }
    }
    
    private void changeCarrierParticles(int frequency, float tpf) {
        
        tpfCumul += tpf;
        
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
        
        if (carrierEmitter != null && tpfCumul >= 1.0f) {
            carrierEmitter.getControl(ParticleEmitterControl.class).emitParticle(selectedCarrier.clone());
            tpfCumul = 0;
        }
        
    }
    
    private void checkTrackableAngle(float trackableAngle, float tpf) {
        
        float stepRange = 2 * pi / 3;
        
        if (trackableAngle >= 0 && trackableAngle < stepRange) {
            turnTunerButton(trackableAngle);
            changeModulation(1, isFM, tpf);
        } else if (trackableAngle >= stepRange && trackableAngle < 2 * stepRange) {
            turnTunerButton(trackableAngle);
            changeModulation(2, isFM, tpf);
        } else if (trackableAngle >= 2 * stepRange && trackableAngle < 3 * stepRange) {
            turnTunerButton(trackableAngle);
            changeModulation(3, isFM, tpf);
        }
    }
    

    /**
     * Switches the FM/AM switch dynamically
     *
     * @param isFM
     * @param tpfCumul
     */
    private void switchRotation(boolean isFM, float tpfCumul) {
        if (!isFM) {
            rotationXSwitch.fromAngleAxis(angleRangeTwoPi(initAngleSwitch - tpfCumul), Vector3f.UNIT_X);
            actionSwitch.setLocalRotation(rotationXSwitch);
        } else {
            rotationXSwitch.fromAngleAxis(angleRangeTwoPi(-initAngleSwitch + tpfCumul), Vector3f.UNIT_X);
            actionSwitch.setLocalRotation(rotationXSwitch);
        }
    }

    //convert angle for range [0 ; 2pi]
    private float angleRangeTwoPi(float angle) {
        float resultat = 0;
        if (angle >= 0) {
            resultat = angle;
        } else {
            resultat = 2 * pi + angle;
        }
        return resultat;
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
                    }
                }
                break;
        }
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
            invRotScenario(trackableAngle + (pi / 2));
        }

        switchArrow.simpleUpdate(tpf);
        rotationArrow.simpleUpdate(tpf);
        
        checkTrackableAngle(trackableAngle, tpf);
        checkModulationMode(tpf);
        
        return false;
    }
    
    @Override
    protected Spatial getInputHandle() {
        return wirePcbEmitter;
    }

    /**
     * Called when receiving an event from the observable emitter
     *
     * @param spatial
     * @param notifierId
     */
    @Override
    public void emitterObserverUpdate(Spatial spatial, String notifierId) {
        
        if (notifierId.equals("CarrierEmitter")) {

            //System.out.println("I am in " + notifierId);
            changeOuputParticles(spatial, notifierId);
            
        } else if (notifierId.equals("WirePCBEmitter")) {

            //System.out.println("I am in " + notifierId);
            
            if (pcbAmpEmitter != null && spatial != null) {
                Node clone = (Node)outputSignal.clone();
                
                ModulationCommon.modulateFMorAM(clone, spatial, isFM);
                
                clone.attachChild(spatial);
                
                //System.out.println("Scaling : " + spatial.getLocalScale().toString());
                pcbAmpEmitter.getControl(ParticleEmitterControl.class).emitParticle(clone);
                clone.setUserData("CarrierShape", outputSignal.getChild(0).getName());
                clone.setUserData("isFM", isFM);
            }
            
        }
    }
    private void loadArrows() {
        switchArrow = new Arrows("touch", actionSwitch.getWorldTranslation(), assetManager, 1);
        LookAtCameraControl control = new LookAtCameraControl(Camera);
        switchArrow.addControl(control);
        this.attachChild(switchArrow);
        
        rotationArrow = new Arrows("rotation", null, assetManager, 10);
        this.attachChild(rotationArrow);
    }
	
    @Override
    protected void setAutoGenerationParticle(Geometry particle){
        this.micTapParticle = particle;
        this.wirePcbEmitter.getControl(PatternGeneratorControl.class).
            setBaseParticle(this.micTapParticle);
        
    }
	    
    /**
     * Remove hints, is called after touch occurs
     */
    public void removeHintImages()
    {
        switchArrow.getControl(FadeControl.class).setShowImage(false);
        switchArrow.resetTimeLastTouch();
    }
    
}
