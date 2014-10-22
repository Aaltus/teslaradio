/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.ar4android.vuforiaJME.AppGetter;
import com.galimatias.teslaradio.world.effects.*;
import com.galimatias.teslaradio.world.effects.ParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.PatternGeneratorControl;

import com.galimatias.teslaradio.world.effects.StaticWireParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.TextBox;
import com.galimatias.teslaradio.world.observer.AutoGenObserver;
import com.galimatias.teslaradio.world.observer.EmitterObserver;

import com.jme3.font.BitmapFont;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.material.RenderState;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.List;


/**
 *
 * @author Barliber
 */
public final class Amplification extends Scenario implements EmitterObserver, AutoGenObserver{
    
    private final static String TAG = "Amplification";
    
      // 3D objects of the scene
    private Spatial turnAmpliButton;

    //Test 
    private Spatial generateParticle;
    private Geometry cubeTestParticle;
    
    private Node autoGenParticle;
    
    private Node cubeSignal;
    private Node pyramidSignal;
    private Node dodecagoneSignal;
    private Arrows moveArrow;
    
    private Boolean isFM = true;
    
    
    
    // TextBox of the scene
    private TextBox titleTextBox;
    
    // Default text to be seen when scenario starts
    private String titleText = "L'Amplification";

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

    
    // this is PIIIIIII! (kick persian)
    private final float pi = (float) Math.PI;
    
    private float tpfCumul = 0;
    //try particle
    private Geometry particle;
    
    public Amplification(ScenarioCommon sc,Camera Camera, Spatial destinationHandle){
        super(sc, Camera, destinationHandle, "Sounds/amplification.ogg");
        this.needAutoGenIfMain = true;
        this.destinationHandle = destinationHandle;
        this.cam = Camera;
        loadUnmovableObjects();
        loadMovableObjects();
        loadArrows();
    }
    
    @Override
    protected void loadUnmovableObjects() {
        scene = (Node) assetManager.loadModel("Models/Amplification/Antenne_Tx.j3o");
        scene.setName("Amplification");
        this.attachChild(scene);

        //scene rotation
        scene.setLocalTranslation(new Vector3f(0.5f, 0.0f, 1.7f));
        Quaternion rot = new Quaternion();
        rot.fromAngleAxis(-pi / 2, Vector3f.UNIT_Y);
        scene.setLocalRotation(rot);

        initTitleBox();
        
        // Get the handles of the emitters
        pathInputAmpli = scene.getChild("Module.Handle.In");
        pathOutputAmpli = scene.getChild("Ampli.Handle");
        pathAntenneTx = scene.getChild("Module.Handle.Out");
   
        // Get the different paths
        Node wireInAmpli_node = (Node) scene.getChild("Path.Entree");
        inputAmpPath = (Geometry) wireInAmpli_node.getChild("NurbsPath.000");
        Node wireOutAmpli_node = (Node) scene.getChild("Path.PostAmpli");
        outputAmpPath = (Geometry) wireOutAmpli_node.getChild("NurbsPath.005");
     
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

      //-------------------------------AirParticleEmitterControl------------------

            inputWireAmpli.getControl(ParticleEmitterControl.class).registerObserver(this);
            outputWireAmpli.getControl(ParticleEmitterControl.class).registerObserver(this);
        }
        
        
        this.initModulatedParticles();
        this.getInputHandle().addControl(new PatternGeneratorControl(0.5f, autoGenParticle.clone(), 7, scenarioCommon.minBaseParticleScale, 
                                                                     scenarioCommon.maxBaseParticleScale, true));
        
        this.waveTime = 1;
        this.particlePerWave = 4;
   
    }

    @Override
    protected void loadMovableObjects() {
         turnAmpliButton = scene.getChild("Button.001");      
         //Test
        touchable = new Node();
        touchable.setName("Touchable");
        scene.attachChild(touchable);
        
        //Test Board
        generateParticle = scene.getChild("Board.001");
    }

    private void loadArrows()
    {
        moveArrow = new Arrows("move", null, assetManager, 10);
        this.attachChild(moveArrow);
    }
    
    private void ampliButtonRotation(float ZXangle) {
        Quaternion rot = new Quaternion();
        rot.fromAngleAxis(ZXangle, Vector3f.UNIT_Y);
        turnAmpliButton.setLocalRotation(rot);
    }
    //Scale handle of the particle
    private Spatial particleAmplification(Spatial particle){
        float angle = turnAmpliButton.getLocalRotation().toAngleAxis(Vector3f.UNIT_X);
        float ampliScale = 1 + angle/(2*pi);
        particle.scale(ampliScale/1.25f);
        this.setUserData(AppGetter.USR_AUDIO_SCALE, ampliScale-1);
        return particle;
    }
    
     private void initParticlesEmitter(Node signalEmitter, Spatial handle, Geometry path, Camera cam) {
        scene.attachChild(signalEmitter);
        signalEmitter.setLocalTranslation(handle.getLocalTranslation()); // TO DO: utiliser le object handle blender pour position
        signalEmitter.addControl(new StaticWireParticleEmitterControl(path.getMesh(), 3.5f, cam));
        signalEmitter.getControl(ParticleEmitterControl.class).setEnabled(true); 
    }
    
     
        
    private void initModulatedParticles(){
        Geometry baseGeom = scenarioCommon.initBaseGeneratorParticle();
        Spatial[] carrier = scenarioCommon.initCarrierGeometries();
                
        this.cubeSignal = new Node();
        this.cubeSignal.attachChild(carrier[0].clone());
        scenarioCommon.modulateFMorAM(this.cubeSignal, baseGeom, isFM);
        this.cubeSignal.attachChild(baseGeom.clone());
        this.cubeSignal.setUserData("CarrierShape", this.cubeSignal.getChild(0).getName());
        this.cubeSignal.setUserData("isFM", isFM);
        
        this.pyramidSignal = new Node();
        this.pyramidSignal.attachChild(carrier[1].clone());
        scenarioCommon.modulateFMorAM(this.pyramidSignal, baseGeom, isFM);
        this.pyramidSignal.attachChild(baseGeom.clone());
        this.pyramidSignal.setUserData("CarrierShape", this.pyramidSignal.getChild(0).getName());
        this.pyramidSignal.setUserData("isFM", isFM);
       
        this.dodecagoneSignal = new Node();
        this.dodecagoneSignal.attachChild(carrier[2].clone());
        scenarioCommon.modulateFMorAM(this.dodecagoneSignal, baseGeom, isFM);
        this.dodecagoneSignal.attachChild(baseGeom.clone());
        this.dodecagoneSignal.setUserData("CarrierShape", this.dodecagoneSignal.getChild(0).getName());
        this.dodecagoneSignal.setUserData("isFM", isFM);
        
        this.autoGenParticle = this.cubeSignal;
       
    }

     
        
    

    @Override
    public void restartScenario() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onScenarioTouch(String name, TouchEvent touchEvent, float v) {
        
    }

    @Override
    protected boolean simpleUpdate(float tpf) {
		moveArrow.simpleUpdate(tpf);

        if (DEBUG_ANGLE) {
            tpfCumul = tpf+ tpfCumul;
            ampliButtonRotation(tpfCumul);
        } else {
            float trackableAngle = this.getUserData("angleX");
            ampliButtonRotation(trackableAngle);
            invRotScenario(trackableAngle + (pi / 2));
        }
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
        Spatial carrier = spatial;//null;
        /*for(Spatial sp : ((Node) spatial).getChildren()){
            if(sp.getName().contains("Carrier")){
                carrier =  sp;
            }
        }*/
         if (notifierId.equals("InputWireAmpli")) {
          //Change Scale
             carrier = this.particleAmplification(carrier);
             outputWireAmpli.getControl(ParticleEmitterControl.class).emitParticle(spatial);
         } else if(notifierId.equals("OutputWireAmpli")) {
             Float scale = new Float(spatial.getWorldScale().length());
             spatial.setUserData(AppGetter.USR_SCALE, scale);
             carrier = this.particleAmplification(carrier);
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
    public void autoGenObserverUpdate(Spatial newCarrier, boolean isFm) {
        this.isFM = isFm;
        Node node = new Node();
        Geometry baseGeom = scenarioCommon.initBaseGeneratorParticle();
        node.attachChild(baseGeom);
        List<Spatial> lst = scenarioCommon.generateModulatedWaves(
               node , newCarrier, isFm, 7,scenarioCommon.minBaseParticleScale ,scenarioCommon.maxBaseParticleScale);
        node.attachChild(newCarrier);
        this.getInputHandle().getControl(PatternGeneratorControl.class).setParticleList(lst);
    }
    
}
