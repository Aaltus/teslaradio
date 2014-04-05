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
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.galimatias.teslaradio.world.effects.SignalEmitter;
import com.galimatias.teslaradio.world.effects.SignalTrajectories;
import com.galimatias.teslaradio.world.effects.TextBoxes;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.plugins.blender.curves.BezierCurve;
import com.jme3.scene.shape.Curve;
import com.jme3.scene.shape.Sphere;
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
    private Node guiNode;
    private Spatial scene;
    private Spatial drum;
    private Spatial micro;
    private Spatial circles;
    
    private Spatial drumHandleOut;
    private Spatial micHandleIn;
    
    private SignalEmitter DrumSoundEmitter;
    private SignalEmitter MicWireEmitter;
    
    // animation encore utile?
    private Animation animation;
    private AnimControl mAnimControl = new AnimControl();
    private AnimChannel mAnimChannel;

    private Vector<Vector3f> trajectories = new Vector<Vector3f>();
    private Vector3f drumPosition;
    private Vector3f micPosition;
    private Vector3f drumHandleOutPosition;
    private Vector3f micHandleInPosition;
           
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
           
        scene = assetManager.loadModel("Models/SoundCapture.j3o");
        scene.setName("SoundCapture");
        scene.scale(10.0f,10.0f,10.0f);
        this.attachChild(scene);

        drum = scene.getParent().getChild("Tambour");
        micro = scene.getParent().getChild("Boule_micro");
        drumHandleOut = scene.getParent().getChild("Drum_Output_Handle");
        micHandleIn = scene.getParent().getChild("Mic_Input_Handle");
        
        drumPosition = drum.getWorldTranslation();
        micPosition = micro.getWorldTranslation();
        drumHandleOutPosition = drumHandleOut.getWorldTranslation();
        micHandleInPosition = micHandleIn.getWorldTranslation();

        drum_sound = new AudioNode(assetManager, "Sounds/drum_taiko.wav", false);
        drum_sound.setPositional(false);
        drum_sound.setLooping(false);
        drum_sound.setVolume(2);
        //rootNode.attachChild(audio_gun);
        this.attachChild(drum_sound);
        
        Quaternion textRotation = new Quaternion();
        textRotation.fromAngleAxis(-3.14159f/2.0f, Vector3f.UNIT_Y);
        
        Vector3f v = new Vector3f(micPosition.x, micPosition.y + 5.0f, micPosition.z);
        
        TextBoxes text = new TextBoxes(assetManager);
        text.initText("Hello World", 10.0f, v, textRotation, ColorRGBA.Magenta);
        text.setName("text");
        this.attachChild(text);

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
        //List<Vector3f> listPaths = new ArrayList<Vector3f>();
        //listPaths.add(new Vector3f(0,40,0));
        
    }

    /**
     * Initialisation of the tambour effects
     */
    private void initDrumParticlesEmitter()
    {
        // Getting all the trajectories from the position of the mic-drums and 
        // the number of directions        
        Vector3f drumMicDirection = micHandleInPosition.subtract(drumHandleOutPosition);        
                        
        int totalNbDirections = 50;
        int nbXYDirections = 5;
        
        // Setting the direction norms and the speed displacement to the trajectories
        float VecDirectionNorms = 80f;
        float SoundParticles_Speed = 35f;
                
        // Creating the trajectories
        SignalTrajectories directionFactory = new SignalTrajectories(totalNbDirections, nbXYDirections);
        directionFactory.setTrajectories(drumMicDirection, VecDirectionNorms);
        trajectories = directionFactory.getTrajectories();
        
        // instantiate 3d Sound particul model
        Sphere sphere = new Sphere(8, 8, 0.9f);
        Geometry soundParticle = new Geometry("particul",sphere);
        Material soundParticul_mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        soundParticul_mat.setColor("Color", ColorRGBA.Blue);

        soundParticle.setMaterial(soundParticul_mat);
                
        DrumSoundEmitter = new SignalEmitter(trajectories, soundParticle,SoundParticles_Speed, ColorRGBA.Blue );
        Vector3f v = drum.getWorldTranslation();
        this.attachChild(DrumSoundEmitter);
        DrumSoundEmitter.setLocalTranslation(v.x, v.y + 21f, v.z); // TO DO: utiliser le object handle blender pour position        
        
    }
    
    private void initCircles()
    {
        circles.scale(10.0f, 10.0f, 10.0f);
        Quaternion rot = new Quaternion();
        rot.fromAngleAxis(3.14f, new Vector3f(1.0f,0.0f,0.0f));
        
        Material circleMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        circleMat.setColor("Color", ColorRGBA.Gray);
        circles.setMaterial(circleMat);

        float duration = 5.0f; 
        AnimationFactory animationFactory = new AnimationFactory(duration,"DrumEffect");
        
        Vector3f v = drum.getWorldTranslation();
        animationFactory.addTimeTranslation(0.0f, new Vector3f(v.x, v.y + 20.0f, v.z));
        animationFactory.addTimeRotation(0.0f, rot);
        animationFactory.addTimeScale(0.0f, new Vector3f(0.2f, 0.0f, 0.2f));
        animationFactory.addTimeScale(5.0f, new Vector3f(10.0f, 0.0f, 10.0f));
        
        animation = animationFactory.buildAnimation();
        
        mAnimControl.addAnim(animation);
        circles.addControl(mAnimControl);
   
        mAnimChannel = mAnimControl.createChannel();
    }
    
    private void initMicWireParticlesEmitter()
    {
        SignalTrajectories directionFactory = new SignalTrajectories();
        Vector<Vector3f> curvedPath = new Vector <Vector3f>();
        
        Node micWire_node = (Node) scene.getParent().getChild("BezierCurve");
        Geometry micWire_geom = (Geometry) micWire_node.getChild("BezierCurve");
        Mesh micWire_mesh = micWire_geom.getMesh();
        
        curvedPath = directionFactory.getCurvedPath(micWire_mesh);
        
        
        // instantiate 3d Sound particul model
        Sphere sphere = new Sphere(8, 8, 0.9f);
        Geometry electricParticle = new Geometry("particul",sphere);
        Material electricParticle_mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        electricParticle_mat.setColor("Color", ColorRGBA.Green);
        electricParticle.setMaterial(electricParticle_mat);
                
        
        MicWireEmitter = new SignalEmitter(curvedPath, electricParticle, 35f /*Speed*/, ColorRGBA.Green );
        this.attachChild(MicWireEmitter);
        MicWireEmitter.setLocalTranslation(micPosition.x, micPosition.y, micPosition.z); // TO DO: utiliser le object handle blender pour position        
        
    }
    
     
    @Override
    public void initAllMovableObjects()
    {
        initCircles();
        initDrumParticlesEmitter();
        initMicWireParticlesEmitter();
        
        this.attachChild(movableObjects);
    }
    
    public void drumTouchEffect()
    {        
        DrumSoundEmitter.emitParticles();
        MicWireEmitter.emitCurvedPathParticle();
        
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
         MicWireEmitter.simpleUpdate(tpf);
     }

}