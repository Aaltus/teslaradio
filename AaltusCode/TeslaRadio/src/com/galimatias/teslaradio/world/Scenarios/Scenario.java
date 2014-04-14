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
import com.jme3.scene.Spatial;

/**
 *
 * @author Alexandre Hamel
 */
public abstract class Scenario extends Node implements AnimEventListener{

    private final static String TAG = "Scenario";

    protected boolean showInformativeMenu = false;

    protected ViewState mViewState;

    protected Node movableObjects = new Node("movable");

    protected AssetManager assetManager;
    
    protected Node touchable;

    protected com.jme3.renderer.Camera Camera = null;

    protected Spatial scene;
    
    public Scenario(AssetManager assetManager)
    {
        this.assetManager = assetManager;
    }

    /**
     * Methods to load the associated 3D objects with the scenario
     */
    protected abstract void loadUnmovableObjects();

    protected abstract void loadMovableObjects();
    
    protected abstract void restartScenario();
    
    protected abstract void initAllMovableObjects();
    
    public abstract void onAnimCycleDone(AnimControl animControl, AnimChannel animChannel, String s);

    public abstract void onAnimChange(AnimControl animControl, AnimChannel animChannel, String s);

    public abstract void onScenarioTouch(String name, TouchEvent touchEvent, float v);

    public abstract boolean simpleUpdate(float tpf);
    
    public abstract void onAudioEvent();
}
