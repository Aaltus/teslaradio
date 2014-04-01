/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.jme3.animation.*;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author Alexandre Hamel
 * 
 * This class contains all the models and the animations related to sound capture
 * 
 */
public final class SoundCapture extends Scenario {

    private final static String TAG = "Capture";

    private Spatial scene;
    private Spatial drum;
    private Spatial circles;
    
    private Animation animation;
    private AnimControl mAnimControl = new AnimControl();
    private AnimChannel mAnimChannel;

    private int nbTrajectories = 100;
    private Vector3f[] trajectories = new Vector3f[nbTrajectories];
    
    private boolean firstTry = true;
       
    public SoundCapture(AssetManager assetManager)
    {
        super(assetManager);

        loadUnmovableObjects();
        loadMovableObjects();
    }

    /**
     * Loading the models from the asset manager and attaching it to the
     * Node containing the unmovable objects in the scene.
     */
    @Override
    protected void loadUnmovableObjects()
    {
        scene = assetManager.loadModel("Models/SoundCapture.j3o");
        scene.setName("SoundCapture");
        scene.scale(10.0f,10.0f,10.0f);
        this.attachChild(scene);

        drum = scene.getParent().getChild("Tambour");
    }

    /**
     * Loading the models from the asset manager and attaching it to the
     * Node containing the movable objects in the scene.
     */
    @Override
    protected void loadMovableObjects()
    {
        /**
         * TODO : Load the sound particules models
         */
        circles = assetManager.loadModel("Models/Effet_tambour.j3o");
        circles.setName("Circles");  
    }

    /**
     * Initialisation of the tambour effects
     */
    private void initCircles()
    {
        circles.scale(10.0f, 10.0f, 10.0f);
        Quaternion rot = new Quaternion();
        rot.fromAngleAxis(3.14f, new Vector3f(1.0f,0.0f,0.0f));
        
        Material circleMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        circleMat.setColor("Color", ColorRGBA.Pink);
        circles.setMaterial(circleMat);

        float duration = 5.0f; 
        AnimationFactory animationFactory = new AnimationFactory(duration,"DrumEffect");
                
        animationFactory.addTimeTranslation(0.0f, new Vector3f(0.0f, 20.0f, 0.0f));
        animationFactory.addTimeRotation(0.0f, rot);
        animationFactory.addTimeScale(0.0f, new Vector3f(0.2f, 0.0f, 0.2f));
        animationFactory.addTimeScale(5.0f, new Vector3f(10.0f, 0.0f, 10.0f));
        
        animation = animationFactory.buildAnimation();
        
        mAnimControl.addAnim(animation);
        circles.addControl(mAnimControl);
   
        mAnimChannel = mAnimControl.createChannel();
    }
     
    @Override
    public void initAllMovableObjects()
    {
        initCircles();
        
        this.attachChild(movableObjects);
    }
      
    public void initTrajectories(float nbDirections)
    {
        /**
         * Get the position of the drum and microphone
         */
        //drum = scene.getParent().getChild("Tambour");
        Spatial boule_micro = scene.getParent().getChild("Boule_micro");
        
        Vector3f drumPosition = drum.getLocalTranslation();
        Vector3f microPosition = boule_micro.getLocalTranslation();
        
        Vector3f drum2MicDirection = microPosition.subtract(drumPosition);
        drum2MicDirection.normalize();
       
        float angleHeight = drum2MicDirection.angleBetween(new Vector3f(drumPosition.x, 0.0f, drumPosition.z));
        float angleWidth = drum2MicDirection.angleBetween(new Vector3f(drumPosition.x, drumPosition.y, 0.0f));
        
        Quaternion rotationHeight = new Quaternion();
        Quaternion rotationWidth = new Quaternion();
        
        for(int i=0; i < 20; i++)
        {
            for(int j=0; j < 5; j++)
            {   
                rotationHeight.fromAngleAxis(j*(90.0f/5.0f), trajectories[(i*5)-5]);
                rotationWidth.fromAngleAxis(i*angleWidth, Vector3f.UNIT_Y);
                trajectories[i*(j+1)] = Vector3f.ZERO;
            }
        }
    }
    
    public void tambourTouchEffect()
    {
        movableObjects.attachChild(circles);
        
        if(firstTry == true)
            mAnimControl.addListener(this);

        /**
         * Animation for a better touch feeling
         */
        mAnimChannel.reset(true);
        mAnimChannel.setAnim("DrumEffect");
        mAnimChannel.setLoopMode(LoopMode.DontLoop);
        mAnimChannel.setSpeed(20.0f);
              
        // Not the first time the object is touched
        firstTry = false;
        
    }
    
    @Override
    public void onAnimCycleDone(AnimControl animControl, AnimChannel animChannel, String s) 
    {
        // ...do nothing
        if(mAnimChannel.getAnimationName().equals("DrumEffect"))
            movableObjects.detachChild(circles);
    }

    @Override
    public void onAnimChange(AnimControl animControl, AnimChannel animChannel, String s) 
    {
        // ...do nothing
    }

    @Override
    public void onScenarioClick(CollisionResult closestCollisionResult) {

        Spatial touchedGeometry = closestCollisionResult.getGeometry();
        while(touchedGeometry.getParent() != null)
        {
            //if(touchedGeometry.getParent() != null){
                if (touchedGeometry.getParent().getName() == drum.getName())
                {
                    this.tambourTouchEffect();
                    break;
                }
                else{
                    touchedGeometry = touchedGeometry.getParent();
                }
//            }
//            else{
//                break;
//            }
        }

    }

    @Override
    protected void restartScenario() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}