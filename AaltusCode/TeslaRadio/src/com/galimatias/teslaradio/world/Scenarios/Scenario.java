/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.ar4android.vuforiaJME.AppGetter;
import com.galimatias.teslaradio.world.effects.ParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.PatternGeneratorControl;
import com.galimatias.teslaradio.world.effects.SoundControl;
import com.galimatias.teslaradio.world.effects.StaticWireParticleEmitterControl;
import com.galimatias.teslaradio.world.observer.SignalObserver;
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


/**
 * Abstract class that regroup a scenario.
 * @author Alexandre Hamel
 */
public abstract class Scenario extends Node implements SignalObserver {

    private final static String TAG = "Scenario";

    private float cumulatedRot = 0;
    
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
     * The foregound camera of the scene
     */
    protected Camera cam;

    /**
     * Defines the number of particle per auto-gen wave
     */
    protected int particlePerWave = 4;
    /**
     * Defines the time between 2 auto-wave emission
     */
    protected float waveTime = 1;
    
    /*Path of the background sound*/
    protected String backgroundSound = null;
    
    /**
     * Default parameters for textBoxes
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

    public Scenario(com.jme3.renderer.Camera Camera, Spatial destinationHandle)
    {
        assetManager = AppGetter.getAssetManager();
        this.Camera = Camera;
        this.destinationHandle = destinationHandle;
        this.setUserData("angleX", 0f);
    }
    
    public Scenario(com.jme3.renderer.Camera Camera, Spatial destinationHandle, String bgm)
    {
        this(Camera, destinationHandle);
        
        this.backgroundSound = bgm;
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

    /**
     * Call all of the methods when scenario is on Node A, can be override for 
     * certain scenarios.
     */
    protected void onFirstNodeActions() {
        startAutoGeneration();
    }
    
    /**
     * Call all of the methods when scenario is on Node B, can be override for 
     * certain scenarios.
     */
    protected void onSecondNodeActions() {
        startBackgroundSound();
    }
    
    /**
     * Called when the scenario is detached from one of the two nodes
     */
    protected void notOnNodeActions() {
        if (this.needAutoGenIfMain) {
            stopAutoGeneration();
        }
        
        stopBackgroundSound();
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

    private void startBackgroundSound() {
        if(this.backgroundSound != null){
            this.getControl(SoundControl.class).playSound(true);
            this.getControl(SoundControl.class).setEnabled(true);
        }
    }
    
    private void stopBackgroundSound() {
        if(this.backgroundSound != null){
            this.getControl(SoundControl.class).stopSound();
            this.getControl(SoundControl.class).setEnabled(false);
        }
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
        signalEmitter.addControl(new StaticWireParticleEmitterControl(path.getMesh(), 3.5f, cam));
        signalEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);
    }
}

