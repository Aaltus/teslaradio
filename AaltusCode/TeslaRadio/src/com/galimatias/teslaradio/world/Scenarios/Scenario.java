/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.ar4android.vuforiaJME.AppGetter;
import com.galimatias.teslaradio.world.observer.SignalObserver;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;


/**
 * Abstract class that regroup a scenario.
 * @author Alexandre Hamel
 */
public abstract class Scenario extends Node implements SignalObserver {

    private final static String TAG = "Scenario";
    
    protected final static boolean DEBUG_ANGLE = true;
   
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
     * We make the default constructor private to prevent its use.
     * We always want a assetmanager and a camera
     */
    
    /**
     * Default parameters for textBoxes
     */
    protected float textSize             = 0.5f;
    protected ColorRGBA textColor        = new ColorRGBA(125/255f, 249/255f, 255/255f, 1f);  
    protected ColorRGBA textBoxColor     = new ColorRGBA(0.1f, 0.1f, 0.1f, 0.5f);;
    protected float titleWidth           = 5.2f; 
    protected float titleHeight          = 0.8f;
    protected BitmapFont.Align alignment = BitmapFont.Align.Center;
    protected boolean showTextDebug      = false;
    protected boolean textLookAtCamera   = false;
    
    private Scenario()
    {

    }

    public Scenario(com.jme3.renderer.Camera Camera, Spatial destinationHandle)
    {
        assetManager = AppGetter.getAssetManager();
        this.Camera = Camera;
        this.destinationHandle = destinationHandle;
        
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
    
    
    
}

