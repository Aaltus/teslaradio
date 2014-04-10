/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.SignalEmitter;
import com.galimatias.teslaradio.world.effects.SignalTrajectories;
import com.galimatias.teslaradio.world.effects.SignalType;
import com.galimatias.teslaradio.world.effects.TextBox;
import com.jme3.animation.*;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResult;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
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

    private final static String TAG = "Capture";

    private AudioNode drum_sound;
    private AudioNode guitar_sound;
    
    private Spatial scene;
    private Spatial drum;
    private Spatial guitar;
    private Spatial micro;
    private Spatial circles;
    
    private Spatial drumHandleOut;
    private Spatial guitarHandleOut;
    private Spatial micHandleIn;
    
    private SignalEmitter DrumSoundEmitter;
    private SignalEmitter GuitarSoundEmitter;
    private SignalEmitter MicWireEmitter;
    
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
    
    // Default text to be seen when scenario starts
    private String defaultText = "Sed sit amet mi fringilla leo molestie luctus";
    private float defaultTextSize = 10.0f;
    private ColorRGBA defaultTextColor = ColorRGBA.White;
    
    // Updated values of the textbox, the list contains the messages when updated
    private LinkedList<String> lstUpdatedText = new LinkedList<String>();
    private String updatedText = null;
    private float updatedTextSize = 0.0f;
    private ColorRGBA updatedTextColor = null;
        
    private Camera fgCam = null;

    private boolean firstTry = true;
       
    public SoundCapture(AssetManager assetManager, Camera fgCam)
    {
        super(assetManager);
        
        loadUnmovableObjects();
        loadMovableObjects();

        this.fgCam = fgCam;
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
        scene = assetManager.loadModel("Models/SoundCapture.j3o");
        scene.setName("SoundCapture");
        scene.scale(10.0f,10.0f,10.0f);
        this.attachChild(scene);

        drum = scene.getParent().getChild("Tambour");
        guitar = scene.getParent().getChild("Guitar");
        micro = scene.getParent().getChild("Boule_micro");
        guitarHandleOut = scene.getParent().getChild("Guitar_Output_Handle");
        drumHandleOut = scene.getParent().getChild("Drum_Output_Handle");
        micHandleIn = scene.getParent().getChild("Mic_Input_Handle");
        
        drumPosition = drum.getWorldTranslation();
        guitarPosition = guitar.getWorldTranslation();
        micPosition = micro.getWorldTranslation();
        drumHandleOutPosition = drumHandleOut.getWorldTranslation();
        guitarHandleOutPosition = guitarHandleOut.getWorldTranslation();
        micHandleInPosition = micHandleIn.getWorldTranslation();
        
        
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
        
        Quaternion textRotation = new Quaternion();
        textRotation.fromAngleAxis(-3.14159f/2.0f, Vector3f.UNIT_Y);
        
        Vector3f v = new Vector3f(micHandleInPosition.x, micHandleInPosition.y, micHandleInPosition.z + 15.0f);
        //Vector3f v = new Vector3f(0.0f,0.0f,0.0f);
        
        TextBox text = new TextBox(assetManager);
        text.initDefaultText(defaultText, defaultTextSize, v, textRotation, defaultTextColor);
        text.setName("Text");

        // Messages to display if textBox is touched
        lstUpdatedText.add("Aliquam erat volutpat. Vestibulum tempor ");
        lstUpdatedText.add(" amet quam eu consectetur. Duis dapibus,");
        lstUpdatedText.add("Aliquam euismod diam eget pharetra imperdiet.");
        
        this.attachChild(text);

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
                        
        int totalNbDirections = 50;
        int nbXYDirections = 5;
        
        // Setting the direction norms and the speed displacement to the trajectories
        float VecDirectionNorms = 80f;
        float SoundParticles_Speed = 35f;
                
        // Creating the trajectories
        SignalTrajectories directionFactory = new SignalTrajectories(totalNbDirections, nbXYDirections);
        directionFactory.setTrajectories(drumMicDirection, VecDirectionNorms);
        drum_trajectories = directionFactory.getTrajectories();
        
        // calculalate drum to mic path length
        Vector3f drum2MicVector = drumHandleOutPosition.subtract(micHandleInPosition);
        float drum2MicLength = drum2MicVector.length();
        
        // instantiate 3d Sound particul model
        Sphere sphere = new Sphere(8, 8, 0.9f);
        Geometry soundParticle = new Geometry("particul",sphere);
        Material soundParticul_mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        soundParticul_mat.setColor("Color", ColorRGBA.Blue);
        soundParticle.setMaterial(soundParticul_mat);
        Geometry soundParticleTranslucent = soundParticle.clone();
        soundParticleTranslucent.getMaterial().setColor("Color", new ColorRGBA(0f, 0f, 1f, 0.3f));
                
        DrumSoundEmitter = new SignalEmitter(drum_trajectories, drum2MicLength, soundParticle, soundParticleTranslucent, SoundParticles_Speed, SignalType.Air );
        this.attachChild(DrumSoundEmitter);
        DrumSoundEmitter.setLocalTranslation(drumHandleOutPosition); // TO DO: utiliser le object handle blender pour position

    }
    
    /**
     * Initialisation of the drum effects
     */
    private void initGuitarParticlesEmitter()
    {
        // Getting all the trajectories from the position of the mic-drums and 
        // the number of directions        
        Vector3f guitarMicDirection = micHandleInPosition.subtract(guitarHandleOutPosition);        
                        
        int totalNbDirections = 30;
        int nbXYDirections = 3;
        
        // Setting the direction norms and the speed displacement to the trajectories
        float VecDirectionNorms = 80f;
        float SoundParticles_Speed = 90f;
                
        // Creating the trajectories
        SignalTrajectories directionFactory = new SignalTrajectories(totalNbDirections, nbXYDirections);
        directionFactory.setTrajectories(guitarMicDirection, VecDirectionNorms);
        guitar_trajectories = directionFactory.getTrajectories();
        
        // calculalate drum to mic path length
        Vector3f guitar2MicVector = guitarHandleOutPosition.subtract(micHandleInPosition);
        float guitar2MicLength = guitar2MicVector.length();
        
        // instantiate 3d Sound particul model
        Sphere sphere = new Sphere(8, 8, 0.9f);
        Geometry soundParticle = new Geometry("particul",sphere);
        Material soundParticul_mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        soundParticul_mat.setColor("Color", ColorRGBA.Red);
        soundParticle.setMaterial(soundParticul_mat);
        Geometry soundParticleTranslucent = soundParticle.clone();
        soundParticleTranslucent.getMaterial().setColor("Color", new ColorRGBA(1f, 0f, 0f, 0.3f));
                
        GuitarSoundEmitter = new SignalEmitter(guitar_trajectories, guitar2MicLength, soundParticle, soundParticleTranslucent, SoundParticles_Speed, SignalType.Air );
        this.attachChild(GuitarSoundEmitter);
        GuitarSoundEmitter.setLocalTranslation(guitarHandleOutPosition); // TO DO: utiliser le object handle blender pour position
        
        //Set the impulsional response of the emitter
        ArrayList<Float> waveMagnitudes = new ArrayList(4);
        
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
        Vector<Vector3f> curvedPath = new Vector <Vector3f>();
        
        Node micWire_node = (Node) scene.getParent().getChild("WirePath");
        Geometry micWire_geom = (Geometry) micWire_node.getChild("BezierCurve");
        Mesh micWire_mesh = micWire_geom.getMesh();
        
        //Vector3f f = micWire_node.getWorldScale();
        
        curvedPath = directionFactory.getCurvedPath(micWire_mesh);
        
        // instantiate 3d Sound particul model
        Sphere sphere = new Sphere(8, 8, 0.9f);
        Geometry electricParticle = new Geometry("particul",sphere);
        Material electricParticle_mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        electricParticle_mat.setColor("Color", ColorRGBA.Green);
        electricParticle.setMaterial(electricParticle_mat);
                
        
        MicWireEmitter = new SignalEmitter(curvedPath, electricParticle, electricParticle, 35f /*Speed*/, SignalType.Wire );
        this.attachChild(MicWireEmitter);
        Vector3f test = new Vector3f();
        test.set(curvedPath.lastElement());
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
    
    public void drumTouchEffect()
    {        
        DrumSoundEmitter.emitParticles(1.0f);
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
    public void onScenarioClick(CollisionResult closestCollisionResult) {

        Spatial touchedGeometry = closestCollisionResult.getGeometry();
        while(touchedGeometry.getParent() != null)
        {
            //if(touchedGeometry.getParent() != null){
                if (touchedGeometry.getParent().getName() == drum.getName())
                {
                    this.drumTouchEffect();
                    break;
                }
                else if (touchedGeometry.getParent().getName() == guitar.getName())
                {
                    this.guitarTouchEffect();
                    break;
                }
                else if (touchedGeometry.getParent().getName() == this.getChild("Text").getName()) 
                {
                    this.textTouchEffect();
                    showInformativeMenu = true;
                    break;
                }
                else
                {
                    touchedGeometry = touchedGeometry.getParent();
                }
//            }
//            else{
//                break;
//            }
        }

    }

    @Override
    protected void restartScenario() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean simpleUpdate(float tpf) {
         
        DrumSoundEmitter.simpleUpdate(tpf);
        GuitarSoundEmitter.simpleUpdate(tpf);
        MicWireEmitter.simpleUpdate(tpf);
        
        if(fgCam != null) {
            ((TextBox)this.getChild("Text")).simpleUpdate(updatedText, updatedTextSize, updatedTextColor, this.fgCam);
            
            // Resetting the values so that it is noob proof
            updatedText = null;
            updatedTextSize = 0.0f;
            updatedTextColor = null;
            //Log.d(TAG,"Camera position :" + fgCam.getLocation());
        }
        else {
            Camera cam = new Camera(100,100);
            cam.setLocation(new Vector3f(0.0f,50.0f,0.0f));
            ((TextBox)this.getChild("Text")).simpleUpdate(updatedText, updatedTextSize, updatedTextColor, cam);
            
            // Resetting the values so that it is noob proof
            updatedText = null;
            updatedTextSize = 0.0f;
            updatedTextColor = null;
            //Log.d(TAG,"Camera position :");
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

}