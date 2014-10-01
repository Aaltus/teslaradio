package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.*;
import com.galimatias.teslaradio.world.observer.ParticleEmitReceiveLinker;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Greenwood0 on 2014-09-08.
 */
public final class SoundEmission extends Scenario {

    private AudioNode drum_sound;
    private AudioNode guitar_sound;

    private Spatial drum;
    private Spatial guitar;
    
    private Geometry soundParticle;

    private Spatial drumHandleOut;
    private Spatial guitarHandleOut;
    private Spatial destinationHandle;
    
    private Spatial guitarAirParticleEmitter;
    private Spatial drumAirParticleEmitter;

    //private Halo halo_drum, halo_guitar;
    //private SignalEmitter DrumSoundEmitter;
    //private SignalEmitter GuitarSoundEmitter;

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

    //CHANGE THIS VALUE CHANGE THE PARTICULE BEHAVIOUR
    //Setting the direction norms and the speed displacement to the trajectories
    private float VecDirectionNorms = 8f;
    private float SoundParticleSpeed = 5f;
    private float SoundParticlePeriod = 0.25f;

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
    private ColorRGBA defaultTextColor = new ColorRGBA(1f, 1f, 1f, 1f);

    // Refresh hint values
    private float maxTimeRefreshHint = 10f;
    private float timeLastTouch = maxTimeRefreshHint;
    private final float hintFadingTime = 1.5f;

    public SoundEmission(com.jme3.renderer.Camera Camera, Spatial destinationHandle)
    {
        super(Camera, destinationHandle);
        
        touchable = new Node();
        touchable.setName("Touchable");
        this.attachChild(touchable);

        this.destinationHandle = destinationHandle;

        loadUnmovableObjects();
        loadMovableObjects();
    }

    @Override
    protected void loadUnmovableObjects() {

        Node sceneDrum = (Node) assetManager.loadModel("Models/SoundCapture/Tambour.j3o");
        Node sceneGuit = (Node) assetManager.loadModel("Models/SoundCapture/Guitare.j3o");
        
        float movementValue  = 2.5f;

        sceneDrum.setLocalTranslation(movementValue,0,movementValue);
        sceneGuit.setLocalTranslation(movementValue,0,-movementValue);
        
        touchable.attachChild(sceneDrum);
        touchable.attachChild(sceneGuit);


        drum = sceneDrum.getChild("Tambour");
        guitar = sceneGuit.getChild("Guitar");
        guitarHandleOut = sceneGuit.getChild("Guitar_Output_Handle");
        drumHandleOut = sceneDrum.getChild("Drum_Output_Handle");
        drumPosition = sceneDrum.getLocalTranslation();
        guitarPosition = sceneGuit.getLocalTranslation();
        drumHandleOutPosition = drumHandleOut.getLocalTranslation().add(sceneDrum.getLocalTranslation());
        guitarHandleOutPosition = guitarHandleOut.getLocalTranslation().add(sceneGuit.getLocalTranslation());

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
        
        ParticleEmitterControl microphoneControl = this.destinationHandle.getControl(ParticleEmitterControl.class);
        Material mat1 = new Material(assetManager, 
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", new ColorRGBA(1, 0, 1, 1f));
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        guitarAirParticleEmitter = new Node();
        guitarAirParticleEmitter.setLocalTranslation(guitarHandleOutPosition);
        this.attachChild(guitarAirParticleEmitter);
        guitarAirParticleEmitter.addControl(new AirParticleEmitterControl(this.destinationHandle, 2f, 13f, mat1, AirParticleEmitterControl.AreaType.DOME));
        guitarAirParticleEmitter.getControl(ParticleEmitterControl.class).registerObserver(microphoneControl);
        guitarAirParticleEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);
        
        
        Material mat2 = new Material(assetManager, 
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", new ColorRGBA(0, 1, 1, 1f));
        mat2.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        drumAirParticleEmitter = new Node();
        drumAirParticleEmitter.setLocalTranslation(drumHandleOutPosition);
        this.attachChild(drumAirParticleEmitter);
        drumAirParticleEmitter.addControl(new AirParticleEmitterControl(this.destinationHandle, 2f, 13f, mat2, AirParticleEmitterControl.AreaType.DOME));
        drumAirParticleEmitter.getControl(ParticleEmitterControl.class).registerObserver(microphoneControl);
        drumAirParticleEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);
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
        // instantiate 3d Sound particul model
        Quad rect = new Quad(1f, 1f);
        soundParticle = new Geometry("particul",rect);
        Material soundParticul_mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        soundParticul_mat.setTexture("ColorMap", assetManager.loadTexture("Textures/Sound.png"));
        soundParticul_mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        soundParticle.setMaterial(soundParticul_mat);
        Geometry soundParticleTranslucent = soundParticle.clone();
        soundParticleTranslucent.getMaterial().setTexture("ColorMap", assetManager.loadTexture("Textures/Sound_wAlpha.png"));
        soundParticle.setQueueBucket(queueBucket.Transparent);
        

        //DrumSoundEmitter = new SignalEmitter(drum_trajectories, drum2MicLength, soundParticle, soundParticleTranslucent, SoundParticleSpeed, SignalType.Air );

        // Initializing the new Signal Emitter
        /*
        DrumSoundEmitter = new SignalEmitter(this);
        this.attachChild(DrumSoundEmitter);
        DrumSoundEmitter.setLocalTranslation(drumHandleOutPosition);
       
        //Set the impulsional response of the emitter
        ArrayList<Float> waveMagnitudes = new ArrayList(3);

        waveMagnitudes.add(5f);
        waveMagnitudes.add(3f);
        waveMagnitudes.add(1f);

        DrumSoundEmitter.setWaves(waveMagnitudes, soundParticle, soundParticleTranslucent, SoundParticlePeriod, SoundParticleSpeed);
        * */
    }
    
    
    /**
     * Initialisation of the drum effects
     */
    private void initGuitarParticlesEmitter()
    {

        Quad rect = new Quad(0.1f, 0.1f);
        Geometry soundParticle = new Geometry("particul",rect);
        Material soundParticul_mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        soundParticul_mat.setTexture("ColorMap", assetManager.loadTexture("Textures/Sound.png"));

        soundParticle.setMaterial(soundParticul_mat);
        Geometry soundParticleTranslucent = soundParticle.clone();
        soundParticleTranslucent.getMaterial().setTexture("ColorMap", assetManager.loadTexture("Textures/Sound_wAlpha.png"));

        /*
        GuitarSoundEmitter = new SignalEmitter(this);
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

        GuitarSoundEmitter.setWaves(waveMagnitudes, soundParticle, soundParticleTranslucent, SoundParticlePeriod, SoundParticleSpeed);
        * */

    }

    private void initOnTouchEffect() {

        /*
        Material effect_mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        effect_mat.setTexture("ColorMap", assetManager.loadTexture("Textures/Halo.png"));
        effect_mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        Box rect = new Box(0.1f, Float.MIN_VALUE, 0.1f);
        Geometry drumTouchEffect = new Geometry("DrumTouchEffect",rect);
        drumTouchEffect.setMaterial(effect_mat);

        touchEffectEmitter = new TouchEffectEmitter("DrumEffect", drumMinScale, drumMaxScale, drumScaleGradient, drumTouchEffect, new Vector3f(1.0f,0.0f,1.0f));
        this.attachChild(touchEffectEmitter);
        touchEffectEmitter.setLocalTranslation(drumHandleOutPosition.add(new Vector3f(0.0f,0.1f,0.0f)));
        * */
    }

    private void initHaloEffects()
    {
        //Add the halo effects under the interactive objects
        /*
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
        * */
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
        
        LookAtCameraControl control1 = new LookAtCameraControl(Camera);
        FadeControl fadeControl1     = new FadeControl(hintFadingTime);
        Vector3f imageHintDrumPosition = /*drumHandleOut.getLocalTranslation()*/drumHandleOutPosition;/*.add(new Vector3f(0, 0.65f, 0f));*/
        imageHintDrum = new ImageBox(0.4f, 0.75f, assetManager, "Drum Touch Hint", "Textures/Selection_Hand.png", 1f);
        imageHintDrum.move(imageHintDrumPosition);
        imageHintDrum.addControl(control1);
        imageHintDrum.addControl(fadeControl1);
        this.attachChild(imageHintDrum);
        
        LookAtCameraControl control2 = new LookAtCameraControl(Camera);
        FadeControl fadeControl2     = new FadeControl(hintFadingTime);
        Vector3f imageHintGuitarPosition = guitarHandleOutPosition;//guitarHandleOut.getLocalTranslation().add(new Vector3f(0, 0.6f, 0f));
        imageHintGuitar = new ImageBox(0.4f, 0.75f, assetManager, "Guitar Touch Hint", "Textures/Selection_Hand.png", 6f);
        imageHintGuitar.move(imageHintGuitarPosition);
        imageHintGuitar.addControl(control2);
        imageHintGuitar.addControl(fadeControl2);
        this.attachChild(imageHintGuitar);
    }

    public void drumTouchEffect()
    {
        this.removeHintImages();

        // Here, we need to get the vector to the mic handle
        //Vector3f receiverHandleVector = particleLinker.GetEmitterDestinationPaths(this);
        //DrumSoundEmitter.prepareEmitParticles(receiverHandleVector);

        //touchEffectEmitter.isTouched();
        drum_sound.playInstance();
        
        AirParticleEmitterControl control = drumAirParticleEmitter.getControl(AirParticleEmitterControl.class);
        control.emitParticle(soundParticle.clone());
        

    }

    public void guitarTouchEffect()
    {
        this.removeHintImages();

        // Here, we need to get the vector to the mic handle
        //Vector3f receiverHandleVector = particleLinker.GetEmitterDestinationPaths(this);
        //GuitarSoundEmitter.prepareEmitParticles(receiverHandleVector);

        guitar_sound.playInstance();
        /*
        Sphere sphere1Mesh = new Sphere();
        Geometry sphere1Geo = new Geometry("My Textured Box", sphere1Mesh);
        sphere1Geo.setLocalTranslation(new Vector3f(-3f,1.1f,0f));
        Material cube1Mat = new Material(assetManager, 
            "Common/MatDefs/Misc/Unshaded.j3md");
        Texture cube1Tex = assetManager.loadTexture(
            "Interface/Logo/Monkey.jpg");
        cube1Mat.setTexture("ColorMap", cube1Tex);
        sphere1Geo.setMaterial(cube1Mat);
        */
        /*
         Material cube1Mat = new Material(assetManager, 
            "Common/MatDefs/Misc/Unshaded.j3md");
        Texture cube1Tex = assetManager.loadTexture(
            "Interface/Logo/Monkey.jpg");
        cube1Mat.setTexture("ColorMap", cube1Tex);
        * */
        
        
        AirParticleEmitterControl control = guitarAirParticleEmitter.getControl(AirParticleEmitterControl.class);
        control.emitParticle(soundParticle.clone());
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
        
        imageHintDrum.getControl(FadeControl.class).setShowImage(false);
        imageHintGuitar.getControl(FadeControl.class).setShowImage(false);
        //imageHintDrum.setShowImage(false);
        //imageHintGuitar.setShowImage(false);
    }

    /**
     * Show Hints, is called when no touch has occured for a while
     */
    public void ShowHintImages()
    {
        imageHintDrum.getControl(FadeControl.class).setShowImage(true);
        imageHintGuitar.getControl(FadeControl.class).setShowImage(true);
        //imageHintDrum.setShowImage(true);
        //imageHintGuitar.setShowImage(true);
    }

    /**
     * regrouping Hints update
     * @param tpf
     * @param upVector
     */
    public void hintsUpdate(float tpf, Vector3f upVector)
    {
        //imageHintDrum.simpleUpdate(tpf, this.Camera, upVector);
        //imageHintGuitar.simpleUpdate(tpf, this.Camera, upVector);
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
                    Vector2f click2d = new Vector2f(touchEvent.getX(),touchEvent.getY());
                    Vector3f click3d = Camera.getWorldCoordinates(
                            new Vector2f(click2d.x, click2d.y), 0f).clone();
                    Vector3f dir = Camera.getWorldCoordinates(
                            new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
                    Ray ray = new Ray(click3d, dir);

                    // 3. Collect intersections between Ray and Shootables in results list.
                    touchable.collideWith(ray, results);

                    // 4. Print the results
                    //for (int i = 0; i < results.size(); i++) {
                        // For each hit, we know distance, impact point, name of geometry.
                        //float dist = results.getCollision(i).getDistance();
                        //Vector3f pt = results.getCollision(i).getContactPoint();
                        //String hit = results.getCollision(i).getGeometry().getName();
                    //}

                    // 5. Use the results (we mark the hit object)
                    if (results.size() > 0)
                    {

                        // The closest collision point is what was truly hit:
                        String nameToCompare =
                                results.getClosestCollision().getGeometry().getParent().getName();
                        
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
        
        /*
        DrumSoundEmitter.simpleUpdate(tpf, this.Camera);
        GuitarSoundEmitter.simpleUpdate(tpf, this.Camera);
        touchEffectEmitter.simpleUpdate(tpf);
        halo_drum.simpleUpdate(tpf);
        halo_guitar.simpleUpdate(tpf);

        if(Camera != null) {
            Vector3f upVector = this.getLocalRotation().mult(Vector3f.UNIT_Y);
            textBoxesUpdate(upVector);
            hintsUpdate(tpf, upVector);
        }
        * */

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
        //drumTouchEffect();
    }

    @Override
    public void signalEndOfPath(Geometry caller, float magnitude) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Spatial getInputHandle() {
        return null;
    }
}
