/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.Arrows;
import com.galimatias.teslaradio.world.effects.FadeControl;
import com.galimatias.teslaradio.world.effects.LookAtCameraControl;
import com.galimatias.teslaradio.world.effects.ParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.PatternGeneratorControl;
import com.galimatias.teslaradio.world.effects.StaticWireParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.TextBox;
import com.galimatias.teslaradio.world.observer.AutoGenObserver;
import com.galimatias.teslaradio.world.observer.EmitterObserver;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.input.event.TouchEvent;
import static com.jme3.input.event.TouchEvent.Type.DOWN;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

/**
 *
 * @author Barliber
 */

public class Demodulation extends Scenario implements EmitterObserver, AutoGenObserver  {

    
    // 3D objects of the scene
    private Spatial demodulationButton;
    private Spatial actionSwitch;
    private Spatial peg;
    
    // TextBox of the scene
    private TextBox titleTextBox;

    // Default text to be seen when scenario starts
    private String titleText = "La Démodulation";
    
     //Variable for switch
    private Quaternion initAngleSwitch = new Quaternion();
    private Quaternion endAngleSwitch = new Quaternion();
    private float stepAngleSwitch = 0;
    private float tpfCumulSwitch = 0;
    private float tpfCumulButton = 0;

    
    private Boolean isFM = true;
    private Boolean switchIsToggled = false;
    
    //-----------------Particles----------------------------------------------
     // Signals emitters 
    private Node inputModule = new Node();
    private Node inputDemodulation = new Node();
    
    // Handles for the emitter positions
    private Spatial pathInputPeg;
    private Spatial pathOutputPeg; // and Input demodulateur
        
    // Paths
    private Geometry inputPegPath;
    private Geometry outputPegPath;
    
    private Node autoGenParticle;
    
    private Node cubeSignal;
    private Node pyramidSignal;
    private Node dodecagoneSignal;
    
  
    // this is PIIIIIII! (kick persian)
    private final float pi = (float) Math.PI;
    
    //Angle for test purposes
    private float trackableAngle = 0;
    
    private String pegFilter = "";
    private float stepRangePeg = 2 * pi / 3;

    //arrows
    private Arrows switchArrow;
    private Arrows rotationArrow;
	
    
    
    public Demodulation(ScenarioCommon sc,com.jme3.renderer.Camera Camera, Spatial destinationHandle){
        super(sc,Camera, destinationHandle,"Sounds/demodulation.ogg");
        
        this.cam = Camera;
        this.destinationHandle = destinationHandle;

        loadUnmovableObjects();
        loadMovableObjects();   
        loadArrows();
        
        
        
    }
    
    
    @Override
    protected void loadUnmovableObjects() {
        scene = (Node) assetManager.loadModel("Models/Demodulation/Demodulation.j3o");
        scene.setName("Demodulation");
        this.attachChild(scene);
        
        //scene rotation
        scene.setLocalTranslation(new Vector3f(1.2f, 0.0f, 1.3f));
        Quaternion rot = new Quaternion();
        rot.fromAngleAxis(-pi/2, Vector3f.UNIT_Y);
        scene.setLocalRotation(rot);
        
        initTitleBox();
                
        pathInputPeg = scene.getChild("Handle.In");
        pathOutputPeg = scene.getChild("Path.Out.Object");
        
         // Get the different paths
        Node wireInputPeg_node = (Node) scene.getChild("Path.In.Object");
        inputPegPath = (Geometry) wireInputPeg_node.getChild("Path.In.Nurbs");
        Node wireOutputPeg_node = (Node) scene.getChild("Path.Out.Object");
        outputPegPath = (Geometry) wireOutputPeg_node.getChild("Path.Out.Nurbs");
       
        initParticlesEmitter(inputModule, pathInputPeg, inputPegPath, null);
        initParticlesEmitter(inputDemodulation, pathOutputPeg, outputPegPath, null);
        initModulatedParticles();
        
        // Set names for the emitters  // VOir si utile dans ce module
        inputModule.setName("InputModule");
        inputDemodulation.setName("InputDemodulation");

        inputModule.getControl(ParticleEmitterControl.class).registerObserver(this);
        
         this.getInputHandle().addControl(new PatternGeneratorControl(0.5f, autoGenParticle.clone(), 7, 0.25f, 2f, true));
        
    }

    @Override
    protected void loadMovableObjects() {
        demodulationButton = scene.getChild("Button");
        actionSwitch = scene.getChild("Switch");
        peg = scene.getChild("Circle");
        initAngleSwitch.fromAngleAxis(0.45f, Vector3f.UNIT_X);
        endAngleSwitch.fromAngleAxis(-0.45f, Vector3f.UNIT_X);
        //Assign touchable
        touchable = new Node();//(Node) scene.getParent().getChild("Touchable")
        touchable.attachChild(actionSwitch);
        scene.attachChild(touchable);
    }
    
    //Dynamic move
    private void checkModulationMode(float tpf) {
        if (switchIsToggled) {
            tpfCumulSwitch += tpf;
            stepAngleSwitch = tpfCumulSwitch/0.35f;
            switchRotation(isFM, stepAngleSwitch);
            Quaternion currAngle = actionSwitch.getLocalRotation();
            System.out.println("currAngle  " + currAngle.getX() + "initAngle  " + initAngleSwitch + "isFM  " + isFM);
            if (stepAngleSwitch >= 1) {
                System.out.println("in  " + stepAngleSwitch);
                switchIsToggled = false;
                tpfCumulSwitch = 0;
            }
        }
    }
    
    private void initParticlesEmitter(Node signalEmitter, Spatial handle, Geometry path, Camera cam) {
        scene.attachChild(signalEmitter);
        signalEmitter.setLocalTranslation(handle.getLocalTranslation()); // TO DO: utiliser le object handle blender pour position
        signalEmitter.addControl(new StaticWireParticleEmitterControl(path.getMesh(), 3.5f, cam));
        signalEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);
    }
    
    private void switchRotation(boolean isFM, float tpfCumul) {
        Quaternion currRotation = new Quaternion();
        if (!isFM) {
            //rotationXSwitch.fromAngleAxis(initAngleSwitch.subtractLocal(new Quaternion(tpfCumul, 0, 0, 0));
            //actionSwitch.initAngleSwitch.subtractLocal(new Quaternion(tpfCumul, 0, 0, 0)));
            actionSwitch.setLocalRotation(currRotation.slerp(initAngleSwitch, endAngleSwitch, tpfCumul));
        } else {
            //rotationXSwitch.fromAngleAxis(angleRangeTwoPi(-initAngleSwitch + tpfCumul), Vector3f.UNIT_X);
            actionSwitch.setLocalRotation(currRotation.slerp(endAngleSwitch, initAngleSwitch, tpfCumul)); 
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

    public void toggleModulationMode() {
        removeHintImages();
        if (!switchIsToggled) {
            isFM = !isFM;
            switchIsToggled = true;
        }
        this.autoGenObserverUpdate(this.autoGenParticle.getChild(0), isFM);
    }

    @Override
    public void restartScenario() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
       checkModulationMode(tpf);
       switchArrow.simpleUpdate(tpf);
       rotationArrow.simpleUpdate(tpf);
       
        if (this.DEBUG_ANGLE) {
            tpfCumulButton = tpf+ tpfCumulButton;
            rotationAxeY(tpfCumulButton, demodulationButton);

            if (tpfCumulButton > 2*pi) {
                tpfCumulButton = 0;
            }

            checkTrackableAngle(tpfCumulButton); // rotation of PEG
        }
        else {
            trackableAngle = this.getUserData("angleX");
            rotationAxeY(trackableAngle, demodulationButton);
            checkTrackableAngle(trackableAngle); // rotation of PEG
            invRotScenario(trackableAngle + (pi / 2));
        }
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
        return inputModule;
    }

    @Override
    public void signalEndOfPath(Geometry caller, float magnitude) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void emitterObserverUpdate(Spatial spatial, String notifierId) {
         if (notifierId.equals("InputModule")) {
             if(spatial.getUserData("CarrierShape").equals(pegFilter) && (spatial.getUserData("isFM") == isFM)){
                 inputDemodulation.getControl(ParticleEmitterControl.class).emitParticle(spatial);
             }
         }
    }
    
    private void rotationAxeY(float ZXangle, Spatial object) { 
        Quaternion rot = new Quaternion();
        rot.fromAngleAxis(ZXangle, Vector3f.UNIT_Y);
        object.setLocalRotation(rot);
    }
    
    private void checkTrackableAngle(float trackableAngle) {
        //stepRangePeg définie  en vraiable d'instance
        float stepPeg = 0;
        pegFilter = "CubeCarrier";
        if (trackableAngle >= stepRangePeg && trackableAngle < 2 * stepRangePeg) {
            stepPeg = stepRangePeg;
            pegFilter = "PyramidCarrier";
        } else if (trackableAngle >= 2 * stepRangePeg && trackableAngle < 3 * stepRangePeg) {
            stepPeg = 2 * stepRangePeg;
            pegFilter = "DodecagoneCarrier";
        }
        rotationAxeY(stepPeg, peg);
    }
    
    @Override
    protected void initTitleBox() {
        titleTextBox = new TextBox(assetManager, 
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
    
   private void initModulatedParticles(){
        Geometry baseGeom = scenarioCommon.initBaseGeneratorParticle();
        Spatial[] carrier = ScenarioCommon.initCarrierGeometries();
                
        this.cubeSignal = new Node();
        this.cubeSignal.attachChild(carrier[0].clone());
        scenarioCommon.modulateFMorAM(this.cubeSignal, baseGeom, isFM);
        this.cubeSignal.attachChild(baseGeom.clone());
        this.cubeSignal.setUserData("CarrierShape", this.cubeSignal.getChild(0).getName());
        this.cubeSignal.setUserData("isFM", isFM);
        
       
        this.pyramidSignal = new Node();
        this.pyramidSignal.attachChild(carrier[1].clone());
        scenarioCommon.modulateFMorAM(this.pyramidSignal, baseGeom, isFM);
        this.pyramidSignal.attachChild(baseGeom.clone());
        this.pyramidSignal.setUserData("CarrierShape", this.pyramidSignal.getChild(0).getName());
        this.pyramidSignal.setUserData("isFM", isFM);
       
        this.dodecagoneSignal = new Node();
        this.dodecagoneSignal.attachChild(carrier[2].clone());
        scenarioCommon.modulateFMorAM(this.dodecagoneSignal, baseGeom, isFM);
        this.dodecagoneSignal.attachChild(baseGeom.clone());
        this.dodecagoneSignal.setUserData("CarrierShape", this.dodecagoneSignal.getChild(0).getName());
        this.dodecagoneSignal.setUserData("isFM", isFM);

        this.autoGenParticle = this.cubeSignal;
       
    }

    @Override
    public void autoGenObserverUpdate(Spatial newCarrier, boolean isFm) {
        this.isFM = isFm;
        this.initModulatedParticles();
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
    
    private void loadArrows() {
        switchArrow = new Arrows("touch", actionSwitch.getLocalTranslation(), assetManager, 1);
        LookAtCameraControl control = new LookAtCameraControl(cam);
        switchArrow.addControl(control);
        scene.attachChild(switchArrow);
        
        rotationArrow = new Arrows("rotation", null, assetManager, 10);
        this.attachChild(rotationArrow);
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
               

