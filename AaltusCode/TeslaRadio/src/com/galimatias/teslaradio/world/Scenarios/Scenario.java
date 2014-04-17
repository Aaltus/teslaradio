/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.ViewState;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.asset.AssetManager;
import com.jme3.input.event.TouchEvent;
import com.jme3.scene.Node;

/**
 * Abstract class that regroup a scenario.
 * @author Alexandre Hamel
 */
public abstract class Scenario extends Node implements AnimEventListener{

    private final static String TAG = "Scenario";

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

    /**
     * Setting that to true
     *
     */
    protected boolean showInformativeMenu = false;

    /**
     * Internal state of the scenario to know if it's in focus, in stand by,...
     *
     */
    protected ViewState mViewState;

    /**
     * REMOVED THIS AFTER ALEX CHANGES TO CIRCLE EFFECT
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
    
    public Scenario(AssetManager assetManager)
    {
        this.assetManager = assetManager;
    }

    /**
     * Methods to load the associated 3D objects with the scenario that are unmovable
     * REMOVE THAT IF NOT NECESSARY
     */
    protected abstract void loadUnmovableObjects();

    /**
     * TO BE REMOVED BECAUSE MOVABLE
     *
     */
    protected abstract void loadMovableObjects();

    /**
     * Method to restart the scenario if a refresh has been called by a
     * the scenario manager container
     *
     */
    public abstract void restartScenario();
    
    protected abstract void initAllMovableObjects();

    /**
     * TO BE REMOVED
     */
    public abstract void onAnimCycleDone(AnimControl animControl, AnimChannel animChannel, String s);

    /**
     * TO BE REMOVED
     */
    public abstract void onAnimChange(AnimControl animControl, AnimChannel animChannel, String s);

    /**
     * Pass down touch event from scenario manager to the scenarios.
     * The arguments are directly the touch event from JME3. The scenario can override this make
     * make ray picking for example.
     */
    public abstract void onScenarioTouch(String name, TouchEvent touchEvent, float v);

    /**
     * Called from scenario manager to make the simpleUpdate of the scenario
     */
    public abstract boolean simpleUpdate(float tpf);

    /**
     * Setter to set the speed of the animation and the particuls in the scenario.
     * Called by the scenario manager.
     */
    public abstract void setGlobalSpeed(float speed);

    /**
     * To receive events from the device microphone from scenario manager.
     */
    public abstract void onAudioEvent();
}
