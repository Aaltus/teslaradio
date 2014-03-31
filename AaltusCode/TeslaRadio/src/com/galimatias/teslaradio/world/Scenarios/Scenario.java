package com.galimatias.teslaradio.world.Scenarios;


import com.galimatias.teslaradio.world.ViewState;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

import java.util.List;


/**
 * Scenario: Defines a Scenario node that will includes multiple objects and
 * manage interaction between them. This could be referred as a Scene
 * Created by jean-christophelavoie on 2014-03-23.
 */
public abstract class Scenario extends Node implements AnimEventListener{

    private final static String TAG = "Scenario";

    protected ViewState mViewState;

    protected Node unmovableObjects = new Node("unmovable");

    protected Node movableObjects = new Node("movable");

    protected AssetManager assetManager;

    public Scenario(AssetManager assetManager)
    {
        this.assetManager = assetManager;
    }

    /**
     * Methods to load the associated 3D objects with the scenario
     */
    protected abstract void loadStaticAnimatedObjects();

    protected abstract void loadMovableAnimatedObjects();

    public abstract void onAnimCycleDone(AnimControl animControl, AnimChannel animChannel, String s);

    public abstract void onAnimChange(AnimControl animControl, AnimChannel animChannel, String s);

    public abstract void setAnimSpeed(float newSpeed);

}