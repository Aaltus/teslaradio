package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.DynamicWireParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.ParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.StaticWireParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.TextBox;
import com.galimatias.teslaradio.world.observer.EmitterObserver;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.*;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;

/**
 * Created by Batcave on 2014-09-09.
 */
public final class Modulation extends Scenario implements EmitterObserver {
    
    private final static String TAG = "Modulation";
    /**
     * TODO Remove this bool and associated code in simpleUpdate when it works
     * on Android. Only for debug purposes.
     */
    private final static boolean DEBUG_ANGLE = true;
    // Values displayed on the digital screen of the PCB 3D object
    private final String sFM1061 = "106.1 FM";
    private final String sFM977 = "97.7 FM";
    private final String sFM952 = "95.2 FM";
    private final String sAM697 = "697 AM";
    private final String sAM498 = "498 AM";
    private final String sAM707 = "707 AM";
    private Boolean isFM = true;
    private Boolean switchIsToggled = false;
    
    // 3D objects of the scene
    private Spatial turnButton;
    private Spatial actionSwitch;
    
    // TextBox of the scene
    private TextBox titleTextBox;
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
    private Spatial destinationHandle;
    
    // Handles for the emitter positions
    private Spatial pathInHandle;
    private Spatial pathCarrierHandle;
    private Spatial pathOutChipHandle;
    private Spatial outputHandle;
    
    // Paths
    private Geometry pathIn;
    private Geometry pathCarrier;
    private Geometry pathOut;
    
    // Geometry of the carrier signals
    private Geometry cubeCarrier;
    private Geometry pyramidCarrier;
    private Geometry dodecagoneCarrier; // Really...
    
    // Output signals
    private Geometry cubeOutputSignal;
    private Geometry pyramidOutputSignal;
    private Geometry dodecagoneOutputSignal;
    private Geometry outSpatial;
    
    // Current carrier signal and his associated output
    private Geometry currentCarrier;
    private Node outputSignal;
    
    // this is PIIIIIII! (kick persian)
    private final float pi = (float) Math.PI;
    
    //Angle for test purposes
    private float trackableAngle = 0;
    private int direction = 1;
    
    //Variable for switch
    private float initAngleSwitch;
    private float tpfCumul = 0;
    private Quaternion rotationXSwitch = new Quaternion();
    
    public Modulation(com.jme3.renderer.Camera Camera, Spatial destinationHandle) {
        
        super(Camera, destinationHandle);
        
        this.cam = Camera;
        this.destinationHandle = destinationHandle;
        
        loadUnmovableObjects();
        loadMovableObjects();
    }
    
    @Override
    protected void loadUnmovableObjects() {
        
        scene = (Node) assetManager.loadModel("Models/Modulation/modulation.j3o");
        scene.setName("Modulation");
        this.attachChild(scene);
        
        scene.setLocalTranslation(new Vector3f(2.5f, 0.0f, 0.5f));
        Quaternion rot = new Quaternion();
        rot.fromAngleAxis(-pi / 2, Vector3f.UNIT_Y);
        scene.setLocalRotation(rot);

        // Get the handles of the emitters
        pathInHandle = scene.getChild("Handle.In");
        pathCarrierHandle = scene.getChild("Handle.Generator");
        pathOutChipHandle = scene.getChild("Handle.Chip.Out");
        outputHandle = scene.getChild("Handle.Out");

        // Get the different paths
        Node wirePcb_node = (Node) scene.getChild("Path.In.Object");
        pathIn = (Geometry) wirePcb_node.getChild("Path.In.Nurbs");
        Node carrier_node = (Node) scene.getChild("Path.Generator.Object");
        pathCarrier = (Geometry) carrier_node.getChild("Path.Generator.Nurbs");
        Node pcbAmp_node = (Node) scene.getChild("Path.Out.Object");
        pathOut = (Geometry) pcbAmp_node.getChild("Path.Out.Nurbs");        
        
        initDigitalDisplay();
        initTitleBox();
        
        initParticlesEmitter(wirePcbEmitter, pathInHandle, pathIn, cam);
        initParticlesEmitter(carrierEmitter, pathCarrierHandle, pathCarrier, null);
        initParticlesEmitter(pcbAmpEmitter, pathOutChipHandle, pathOut, null);
        
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
    public void loadMovableObjects() {
        turnButton = scene.getChild("Button");
        actionSwitch = scene.getChild("Switch");
        initAngleSwitch = actionSwitch.getLocalRotation().toAngleAxis(Vector3f.UNIT_X);
        
        initCarrierGeometries();
        initOutputSignals();

        //Assign touchable
        touchable = new Node();//(Node) scene.getParent().getChild("Touchable")
        touchable.attachChild(actionSwitch);
        scene.attachChild(touchable);
        
    }
    
    private void initCarrierGeometries() {
        
        Box cube = new Box(0.25f, 0.25f, 0.25f);
        cubeCarrier = new Geometry("CubeCarrier", cube);
        Material mat1 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", new ColorRGBA(1, 0, 1, 0.25f));
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        cubeCarrier.setMaterial(mat1);
        cubeCarrier.setQueueBucket(RenderQueue.Bucket.Translucent);
        
        Dome pyramid = new Dome(2, 4, 0.25f);
        pyramidCarrier = new Geometry("PyramidCarrier", pyramid);
        pyramidCarrier.setMaterial(mat1);
        pyramidCarrier.setQueueBucket(queueBucket.Translucent);
        
        Node dodecagone = (Node) assetManager.loadModel("Models/Modulation/Dodecahedron.j3o");
        dodecagoneCarrier = (Geometry) dodecagone.getChild("Solid.0041");
        dodecagoneCarrier.setName("DodecagoneCarrier");
        dodecagoneCarrier.setMaterial(mat1);
        dodecagoneCarrier.setQueueBucket(queueBucket.Translucent);
    }

    // TODO Add the real output signals with a pattern generator
    private void initOutputSignals() {
        
        this.outputSignal = new Node();
        
        Box cube = new Box(0.25f, 0.25f, 0.25f);
        cubeOutputSignal = new Geometry("CubeOutputSignal", cube);
        Material mat1 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", new ColorRGBA(1, 0, 1, 0.25f));
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        cubeOutputSignal.setMaterial(mat1);
        cubeOutputSignal.setQueueBucket(RenderQueue.Bucket.Translucent);
        
        // Default value of the outputSignal
        this.outputSignal.attachChild(cubeOutputSignal);
        
        Dome pyramid = new Dome(2, 4, 0.25f);
        pyramidOutputSignal = new Geometry("PyramidOutputSignal", pyramid);
        pyramidOutputSignal.setMaterial(mat1);
        pyramidOutputSignal.setQueueBucket(RenderQueue.Bucket.Translucent);
        
        Node dodecagone = (Node) assetManager.loadModel("Models/Modulation/Dodecahedron.j3o");
        dodecagoneOutputSignal = (Geometry) dodecagone.getChild("Solid.0041");
        dodecagoneOutputSignal.setName("DodecagoneOutputSignal");
        dodecagoneOutputSignal.setMaterial(mat1);
        dodecagoneOutputSignal.setQueueBucket(RenderQueue.Bucket.Translucent);
        
        Material mat2 = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setTexture("ColorMap", assetManager.loadTexture("Textures/Sound.png"));
        mat2.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        Sphere sphere = new Sphere(5, 5, 0.25f);
        outSpatial = new Geometry("ModulationOutput",sphere);
        outSpatial.setMaterial(mat2);
    }
    
    private void initParticlesEmitter(Node signalEmitter, Spatial handle, Geometry path, Camera cam) {
        
        scene.attachChild(signalEmitter);
        signalEmitter.setLocalTranslation(handle.getLocalTranslation()); // TO DO: utiliser le object handle blender pour position
        signalEmitter.addControl(new StaticWireParticleEmitterControl(path.getMesh(), 3.5f, cam));
        signalEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);
    }
    
    private void initTitleBox() {
        
        boolean lookAtCamera = false;
        boolean showDebugBox = false;
        float textBoxWidth = 5.2f;
        float textBoxHeight = 0.8f;
        
        ColorRGBA titleTextColor = new ColorRGBA(1f, 1f, 1f, 1f);
        ColorRGBA titleBackColor = new ColorRGBA(0.1f, 0.1f, 0.1f, 0.5f);
        titleTextBox = new TextBox(assetManager,
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
        displayPosition = displayPosition.add(-0.4f, 0.15f, 0.0f);
        
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
            tpfCumul = tpfCumul + 3 * tpf;
            switchRotation(isFM, tpfCumul);
            // switchRotationWithoutDynamicSwitch(isFM);
            float currAngle = actionSwitch.getLocalRotation().toAngleAxis(Vector3f.UNIT_X);
            if (currAngle >= initAngleSwitch && currAngle <= (2 * pi - initAngleSwitch)) {
                switchIsToggled = false;
                tpfCumul = 0;
            }
        }
    }
    
    public void toggleModulationMode() {
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
                    digitalDisplay.simpleUpdate(sFM1061, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    // System.out.println(sFM1061);
                    changeCarrierParticles(1);
                    break;
                case 2:
                    digitalDisplay.simpleUpdate(sFM977, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    // System.out.println(sFM977);
                    changeCarrierParticles(2);
                    break;
                case 3:
                    digitalDisplay.simpleUpdate(sFM952, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    // System.out.println(sFM952);
                    changeCarrierParticles(3);
                    break;
                default:
                    digitalDisplay.simpleUpdate(sFM1061, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    // System.out.println(sFM1061);
                    changeCarrierParticles(1);
                    break;
            }
        } else {
            switch (frequency) {
                case 1:
                    digitalDisplay.simpleUpdate(sAM697, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    //  System.out.println(sAM697);
                    changeCarrierParticles(1);
                    break;
                case 2:
                    digitalDisplay.simpleUpdate(sAM498, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    //  System.out.println(sAM498);
                    changeCarrierParticles(2);
                    break;
                case 3:
                    digitalDisplay.simpleUpdate(sAM707, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    //  System.out.println(sAM707);
                    changeCarrierParticles(3);
                    break;
                default:
                    digitalDisplay.simpleUpdate(sAM697, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    // System.out.println(sAM697);
                    changeCarrierParticles(1);
                    break;
            }
        }
    }
    
    private void changeOuputParticles(Spatial spatial, String emitterId) {
        
        if (spatial != null && emitterId.equals("CarrierEmitter")) {
            
            String presentCarrierTypeName = spatial.getName();
            
            outputSignal.detachAllChildren();
                    
            if (presentCarrierTypeName.equals("CubeCarrier")) {
                //outputSignal = cubeOutputSignal;
                outputSignal.attachChild(cubeOutputSignal);
                outputSignal.attachChild(outSpatial);
                
            } else if (presentCarrierTypeName.equals("PyramidCarrier")) {
                //outputSignal = pyramidOutputSignal;
                outputSignal.attachChild(pyramidOutputSignal);
                outputSignal.attachChild(outSpatial);
                
            } else if (presentCarrierTypeName.equals("DodecagoneCarrier")) {
                //outputSignal = dodecagoneOutputSignal;
                outputSignal.attachChild(dodecagoneOutputSignal);
                outputSignal.attachChild(outSpatial);     
            }
        }
    }
    
    private void changeCarrierParticles(int frequency) {
        
        switch (frequency) {
            case 1:
                currentCarrier = cubeCarrier;
                break;
            case 2:
                currentCarrier = pyramidCarrier;
                break;
            case 3:
                currentCarrier = dodecagoneCarrier;
                break;
        }
        
        if (carrierEmitter != null) {
            carrierEmitter.getControl(ParticleEmitterControl.class).emitParticle(currentCarrier);
        }
        
    }
    
    private void checkTrackableAngle(float trackableAngle) {
        
        float stepRange = 2 * pi / 3;
        
        if (trackableAngle >= 0 && trackableAngle < stepRange) {
            turnTunerButton(trackableAngle);
            changeModulation(1, isFM);
        } else if (trackableAngle >= stepRange && trackableAngle < 2 * stepRange) {
            turnTunerButton(trackableAngle);
            changeModulation(2, isFM);
        } else if (trackableAngle >= 2 * stepRange && trackableAngle < 3 * stepRange) {
            turnTunerButton(trackableAngle);
            changeModulation(3, isFM);
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
    
    private void switchRotationWithoutDynamicSwitch(boolean isFM) {
        if (!isFM) {
            rotationXSwitch.fromAngleAxis(initAngleSwitch, Vector3f.UNIT_X);
            actionSwitch.setLocalRotation(rotationXSwitch);
        } else {
            rotationXSwitch.fromAngleAxis(-initAngleSwitch, Vector3f.UNIT_X);
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
    public boolean simpleUpdate(float tpf) {
        
        if (DEBUG_ANGLE) {
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
        checkModulationMode(tpf);
        
        return false;
    }
    
    @Override
    public Spatial getInputHandle() {
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
                outputSignal.setLocalScale(spatial.getLocalScale());
                //System.out.println("The spatial scale : " + spatial.getWorldScale().toString());
                pcbAmpEmitter.getControl(ParticleEmitterControl.class).emitParticle(outputSignal.clone());
            }
            
        }
    }
}
