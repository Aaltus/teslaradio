/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaltus.teslaradio.world.Scenarios;

import com.ar4android.vuforiaJME.AppGetter;
import com.aaltus.teslaradio.world.effects.*;
import com.aaltus.teslaradio.world.observer.AutoGenObserver;
import com.aaltus.teslaradio.world.observer.EmitterObserver;
import com.jme3.collision.CollisionResults;

import com.jme3.font.BitmapFont;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.material.RenderState;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.List;


/**
 *
 * @author Barliber
 */
public final class Amplification extends Scenario implements EmitterObserver, AutoGenObserver {
    
    private final static String TAG = "Amplification";
    
    // 3D objects of the scene
    private Spatial ampliSliderButton;
    private Spatial ampliSliderBox;
    private Vector3f translationIncrement;
    private boolean isTouched = false;
    private float ampliScale = 0.5f;

    private TextBox titleTextBox;
    
    private Node moveArrow;
    private Node sliderArrow;
    
    private Boolean isFM = true;
    
    // Default text to be seen when scenario starts
    private String titleText = "L'Amplification & Transmission";

    // Signals emitters 
    private Node inputWireAmpli = new Node();
    private Node outputWireAmpli = new Node();
    private Node outputModule = new Node();

    // Handles for the emitter positions
    private Spatial pathInputAmpli;
    private Spatial pathOutputAmpli;
    private Spatial pathAntenneTx;
    
    // Paths
    private Geometry inputAmpPath;
    private Geometry outputAmpPath;
    
    private int touchCount = 0;

    
    // this is PIIIIIII! (kick persian)
    private final float pi = (float) Math.PI;
    
    private float tpfCumul = 0;
    //try particle
    private Geometry particle;
    
    public Amplification(ScenarioCommon sc,Camera Camera, Spatial destinationHandle){
        super(sc, Camera, destinationHandle);
        this.needAutoGenIfMain = true;
        scenarioCommon.registerObserver(this);
        this.setName("Amplification");
        loadUnmovableObjects();
        loadMovableObjects();
        loadArrows();
    }
    
    @Override
    protected void loadUnmovableObjects() {
        scene = (Node) assetManager.loadModel("Models/Amplification/Antenne_Tx.j3o");
        scene.setName("Amplification");
        this.attachChild(scene);
        
        // Get the handles of the emitters
        pathInputAmpli = scene.getChild("Module.Handle.In");
        pathOutputAmpli = scene.getChild("Ampli.Handle");
        pathOutputAmpli.setCullHint(cullHint.Always);
        pathAntenneTx = scene.getChild("Module.Handle.Out");
   
        // Get the different paths
        Node wireInAmpli_node = (Node) scene.getChild("Path.Entree");
        inputAmpPath = (Geometry) wireInAmpli_node.getChild("NurbsPath.000");
        inputAmpPath.setCullHint(cullHint.Always);
        Node wireOutAmpli_node = (Node) scene.getChild("Path.PostAmpli");
        outputAmpPath = (Geometry) wireOutAmpli_node.getChild("NurbsPath.005");
        outputAmpPath.setCullHint(cullHint.Always);
        
        initParticlesEmitter(inputWireAmpli, pathInputAmpli, inputAmpPath, null);
        initParticlesEmitter(outputWireAmpli, pathOutputAmpli, outputAmpPath, null);
        
     
        // Set names for the emitters  // VOir si utile dans ce module
        inputWireAmpli.setName("InputWireAmpli");
        outputWireAmpli.setName("OutputWireAmpli");
        outputModule.setName("OutputModule");
        
        
   //-------------------------------AirParticleEmitterControl------------------
        Material mat2 = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", new ColorRGBA(0, 1, 1, 0.5f));
        mat2.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        scene.attachChild(outputModule);
        
        if(this.destinationHandle != null){
            outputModule.setLocalTranslation(pathAntenneTx.getLocalTranslation()); // TO DO: utiliser le object handle blender pour position
            outputModule.addControl(new AirParticleEmitterControl(this.destinationHandle, 20, 25, mat2));
            outputModule.getControl(ParticleEmitterControl.class).registerObserver(this.destinationHandle.getControl(ParticleEmitterControl.class));
            outputModule.getControl(ParticleEmitterControl.class).setEnabled(true);
            outputModule.setUserData(AppGetter.USR_AMPLIFICATION, 0.5f);

      //-------------------------------AirParticleEmitterControl------------------

            inputWireAmpli.getControl(ParticleEmitterControl.class).registerObserver(this);
            outputWireAmpli.getControl(ParticleEmitterControl.class).registerObserver(this);
        }
        
        
        this.initPatternGenerator();
        
        Vector3f handleSliderBegin = scene.getChild("Slider.Handle.Begin").getLocalTranslation();
        scene.getChild("Slider.Handle.Begin").setCullHint(cullHint.Always);
        Vector3f handleSliderEnd = scene.getChild("Slider.Handle.End").getLocalTranslation();
        scene.getChild("Slider.Handle.End").setCullHint(cullHint.Always);
        translationIncrement = handleSliderEnd.subtract(handleSliderBegin).divide(4);
        
    }

    @Override
    protected void loadMovableObjects() {
        ampliSliderButton = scene.getChild("Button.000");
        ampliSliderBox = scene.getChild("Cube");
        ampliSliderButton.setName("SliderButton");
        ampliSliderBox.setName("SliderBox");
        
        this.spotlight = ScenarioCommon.spotlightFactory();
        
         //Test
        touchable = new Node();
        touchable.setName("Touchable");
        scene.attachChild(touchable);
        
        touchable.attachChild(ampliSliderButton);
        touchable.attachChild(ampliSliderBox);
        
       
    }

    private void loadArrows()
    {
        sliderArrow = new Node();
        sliderArrow.move(ampliSliderBox.getLocalTranslation().add(0.0f,1.0f,0.0f));
        sliderArrow.addControl(new Arrows("touch", assetManager, 3));
        LookAtCameraControl control1 = new LookAtCameraControl(Camera);
        sliderArrow.addControl(control1);
        sliderArrow.setLocalScale(2f);
        scene.attachChild(sliderArrow);
        
        moveArrow = new Node();
        moveArrow.move(ampliSliderBox.getLocalTranslation().add(0.0f,1.0f,0.0f));
        moveArrow.addControl(new Arrows("move", assetManager, 10));
    }
    
    private void ampliSliderUpdate() {
        if (isTouched) {
            switch(touchCount) {
                case 1:
                case 2:
                case 3:
                case 4:
                    ampliSliderButton.move(translationIncrement);
                    ampliScale = touchCount*0.25f + 0.5f;
                    break;
                
                case 5:
                case 6:
                case 7:
                    ampliSliderButton.move(translationIncrement.negate());
                    ampliScale = 2.5f - touchCount*0.25f;
                    break;
                case 8:
                    ampliSliderButton.move(translationIncrement.negate());
                    ampliScale = 2.5f - touchCount*0.25f;
                    touchCount = 0;
                    break;
            }
            isTouched = false;
            if(ampliScale < 0){
                ampliScale = -1/ampliScale;
            }
            this.outputModule.setUserData(AppGetter.USR_AMPLIFICATION, ampliScale);
            
        }
        
 
    }
    
    /**
     * Remove hints, is called after touch occurs
     */
    private void removeHintImages()
    {
        sliderArrow.getControl(FadeControl.class).setShowImage(false);
        sliderArrow.getControl(Arrows.class).resetTimeLastTouch();
    }
    
    //Scale handle of the particle
    private Spatial particleAmplification(Spatial particle){

        this.destinationHandle.setUserData(AppGetter.USR_AMPLIFICATION, ampliScale);
        return particle.scale(ampliScale);
    }
    
     private void initParticlesEmitter(Node signalEmitter, Spatial handle, Geometry path, Camera cam) {
        scene.attachChild(signalEmitter);
        signalEmitter.setLocalTranslation(handle.getLocalTranslation()); // TO DO: utiliser le object handle blender pour position
        signalEmitter.setLocalRotation(handle.getWorldRotation());
        signalEmitter.addControl(new StaticWireParticleEmitterControl(path.getMesh(), 3.5f, cam));
        signalEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);
    }
    
     
    @Override
    protected void initPatternGenerator(){
               this.initDrumGuitarSound();
        Spatial baseGeom = scenarioCommon.initBaseGeneratorParticle();
        Spatial[] carrier = ScenarioCommon.initCarrierGeometries();
              
        Node signal = new Node();
        signal.attachChild(carrier[0].clone());
        signal.attachChild(baseGeom.clone());
        scenarioCommon.modulateFMorAM(signal, baseGeom, isFM);
        signal.setUserData("CarrierShape", signal.getChild(0).getName());
        signal.setUserData("isFM", isFM);

        this.getInputHandle().addControl(new PatternGeneratorControl(0.5f, signal.clone(), 7, scenarioCommon.minBaseParticleScale, 
                                                                     scenarioCommon.maxBaseParticleScale, true));  
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

                    if (nameToCompare.equals("SliderButton")) {
                        touchCount++;
                        isTouched = true;
                        removeHintImages();
                        break;
                    } else if (nameToCompare.equals("SliderBox")) {
                        touchCount++;
                        isTouched = true;
                        removeHintImages();
                        break;
                    }
                }
            }
            break;
        }
    }

    @Override
    protected boolean simpleUpdate(float tpf) {
        //moveArrow.simpleUpdate(tpf);
        //sliderArrow.simpleUpdate(tpf);
        
        if (this.emphasisChange) {
            objectEmphasis();
            this.emphasisChange = false;
        }
        
        this.destinationHandle.setUserData(AppGetter.USR_SOURCE_TRANSLATION, this.getWorldTranslation());
        ampliSliderUpdate();
        
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


    public Vector3f getParticleReceiverHandle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void sendSignalToEmitter(Geometry newSignal, float magnitude) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void emitterObserverUpdate(Spatial spatial, String notifierId) {
       
         if (notifierId.equals("InputWireAmpli")) {
          //Change Scale
             spatial = this.particleAmplification(spatial);
             outputWireAmpli.getControl(ParticleEmitterControl.class).emitParticle(spatial);
         } else if(notifierId.equals("OutputWireAmpli")) {
             spatial = this.particleAmplification(spatial);
             outputModule.getControl(ParticleEmitterControl.class).emitParticle(spatial);
         }   
    }

    @Override
    public void signalEndOfPath(Geometry caller, float magnitude) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Spatial getInputHandle() {
        return inputWireAmpli;
    }
    
    @Override
    protected void initTitleBox() {
        titleTextBox = new TextBox(assetManager, 
                                    titleText, 
                                    TEXTSIZE,
                                    TEXTCOLOR, 
                                    TEXTBOXCOLOR,
                                    TITLEWIDTH * 1.1f, 
                                    TITLEHEIGHT * 2f, 
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
    public void autoGenObserverUpdate(Spatial newCarrier, boolean isFm) {
        this.isFM = isFm;
        Node node = new Node();
        Spatial baseGeom = scenarioCommon.initBaseGeneratorParticle();
        node.attachChild(newCarrier.clone());
        node.attachChild(baseGeom);
        List<Spatial> lst = scenarioCommon.generateModulatedWaves(
               node , baseGeom, isFm, 10,scenarioCommon.minBaseParticleScale ,scenarioCommon.maxBaseParticleScale);
        
        this.getInputHandle().getControl(PatternGeneratorControl.class).setParticleList(lst);
    }

    @Override
    protected void onFirstNodeActions() {
        super.onFirstNodeActions(); //To change body of generated methods, choose Tools | Templates.
        this.attachChild(moveArrow);
    }

    @Override
    protected void onSecondNodeActions() {
        super.onSecondNodeActions(); //To change body of generated methods, choose Tools | Templates.
        this.detachChild(moveArrow);
        this.updateNoise(0f);
    }

    @Override
    protected void objectEmphasis() {
        if (this.spotlight != null) {            
            switch(this.currentObjectToEmphasisOn) {
                // Attach on microphone
                case 0:
                    this.spotlight.setLocalTranslation(scene.getChild("Ampli.Handle").getLocalTranslation().add(0.0f,-scene.getChild("Ampli.Handle").getLocalTranslation().y,0.0f));
                    this.spotlight.setLocalScale(new Vector3f(3.0f,30.0f,3.0f));
                    scene.attachChild(this.spotlight);
                    break;
                case 1:
                    this.spotlight.setLocalTranslation(scene.getChild("Antenna.Handle.In").getLocalTranslation().add(0.0f,-scene.getChild("Antenna.Handle.In").getLocalTranslation().y,0.0f));
                    this.spotlight.setLocalScale(new Vector3f(2.0f,30.0f,2.0f));
                    scene.attachChild(this.spotlight);
                    break;
                default:
                    scene.detachChild(this.spotlight);
                    break;
            }
        }
    }
    
}
