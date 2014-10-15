package com.galimatias.teslaradio.world.Scenarios;

import static com.galimatias.teslaradio.world.Scenarios.Scenario.DEBUG_ANGLE;
import com.galimatias.teslaradio.world.effects.*;
import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.input.event.TouchEvent;
import static com.jme3.input.event.TouchEvent.Type.DOWN;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

/**
 * Created by Greenwood0 on 2014-09-08.
 */
public final class SoundEmission extends Scenario {



    private Spatial drum;
    private Spatial guitar;
    private Node drumEmitter;
    private Node guitarEmitter;
    
    private Geometry soundParticle;

    private Spatial drumHandleOut;
    private Spatial guitarHandleOut;
    

    
    private TextBox titleTextBox;
    private TextBox instrumentTextBox;

    private ImageBox imageHintDrum;
    private ImageBox imageHintGuitar;

    private Vector3f drumHandleOutPosition;
    private Vector3f guitarHandleOutPosition;

      // Default text to be seen when scenario starts
    private String titleText = "L'émission du son";
    private float titleTextSize = 0.5f;

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
        drumHandleOutPosition = drumHandleOut.getLocalTranslation().add(sceneDrum.getLocalTranslation());
        guitarHandleOutPosition = guitarHandleOut.getLocalTranslation().add(sceneGuit.getLocalTranslation());

        drumEmitter = new Node();
        drumEmitter.setLocalTranslation(drumHandleOutPosition);
        guitarEmitter = new Node();
        guitarEmitter.setLocalTranslation(guitarHandleOutPosition);
        this.attachChild(guitarEmitter);
        this.attachChild(drumEmitter);
        
        initAudio();
        initTitleBox();
        initImageBoxes();
        initOnTouchEffect();
  
    }

    @Override
    protected void loadMovableObjects() {

        initParticles();
        
        ParticleEmitterControl microphoneControl = this.destinationHandle.getControl(ParticleEmitterControl.class);
        Material mat1 = new Material(assetManager, 
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", new ColorRGBA(1, 0, 1, 1f));
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        this.guitarEmitter.addControl(new AirParticleEmitterControl(this.destinationHandle, 20f, 13f, mat1, AirParticleEmitterControl.AreaType.DOME));
        this.guitarEmitter.getControl(ParticleEmitterControl.class).registerObserver(microphoneControl);
        this.guitarEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);
        this.guitarEmitter.addControl(new PatternGeneratorControl((float) 0.05, soundParticle, 1, 1, 1, false));
        this.guitarEmitter.addControl(new SoundControl("Sounds/guitar.wav",false,2));
        Material mat2 = new Material(assetManager, 
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", new ColorRGBA(0, 1, 1, 1f));
        mat2.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
       
        this.drumEmitter.addControl(new AirParticleEmitterControl(this.destinationHandle, 20f, 13f, mat2, AirParticleEmitterControl.AreaType.DOME));
        this.drumEmitter.getControl(ParticleEmitterControl.class).registerObserver(microphoneControl);
        this.drumEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);
        this.drumEmitter.addControl(new PatternGeneratorControl((float) 0.05, soundParticle, 1, 1, 1, false));
        this.drumEmitter.addControl(new SoundControl("Sounds/drum_taiko.wav",false,2));
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
    private void initParticles()
    {
        Material mat1 = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        
        // instantiate 3d Sound particul model
        if (DEBUG_ANGLE) {
            
            Texture nyan = assetManager.loadTexture("Textures/Nyan_Cat.jpg");
            mat1.setTexture("ColorMap", nyan);
            Quad rect = new Quad(0.5f,0.5f);
            soundParticle = new Geometry("MicTapParticle", rect);
        } else {
            mat1.setColor("Color", new ColorRGBA(0.0f,0.0f,1.0f,1.0f));
            Sphere sphere = new Sphere(10, 10, 0.4f);
            soundParticle = new Geometry("MicTapParticle", sphere);
        }
        
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        soundParticle.setMaterial(mat1);
        soundParticle.setQueueBucket(queueBucket.Opaque);
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

    private void initAudio()
    {
       

    }

    
    @Override
    protected void initTitleBox()
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

        //Was in its position when in the air and rotating
        //Vector3f titleTextPosition = new Vector3f(0f, 8f, 0f);

        titleTextBox.move(titleTextPosition);

        touchable.attachChild(titleTextBox);
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

        this.drumEmitter.getControl(PatternGeneratorControl.class).toggleNewWave(1);
        

    }

    public void guitarTouchEffect()
    {
        this.removeHintImages();

        // Here, we need to get the vector to the mic handle
        //Vector3f receiverHandleVector = particleLinker.GetEmitterDestinationPaths(this);
        //GuitarSoundEmitter.prepareEmeitParticles(receiverHandleVector);

        this.guitarEmitter.getControl(PatternGeneratorControl.class).toggleNewWave(1);
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
                        if(nameToCompare == null){
                            break;
                        }
                        else if (nameToCompare.equals(drum.getName()))
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
    protected boolean simpleUpdate(float tpf) {
        
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
    protected Spatial getInputHandle() {
        return null;
    }
}
