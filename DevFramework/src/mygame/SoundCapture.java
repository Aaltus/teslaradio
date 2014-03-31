/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.AnimationFactory;
import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.cinematic.MotionPath;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
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

    private Spatial drums;
    private Node micro = new Node("Micro");
    private Spatial baseMicro;
    private Spatial bouleMicro;
    private Spatial standMicro;
    private Spatial circles;
    
    private Animation animation;
    private AnimControl mAnimControl = new AnimControl();
    private AnimChannel mAnimChannel;
    
    private ParticleEmitter soundWaves;
    
    private boolean firstTry = true;
       
    public SoundCapture(AssetManager assetManager)
    {
        super(assetManager);

        loadStaticAnimatedObjects();
    }

    /**
     * Loading the models from the asset manager and attaching it to the
     * Node containing the unmovable objects in the scene.
     */
    @Override
    protected void loadStaticAnimatedObjects()
    {
        // Load a model from test_data (OgreXML + material + texture)
        drums = assetManager.loadModel("Models/Tambour.j3o");
        drums.setName("Tambour");
        
        baseMicro = assetManager.loadModel("Models/Base_micro.j3o");
        baseMicro.setName("Base_micro");
        
        bouleMicro = assetManager.loadModel("Models/Boule_micro.j3o");
        bouleMicro.setName("Boule_micro");
        
        standMicro = assetManager.loadModel("Models/Stand_micro.j3o");
        standMicro.setName("Stand_micro");
        
        // Attaching all the micro parts to the micro Node
        micro.attachChild(baseMicro);
        micro.attachChild(bouleMicro);
        micro.attachChild(standMicro);
        
        circles = assetManager.loadModel("Models/Effet_tambour.j3o");
        circles.setName("Circles");  
    }

    /**
     * Loading the models from the asset manager and attaching it to the
     * Node containing the movable objects in the scene.
     */
    @Override
    protected void loadMovableAnimatedObjects()
    {
        /**
         * TODO : Load the sound particules models
         */

    }

    /**
     * Initiation of the tambour object.
     */
    private void initTambour()
    {
        drums.scale(10.0f, 10.0f, 10.0f);
        Quaternion rotateDrumsX=new Quaternion();
        rotateDrumsX.fromAngleAxis(3.14f/2.0f,new Vector3f(1.0f,0.0f,0.0f));
        Quaternion rotateDrumsZ=new Quaternion();
        rotateDrumsZ.fromAngleAxis(3.14f, new Vector3f(0.0f,0.0f,1.0f));
        Quaternion rotateDrumsY=new Quaternion();
        rotateDrumsY.fromAngleAxis(3.14f,new Vector3f(0.0f,1.0f,0.0f));

        rotateDrumsX.mult(rotateDrumsZ);
        Quaternion rotateTambourXZ=rotateDrumsZ.mult(rotateDrumsX);
        Quaternion rotateTambourXYZ = rotateTambourXZ.mult(rotateDrumsY);

        //tambour.rotate(rotateTambourXYZ);

        //3.14/2.,new Vector3f(1.0.,0.0,1.0)));
        drums.rotate(0.0f, -3.0f, 0.0f);
        drums.setLocalTranslation(0.0f, 0.0f, 0.0f);
        
        // Attach the object to the scene
        unmovableObjects.attachChild(drums);
    }

    /**
     * Initialisation of the microphone objects
     */
    private void initMic()
    {
        micro.scale(10.0f, 10.0f, 10.0f);
        micro.setLocalTranslation(50.0f,0.0f,50.0f);
        
        Quaternion rotateMicro = new Quaternion();
        rotateMicro.fromAngleAxis(-3.14f/4.0f, new Vector3f(0.0f, 1.0f, 0.0f));
        micro.rotate(rotateMicro);
        
        Material microMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        micro.setMaterial(microMat);
        
        // Attach the object to the scene
        unmovableObjects.attachChild(micro);
    }
    
    /**
     * Initialisation of the tambour effects
     */
    private void initCircles()
    {
        circles.scale(10.0f, 10.0f, 10.0f);
        
        Material circleMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        circleMat.setColor("Color", ColorRGBA.Pink);
        circles.setMaterial(circleMat);

        float duration = 5.0f; 
        AnimationFactory animationFactory = new AnimationFactory(duration,"DrumEffect");
                
        animationFactory.addTimeTranslation(0.0f, new Vector3f(0.0f, 20.0f, 0.0f));
        animationFactory.addTimeScale(0.0f, new Vector3f(0.2f, 0.0f, 0.2f));
        animationFactory.addTimeScale(5.0f, new Vector3f(8.0f, 0.0f, 8.0f));
        
        animation = animationFactory.buildAnimation();
        
        mAnimControl.addAnim(animation);
        circles.addControl(mAnimControl);
   
        mAnimChannel = mAnimControl.createChannel();
    }
    
    private void initParticles()
    {        
        soundWaves = new ParticleEmitter("Sound",ParticleMesh.Type.Triangle, 10);
        soundWaves.setLocalTranslation(0.0f, 20.0f, 0.0f);
        
        Material soundMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        soundWaves.setMaterial(soundMat);
                
        soundWaves.setImagesX(2);
        soundWaves.setImagesY(2);
        
        soundWaves.setStartColor(ColorRGBA.Red);
        soundWaves.setEndColor(ColorRGBA.Yellow);
        soundWaves.getParticleInfluencer().setInitialVelocity(new Vector3f(5.0f, 15.0f, 5.0f));
        soundWaves.getParticleInfluencer().setVelocityVariation(0.0f);
        
        soundWaves.setGravity(0.0f, 0.0f, 0.0f);
        
        soundWaves.setShape(new EmitterSphereShape(Vector3f.ZERO, 7.0f));
        
        soundWaves.getParticleInfluencer().setVelocityVariation(0.5f);
        
        soundWaves.setParticlesPerSec(10.0f);
        
        soundWaves.setFacingVelocity(true);
                
    }
    
    /**
     * Initialisation of all the objects within the scene.
     */
    public void initAllMovableObjects()
    {
        initTambour();
        initMic();
        
        this.attachChild(unmovableObjects);
    }
    
    public void initAllUnmovableObjects()
    {
        initParticles();
        initCircles();
        
        this.attachChild(movableObjects);
    }
    
    public void tambourTouchEffect()
    {
        movableObjects.attachChild(circles);
        movableObjects.attachChild(soundWaves);
        
        if(firstTry == true)
            mAnimControl.addListener(this);

        /**
         * Animation for a better touch feeling
         */
        mAnimChannel.reset(true);
        mAnimChannel.setAnim("DrumEffect");
        mAnimChannel.setLoopMode(LoopMode.DontLoop);
        mAnimChannel.setSpeed(20.0f);
        
        /**
         * Shooting the particles when we touch the tambour
         */
        soundWaves.emitAllParticles();
              
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


}