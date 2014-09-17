/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.*;
//import com.galimatias.teslaradio.world.observer.ScenarioObserver;
import com.galimatias.teslaradio.world.observer.ParticleEmitReceiveLinker;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

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
    

    
    
    private AudioNode micro_sound;
    
    private Spatial micro;
    private TouchEffectEmitter touchEffectEmitter;
    private Spatial micHandleIn;

    private SignalEmitter MicWireEmitter;
    private Material electricParticleMat;

    private TextBox titleTextBox;
    private TextBox microphoneTextBox;

    private Vector3f micPosition;
    private Vector3f micHandleInPosition;
    
    //CHANGE THIS VALUE CHANGE THE PARTICULE BEHAVIOUR 
    //Setting the direction norms and the speed displacement to the trajectories
    private float VecDirectionNorms = 80f;
    private float SoundParticles_Speed = 50f;
    
    // Default text to be seen when scenario starts
    private String titleText = "La Capture du Son";
    private String microphoneText = "L'énergie acoustique contenue dans le son se transforme en énergie électrique grâce à la vibration de la bobine magnétique dans le microphone.";
    private float titleTextSize = 0.5f;
    private float secondaryTextSize = 0.25f;
    private float instrumentTextSize = 0.25f;
    private float microphoneTextSize = 0.25f;
    private ColorRGBA defaultTextColor = new ColorRGBA(1f, 0f, 1f, 1f);

    // Refresh hint values
    private float maxTimeRefreshHint = 30f;
    private float timeLastTouch = maxTimeRefreshHint;
       
    public SoundCapture(AssetManager assetManager, Camera Camera, ParticleEmitReceiveLinker particleLinker)
    {
        super(assetManager,Camera, particleLinker);
        
        loadUnmovableObjects();
        loadMovableObjects();
    }

    public SoundCapture(AssetManager assetManager)
    {
        this(assetManager, null/*, null*/);
    }

    /**
     * Loading the models from the asset manager and attaching it to the
     * Node containing the unmovable objects in the scene.
     */
    @Override
    protected void loadUnmovableObjects()
    {
        scene = (Node) assetManager.loadModel("Models/SoundCapture/micro.j3o");
        scene.setName("SoundCapture");
        this.attachChild(scene);
        
        touchable = new Node();//(Node) scene.getParent().getChild("Touchable");
        micro = scene.getParent().getChild("Boule_micro");
        micHandleIn = scene.getParent().getChild("Mic_Input_Handle");
        micPosition = micro.getWorldTranslation();
        micHandleInPosition = micHandleIn.getWorldTranslation();
        touchable.attachChild(micro);
        scene.attachChild(touchable);
        
        initAudio();
        initTextBox();

    }

    @Override
    public void loadMovableObjects()
    {
        initMicWireParticlesEmitter();
        initOnTouchEffect();

        this.attachChild(movableObjects);
    }

       
    private void initMicWireParticlesEmitter()
    {
 MicWireEmitter = new SignalEmitter(this);
        //MicWireEmitter = new SignalEmitter(curvedPath, electricParticle, electricParticle, 35f /*Speed*/, SignalType.Wire );
        this.attachChild(MicWireEmitter);
        MicWireEmitter.setLocalTranslation(micPosition.x, micPosition.y,micPosition.z); // TO DO: utiliser le object handle blender pour position

        Node micWire_node = (Node) scene.getParent().getChild("WirePath");
        Geometry micWire_geom = (Geometry) micWire_node.getChild("BezierCurve");
        Geometry tmpGeom = (Geometry)micWire_geom;//.scale(1/ScenarioManager.WORLD_SCALE_DEFAULT);
        MicWireEmitter.setWaves(tmpGeom.getMesh(), 0.25f, 3.5f);

        electricParticleMat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        electricParticleMat.setTexture("ColorMap", assetManager.loadTexture("Textures/Electric3.png"));

    }
    
    private void initOnTouchEffect() {
        /**
         * Will be used for the mic touch effect
         */
    }


    private void initAudio()
    {
        /**
         * Will be used for the mic touch effect
         */
        
        micro_sound = new AudioNode(assetManager, "Sounds/micro_sound.wav", false);
        micro_sound.setPositional(false);
        micro_sound.setLooping(false);
        micro_sound.setVolume(2);
        this.attachChild(micro_sound);

    }

    public void initTextBox()
    {
        boolean lookAtCamera = false;
        boolean showDebugBox = false;
        float textBoxWidth = 5.2f;
        float textBoxHeight = 0.8f;
        
        ColorRGBA titleTextColor = new ColorRGBA(1f, 1f, 1f, 1f);
        ColorRGBA titleBackColor = new ColorRGBA(0.1f, 0.1f, 0.1f, 0.5f);
        titleTextBox = new TextBox(assetManager, titleText, titleTextSize, titleTextColor, titleBackColor, textBoxWidth, textBoxHeight, "titleText", BitmapFont.Align.Center, showDebugBox, lookAtCamera);
        
        //move the text on the ground without moving
        Vector3f titleTextPosition = new Vector3f(0f, 0.25f, 6f);
        titleTextBox.rotate((float)-Math.PI/2, 0, 0);
        
        titleTextBox.move(titleTextPosition);

        float micTextBoxWidth = 6f;
        float micTextBoxHeight = 1.2f;
        ColorRGBA micTextBackColor = new ColorRGBA(0.2f, 0.2f, 0.2f, 0.5f);
        
        microphoneTextBox = new TextBox(assetManager, microphoneText, secondaryTextSize, defaultTextColor, micTextBackColor, micTextBoxWidth, micTextBoxHeight, "instrumentText", BitmapFont.Align.Center, showDebugBox, lookAtCamera);
        
        //move the text on the ground without moving
        Vector3f microphoneTextPosition = new Vector3f(0f, 0.25f, 3.5f);
        microphoneTextBox.rotate((float)-Math.PI/2, 0, 0);
        
        microphoneTextBox.move(microphoneTextPosition);

        touchable.attachChild(titleTextBox);
        touchable.attachChild(microphoneTextBox);
    }
    
    public void microTouchEffect()
    {
        
        //DrumSoundEmitter.emitParticles(1.0f);
        //DrumSoundEmitter.emitWaves();
        MicWireEmitter.emitParticles(3.0f);

        //touchEffectEmitter.isTouched();

        micro_sound.playInstance();

    }
    
    public void textBoxesUpdate(Vector3f upVector)
    {
        titleTextBox.simpleUpdate(null, 0.0f, null, this.Camera, upVector);
        microphoneTextBox.simpleUpdate(null, 0.0f, null, this.Camera, upVector);  
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
                    for (int i = 0; i < results.size(); i++) {
                        // For each hit, we know distance, impact point, name of geometry.
                        float dist = results.getCollision(i).getDistance();
                        Vector3f pt = results.getCollision(i).getContactPoint();
                        String hit = results.getCollision(i).getGeometry().getName();

                        //Log.e(TAG, "  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
                    }

                    // 5. Use the results (we mark the hit object)
                    if (results.size() > 0)
                    {

                        // The closest collision point is what was truly hit:
                        CollisionResult closest = results.getClosestCollision();

                        Spatial touchedGeometry = closest.getGeometry();
                        String nameToCompare = touchedGeometry.getParent().getName();

                        if (nameToCompare.equals(micro.getName()))
                        {
                            this.microTouchEffect();
                        }
                        else if (nameToCompare.equals(microphoneTextBox.getName()))
                        {
                            showInformativeMenu = true;
                        }

                }
            }
        }
    }

    @Override
    public void restartScenario() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public boolean simpleUpdate(float tpf) {

        MicWireEmitter.simpleUpdate(tpf, this.Camera);
        //touchEffectEmitter.simpleUpdate(tpf);
        
        if(Camera != null) {
            Vector3f upVector = this.getLocalRotation().mult(Vector3f.UNIT_Y);
            textBoxesUpdate(upVector);
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
    public Vector3f getParticleReceiverHandle(){

        return micHandleIn.getWorldTranslation();
    }

    @Override
    public void sendSignalToEmitter(Geometry newSignal, float magnitude) {

        if (MicWireEmitter != null){
            // We have a new material out there! Since the Signal is now becoming "Electrical", we set the Electrical material to it
            newSignal.setMaterial(electricParticleMat);
            MicWireEmitter.prepareEmitParticles(newSignal, magnitude);
        }
    }
}
