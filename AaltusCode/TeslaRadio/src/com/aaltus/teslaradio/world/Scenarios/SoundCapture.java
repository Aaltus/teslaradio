/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaltus.teslaradio.world.Scenarios;

import com.ar4android.vuforiaJME.AppGetter;
import com.aaltus.teslaradio.world.effects.Arrows;
import com.aaltus.teslaradio.world.effects.DynamicWireParticleEmitterControl;
import com.aaltus.teslaradio.world.effects.FadeControl;
import com.aaltus.teslaradio.world.effects.LookAtCameraControl;
import com.aaltus.teslaradio.world.effects.ParticleEmitterControl;
import com.aaltus.teslaradio.world.effects.StaticWireParticleEmitterControl;
import com.aaltus.teslaradio.world.effects.PatternGeneratorControl;
import com.aaltus.teslaradio.world.effects.SoundControl;
import com.aaltus.teslaradio.world.effects.TextBox;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ResourceBundle;

//import com.galimatias.teslaradio.world.observer.ScenarioObserver;

//import com.galimatias.teslaradio.world.observer.ScenarioObserver;

/**
 *
 * @author Alexandre Hamel
 * 
 * This class contains all the models and the animations related to sound capture
 * 
 */
public final class SoundCapture extends Scenario {

    private final static String TAG = "SoundCapture";
    
    private Spatial micro;
    private Spatial micHandleIn;

    private Spatial micTapParticle;
    
    // Emitters of the scenario
    private Node micWireEmitter;
    private Node wireDestinationEmitter;

    private TextBox titleTextBox;
    private TextBox microphoneTextBox;

    private Vector3f micPosition;
    private Vector3f micHandleInPosition;
    
    // Default text to be seen when scenario starts
    //private String titleText = ResourceBundle.getBundle("com.galimatias.teslaradio.world.Scenarios.Bundle").getString("sound_capture_title");//"La Capture du Son";
    private String titleText = AppGetter.getResourceBundle().getString("sound_capture_title");//"La Capture du Son";
    
    
    //Arrows
    private Node micArrow;
    private Node moveArrow;
       
    public SoundCapture(ScenarioCommon sc,Camera Camera, Spatial destinationHandle)
    {
        super(sc,Camera, destinationHandle);
        this.setName("SoundCapture");
        this.needAutoGenIfMain = true;
        
        loadUnmovableObjects();
        loadMovableObjects();
        loadArrows();
    }

    /**
     * Loading the models from the asset manager and attaching it to the
     * Node containing the unmovable objects in the scene.
     */
    @Override
    protected void loadUnmovableObjects()
    {
        scene = (Node) assetManager.loadModel("Models/SoundCapture/Micro.j3o");
        scene.setName("SoundCapture");
        this.attachChild(scene);
        
        touchable = new Node();//(Node) scene.getParent().getChild("Touchable");
        micro = scene.getParent().getChild("Boule_micro");
        micHandleIn = scene.getParent().getChild("Mic_Input_Handle");
        micPosition = micro.getWorldTranslation();
        micHandleInPosition = micHandleIn.getWorldTranslation();
        touchable.attachChild(micro);
        scene.attachChild(touchable);
        
        //initTitleBox();

    }

    @Override
    protected void loadMovableObjects()
    {
        initMicWireParticlesEmitter();
        
        this.spotlight = ScenarioCommon.spotlightFactory();

        this.attachChild(movableObjects);
    }

    @Override
    protected void onFirstNodeActions() {
        super.onFirstNodeActions();
        
        this.detachChild(moveArrow);
    }
    
    @Override
    protected void onSecondNodeActions() {
        super.onSecondNodeActions();
        
        this.attachChild(moveArrow);
    }

       
    private void initMicWireParticlesEmitter()
    {
        micWireEmitter = new Node();
        micWireEmitter.setLocalTranslation(micPosition.x, micPosition.y,micPosition.z); // TO DO: utiliser le object handle blender pour position
        scene.attachChild(micWireEmitter);
        
        Node micWire_node = (Node) scene.getParent().getChild("WirePath");
        Geometry micWire_geom = (Geometry) micWire_node.getChild("BezierCurve");
        //Geometry tmpGeom = (Geometry)micWire_geom;//.scale(1/ScenarioManager.WORLD_SCALE_DEFAULT);
        
        micWireEmitter.addControl(new StaticWireParticleEmitterControl(micWire_geom.getMesh(), 3.5f, cam));
        Material mat1 = new Material(AppGetter.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", new ColorRGBA(1.0f,0.63f,0.0f,1.0f));
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        micWireEmitter.getControl(ParticleEmitterControl.class).setDefaultMaterial(mat1);
        //micWireEmitter.addControl(new SoundControl("Sounds/hit_mic.wav", false, 2));
        
        wireDestinationEmitter = new Node();
        wireDestinationEmitter.setName("WireDestinationEmitter");
        Spatial moduleHandleOut_node = scene.getParent().getChild("Module.Handle.Out");
        wireDestinationEmitter.setLocalTranslation(moduleHandleOut_node.getLocalTranslation());
        scene.attachChild(wireDestinationEmitter);
        
        //System.out.println(destinationHandle.getName());
        if(this.destinationHandle != null) {
            wireDestinationEmitter.addControl(new DynamicWireParticleEmitterControl(this.destinationHandle, 3.5f, cam, true));

            wireDestinationEmitter.getControl(ParticleEmitterControl.class).registerObserver(this.destinationHandle.getControl(ParticleEmitterControl.class));
            micWireEmitter.getControl(ParticleEmitterControl.class).registerObserver(wireDestinationEmitter.getControl(ParticleEmitterControl.class));

            wireDestinationEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);
            micWireEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);

            initPatternGenerator();
        }
    }
     
        
    private void initOnTouchEffect() {
        /**
         * Will be used for the mic touch effect
         */
    }


    protected void microTouchEffect() {
        removeHintImages();
        micWireEmitter.addControl(new SoundControl("Sounds/hit_mic.ogg", false, 2));
        micWireEmitter.getControl(PatternGeneratorControl.class).toggleNewWave(particlePerWave);
    }
    
    
    @Override
    public void onScenarioTouch(String name, TouchEvent touchEvent, float v) {

        switch(touchEvent.getType()){

            //Checking for down event is very responsive
            case DOWN:

            //case TAP:
                if (name.equals("Touch"))
                {

                    // 1. Reset results list.
                    CollisionResults results = new CollisionResults();

                    // 2. Mode 1: user touch location.
                    //Vector2f click2d = inputManager.getCursorPosition();

                    Vector2f click2d = new Vector2f(touchEvent.getX(),touchEvent.getY());
                    Vector3f click3d = Camera.getWorldCoordinates(
                            new Vector2f(click2d.x, click2d.y), 0f).clone();
                    Vector3f dir = Camera.getWorldCoordinates(
                            new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
                    Ray ray = new Ray(click3d, dir);

                    // 3. Collect intersections between Ray and Shootables in results list.
                    //focusableObjects.collideWith(ray, results);
                    touchable.collideWith(ray, results);

                    // 4. Print the results
                    //Log.d(TAG, "----- Collisions? " + results.size() + "-----");
                    //for (int i = 0; i < results.size(); i++) {
                        // For each hit, we know distance, impact point, name of geometry.
                        //float dist = results.getCollision(i).getDistance();
                        //Vector3f pt = results.getCollision(i).getContactPoint();
                        //String hit = results.getCollision(i).getGeometry().getName();

                        //Log.e(TAG, "  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
                    //}

                    // 5. Use the results (we mark the hit object)
                    if (results.size() > 0)
                    {

                        // The closest collision point is what was truly hit:
                        String nameToCompare =
                                results.getClosestCollision().getGeometry().getParent().getName();

                        if (nameToCompare.equals(micro.getName()))
                        {
                            this.microTouchEffect();
                            break;
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
    protected void initPatternGenerator() {
        this.initDrumGuitarSound();
        micTapParticle = scenarioCommon.initBaseGeneratorParticle();

        micTapParticle.setQueueBucket(RenderQueue.Bucket.Opaque);
        micWireEmitter.addControl(new PatternGeneratorControl(0.25f, micTapParticle, 10, scenarioCommon.minBaseParticleScale, 
                                                                  scenarioCommon.maxBaseParticleScale, true));
        micWireEmitter.getControl(PatternGeneratorControl.class).setEnabled(true);
    }
    
    @Override
    public void restartScenario() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    protected boolean simpleUpdate(float tpf) {

        //touchEffectEmitter.simpleUpdate(tpf);
        //micArrow.simpleUpdate(tpf);
        //moveArrow.simpleUpdate(tpf);
        
        if(Camera != null) {
            Vector3f upVector = this.getLocalRotation().mult(Vector3f.UNIT_Y);
        }
        
        if (this.emphasisChange) {
            objectEmphasis();
            this.emphasisChange = false;
        }

        if (showInformativeMenu)
        {
            showInformativeMenu = false;
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void onAudioEvent()
    {
        /**
         * Might use it for the mic effect
         */
    }

    @Override
    public void setGlobalSpeed(float speed)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void signalEndOfPath(Geometry caller, float magnitude) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Spatial getInputHandle() {
        return micWireEmitter;
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
        titleTextBox.rotate((float)-Math.PI/2, 0, 0);
        
        titleTextBox.move(titleTextPosition);

        touchable.attachChild(titleTextBox);
    }

    private void loadArrows() {
        micArrow = new Node();
        micArrow.move(micHandleInPosition);
        micArrow.addControl(new Arrows("touch",  assetManager, 3));
        LookAtCameraControl control = new LookAtCameraControl(Camera);
        micArrow.addControl(control);
        scene.attachChild(micArrow);
        
        moveArrow = new Node();
        moveArrow.addControl(new Arrows("move", assetManager, 10));
    }
    
        /**
     * Remove hints, is called after touch occurs
     */
    public void removeHintImages()
    {
        micArrow.getControl(FadeControl.class).setShowImage(false);
        micArrow.getControl(Arrows.class).resetTimeLastTouch();
    }

    @Override
    protected void objectEmphasis() {
        if (this.spotlight != null) {            
            switch(this.currentObjectToEmphasisOn) {
                // Attach on microphone
                case 0:
                    this.spotlight.setLocalTranslation(scene.getChild("Stand_micro").getLocalTranslation().add(0.0f,-scene.getChild("Stand_micro").getLocalTranslation().y,0.0f));
                    this.spotlight.setLocalScale(new Vector3f(5.0f,30.0f,5.0f));
                    scene.attachChild(this.spotlight);
                    break;
                default:
                    scene.detachChild(this.spotlight);
                    break;
            }
        }
    }
}
