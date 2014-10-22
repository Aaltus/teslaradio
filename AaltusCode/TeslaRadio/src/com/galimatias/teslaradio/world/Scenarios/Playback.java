/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import static com.galimatias.teslaradio.world.Scenarios.Scenario.DEBUG_ANGLE;
import com.galimatias.teslaradio.world.effects.AirParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.DynamicWireParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.ParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.PatternGeneratorControl;
import com.galimatias.teslaradio.world.effects.TextBox;
import com.galimatias.teslaradio.world.observer.EmitterObserver;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.input.event.TouchEvent;
import static com.jme3.input.event.TouchEvent.Type.DOWN;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

/**
 *
 * @author Batcave
 */
public class Playback extends Scenario implements EmitterObserver {
        
    private String titleText = "Envoi du son";
    
    private Geometry soundParticle;
    
    private TextBox titleTextBox;
    
    private Spatial  speakerHandleOut;
    private Spatial  speakerHandleIn;
    private Vector3f speakerHandleOutPosition;
    private Vector3f speakerHandleInPosition;
    private Node     speakerEmitter;
    private Node     speakerIn = new Node();
    
    Playback(Camera Camera, Spatial destinationHandle) {
        
        super(Camera, destinationHandle, "Sounds/Nyan cat.ogg");
        touchable = new Node();
        touchable.setName("Touchable");
        this.attachChild(touchable);
        
        loadUnmovableObjects();
        loadMovableObjects();
    }

    @Override
    protected void loadUnmovableObjects() {
        initParticles();
        
        scene = (Node) assetManager.loadModel("Models/Playback/Speaker.j3o");
        scene.setName("Playback");
        this.attachChild(scene);       
        
        touchable.attachChild(scene);
        
        Material mat1 = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", new ColorRGBA(1, 0, 1, 0.5f));
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        speakerHandleIn = scene.getChild("Speaker.Handle.In");
        speakerHandleIn.setName("InputSpeaker");
        speakerHandleOut = scene.getChild("Speaker.Handle.Out");
        speakerHandleOutPosition = speakerHandleOut.getLocalTranslation().add(scene.getLocalTranslation());
        speakerHandleInPosition = speakerHandleIn.getLocalTranslation().add(scene.getLocalTranslation());
        
        speakerEmitter = new Node();
        speakerEmitter.setLocalTranslation(speakerHandleOutPosition);
        Quaternion quat = new Quaternion();
        quat.fromAngleAxis(3*pi/2, Vector3f.UNIT_Z);
        speakerEmitter.setLocalRotation(quat);
        this.attachChild(speakerEmitter);
        
        speakerIn = new Node();
        speakerIn.setLocalTranslation(speakerHandleInPosition);
        this.attachChild(speakerIn);
        
        speakerIn.addControl(new DynamicWireParticleEmitterControl(speakerEmitter, 1000f));
        speakerEmitter.addControl(new AirParticleEmitterControl(speakerHandleOut, 20f, 13f, mat1, AirParticleEmitterControl.AreaType.DOME));
        speakerEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);
        speakerEmitter.addControl(new PatternGeneratorControl((float) 0.05, soundParticle, 1, 1, 1, false));
        speakerIn.getControl(ParticleEmitterControl.class).registerObserver(this);
        speakerIn.getControl(ParticleEmitterControl.class).setEnabled(true);
        
        
        initTitleBox();
    }

    @Override
    protected void loadMovableObjects() {
        

        
    }

    @Override
    public void restartScenario() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
                    Vector2f click2d = new Vector2f(touchEvent.getX(),touchEvent.getY());
                    Vector3f click3d = Camera.getWorldCoordinates(
                            new Vector2f(click2d.x, click2d.y), 0f).clone();
                    Vector3f dir = Camera.getWorldCoordinates(
                            new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
                    Ray ray = new Ray(click3d, dir);

                    // 3. Collect intersections between Ray and Shootables in results list.
                    touchable.collideWith(ray, results);

                    // 4. Print the results
                    //for (int i = 0; i < results.size(); i++) {
                        // For each hit, we know distance, impact point, name of geometry.
                        //float dist = results.getCollision(i).getDistance();
                        //Vector3f pt = results.getCollision(i).getContactPoint();
                        //String hit = results.getCollision(i).getGeometry().getName();
                    //}

                    // 5. Use the results (we mark the hit object)
                    if (results.size() > 0)
                    {

                        // The closest collision point is what was truly hit:
                        String nameToCompare =
                                results.getClosestCollision().getGeometry().getParent().getParent().getParent().getName();
                        if(nameToCompare == null){
                            break;
                        }
                        else if (nameToCompare.equals(scene.getName()))
                        {
                            this.speakerTouchEffect();
                            break;
                        }
                        else if (nameToCompare.equals(titleTextBox.getName()))
                        {
                            //this.textTouchEffect();
                            showInformativeMenu = true;
                            break;
                        }
                        
                    }
                }
                break;
        }
    }

    @Override
    protected boolean simpleUpdate(float tpf) {
        
        return false;
    }

    @Override
    public void setGlobalSpeed(float speed) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onAudioEvent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Spatial getInputHandle() {
        return speakerIn;
    }

    @Override
    protected void initTitleBox() {
        
        titleTextBox = new TextBox(assetManager, 
                                    titleText, 
                                    TEXTSIZE,
                                    TEXTCOLOR, 
                                    TEXTBOXCOLOR,
                                    TITLEWIDTH, 
                                    TITLEHEIGHT, 
                                    "titleText", 
                                    BitmapFont.Align.Center, 
                                    SHOWTEXTDEBUG, 
                                    TEXTLOOKATCAMERA);

        //move the text on the ground without moving
        Vector3f titleTextPosition = new Vector3f(0f, 0.25f, 6f);
        titleTextBox.rotate((float) -Math.PI / 2, 0, 0);

        titleTextBox.move(titleTextPosition);
        this.attachChild(titleTextBox);
    }

    @Override
    public void signalEndOfPath(Geometry caller, float magnitude) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void initPatternGenerator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Initialisation of the tambour effects
     */
    private void initParticles()
    {
        Material mat1 = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        
        // instantiate 3d Sound particul model
        if (DEBUG_ANGLE) {
            
            Texture nyan = assetManager.loadTexture("Textures/Nyan_Cat.png");
            mat1.setTexture("ColorMap", nyan);
            Quad rect = new Quad(0.5f,0.5f);
            soundParticle = new Geometry("MicTapParticle", rect);
        } else {
            mat1.setColor("Color", new ColorRGBA(0.0f,0.0f,1.0f,1.0f));
            Sphere sphere = new Sphere(10, 10, 0.4f);
            soundParticle = new Geometry("MicTapParticle", sphere);
        }
        
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        soundParticle.setMaterial(mat1);
        soundParticle.setQueueBucket(queueBucket.Opaque);
    }
    
    public void speakerTouchEffect()
    {

        // Here, we need to get the vector to the mic handle
        //Vector3f receiverHandleVector = particleLinker.GetEmitterDestinationPaths(this);
        //GuitarSoundEmitter.prepareEmeitParticles(receiverHandleVector);

        this.speakerEmitter.getControl(PatternGeneratorControl.class).toggleNewWave(1);
    }

    @Override
    public void emitterObserverUpdate(Spatial spatial, String notifierId) {
        speakerTouchEffect();
    }
    
}
