/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

/**
 *
 * @author Alexandre Hamel
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
    
    protected abstract void restartScenario();
    
    protected abstract void initAllMovableObjects();
    
    protected abstract void initAllUnmovableObjects();

    public abstract void onAnimCycleDone(AnimControl animControl, AnimChannel animChannel, String s);

    public abstract void onAnimChange(AnimControl animControl, AnimChannel animChannel, String s);

}
