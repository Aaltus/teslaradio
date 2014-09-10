package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.*;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResult;
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
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Greenwood0 on 2014-09-08.
 */
public class SoundEmission extends Scenario {

    private AudioNode drum_sound;
    private AudioNode guitar_sound;

    private Spatial drum;
    private Spatial guitar;

    private Spatial drumHandleOut;
    private Spatial guitarHandleOut;
    private Spatial micHandleIn;

    private Halo halo_drum, halo_guitar;

    private SignalEmitter DrumSoundEmitter;
    private SignalEmitter GuitarSoundEmitter;

    private TouchEffectEmitter touchEffectEmitter;

    private TextBox titleTextBox;
    private TextBox instrumentTextBox;

    private ImageBox imageHintDrum;
    private ImageBox imageHintGuitar;

    private Vector<Vector3f> drum_trajectories = new Vector<Vector3f>();
    private Vector<Vector3f> guitar_trajectories = new Vector<Vector3f>();

    private Vector3f drumPosition;
    private Vector3f guitarPosition;
    private Vector3f drumHandleOutPosition;
    private Vector3f guitarHandleOutPosition;
    private Vector3f micHandleInPosition;

    //CHANGE THIS VALUE CHANGE THE PARTICULE BEHAVIOUR
    //Setting the direction norms and the speed displacement to the trajectories
    private float VecDirectionNorms = 8f;
    private float SoundParticles_Speed = 5f;

    // CHANGE THESE VALUES TO SET THE TOUCH EFFECT BEHAVIOUR
    private float drumScaleGradient = 50.0f;
    private float drumMaxScale = 20.0f;
    private float drumMinScale = 0.0f;

    // Default text to be seen when scenario starts
    private String titleText = "L'émission du son";
    private String instrumentText = "Les instruments modifient la pression d'air autour d’eux avec leur vibration. Ces zones de pressions se propagent à la vitesse du son.";
    private float titleTextSize = 0.5f;
    private float secondaryTextSize = 0.25f;
    private float instrumentTextSize = 0.25f;
    private ColorRGBA defaultTextColor = new ColorRGBA(1f, 0f, 1f, 1f);

    // Refresh hint values
    private float maxTimeRefreshHint = 30f;
    private float timeLastTouch = maxTimeRefreshHint;

    public SoundEmission(AssetManager assetManager, com.jme3.renderer.Camera Camera/*, ScenarioObserver observer*/)
    {
        super(assetManager,Camera/*, observer*/);
        
        touchable = new Node();
        touchable.setName("Touchable");
        this.attachChild(touchable);
        
        loadUnmovableObjects();
        loadMovableObjects();
    }

    @Override
    protected void loadUnmovableObjects() {

        Node sceneDrum = (Node) assetManager.loadModel("Models/SoundCapture/drum.j3o");
        Node sceneGuit = (Node) assetManager.loadModel("Models/SoundCapture/guitar.j3o");
        
        float movementValue  = 2.5f;
        float movementValue2 = 2.5f;
        sceneDrum.setLocalTranslation(movementValue2,0,movementValue);
        sceneGuit.setLocalTranslation(movementValue2,0,-movementValue);
        
        touchable.attachChild(sceneDrum);
        touchable.attachChild(sceneGuit);
        
        micHandleIn = new Node();
        this.attachChild(micHandleIn);
        micHandleIn.setLocalTranslation(10f, 10f, 0f);
        
        //this.attachChild(sceneDrum);
        //this.attachChild(sceneGuit);
        this.scale(10.0f,10.0f,10.0f);

        drum = sceneDrum.getChild("Tambour");
        guitar = sceneGuit.getChild("Guitar");
        guitarHandleOut = sceneGuit.getChild("Guitar_Output_Handle");
        drumHandleOut = sceneDrum.getChild("Drum_Output_Handle");
        drumPosition = sceneDrum.getLocalTranslation();
        guitarPosition = sceneGuit.getLocalTranslation();
        drumHandleOutPosition = drumHandleOut.getLocalTranslation().add(sceneDrum.getLocalTranslation());
        guitarHandleOutPosition = guitarHandleOut.getLocalTranslation().add(sceneGuit.getLocalTranslation());//guitarHandleOut.getWorldTranslation();
        micHandleInPosition = micHandleIn.getLocalTranslation();

        initAudio();
        initTextBox();
        initImageBoxes();
        initHaloEffects();
        initOnTouchEffect();
        
        

    }

    @Override
    protected void loadMovableObjects() {

        initDrumParticlesEmitter();
        initGuitarParticlesEmitter();

//        DrumSoundEmitter.registerObserver(MicWireEmitter);
//        GuitarSoundEmitter.registerObserver(MicWireEmitter);
    }

    @Override
    public void restartScenario() {

    }

    @Override
    public void setGlobalSpeed(float speed) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    /**
     * Initialisation of the tambour effects
     */
    private void initDrumParticlesEmitter()
    {
        // Getting all the trajectories from the position of the mic-drums and 
        // the number of directions        
        Vector3f drumMicDirection = micHandleInPosition.subtract(drumHandleOutPosition);

        int totalNbDirections = 10;
        int nbXYDirections = 2;

        /**
         * The trajectories will not be sent in the init
         */
        // Creating the trajectories
        SignalTrajectories directionFactory = new SignalTrajectories(totalNbDirections, nbXYDirections);
        directionFactory.setTrajectories(drumMicDirection, VecDirectionNorms);
        drum_trajectories = directionFactory.getTrajectories();

        // calculalate drum to mic path length
        Vector3f drum2MicVector = drumHandleOutPosition.subtract(micHandleInPosition);
        float drum2MicLength = drum2MicVector.length();

        // instantiate 3d Sound particul model
        Quad rect = new Quad(0.1f, 0.1f);
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
//
        int totalNbDirections = 10;
        int nbXYDirections = 2;
//
//        // Creating the trajectories
        SignalTrajectories directionFactory = new SignalTrajectories(totalNbDirections, nbXYDirections);
        directionFactory.setTrajectories(guitarMicDirection, VecDirectionNorms);
        guitar_trajectories = directionFactory.getTrajectories();
//
//        // calculalate drum to mic path length
        Vector3f guitar2MicVector = guitarHandleOutPosition.subtract(micHandleInPosition);
        float guitar2MicLength = guitar2MicVector.length();

        // instantiate 3d Sound particul model
        //Sphere sphere = new Sphere(8, 8, 0.9f);
        //Geometry soundParticle = new Geometry("particul",sphere);
        //Material soundParticul_mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");

        Quad rect = new Quad(0.1f, 0.1f);
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

    private void initOnTouchEffect() {

        Material effect_mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        effect_mat.setTexture("ColorMap", assetManager.loadTexture("Textures/Halo.png"));
        effect_mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        Box rect = new Box(0.1f, Float.MIN_VALUE, 0.1f);
        Geometry drumTouchEffect = new Geometry("DrumTouchEffect",rect);
        drumTouchEffect.setMaterial(effect_mat);

        touchEffectEmitter = new TouchEffectEmitter("DrumEffect", drumMinScale, drumMaxScale, drumScaleGradient, drumTouchEffect, new Vector3f(1.0f,0.0f,1.0f));
        this.attachChild(touchEffectEmitter);
        touchEffectEmitter.setLocalTranslation(drumHandleOutPosition.add(new Vector3f(0.0f,0.1f,0.0f)));
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

        this.attachChild(halo_drum);
        this.attachChild(halo_guitar);

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
        boolean lookAtCamera = false;
        boolean showDebugBox = false;
        float textBoxWidth = 5.2f;
        float textBoxHeight = 0.8f;

        ColorRGBA titleTextColor = new ColorRGBA(1f, 1f, 1f, 1f);
        ColorRGBA titleBackColor = new ColorRGBA(0.1f, 0.1f, 0.1f, 0.5f);
        titleTextBox = new TextBox(assetManager, titleText, titleTextSize, titleTextColor, titleBackColor, textBoxWidth, textBoxHeight, "titleText", BitmapFont.Align.Center, showDebugBox, lookAtCamera);

        //move the text on the ground without moving
        Vector3f titleTextPosition = new Vector3f(0f, 0.25f, 5f);
        titleTextBox.rotate((float)-Math.PI/2, 0, 0);

        //Was in its position when in the air and rotating
        //Vector3f titleTextPosition = new Vector3f(0f, 8f, 0f);

        titleTextBox.move(titleTextPosition);


        // Add other text boxes here
        float instrumentTextBoxWidth = 4f;
        float instrumentTextBoxHeight = 1.7f;
        ColorRGBA instrumentTextBackColor = new ColorRGBA(0.2f, 0.2f, 0.2f, 0.5f);
        instrumentTextBox = new TextBox(assetManager, instrumentText, secondaryTextSize, defaultTextColor, instrumentTextBackColor, instrumentTextBoxWidth, instrumentTextBoxHeight, "instrumentText", BitmapFont.Align.Center, showDebugBox, lookAtCamera);

        //move the text on the ground without moving
        Vector3f instrumentTextPosition = new Vector3f(-3f, 0.25f, 1f);
        instrumentTextBox.rotate((float)-Math.PI/2, 0, 0);

        //Was in its position when in the air and rotating
        //Vector3f instrumentTextPosition = ((drumHandleOut.getLocalTranslation().subtract(guitarHandleOut.getLocalTranslation())).divide(2f)).add(new Vector3f(-4f, 2f, 0f));
        instrumentTextBox.move(instrumentTextPosition);


        float micTextBoxWidth = 6f;
        float micTextBoxHeight = 1.2f;
        ColorRGBA micTextBackColor = new ColorRGBA(0.2f, 0.2f, 0.2f, 0.5f);

        //move the text on the ground without moving
        Vector3f microphoneTextPosition = new Vector3f(5f, 0.25f, -3.5f);

        touchable.attachChild(titleTextBox);
        touchable.attachChild(instrumentTextBox);
    }

    public void initImageBoxes()
    {
        Vector3f imageHintDrumPosition = /*drumHandleOut.getLocalTranslation()*/drumHandleOutPosition;/*.add(new Vector3f(0, 0.65f, 0f));*/
        imageHintDrum = new ImageBox(0.4f, 0.75f, assetManager, "Drum Touch Hint", "Textures/Selection_Hand.png", 1f);
        imageHintDrum.move(imageHintDrumPosition);
        this.attachChild(imageHintDrum);

        Vector3f imageHintGuitarPosition = guitarHandleOutPosition;//guitarHandleOut.getLocalTranslation().add(new Vector3f(0, 0.6f, 0f));
        imageHintGuitar = new ImageBox(0.4f, 0.75f, assetManager, "Guitar Touch Hint", "Textures/Selection_Hand.png", 1f);
        imageHintGuitar.move(imageHintGuitarPosition);
        this.attachChild(imageHintGuitar);
    }

    public void drumTouchEffect()
    {
        this.removeHintImages();
        //DrumSoundEmitter.emitParticles(1.0f);
        DrumSoundEmitter.emitWaves();
        //MicWireEmitter.emitParticles();

        touchEffectEmitter.isTouched();

        drum_sound.playInstance();

    }

    public void guitarTouchEffect()
    {
        this.removeHintImages();
        GuitarSoundEmitter.emitWaves();
        guitar_sound.playInstance();
    }

    public void textTouchEffect()
    {
//        updatedText = lstUpdatedText.pop();
//        lstUpdatedText.add(updatedText);
//        updatedTextSize = 0.0f;
//        updatedTextColor = null;
    }


    /**
     * Remove hints, is called after touch occurs
     */
    public void removeHintImages()
    {
        timeLastTouch = 0f;
        imageHintDrum.setShowImage(false);
        imageHintGuitar.setShowImage(false);
    }

    /**
     * Show Hints, is called when no touch has occured for a while
     */
    public void ShowHintImages()
    {
        imageHintDrum.setShowImage(true);
        imageHintGuitar.setShowImage(true);
    }

    /**
     * regrouping Hints update
     * @param tpf
     * @param upVector
     */
    public void hintsUpdate(float tpf, Vector3f upVector)
    {
        imageHintDrum.simpleUpdate(tpf, this.Camera, upVector);
        imageHintGuitar.simpleUpdate(tpf, this.Camera, upVector);
    }

    public void textBoxesUpdate(Vector3f upVector)
    {
        titleTextBox.simpleUpdate(null, 0.0f, null, this.Camera, upVector);
        instrumentTextBox.simpleUpdate(null, 0.0f, null, this.Camera, upVector);
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
                        
                        if (nameToCompare.equals(drum.getName()))
                        {
                            this.drumTouchEffect();
                            break;
                        }
                        else if (nameToCompare.equals(guitar.getName()))
                        {
                            this.guitarTouchEffect();
                            break;
                        }
                        else if (nameToCompare.equals(titleTextBox.getName()) || nameToCompare.equals(instrumentTextBox.getName()))
                        {
                            //this.textTouchEffect();
                            showInformativeMenu = true;
                            break;
                        }
                    }
                }
                break;
        }
    }

    @Override
    public boolean simpleUpdate(float tpf) {

        timeLastTouch += tpf;

        if ((int)timeLastTouch == maxTimeRefreshHint)
        {
            ShowHintImages();
        }

        DrumSoundEmitter.simpleUpdate(tpf, this.Camera);
        GuitarSoundEmitter.simpleUpdate(tpf, this.Camera);
        touchEffectEmitter.simpleUpdate(tpf);
        halo_drum.simpleUpdate(tpf);
        halo_guitar.simpleUpdate(tpf);

        if(Camera != null) {
            Vector3f upVector = this.getLocalRotation().mult(Vector3f.UNIT_Y);

            // Resetting the values so that it is noob proof
            //updatedText = null;
            //updatedTextSize = 0.0f;
            //updatedTextColor = null;
            //Log.d(TAG,"Camera position :" + Camera.getLocation());

            textBoxesUpdate(upVector);
            hintsUpdate(tpf, upVector);
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
    public void onAudioEvent()
    {
        drumTouchEffect();
    }

}
