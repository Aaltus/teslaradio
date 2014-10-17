/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.Arrows;
import com.galimatias.teslaradio.world.effects.DynamicWireParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.FadeControl;
import com.galimatias.teslaradio.world.effects.LookAtCameraControl;
import com.galimatias.teslaradio.world.effects.ParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.StaticWireParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.PatternGeneratorControl;
import com.galimatias.teslaradio.world.effects.SoundControl;
import com.galimatias.teslaradio.world.effects.TextBox;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

//import com.galimatias.teslaradio.world.observer.ScenarioObserver;

//import com.galimatias.teslaradio.world.observer.ScenarioObserver;

/**
 *
 * @author Alexandre Hamel
 * 
 * This class contains all the models and the animations related to sound capture
 * 
 */
public final class SoundCapture extends Scenario {

    private final static String TAG = "SoundCapture";
    
    private Spatial micro;
    private Spatial micHandleIn;

    private Geometry micTapParticle;
    
    // Emitters of the scenario
    private Node micWireEmitter;
    private Node wireDestinationEmitter;

    private TextBox titleTextBox;
    private TextBox microphoneTextBox;

    private Vector3f micPosition;
    private Vector3f micHandleInPosition;
    
    // Default text to be seen when scenario starts
    private String titleText = "La Capture du Son";
    
    //Arrows
    private Arrows micArrow;
       
    public SoundCapture(Camera Camera, Spatial destinationHandle)
    {
        super(Camera, destinationHandle);
        
        this.destinationHandle = destinationHandle;
        this.cam = Camera;
        this.needAutoGenIfMain = true;
        loadUnmovableObjects();
        loadMovableObjects();
        loadArrows();
    }

    /**
     * Loading the models from the asset manager and attaching it to the
     * Node containing the unmovable objects in the scene.
     */
    @Override
    protected void loadUnmovableObjects()
    {
        scene = (Node) assetManager.loadModel("Models/SoundCapture/Micro.j3o");
        scene.setName("SoundCapture");
        this.attachChild(scene);
        
        touchable = new Node();//(Node) scene.getParent().getChild("Touchable");
        micro = scene.getParent().getChild("Boule_micro");
        micHandleIn = scene.getParent().getChild("Mic_Input_Handle");
        micPosition = micro.getWorldTranslation();
        micHandleInPosition = micHandleIn.getWorldTranslation();
        touchable.attachChild(micro);
        scene.attachChild(touchable);
        
        initTitleBox();

    }

    @Override
    protected void loadMovableObjects()
    {
        initMicWireParticlesEmitter();
        initOnTouchEffect();

        this.attachChild(movableObjects);
    }

       
    private void initMicWireParticlesEmitter()
    {
        micWireEmitter = new Node();
        micWireEmitter.setLocalTranslation(micPosition.x, micPosition.y,micPosition.z); // TO DO: utiliser le object handle blender pour position
        scene.attachChild(micWireEmitter);
        
        Node micWire_node = (Node) scene.getParent().getChild("WirePath");
        Geometry micWire_geom = (Geometry) micWire_node.getChild("BezierCurve");
        //Geometry tmpGeom = (Geometry)micWire_geom;//.scale(1/ScenarioManager.WORLD_SCALE_DEFAULT);
        
        micWireEmitter.addControl(new StaticWireParticleEmitterControl(micWire_geom.getMesh(), 3.5f, cam));
        micWireEmitter.addControl(new SoundControl("Sounds/micro_sound.wav", false, 2));
        
        wireDestinationEmitter = new Node();
        wireDestinationEmitter.setName("WireDestinationEmitter");
        Spatial moduleHandleOut_node = scene.getParent().getChild("Module.Handle.Out");
        wireDestinationEmitter.setLocalTranslation(moduleHandleOut_node.getLocalTranslation());
        scene.attachChild(wireDestinationEmitter);
        
        //System.out.println(destinationHandle.getName());
        if(this.destinationHandle != null){
            wireDestinationEmitter.addControl(new DynamicWireParticleEmitterControl(this.destinationHandle, 3.5f, cam));

            wireDestinationEmitter.getControl(ParticleEmitterControl.class).registerObserver(this.destinationHandle.getControl(ParticleEmitterControl.class));
            micWireEmitter.getControl(ParticleEmitterControl.class).registerObserver(wireDestinationEmitter.getControl(ParticleEmitterControl.class));

            wireDestinationEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);
            micWireEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);

            micTapParticle = ModulationCommon.initBaseGeneratorParticle();

            micTapParticle.setQueueBucket(RenderQueue.Bucket.Opaque);
            micWireEmitter.addControl(new PatternGeneratorControl(0.25f, micTapParticle, 10, ModulationCommon.minBaseParticleScale, 
                                                                  ModulationCommon.maxBaseParticleScale, true));
            micWireEmitter.getControl(PatternGeneratorControl.class).setEnabled(true);
        }
        this.particlePerWave = 4;
        this.waveTime = 1;
    }
     
        
    private void initOnTouchEffect() {
        /**
         * Will be used for the mic touch effect
         */
    }


    protected void microTouchEffect() {
        removeHintImages();
        
        int wavesPerTap = 4;
        micWireEmitter.getControl(PatternGeneratorControl.class).toggleNewWave(wavesPerTap);
    }
    
    private void textBoxesUpdate(Vector3f upVector)
    {
        titleTextBox.simpleUpdate(null, 0.0f, null, this.Camera, upVector);
    }
    
    @Override
    public void onScenarioTouch(String name, TouchEvent touchEvent, float v) {

        switch(touchEvent.getType()){

            //Checking for down event is very responsive
            case DOWN:

            //case TAP:
                if (name.equals("Touch"))
                {

                    // 1. Reset results list.
                    CollisionResults results = new CollisionResults();

                    // 2. Mode 1: user touch location.
                    //Vector2f click2d = inputManager.getCursorPosition();

                    Vector2f click2d = new Vector2f(touchEvent.getX(),touchEvent.getY());
                    Vector3f click3d = Camera.getWorldCoordinates(
                            new Vector2f(click2d.x, click2d.y), 0f).clone();
                    Vector3f dir = Camera.getWorldCoordinates(
                            new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
                    Ray ray = new Ray(click3d, dir);

                    // 3. Collect intersections between Ray and Shootables in results list.
                    //focusableObjects.collideWith(ray, results);
                    touchable.collideWith(ray, results);

                    // 4. Print the results
                    //Log.d(TAG, "----- Collisions? " + results.size() + "-----");
                    //for (int i = 0; i < results.size(); i++) {
                        // For each hit, we know distance, impact point, name of geometry.
                        //float dist = results.getCollision(i).getDistance();
                        //Vector3f pt = results.getCollision(i).getContactPoint();
                        //String hit = results.getCollision(i).getGeometry().getName();

                        //Log.e(TAG, "  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
                    //}

                    // 5. Use the results (we mark the hit object)
                    if (results.size() > 0)
                    {

                        // The closest collision point is what was truly hit:
                        String nameToCompare =
                                results.getClosestCollision().getGeometry().getParent().getName();

                        if (nameToCompare.equals(micro.getName()))
                        {
                            this.microTouchEffect();
                            break;
                        }
                        else if (nameToCompare.equals(titleTextBox.getName()))
                        {
                            showInformativeMenu = true;
                            break;
                        }

                }
            }
            break;
        }
    }

    @Override
    public void restartScenario() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    protected boolean simpleUpdate(float tpf) {

        //touchEffectEmitter.simpleUpdate(tpf);
        micArrow.simpleUpdate(tpf);
        
        if(Camera != null) {
            Vector3f upVector = this.getLocalRotation().mult(Vector3f.UNIT_Y);
            textBoxesUpdate(upVector);
        }

        if (showInformativeMenu)
        {
            showInformativeMenu = false;
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void onAudioEvent()
    {
        /**
         * Might use it for the mic effect
         */
    }

    @Override
    public void setGlobalSpeed(float speed)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void signalEndOfPath(Geometry caller, float magnitude) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Spatial getInputHandle() {
        return micWireEmitter;
    }

    @Override
    protected void initTitleBox() {
        titleTextBox = new TextBox(assetManager, 
                                    titleText, 
                                    TextSize,
                                    TextColor, 
                                    TextBoxColor,
                                    TitleWidth, 
                                    TitleHeight, 
                                    "titleText", 
                                    BitmapFont.Align.Center, 
                                    ShowTextDebug, 
                                    TextLookAtCamera);
        
        //move the text on the ground without moving
        Vector3f titleTextPosition = new Vector3f(0f, 0.25f, 6f);
        titleTextBox.rotate((float)-Math.PI/2, 0, 0);
        
        titleTextBox.move(titleTextPosition);

        touchable.attachChild(titleTextBox);
    }

    private void loadArrows() {
        micArrow = new Arrows("touch", micHandleInPosition, assetManager, 1);
        LookAtCameraControl control = new LookAtCameraControl(Camera);
        micArrow.addControl(control);
        this.attachChild(micArrow);
    }
    
        /**
     * Remove hints, is called after touch occurs
     */
    public void removeHintImages()
    {
        micArrow.getControl(FadeControl.class).setShowImage(false);
        micArrow.resetTimeLastTouch();
    }
}
