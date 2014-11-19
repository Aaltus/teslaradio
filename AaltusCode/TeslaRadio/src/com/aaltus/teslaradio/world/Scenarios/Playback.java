/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaltus.teslaradio.world.Scenarios;

import static com.aaltus.teslaradio.world.Scenarios.Scenario.DEBUG_ANGLE;

import com.ar4android.vuforiaJME.AppGetter;
import com.aaltus.teslaradio.world.effects.AirParticleEmitterControl;
import com.aaltus.teslaradio.world.effects.Arrows;
import com.aaltus.teslaradio.world.effects.DynamicWireParticleEmitterControl;
import com.aaltus.teslaradio.world.effects.FadeControl;
import com.aaltus.teslaradio.world.effects.LookAtCameraControl;
import com.aaltus.teslaradio.world.effects.ParticleEmitterControl;
import com.aaltus.teslaradio.world.effects.PatternGeneratorControl;
import com.aaltus.teslaradio.world.effects.SoundControl;
import com.aaltus.teslaradio.world.effects.StaticWireParticleEmitterControl;
import com.aaltus.teslaradio.world.effects.TextBox;
import com.aaltus.teslaradio.world.observer.EmitterObserver;
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
    private Vector3f translationIncrement;
    private boolean isTouched = false;
    private float ampliScale = 0f;
    private int touchCount = 0;
    
    private Geometry soundParticle;
    
    private TextBox titleTextBox;
    
    private Node sliderArrow;
   
    // Scenario entry point
    private Spatial  cableHandleIn;
    
    // The speakers entries
    private Spatial speaker1HandleIn;
    private Spatial speaker2HandleIn;
    
    // The splitter handle
    private Spatial splitterHandle;
      
    // The speakers output handles
    private Spatial speaker1HandleOut;
    private Spatial speaker2HandleOut;
    
    // The speaker emitters
    private Node     speakerEmitter1;
    private Node     speakerEmitter2;
    
    private Spatial speaker1;
    private Spatial speaker2;
    
    private Node     cableEmitter = new Node();
    private Node     cableSpeaker1Emitter = new Node();
    private Node     cableSpeaker2Emitter = new Node();
    
    public Playback(ScenarioCommon sc, Camera Camera, Spatial destinationHandle) {
        
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
        
        cableHandleIn = scene.getChild("Handle.In");
        cableHandleIn.setName("Cableinput");
        
        splitterHandle = scene.getChild("Splitter.Handle");
        
        speaker1HandleIn = scene.getChild("Speaker1.Handle.In");
        speaker2HandleIn = scene.getChild("Speaker2.Handle.In");
        
        speaker1HandleOut = scene.getChild("Speaker1.Handle.Out");
        speaker2HandleOut = scene.getChild("Speaker2.Handle.Out");
                
        speakerEmitter1 = new Node();
        speakerEmitter1.setLocalTranslation(speaker1HandleOut.getLocalTranslation().add(scene.getLocalTranslation()));
        Quaternion quat = new Quaternion();
        quat.fromAngleAxis(3*pi/2, Vector3f.UNIT_Z);
        speakerEmitter1.setLocalRotation(quat);
        scene.attachChild(speakerEmitter1);
        
        speakerEmitter2 = new Node();
        speakerEmitter2.setLocalTranslation(speaker2HandleOut.getLocalTranslation().add(scene.getLocalTranslation()));
        speakerEmitter2.setLocalRotation(quat);
        scene.attachChild(speakerEmitter2);
               
        Node pathIn = (Node) scene.getChild("NurbsPath.002");
        Geometry inputCablePath = (Geometry) pathIn.getChild("NurbsPath.002");
        inputCablePath.setCullHint(cullHint.Always);
        
        Node pathInSpeaker1 = (Node) scene.getChild("NurbsPath");
        Geometry inputPathSpeaker1 = (Geometry) pathInSpeaker1.getChild("NurbsPath");
        inputCablePath.setCullHint(cullHint.Always);
        
        Node pathInSpeaker2 = (Node) scene.getChild("NurbsPath.001");
        Geometry inputPathSpeaker2 = (Geometry) pathInSpeaker2.getChild("NurbsPath.001");
        inputCablePath.setCullHint(cullHint.Always);
        
        initParticlesEmitter(cableEmitter, cableHandleIn, inputCablePath, null);
        initParticlesEmitter(cableSpeaker1Emitter, splitterHandle, inputPathSpeaker1, null);
        initParticlesEmitter(cableSpeaker2Emitter, splitterHandle, inputPathSpeaker2, null);
        
        cableSpeaker1Emitter.setName("splitterSpeaker1");
        cableSpeaker2Emitter.setName("splitterSpeaker2");
        
        cableEmitter.getControl(ParticleEmitterControl.class).registerObserver(cableSpeaker1Emitter.getControl(ParticleEmitterControl.class));
        cableEmitter.getControl(ParticleEmitterControl.class).registerObserver(cableSpeaker2Emitter.getControl(ParticleEmitterControl.class));
        
        speakerEmitter1.addControl(new AirParticleEmitterControl(speaker1HandleOut, 10f, 13f, mat1, AirParticleEmitterControl.AreaType.DOME));
        speakerEmitter1.getControl(ParticleEmitterControl.class).setEnabled(true);
        speakerEmitter1.addControl(new PatternGeneratorControl((float) 0.05, soundParticle, 1, 1, 1, false));
        
        speakerEmitter2.addControl(new AirParticleEmitterControl(speaker2HandleOut, 10f, 13f, mat1, AirParticleEmitterControl.AreaType.DOME));
        speakerEmitter2.getControl(ParticleEmitterControl.class).setEnabled(true);
        speakerEmitter2.addControl(new PatternGeneratorControl((float) 0.05, soundParticle, 1, 1, 1, false));
        
        cableSpeaker1Emitter.getControl(ParticleEmitterControl.class).registerObserver(this);
        cableSpeaker2Emitter.getControl(ParticleEmitterControl.class).registerObserver(this);
                
        Vector3f handleSliderBegin = scene.getChild("Slider.Handle.Begin").getLocalTranslation();
        scene.getChild("Slider.Handle.Begin").setCullHint(cullHint.Always);
        Vector3f handleSliderEnd = scene.getChild("Slider.Handle.End").getLocalTranslation();
        scene.getChild("Slider.Handle.End").setCullHint(cullHint.Always);
        translationIncrement = handleSliderEnd.subtract(handleSliderBegin).divide(4);

        //initTitleBox();
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
        
        speaker1 = scene.getChild("Rectangle0");
        speaker1.setLocalScale(0.5f);
        speaker1.setName("Speaker1");
        
        speaker2 = scene.getChild("Rectangle0.003");
        speaker2.setLocalScale(0.5f);
        speaker2.setName("Speaker2");
        
        this.spotlight = ScenarioCommon.spotlightFactory();
        
        touchable.attachChild(speaker1);
        touchable.attachChild(ampliSliderButton);
        touchable.attachChild(ampliSliderBox);  
        
    }
    
    private void loadArrows() {
        
        sliderArrow = new Node();
        sliderArrow.move(ampliSliderBox.getLocalTranslation().add(0.0f,1.0f,0.0f));
        sliderArrow.addControl(new Arrows("touch", assetManager, 3));
        //sliderArrow = new Arrows("touch", ampliSliderBox.getLocalTranslation().add(0.0f,1.0f,0.0f), assetManager, 1);
        LookAtCameraControl control1 = new LookAtCameraControl(Camera);
        sliderArrow.addControl(control1);
        sliderArrow.setLocalScale(2f);
        this.attachChild(sliderArrow);
    }
    
    private void initParticlesEmitter(Node signalEmitter, Spatial handle, Geometry path, Camera cam) {
        scene.attachChild(signalEmitter);
        signalEmitter.setLocalTranslation(handle.getLocalTranslation()); // TO DO: utiliser le object handle blender pour position
        signalEmitter.addControl(new StaticWireParticleEmitterControl(path.getMesh(), 3.5f, cam));
        signalEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);
    }
    
    /**
     * Remove hints, is called after touch occurs
     */
    private void removeHintImages()
    {
        sliderArrow.getControl(FadeControl.class).setShowImage(false);
        sliderArrow.getControl(Arrows.class).resetTimeLastTouch();
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
                        } else if (nameToCompare.equals("Speaker1")) {
                            this.speakerTouchEffect(1f,1);
                            break;
                        } else if (nameToCompare.equals("Speaker2")) {
                            this.speakerTouchEffect(1f,2);
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
        //sliderArrow.simpleUpdate(tpf);
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
        return cableEmitter;
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
    
    public void speakerTouchEffect(float particleScale, int speakerId)
    {

        // Here, we need to get the vector to the mic handle
        //Vector3f receiverHandleVector = particleLinker.GetEmitterDestinationPaths(this);
        //GuitarSoundEmitter.prepareEmeitParticles(receiverHandleVector);
        if (speakerId == 1) {
            this.speakerEmitter1.getControl(PatternGeneratorControl.class).toggleNewWave(ampliScale * particleScale);
        } else {
            this.speakerEmitter2.getControl(PatternGeneratorControl.class).toggleNewWave(ampliScale * particleScale);
        }
    }

    @Override
    public void emitterObserverUpdate(Spatial spatial, String notifierId) {       
        
        if (speakerEmitter1 != null && notifierId.equals("splitterSpeaker1") && touchCount != 0) {
            speakerTouchEffect(spatial.getLocalScale().length(),1);
        } else if (speakerEmitter2 != null && notifierId.equals("splitterSpeaker2") && touchCount != 0) {
            speakerTouchEffect(spatial.getLocalScale().length(),2);
        }
    }

    @Override
    protected void objectEmphasis() {
        if (this.spotlight != null) {            
            switch(this.currentObjectToEmphasisOn) {
                // Attach on microphone
                case 0:
                    Node dummy = new Node();
                    dummy.attachChild(speaker1);
                    dummy.attachChild(speaker2);
                    this.spotlight.setLocalTranslation(dummy.getLocalTranslation().add(0.0f,-dummy.getLocalTranslation().y,0.0f));
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
