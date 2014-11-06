/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import static com.galimatias.teslaradio.world.Scenarios.Scenario.DEBUG_ANGLE;
import com.galimatias.teslaradio.world.effects.AirParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.Arrows;
import com.galimatias.teslaradio.world.effects.DynamicWireParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.FadeControl;
import com.galimatias.teslaradio.world.effects.LookAtCameraControl;
import com.galimatias.teslaradio.world.effects.ParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.PatternGeneratorControl;
import com.galimatias.teslaradio.world.effects.SoundControl;
import com.galimatias.teslaradio.world.effects.TextBox;
import com.galimatias.teslaradio.world.observer.EmitterObserver;
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
 * @author Batcave
 */
public final class Playback extends Scenario implements EmitterObserver {
        
    private String titleText = "Hautparleur";
    
    private Spatial ampliSliderButton;
    private Spatial ampliSliderBox;
    private Spatial speaker;
    private Vector3f translationIncrement;
    private boolean isTouched = false;
    private float ampliScale = 0f;
    private int touchCount = 0;
    
    private Geometry soundParticle;
    
    private TextBox titleTextBox;
    
    private Arrows sliderArrow;
    
    private Spatial  speakerHandleOut;
    private Spatial  speakerHandleIn;
    private Vector3f speakerHandleOutPosition;
    private Vector3f speakerHandleInPosition;
    private Node     speakerEmitter;
    private Node     speakerIn = new Node();
    
    Playback(ScenarioCommon sc, Camera Camera, Spatial destinationHandle) {
        
        super(sc, Camera, destinationHandle);
        this.setName("Playback");
        loadUnmovableObjects();
        loadMovableObjects();
        loadArrows();
    }

    @Override
    protected void loadUnmovableObjects() {
        initParticles();
        
        scene = (Node) assetManager.loadModel("Models/Playback/Speaker.j3o");
        scene.setName("Playback");
        this.attachChild(scene);       
        
        Material mat1 = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", new ColorRGBA(0, 0, 1, 0.5f));
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        speakerHandleIn = scene.getChild("Speaker.Handle.In");
        speakerHandleIn.setName("InputSpeaker");
        speakerHandleOut = scene.getChild("Speaker.Handle.Out");
        speakerHandleOutPosition = speakerHandleOut.getLocalTranslation().add(scene.getLocalTranslation());
        speakerHandleInPosition = speakerHandleIn.getLocalTranslation();
        
        speakerEmitter = new Node();
        speakerEmitter.setLocalTranslation(speakerHandleOutPosition);
        Quaternion quat = new Quaternion();
        quat.fromAngleAxis(3*pi/2, Vector3f.UNIT_Z);
        speakerEmitter.setLocalRotation(quat);
        scene.attachChild(speakerEmitter);
        
        speakerIn = new Node();
        speakerIn.setLocalTranslation(speakerHandleInPosition);
        scene.attachChild(speakerIn);
        
        speakerIn.addControl(new DynamicWireParticleEmitterControl(speakerEmitter, 1000f));
        speakerEmitter.addControl(new AirParticleEmitterControl(speakerHandleOut, 20f, 13f, mat1, AirParticleEmitterControl.AreaType.DOME));
        speakerEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);
        speakerEmitter.addControl(new PatternGeneratorControl((float) 0.05, soundParticle, 1, 1, 1, false));
        speakerIn.getControl(ParticleEmitterControl.class).registerObserver(this);
        speakerIn.getControl(ParticleEmitterControl.class).setEnabled(true);
        
        Vector3f handleSliderBegin = scene.getChild("Slider.Handle.Begin").getLocalTranslation();
        Vector3f handleSliderEnd = scene.getChild("Slider.Handle.End").getLocalTranslation();
        translationIncrement = handleSliderEnd.subtract(handleSliderBegin).divide(4);
        
        
        initTitleBox();
    }

    @Override
    protected void loadMovableObjects() {
        
        touchable = new Node();
        touchable.setName("Touchable");
        this.attachChild(touchable);
        
        ampliSliderButton = scene.getChild("Button.000");
        ampliSliderBox = scene.getChild("Cube");
        ampliSliderButton.setName("SliderButton");
        ampliSliderBox.setName("SliderBox");
        
        speaker = scene.getChild("Box01");
        speaker.setName("Speaker");
        
        this.spotlight = ScenarioCommon.spotlightFactory();
        
        touchable.attachChild(speaker);
        touchable.attachChild(ampliSliderButton);
        touchable.attachChild(ampliSliderBox);  
        
    }
    
    private void loadArrows() {
        
        sliderArrow = new Arrows("touch", ampliSliderBox.getLocalTranslation().add(0.0f,1.0f,0.0f), assetManager, 1);
        LookAtCameraControl control1 = new LookAtCameraControl(Camera);
        sliderArrow.addControl(control1);
        this.attachChild(sliderArrow);
    }
    
    /**
     * Remove hints, is called after touch occurs
     */
    private void removeHintImages()
    {
        sliderArrow.getControl(FadeControl.class).setShowImage(false);
        sliderArrow.resetTimeLastTouch();
    }

    @Override
    public void restartScenario() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
                        } else if (nameToCompare.equals("Speaker")) {
                            this.speakerTouchEffect();
                            break;
                        } else if (nameToCompare.equals(titleTextBox.getName())) {
                            //this.textTouchEffect();
                            showInformativeMenu = true;
                            break;
                        } else if (nameToCompare.equals("SliderButton")) {
                            touchCount++;
                            isTouched = true;
                            this.removeHintImages();
                            break;
                        } else if (nameToCompare.equals("SliderBox")) {
                            touchCount++;
                            isTouched = true;
                            this.removeHintImages();
                            break;
                        }    
                    }
                }
                break;
        }
    }

    @Override
    protected boolean simpleUpdate(float tpf) {
        
        if (this.emphasisChange) {
            objectEmphasis();
            this.emphasisChange = false;
        }
        
        ampliSliderUpdate();
        sliderArrow.simpleUpdate(tpf);
        return false;
    }
    
    private void ampliSliderUpdate() {
        if (isTouched) {
            switch(touchCount) {
                case 1:
                case 2:
                case 3:
                case 4:
                    ampliSliderButton.move(translationIncrement);
                    ampliScale = touchCount*0.25f;
                    break;
                case 5:
                case 6:
                case 7:
                    ampliSliderButton.move(translationIncrement.negate());
                    ampliScale = 2f - touchCount*0.25f;
                    break;
                case 8:
                    ampliSliderButton.move(translationIncrement.negate());
                    ampliScale = 2f - touchCount*0.25f;
                    touchCount = 0;
                    break;
            }
            
            isTouched = false;
        }
        
        /*TR-261 apparently we don't want this, but in this scenario we want! */
        this.updateVolume(ampliScale);

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
        return speakerIn;
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

    @Override
    public void signalEndOfPath(Geometry caller, float magnitude) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void initPatternGenerator() {
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
    
    public void speakerTouchEffect()
    {

        // Here, we need to get the vector to the mic handle
        //Vector3f receiverHandleVector = particleLinker.GetEmitterDestinationPaths(this);
        //GuitarSoundEmitter.prepareEmeitParticles(receiverHandleVector);

        this.speakerEmitter.getControl(PatternGeneratorControl.class).toggleNewWave(1);
    }

    @Override
    public void emitterObserverUpdate(Spatial spatial, String notifierId) {       
        
        if (speakerEmitter != null) {
            if (touchCount != 0) {
                speakerTouchEffect();
            }
        }
    }

    @Override
    protected void objectEmphasis() {
        if (this.spotlight != null) {            
            switch(this.currentObjectToEmphasisOn) {
                // Attach on microphone
                case 0:
                    this.spotlight.setLocalTranslation(speaker.getLocalTranslation().add(0.0f,-speaker.getLocalTranslation().y,0.0f));
                    this.spotlight.setLocalScale(new Vector3f(5.0f,20.0f,5.0f));
                    scene.attachChild(this.spotlight);
                    break;
                case 1:
                    this.spotlight.setLocalTranslation(ampliSliderBox.getLocalTranslation().add(0.0f,-ampliSliderBox.getLocalTranslation().y,0.0f));
                    this.spotlight.setLocalScale(new Vector3f(5.0f,20.0f,5.0f));
                    scene.attachChild(this.spotlight);
                    break;    
                default:
                    scene.detachChild(this.spotlight);
                    break;
            }
        }
    }
    
}
