/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.jme3.animation.*;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResult;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import effects.SignalEmitter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author Alexandre Hamel
 * 
 * This class contains all the models and the animations related to sound capture
 * 
 */
public final class SoundCapture extends Scenario {

    private final static String TAG = "Capture";


    private AudioNode drum_sound;
    private Spatial scene;
    private Spatial drum;
    private Spatial circles;
    
    private SignalEmitter DrumSoundEmitter;
    private Animation animation;
    private AnimControl mAnimControl = new AnimControl();
    private AnimChannel mAnimChannel;

    private Vector<Vector3f> trajectories = new Vector();
    
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

        drum_sound = new AudioNode(assetManager, "Sounds/drum_taiko.wav", false);
        drum_sound.setPositional(false);
        drum_sound.setLooping(false);
        drum_sound.setVolume(2);
        //rootNode.attachChild(audio_gun);
        this.attachChild(drum_sound);

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
        List<Vector3f> listPaths = new ArrayList<Vector3f>();
        listPaths.add(new Vector3f(0,40,0));
        
        // instantiate 3d Sound particul model
        Sphere sphere = new Sphere(8, 8, 0.9f);
        Geometry soundParticle = new Geometry("particul",sphere);
        Material soundParticul_mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        soundParticul_mat.setColor("Color", ColorRGBA.Pink);
        soundParticle.setMaterial(soundParticul_mat);
                
        DrumSoundEmitter = new SignalEmitter(listPaths, soundParticle);
        Vector3f v = scene.getParent().getChild("Tambour").getLocalTranslation();
        DrumSoundEmitter.setLocalTranslation(v.x, v.y + 20, v.y); // TO DO: utiliser le object handle blender pour position
        this.attachChild(DrumSoundEmitter);
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
      
    public void initTrajectories(int nbDirections)
    {
        int XZmaxAngle = 360;
        int YXmaxAngle = 90;
        int nbYXrotations = 5;
        
        /**
         * Get the position of the drum and microphone
         */
        Spatial drum = scene.getParent().getChild("Tambour");
        Spatial boule_micro = scene.getParent().getChild("Boule_micro");
        
        Vector3f drumPosition = drum.getLocalTranslation();
        Vector3f microPosition = boule_micro.getLocalTranslation();
        
        Vector3f drum2MicDirection = microPosition.subtract(drumPosition);
        drum2MicDirection.normalize();
        
        Quaternion rotationPlanXY = new Quaternion();
        Quaternion rotationPlanXZ = new Quaternion();
        Quaternion normalRotation = new Quaternion();
        
        Matrix3f rotMatrixXY = new Matrix3f();
        Matrix3f rotMatrixXZ = new Matrix3f();
        Matrix3f rotMatrixNormal = new Matrix3f();
        
        drum2MicDirection.y = 0;
        trajectories.add(drum2MicDirection);
        
        Vector3f normalVector = new Vector3f();
        Vector3f XZPlanVector = new Vector3f();
        
        for(int i=0; i < nbDirections/nbYXrotations; i++)
        {                       
            rotationPlanXZ.fromAngleAxis(i*(XZmaxAngle/20.0f)*2.0f*3.14f, Vector3f.UNIT_Y);
            rotMatrixXY = rotationPlanXZ.toRotationMatrix();
            XZPlanVector = rotMatrixXY.mult(trajectories.elementAt(i*5));
            
            normalRotation.fromAngleAxis(3.14f/2.0f, Vector3f.UNIT_Y);            
            rotMatrixNormal = normalRotation.toRotationMatrix();
            normalVector = rotMatrixNormal.mult(trajectories.elementAt(i*5));
                        
            for(int j=0; j < YXmaxAngle; j++)
            {                  
                rotationPlanXY.fromAngleAxis(j*(YXmaxAngle/5.0f)*2.0f*3.14f, normalVector);

                rotMatrixXZ = rotationPlanXY.toRotationMatrix();
                
                XZPlanVector.y = 0;
                trajectories.add(rotMatrixXZ.mult(XZPlanVector));
            }
        }
        
        trajectories.remove(0);
    }
    
    public void drumTouchEffect()
    {
		DrumSoundEmitter.emitParticles();
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

        drum_sound.playInstance();
        
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
                    this.drumTouchEffect();
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

     public void simpleUpdate(float tpf) {
         
         DrumSoundEmitter.simpleUpdate(tpf);
     }

}