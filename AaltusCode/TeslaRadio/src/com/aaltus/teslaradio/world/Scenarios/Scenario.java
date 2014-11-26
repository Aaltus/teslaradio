/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaltus.teslaradio.world.Scenarios;

import com.ar4android.vuforiaJME.AppGetter;
import com.aaltus.teslaradio.subject.AudioOptionEnum;
import com.aaltus.teslaradio.world.effects.DrumGuitarSoundControl;
import com.aaltus.teslaradio.world.effects.NoiseControl;
import com.aaltus.teslaradio.world.effects.ParticleEmitterControl;
import com.aaltus.teslaradio.world.effects.PatternGeneratorControl;
import com.aaltus.teslaradio.world.effects.SoundControl;
import com.aaltus.teslaradio.world.effects.StaticWireParticleEmitterControl;
import com.aaltus.teslaradio.world.observer.SignalObserver;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.utils.AppLogger;
import java.util.ArrayList;


/**
 * Abstract class that regroup a scenario.
 * @author Alexandre Hamel
 */
public abstract class Scenario extends Node implements SignalObserver {

    private final static String TAG = "Scenario";

    private float cumulatedRot = 0;
    
    protected boolean hasBackgroundSound = true;
    protected boolean isFirst;
    
    protected ScenarioCommon scenarioCommon = null;
    protected final static boolean DEBUG_ANGLE = false;
    /**
     * Set to true to start autogeneration when scenario is the main scenario
     */
    protected boolean needAutoGenIfMain = false;
   
    /**
     * The destination of the current scenario
     */
    protected Spatial destinationHandle;
    
    /**
     * AssetManager object needed to loal model and souns in a scenario
     *
     */
    protected AssetManager assetManager;
    
    /**
     * Camera linked to the scenario. All "LookAt" and camera-dependant effect
     * will use this camera.
     */
    protected com.jme3.renderer.Camera Camera = null;
    
    // this is PIIIIIII! (kick persian)
    protected final float pi = (float) FastMath.PI;

    
    public void setCamera(Camera cam){
        this.Camera = cam;
    }

    /**
     * Setting that to true
     *
     */
    protected boolean showInformativeMenu = false;

     /** REMOVED THIS AFTER ALEX CHANGES TO CIRCLE EFFECT
     *
     */
    protected Node movableObjects = new Node("movable");

    /**
     * Ray Picking will be done on this node.
     * Ideally created directly in Blender model to regroup clickable object.
     *
     */
    protected Node touchable;

    /**
     * Blender model Node.
     *
     */
    protected Node scene;
    
    /**
     * The foregound camera of the scene.
     */
    protected Camera cam;
    
    /**
     * The spotlight which will be displayed on the object talked in the 
     * scenario's tutorial.
     */
    protected Node spotlight;
    protected boolean emphasisChange = false;

    /**
     * Defines the number of particle per auto-gen wave.
     */
    protected int particlePerWave = 1;
    
    /**
     * Current object to display the spotlight on.
     */
    protected int currentObjectToEmphasisOn = 0;
            
    /**
     * Defines the time between 2 auto-wave emission.
     */
    protected float waveTime = 1;

    /**
     * This boolean defines if the scenarios should look at each others.
     */
    protected boolean needFixedScenario = false;
    
    /*Path of the background sound*/
    protected String backgroundSound = null;
    
    /**
     * Default parameters for textBoxes.
     */
    protected final float TEXTSIZE             = 0.5f;
    protected final ColorRGBA TEXTCOLOR        = new ColorRGBA(125/255f, 249/255f, 255/255f, 1f);  
    protected final ColorRGBA TEXTBOXCOLOR     = new ColorRGBA(0.1f, 0.1f, 0.1f, 0.5f);;
    protected final float TITLEWIDTH           = 5.2f; 
    protected final float TITLEHEIGHT          = 0.8f;
    protected final BitmapFont.Align ALIGNEMENT = BitmapFont.Align.Center;
    protected final boolean SHOWTEXTDEBUG      = false;
    protected final boolean TEXTLOOKATCAMERA   = false;
    
    
    /**
     * We make the default constructor private to prevent its use.
     * We always want a assetmanager and a camera
     */
        
    private Scenario()
    {

    }

    public Scenario(ScenarioCommon sc, com.jme3.renderer.Camera Camera, Spatial destinationHandle)
    {
        assetManager = AppGetter.getAssetManager();
        this.Camera = Camera;
        this.destinationHandle = destinationHandle;
        this.setUserData("angleX", 0f);
        this.scenarioCommon = sc;
    }
    
    public Scenario(ScenarioCommon sc, com.jme3.renderer.Camera Camera, Spatial destinationHandle, String bgm)
    {
        this(sc, Camera, destinationHandle);
        
        this.backgroundSound = bgm;
        this.scenarioCommon = sc;
        if(this.backgroundSound != null){
            this.addControl(new SoundControl(this.backgroundSound,false,1));
        }
    }
    
 
    
    /**
     * Methods to load the associated 3D objects with the scenario that are unmovable.
     */
    protected abstract void loadUnmovableObjects();

    /**
     * TO BE REMOVED BECAUSE MOVABLE
     *
     */
    protected abstract void loadMovableObjects();

    /**
     * Method to restart the scenario if a refresh has been called by a
     * the scenario manager container.
     *
     */
    public abstract void restartScenario();

    /**
     * Pass down touch event from scenario manager to the scenarios.
     * The arguments are directly the touch event from JME3. The scenario can override this make
     * make ray picking for example.
     *
     * @param name
     * @param touchEvent
     * @param v
     */
    public abstract void onScenarioTouch(String name, TouchEvent touchEvent, float v);
    
    /**
     * Get the list of objects to display within two given scenarios.
     * @param objectsToShow 
     */
    protected abstract void objectEmphasis();

    /**
     * Called periodically from scenario manager to make the simpleUpdate of the scenario.
     */
    protected abstract boolean simpleUpdate(float tpf);

    /**
     * Setter to set the speed of the animation and the particuls in the scenario.
     * Called by the scenario manager.
     */
    public abstract void setGlobalSpeed(float speed);

    /**
     * To receive events from the device microphone from scenario manager.
     */
    public abstract void onAudioEvent();
    
    /**
     * Getter to get to handle of the input of the next scenario.
     */
    protected abstract Spatial getInputHandle();
    
    /**
     * Initialization of the title boxes of a scenario.
     */
    protected abstract void initTitleBox();
    
    /**
     * Initialize the pattern generators of a scenario.
     */
    protected abstract void initPatternGenerator();
    
    protected void initDrumGuitarSound(){
        Spatial handler = this.getInputHandle();
        if(handler != null){
            handler.addControl(new DrumGuitarSoundControl());
            handler.getControl(DrumGuitarSoundControl.class).setEnabled(false);
        }
    }

    protected void onAudioOptionTouched(AudioOptionEnum value){
        Spatial handler = this.getInputHandle();
        AppLogger.getInstance().e("AudioTouched", value.toString());
        if(handler != null){
           DrumGuitarSoundControl dgsc =  handler.getControl(DrumGuitarSoundControl.class);
           PatternGeneratorControl pgc = handler.getControl(PatternGeneratorControl.class);
           switch(value){
               case IPOD:
                   this.startAutoGeneration();
                   break;
               case SCENARIO_SWITCH:
               case NOSOUND:
                   break;
               default:
                   this.stopAutoGeneration();
                   dgsc.setNextInstrument(value);
                   dgsc.setEnabled(true);
                   pgc.toggleNewWave(1);
           }
        }
    }
    /**
     * Call all of the methods when scenario is on Node A, can be override for 
     * certain scenarios.
     *
     */
    protected void onFirstNodeActions() {
        this.isFirst = true;
        if(this.needAutoGenIfMain){
            startAutoGeneration();
        }
    }
    
    public boolean getNeedsBackgroundSound(){
        return this.hasBackgroundSound;
    }
    /**
     * Call all of the methods when scenario is on Node B, can be override for 
     * certain scenarios.
     */
    protected void onSecondNodeActions() {
        this.isFirst = false;
        //startBackgroundSound();
    }
    
    /**
     * Called when the scenario is detached from one of the two nodes
     */
    protected void notOnNodeActions() {
        
        if (this.needAutoGenIfMain) {
            stopAutoGeneration();
        }
        //stopBackgroundSound();
    }
    
    /**
     * Start the auto generation of particles
     */
    private void startAutoGeneration() {
        this.getInputHandle().getControl(PatternGeneratorControl.class).startAutoPlay(1,this.particlePerWave);
    };
    
    /**
     * Stop the auto generation of particles
     */
    private void stopAutoGeneration() {
       if(this.getInputHandle() != null) { 
            this.getInputHandle().getControl(PatternGeneratorControl.class).stopAutoPlay();
       }
    };
    
    /**
     * Sets the base particle for auto-generation
     */
    protected void setAutoGenerationParticle(Spatial particle){
      this.getInputHandle().getControl(PatternGeneratorControl.class).
              setBaseParticle(particle);
    };
    
    public boolean getNeedsAutoGen() {
        return this.needAutoGenIfMain;
    }

  
    /**
     * This method will apply an opposite trackable rotation on the model, preventing it from rotating
     * @param ZXangle
     */
    protected void invRotScenario(float ZXangle) {
        if (Math.abs(ZXangle - cumulatedRot) > (3.1416f / 40f)){
            Quaternion rot = new Quaternion();
            rot.fromAngleAxis(-ZXangle, Vector3f.UNIT_Y);
            scene.setLocalRotation(rot);
            cumulatedRot = ZXangle;
        }
    }

    protected void initStaticParticlesEmitter(Node signalEmitter, Spatial handle, Geometry path, Camera cam) {

        scene.attachChild(signalEmitter);
        signalEmitter.setLocalTranslation(handle.getLocalTranslation()); // TO DO: utiliser le object handle blender pour position
        signalEmitter.setLocalRotation(handle.getWorldRotation());
        signalEmitter.addControl(new StaticWireParticleEmitterControl(path.getMesh(), 3.5f, cam));
        signalEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);
    }
    
    public void setCurrentObjectEmphasis(int currentObjectToEmphasisOn) {
        this.currentObjectToEmphasisOn = currentObjectToEmphasisOn;
        this.emphasisChange = true;
    }
    
   
    protected void updateVolume(float volume){
        if(this.scenarioCommon.getNoiseControl() != null){
        this.scenarioCommon.getNoiseControl().updateVolume(volume);
        }
    }
    protected void updateNoise(float noise){
        if(this.scenarioCommon.getNoiseControl() != null){
            this.scenarioCommon.getNoiseControl().updateNoiseLevel(noise);
        }
    }

    public boolean getNeedFixedScenario() { return needFixedScenario; }
}

