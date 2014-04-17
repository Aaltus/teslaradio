/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.*;
import com.jme3.animation.*;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.*;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

/**
 *
 * @author Alexandre Hamel
 * 
 * This class contains all the models and the animations related to sound capture
 * 
 */
public final class SoundCapture extends Scenario {

    private final static String TAG = "SoundCapture";

    private AudioNode drum_sound;
    private AudioNode guitar_sound;

    private Spatial drum;
    private Spatial guitar;
    private Spatial micro;
    private Spatial circles;
    
    private Spatial drumHandleOut;
    private Spatial guitarHandleOut;
    private Spatial micHandleIn;
    
    private Halo halo_drum, halo_guitar;
    
    private SignalEmitter DrumSoundEmitter;
    private SignalEmitter GuitarSoundEmitter;
    private SignalEmitter MicWireEmitter;

    private TextBox textBox;
    
    // animation encore utile?
    private Animation animation;
    private AnimControl mAnimControl = new AnimControl();
    private AnimChannel mAnimChannel;

    private Vector<Vector3f> drum_trajectories = new Vector<Vector3f>();
    private Vector<Vector3f> guitar_trajectories = new Vector<Vector3f>();
    private Vector3f drumPosition;
    private Vector3f guitarPosition;
    private Vector3f micPosition;
    private Vector3f drumHandleOutPosition;
    private Vector3f guitarHandleOutPosition;
    private Vector3f micHandleInPosition;
    
    //CHANGE THIS VALUE CHANGE THE PARTICULE BEHAVIOUR 
    //Setting the direction norms and the speed displacement to the trajectories
    private float VecDirectionNorms = 80f;
    private float SoundParticles_Speed = 50f;
    
    // Default text to be seen when scenario starts
    private String defaultText = "This is the first module: \n Sound Capture";
    private float defaultTextSize = 0.5f;
    private ColorRGBA defaultTextColor = ColorRGBA.White;
    
    // Updated values of the textbox, the list contains the messages when updated
    private LinkedList<String> lstUpdatedText = new LinkedList<String>();
    private String updatedText = null;
    private float updatedTextSize = 0.0f;
    private ColorRGBA updatedTextColor = null;

    private boolean firstTry = true;
       
    public SoundCapture(AssetManager assetManager, Camera Camera)
    {
        super(assetManager);
        
        loadUnmovableObjects();
        loadMovableObjects();

        this.Camera = Camera;
    }

    public SoundCapture(AssetManager assetManager)
    {
        this(assetManager, null);
    }

    /**
     * Loading the models from the asset manager and attaching it to the
     * Node containing the unmovable objects in the scene.
     */
    @Override
    protected void loadUnmovableObjects()
    {          
        scene = (Node) assetManager.loadModel("Models/SoundCapture.j3o");
        scene.setName("SoundCapture");
        this.attachChild(scene);
        scene.scale(10.0f,10.0f,10.0f);
        
        touchable = (Node) scene.getParent().getChild("Touchable");
        drum = touchable.getParent().getChild("Tambour");
        guitar = touchable.getParent().getChild("Guitar");
        micro = scene.getParent().getChild("Boule_micro");
        guitarHandleOut = scene.getParent().getChild("Guitar_Output_Handle");
        drumHandleOut = scene.getParent().getChild("Drum_Output_Handle");
        micHandleIn = scene.getParent().getChild("Mic_Input_Handle");
        
        drumPosition = drum.getLocalTranslation(); //drum.getLocalTranslation();
        guitarPosition = guitar.getLocalTranslation(); //guitar.getWorldTranslation();
        micPosition = micro.getWorldTranslation();
        drumHandleOutPosition = drumHandleOut.getWorldTranslation();
        guitarHandleOutPosition = guitarHandleOut.getWorldTranslation();
        micHandleInPosition = micHandleIn.getWorldTranslation();
        
        initAudio();
        initTextBox();
        initHaloEffects();

    }

    /**
     * Loading the models from the asset manager and attaching it to the
     * Node containing the movable objects in the scene.
     */
    @Override
    protected void loadMovableObjects()
    {
        /**
         * TODO : Load the sound particules models
         */
        circles = assetManager.loadModel("Models/Effet_tambour.j3o");
        circles.setName("Circles");  
        //List<Vector3f> listPaths = new ArrayList<Vector3f>();
        //listPaths.add(new Vector3f(0,40,0));
        /* A colored lit cube. Needs light source! */ 
    }

    /**
     * Initialisation of the tambour effects
     */
    private void initDrumParticlesEmitter()
    {
        // Getting all the trajectories from the position of the mic-drums and 
        // the number of directions        
        Vector3f drumMicDirection = micHandleInPosition.subtract(drumHandleOutPosition);        
                        
        int totalNbDirections = 21;
        int nbXYDirections = 3;
        
        // Creating the trajectories
        SignalTrajectories directionFactory = new SignalTrajectories(totalNbDirections, nbXYDirections);
        directionFactory.setTrajectories(drumMicDirection, VecDirectionNorms);
        drum_trajectories = directionFactory.getTrajectories();
        
        // calculalate drum to mic path length
        Vector3f drum2MicVector = drumHandleOutPosition.subtract(micHandleInPosition);
        float drum2MicLength = drum2MicVector.length();
        
        // instantiate 3d Sound particul model
        Box rect = new Box(1.0f, 1.0f, 0.01f);
        Geometry soundParticle = new Geometry("particul",rect);
        Material soundParticul_mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        soundParticul_mat.setTexture("ColorMap", assetManager.loadTexture("Textures/Sound.png"));
        
        //soundParticul_mat.setColor("Color", ColorRGBA.Red);
        soundParticle.setMaterial(soundParticul_mat);
        Geometry soundParticleTranslucent = soundParticle.clone();
        soundParticleTranslucent.getMaterial().setTexture("ColorMap", assetManager.loadTexture("Textures/Sound_wAlpha.png"));
                
        DrumSoundEmitter = new SignalEmitter(drum_trajectories, drum2MicLength, soundParticle, soundParticleTranslucent, SoundParticles_Speed, SignalType.Air );
        this.attachChild(DrumSoundEmitter);
        DrumSoundEmitter.setLocalTranslation(drumHandleOutPosition); // TO DO: utiliser le object handle blender pour position

        //Set the impulsional response of the emitter
        ArrayList<Float> waveMagnitudes = new ArrayList(3);
        
        waveMagnitudes.add(5f);  
        waveMagnitudes.add(3f);
        waveMagnitudes.add(1f);
        
        DrumSoundEmitter.setWaves(waveMagnitudes, 0.25f);
    }
    
    /**
     * Initialisation of the drum effects
     */
    private void initGuitarParticlesEmitter()
    {
        // Getting all the trajectories from the position of the mic-drums and 
        // the number of directions        
        Vector3f guitarMicDirection = micHandleInPosition.subtract(guitarHandleOutPosition);        
                        
        int totalNbDirections = 21;
        int nbXYDirections = 3;
                
        // Creating the trajectories
        SignalTrajectories directionFactory = new SignalTrajectories(totalNbDirections, nbXYDirections);
        directionFactory.setTrajectories(guitarMicDirection, VecDirectionNorms);
        guitar_trajectories = directionFactory.getTrajectories();
        
        // calculalate drum to mic path length
        Vector3f guitar2MicVector = guitarHandleOutPosition.subtract(micHandleInPosition);
        float guitar2MicLength = guitar2MicVector.length();
        
        // instantiate 3d Sound particul model
        //Sphere sphere = new Sphere(8, 8, 0.9f);
        //Geometry soundParticle = new Geometry("particul",sphere);
        //Material soundParticul_mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        
        Box rect = new Box(1.0f, 1.0f, 0.01f);
        Geometry soundParticle = new Geometry("particul",rect);
        Material soundParticul_mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        soundParticul_mat.setTexture("ColorMap", assetManager.loadTexture("Textures/Sound.png"));
        
        //soundParticul_mat.setColor("Color", ColorRGBA.Red);
        soundParticle.setMaterial(soundParticul_mat);
        Geometry soundParticleTranslucent = soundParticle.clone();
        soundParticleTranslucent.getMaterial().setTexture("ColorMap", assetManager.loadTexture("Textures/Sound_wAlpha.png"));
                
        GuitarSoundEmitter = new SignalEmitter(guitar_trajectories, guitar2MicLength, soundParticle, soundParticleTranslucent, SoundParticles_Speed, SignalType.Air );
        this.attachChild(GuitarSoundEmitter);
        GuitarSoundEmitter.setLocalTranslation(guitarHandleOutPosition); // TO DO: utiliser le object handle blender pour position
        
        //Set the impulsional response of the emitter
        ArrayList<Float> waveMagnitudes = new ArrayList(7);
        
        waveMagnitudes.add(5f);  
        waveMagnitudes.add(2f);
        waveMagnitudes.add(3f);
        waveMagnitudes.add(1.5f);
        waveMagnitudes.add(1.2f);
        waveMagnitudes.add(1.0f);
        waveMagnitudes.add(0.8f);
        
        GuitarSoundEmitter.setWaves(waveMagnitudes, 0.25f);

    }
    
    
    private void initCircles()
    {
        circles.scale(10.0f, 10.0f, 10.0f);
        Quaternion rot = new Quaternion();
        rot.fromAngleAxis(3.14f, new Vector3f(1.0f,0.0f,0.0f));
        
        Material circleMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        circleMat.setColor("Color", ColorRGBA.Gray);
        circles.setMaterial(circleMat);

        float duration = 5.0f; 
        AnimationFactory animationFactory = new AnimationFactory(duration,"DrumEffect");
        
        Vector3f v = drum.getWorldTranslation();
        animationFactory.addTimeTranslation(0.0f, new Vector3f(v.x, v.y + 20.0f, v.z));
        animationFactory.addTimeRotation(0.0f, rot);
        animationFactory.addTimeScale(0.0f, new Vector3f(0.2f, 0.0f, 0.2f));
        animationFactory.addTimeScale(5.0f, new Vector3f(10.0f, 0.0f, 10.0f));
        
        animation = animationFactory.buildAnimation();
        
        mAnimControl.addAnim(animation);
        circles.addControl(mAnimControl);
   
        mAnimChannel = mAnimControl.createChannel();
    }
    
    private void initMicWireParticlesEmitter()
    {
        SignalTrajectories directionFactory = new SignalTrajectories();
        Spline curvedPath = new Spline();
        
        Node micWire_node = (Node) scene.getParent().getChild("WirePath");
        Geometry micWire_geom = (Geometry) micWire_node.getChild("BezierCurve");
        Mesh micWire_mesh = micWire_geom.getMesh();
        
        //Vector3f f = micWire_node.getWorldScale();
        
        curvedPath = directionFactory.getCurvedPath(micWire_mesh);
        
        // instantiate 3d Sound particul model
        Box rect = new Box(1.0f, 1.0f, 0.01f);
        Geometry electricParticle = new Geometry("particul",rect);
        Material electricParticle_mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        electricParticle_mat.setTexture("ColorMap", assetManager.loadTexture("Textures/Electric3.png"));
        electricParticle.setMaterial(electricParticle_mat);
                
        
        MicWireEmitter = new SignalEmitter(curvedPath, electricParticle, electricParticle, 35f /*Speed*/, SignalType.Wire );
        this.attachChild(MicWireEmitter);
        MicWireEmitter.setLocalTranslation(micPosition.x, micPosition.y,micPosition.z); // TO DO: utiliser le object handle blender pour position        
        
    }
    
     
    @Override
    public void initAllMovableObjects()
    {
        initCircles();
        initDrumParticlesEmitter();
        initGuitarParticlesEmitter();
        initMicWireParticlesEmitter();
        
        DrumSoundEmitter.registerObserver(MicWireEmitter);
        GuitarSoundEmitter.registerObserver(MicWireEmitter);
        
        this.attachChild(movableObjects);
    }

    private void initHaloEffects()
    {
        //Add the halo effects under the interactive objects
        Box rect = new Box(2f, Float.MIN_VALUE, 2f);

        Material halo_mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        halo_mat.setTexture("ColorMap", assetManager.loadTexture("Textures/Halo.png"));
        halo_mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        halo_drum = new Halo("halo",rect,halo_mat,0.85f);
        halo_guitar = new Halo("halo",rect,halo_mat,1.30f);

        scene.attachChild(halo_drum);
        scene.attachChild(halo_guitar);


        halo_drum.setLocalTranslation(drumPosition);
        halo_guitar.setLocalTranslation(guitarPosition);


    }

    private void initAudio()
    {

        drum_sound = new AudioNode(assetManager, "Sounds/drum_taiko.wav", false);
        drum_sound.setPositional(false);
        drum_sound.setLooping(false);
        drum_sound.setVolume(2);
        this.attachChild(drum_sound);

        //Add guitar sound
        guitar_sound = new AudioNode(assetManager, "Sounds/guitar.wav", false);
        guitar_sound.setPositional(false);
        guitar_sound.setLooping(false);
        guitar_sound.setVolume(2);
        this.attachChild(guitar_sound);

    }

    public void initTextBox()
    {


        float textBoxWidth = 5f;
        float textBoxHeight = 1.8f;
        textBox = new TextBox(assetManager);
        textBox.init(defaultText, defaultTextSize, defaultTextColor, textBoxWidth, textBoxHeight, BitmapFont.Align.Center, false);
        textBox.move(0, 7.5f, 0);
        textBox.setName("Text");

        // Messages to display if textBox is touched
        lstUpdatedText.add("Aliquam erat volutpat. Vestibulum tempor ");
        lstUpdatedText.add(" amet quam eu consectetur. Duis dapibus,");
        lstUpdatedText.add("Aliquam euismod diam eget pharetra imperdiet.");

        touchable.attachChild(textBox);
    }

    public void drumTouchEffect()
    {        
        //DrumSoundEmitter.emitParticles(1.0f);
        DrumSoundEmitter.emitWaves();
        //MicWireEmitter.emitParticles();
        
        movableObjects.attachChild(circles);
        
        if(firstTry == true)
            mAnimControl.addListener(this);

        /**
         * Animation for a better touch feeling
         */
        mAnimChannel.reset(true);
        mAnimChannel.setAnim("DrumEffect");
        mAnimChannel.setLoopMode(LoopMode.DontLoop);
        mAnimChannel.setSpeed(20.0f);
              
        // Not the first time the object is touched
        firstTry = false;

        drum_sound.playInstance();
                
    }
    
    public void guitarTouchEffect()
    {        
        //GuitarSoundEmitter.emitParticles(1.0f);
        GuitarSoundEmitter.emitWaves();
        
        //MicWireEmitter.emitParticles();
        
        //movableObjects.attachChild(circles);
        
        //if(firstTry == true)
        //    mAnimControl.addListener(this);

        /**
         * Animation for a better touch feeling
         */
        //mAnimChannel.reset(true);
        //mAnimChannel.setAnim("DrumEffect");
        //mAnimChannel.setLoopMode(LoopMode.DontLoop);
        //mAnimChannel.setSpeed(20.0f);
              
        // Not the first time the object is touched
        //firstTry = false;

        guitar_sound.playInstance();
                
    }
    
    public void textTouchEffect()
    {
        updatedText = lstUpdatedText.pop();
        lstUpdatedText.add(updatedText);
        updatedTextSize = 0.0f;
        updatedTextColor = null;
    }
    
    
    @Override
    public void onAnimCycleDone(AnimControl animControl, AnimChannel animChannel, String s) 
    {
        // ...do nothing
        if(mAnimChannel.getAnimationName().equals("DrumEffect"))
            movableObjects.detachChild(circles);
    }

    @Override
    public void onAnimChange(AnimControl animControl, AnimChannel animChannel, String s) 
    {
        // ...do nothing
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

                        if (nameToCompare == drum.getName())
                        {
                            this.drumTouchEffect();
                            break;
                        }
                        else if (nameToCompare == guitar.getName())
                        {
                            this.guitarTouchEffect();
                            break;
                        }
                        else if (nameToCompare == textBox.getName())
                        {
                            //this.textTouchEffect();
                            showInformativeMenu = true;
                            break;
                        }
                }
            }
        }
    }

    @Override
    public void restartScenario() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean simpleUpdate(float tpf) {
         
        DrumSoundEmitter.simpleUpdate(tpf, this.Camera);
        GuitarSoundEmitter.simpleUpdate(tpf, this.Camera);
        MicWireEmitter.simpleUpdate(tpf, this.Camera);
        halo_drum.simpleUpdate(tpf);
        halo_guitar.simpleUpdate(tpf);
        
        if(Camera != null) {
            Vector3f upVector = this.getLocalRotation().mult(Vector3f.UNIT_Y);
            textBox.simpleUpdate(updatedText, updatedTextSize, updatedTextColor, this.Camera, upVector);
            
            // Resetting the values so that it is noob proof
            updatedText = null;
            updatedTextSize = 0.0f;
            updatedTextColor = null;
            //Log.d(TAG,"Camera position :" + Camera.getLocation());
        }

        if (showInformativeMenu)
        {
            showInformativeMenu = false;
            return true;
        }
        else
            return false;
    }

    @Override
    public void onAudioEvent() {
        drumTouchEffect();
    }

    @Override
    public void setGlobalSpeed(float speed) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}