package com.aaltus.teslaradio.world.Scenarios;

import com.aaltus.teslaradio.subject.AudioOptionEnum;
import static com.aaltus.teslaradio.world.Scenarios.Scenario.DEBUG_ANGLE;
import com.aaltus.teslaradio.world.effects.AirParticleEmitterControl;
import com.aaltus.teslaradio.world.effects.Arrows;
import com.aaltus.teslaradio.world.effects.FadeControl;
import com.aaltus.teslaradio.world.effects.ImageBox;
import com.aaltus.teslaradio.world.effects.LookAtCameraControl;
import com.aaltus.teslaradio.world.effects.ParticleEmitterControl;
import com.aaltus.teslaradio.world.effects.PatternGeneratorControl;
import com.aaltus.teslaradio.world.effects.SoundControl;
import com.aaltus.teslaradio.world.effects.TextBox;
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
    
    //Arrows
    Node moveArrow;
    Node drumArrow;
    Node guitarArrow;

    public SoundEmission(ScenarioCommon sc, com.jme3.renderer.Camera Camera, Spatial destinationHandle)
    {

        super(sc,Camera, destinationHandle);
        this.hasBackgroundSound = false;
        touchable = new Node();
        touchable.setName("Touchable");
        this.attachChild(touchable);
        this.setName("SoundEmission");
        loadUnmovableObjects();
        loadMovableObjects();
        loadArrows();
    }

    @Override
    protected void loadUnmovableObjects() {

        scene = (Node) assetManager.loadModel("Models/SoundEmission/Scene_wUV.j3o");
        scene.scale(0.75f);
        this.attachChild(scene);
        
        guitar = scene.getChild("Guitar");
        drum = scene.getChild("Tambour");
        
        touchable.attachChild(guitar);
        touchable.attachChild(drum);

        guitarHandleOut = scene.getChild("Guitar_Output_Handle");
        drumHandleOut = scene.getChild("Drum_Output_Handle");
        drumHandleOutPosition = drumHandleOut.getLocalTranslation();
        guitarHandleOutPosition = guitarHandleOut.getLocalTranslation();

        drumEmitter = new Node();
        drumEmitter.setLocalTranslation(drumHandleOutPosition);
        guitarEmitter = new Node();
        guitarEmitter.setLocalTranslation(guitarHandleOutPosition);
        this.attachChild(guitarEmitter);
        this.attachChild(drumEmitter);
        
        initAudio();
        //initTitleBox();
        initOnTouchEffect();
        
    }

    @Override
    protected void loadMovableObjects() {

        initParticles();
        
        Material mat1 = new Material(assetManager, 
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", new ColorRGBA(0, 0, 1, 0.5f));
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        
        Material mat2 = new Material(assetManager, 
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", new ColorRGBA(0, 0, 1, 0.5f));
        mat2.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        if(destinationHandle != null){
            ParticleEmitterControl microphoneControl = this.destinationHandle.getControl(ParticleEmitterControl.class);

            this.guitarEmitter.addControl(new AirParticleEmitterControl(this.destinationHandle, 10f, 19f, mat1, AirParticleEmitterControl.AreaType.DOME));
            this.guitarEmitter.getControl(ParticleEmitterControl.class).registerObserver(microphoneControl);
            this.guitarEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);
            this.guitarEmitter.addControl(new PatternGeneratorControl((float) 0.05, soundParticle, 1, 1, 1, false));
            this.guitarEmitter.addControl(new SoundControl("Sounds/guitar.ogg",false,5));

            this.drumEmitter.addControl(new AirParticleEmitterControl(this.destinationHandle, 10f, 19f, mat2, AirParticleEmitterControl.AreaType.DOME));
            this.drumEmitter.getControl(ParticleEmitterControl.class).registerObserver(microphoneControl);
            this.drumEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);
            this.drumEmitter.addControl(new PatternGeneratorControl((float) 0.05, soundParticle, 1, 1, 1, false));
            this.drumEmitter.addControl(new SoundControl("Sounds/drum_taiko.ogg",false,5));
        }
        
        this.spotlight = ScenarioCommon.spotlightFactory();
    }

    @Override
    public void restartScenario() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
            
            Texture nyan = assetManager.loadTexture("Textures/Nyan_Cat.png");
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

        //Was in its position when in the air and rotating
        //Vector3f titleTextPosition = new Vector3f(0f, 8f, 0f);

        titleTextBox.move(titleTextPosition);

        touchable.attachChild(titleTextBox);
    }

    private void loadArrows()
    {        
        drumArrow = new Node();
        drumArrow.move(drumHandleOutPosition);
        drumArrow.addControl(new Arrows("touch", assetManager, 3));
        LookAtCameraControl control1 = new LookAtCameraControl(Camera);
        drumArrow.addControl(control1);
        this.attachChild(drumArrow);
        
        guitarArrow = new Node();
        guitarArrow.move(guitarHandleOutPosition.add(0.0f,1.0f,0.0f));
        guitarArrow.addControl(new Arrows("touch", assetManager, 3));
        LookAtCameraControl control2 = new LookAtCameraControl(Camera);
        guitarArrow.addControl(control2);
        guitarArrow.setLocalScale(8f);
        this.attachChild(guitarArrow);
        
        moveArrow = new Node();
        moveArrow.addControl(new Arrows("move", assetManager, 10));
        this.attachChild(moveArrow);
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
    private void removeHintImages()
    {
        drumArrow.getControl(FadeControl.class).setShowImage(false);
        drumArrow.getControl(Arrows.class).resetTimeLastTouch();
        guitarArrow.getControl(FadeControl.class).setShowImage(false);
        guitarArrow.getControl(Arrows.class).resetTimeLastTouch();
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
                    }
                }
                break;
        }
    }

    @Override
    protected boolean simpleUpdate(float tpf) {
        
        //drumArrow.simpleUpdate(tpf);
        //guitarArrow.simpleUpdate(tpf);
        //moveArrow.simpleUpdate(tpf);
        
        if (this.emphasisChange) {
            objectEmphasis();
            this.emphasisChange = false;
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

    @Override
    protected void initPatternGenerator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void objectEmphasis() {
        
        if (this.spotlight != null) {            
            switch(this.currentObjectToEmphasisOn) {
                // Attach on drum
                case 0:
                    this.spotlight.setLocalTranslation(scene.getLocalTranslation().add(0.0f,-scene.getLocalTranslation().y,0.0f));
                    this.spotlight.setLocalScale(new Vector3f(7.0f,30.0f,7.0f));
                    this.attachChild(this.spotlight);
                    break;
                default:
                    this.detachChild(this.spotlight);
                    break;
            }
        }
    }
    
    @Override
    protected void onAudioOptionTouched(AudioOptionEnum value){
        if(value == AudioOptionEnum.DRUM){
            this.drumTouchEffect();
        }else if(value == AudioOptionEnum.GUITAR){
            this.guitarTouchEffect();
        }
    }
}
