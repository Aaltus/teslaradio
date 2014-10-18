/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.*;
import com.galimatias.teslaradio.world.observer.EmitterObserver;
import com.jme3.font.BitmapFont;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.utils.AppLogger;

/**
 *
 * @author Barliber
 */
public final class Reception extends Scenario implements EmitterObserver  {
    
    // TextBox of the scene
    private TextBox titleTextBox;
    
    // Default text to be seen when scenario starts
    private String titleText = "La Réception";

    //Test 
    private Spatial antenne;
    
    // this is PIIIIIII! (kick persian)
    private final float pi = (float) Math.PI;
    
    // Signals emitters 
    private Node inputAntenneRx = new Node();
    private Node outputAntenneRx = new Node();
    private Node outputModule = new Node();
    
    // Handles for the emitter positions
    private Spatial pathAntenneRx;
    private Spatial outputHandle;
    
    //Test
    // Output signals
    private Geometry cubeOutputSignal;
    
    // Paths
    private Geometry antenneRxPath;
    //try particle
    private Geometry particle;
    
    // Wifi logo related
    int signalIntensity = 0;
    private ImageBox wifiLogoLow;
    private ImageBox wifiLogoMedium;
    private ImageBox wifiLogoFull;
    private Node wifi = new Node();
    
    //Autogen stuff
    private Node autoGenParticle;
    private Node cubeSignal;
    private Node pyramidSignal;
    private Node dodecagoneSignal;

    private Arrows moveArrow;
    
    private Boolean isFM = true;
    
    public Reception(com.jme3.renderer.Camera Camera, Spatial destinationHandle) {
        super(Camera, destinationHandle);

        this.cam = Camera;
        this.destinationHandle = destinationHandle;
        this.needAutoGenIfMain = true;
        loadUnmovableObjects();
        loadMovableObjects();
        loadArrows();
        
        //Generate try particle
        Box cube = new Box(0.25f, 0.25f, 0.25f);
        particle = new Geometry("CubeCarrier", cube);
        Material mat1 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        particle.setMaterial(mat1);
        particle.setUserData("CarrierShape", "CubeCarrier");
        particle.setUserData("isFM", true);
    }

    @Override
    protected void loadUnmovableObjects() {
        scene = (Node) assetManager.loadModel("Models/Reception/Antenne_Rx.j3o");
        scene.setName("Reception");
        this.attachChild(scene);
        
        //scene rotation
        Quaternion rot = new Quaternion();
        rot.fromAngleAxis(-pi, Vector3f.UNIT_Y);
        scene.setLocalRotation(rot);
        
        initTitleBox();
        
        wifiLogoLow = new ImageBox(1.0f, 1.0f, assetManager, "Wifi Logo Low", "Models/Reception/wifi-logo_low.png", 0.0f);
        wifiLogoMedium = new ImageBox(1.0f, 1.0f, assetManager, "Wifi Logo Medium", "Models/Reception/wifi-logo_medium.png", 0.0f);
        wifiLogoFull = new ImageBox(1.0f, 1.0f, assetManager, "Wifi Logo Full", "Models/Reception/wifi-logo_full.png", 0.0f);
        
        addWifiControl(wifiLogoLow);
        addWifiControl(wifiLogoMedium);
        addWifiControl(wifiLogoFull);

        scene.attachChild(wifi);
        wifi.attachChild(wifiLogoLow);
        
        pathAntenneRx = scene.getChild("Path.Sortie.001");
        outputHandle = scene.getChild("Antenna.Handle.Out");
        
        wifi.setLocalTranslation(outputHandle.getLocalTranslation().add(3.0f, 5.0f, -3.0f));
        
        // Get the different paths
        antenneRxPath = (Geometry)((Node) pathAntenneRx).getChild("NurbsPath.005");
       
        initParticlesEmitter(outputAntenneRx, pathAntenneRx, antenneRxPath, null);
        
        scene.attachChild(outputModule);
        System.out.println(outputHandle.getLocalTranslation().toString());
        outputModule.setLocalTranslation(outputHandle.getLocalTranslation()); // TO DO: utiliser le object handle blender pour position
        if(this.destinationHandle != null){
            outputModule.addControl(new DynamicWireParticleEmitterControl(this.destinationHandle, 3.5f, null));
            outputModule.getControl(ParticleEmitterControl.class).setEnabled(true);
        }

        // Set names for the emitters  // VOir si utile dans ce module
        // inputAntenneRx.setName("InputAntenneRx");
        outputAntenneRx.setName("OutputAntenneRx");

        // inputAntenneRx.getControl(ParticleEmitterControl.class).registerObserver(this);
        if(this.destinationHandle != null){
            outputModule.getControl(ParticleEmitterControl.class).registerObserver(this.destinationHandle.getControl(ParticleEmitterControl.class));    
            outputAntenneRx.getControl(ParticleEmitterControl.class).registerObserver(this);
        }
        
        initModulatedParticles();
        this.getInputHandle().addControl(new PatternGeneratorControl(0.5f, autoGenParticle.clone(), 7, ModulationCommon.minBaseParticleScale, 
                                                                     ModulationCommon.maxBaseParticleScale, true));
        this.waveTime = 1;
        this.particlePerWave = 4;
    }

    private void loadArrows()
    {
        moveArrow = new Arrows("move", null, assetManager, 10);
        this.attachChild(moveArrow);
    }
   
    private void addWifiControl(ImageBox wifiLogo) {
        LookAtCameraControl lookAtControl = new LookAtCameraControl(Camera);
        wifiLogo.addControl(lookAtControl);
        wifiLogo.getControl(LookAtCameraControl.class).setEnabled(true);
    }

    @Override
    protected void loadMovableObjects() {
        //implement touchable
        touchable = new Node();
        touchable.setName("Touchable");
        scene.attachChild(touchable);
        
        //Test Board
        antenne = scene.getChild("Board.001");
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
        updateWifiLogos(signalIntensity);
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
        return outputAntenneRx;
    }

    @Override
    public void signalEndOfPath(Geometry caller, float magnitude) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void updateSignalIntensity(Float normScale) { 
        wifi.detachAllChildren();
        if (normScale >= 0 && normScale < 0.33f) {
            signalIntensity = 1;
        } else if (normScale >= 0.33f && normScale < 0.66f) {
            signalIntensity = 2;
        } else {
            signalIntensity = 3;
        }
    }
    
    private void updateWifiLogos(int signalIntensity) {
        
        switch(signalIntensity) {
            case 1:
                wifi.attachChild(wifiLogoLow);
                break;
            case 2:
                wifi.attachChild(wifiLogoMedium);
                break;
            case 3:
                wifi.attachChild(wifiLogoFull);
                break;
            default:
                wifi.attachChild(wifiLogoLow);
                break;
        }
    }

    @Override
    public void emitterObserverUpdate(Spatial spatial, String notifierId) {
        if (notifierId.equals("OutputAntenneRx")) {

             if (outputAntenneRx != null) {
                 
                Float particleScale = spatial.getUserData("Scale");
                float normScale = spatial.getWorldScale().length()/particleScale;
                updateSignalIntensity(normScale);
                outputModule.getControl(ParticleEmitterControl.class).emitParticle(spatial);
             }
        }
    }
    
    private void initParticlesEmitter(Node signalEmitter, Spatial handle, Geometry path, Camera cam) {
        scene.attachChild(signalEmitter);
        signalEmitter.setLocalTranslation(handle.getLocalTranslation()); // TO DO: utiliser le object handle blender pour position
        signalEmitter.addControl(new StaticWireParticleEmitterControl(path.getMesh(), 3.5f, cam));
        signalEmitter.getControl(ParticleEmitterControl.class).setEnabled(true); 
    }
    
    @Override
    protected void startAutoGeneration(){
        super.startAutoGeneration();
        
        scene.detachChild(wifi);
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
    

    private void initModulatedParticles() {
        Geometry baseGeom = ModulationCommon.initBaseGeneratorParticle();
        Spatial[] carrier = ModulationCommon.initCarrierGeometries();
              
        this.cubeSignal = new Node();
        this.cubeSignal.attachChild(carrier[0].clone());
        ModulationCommon.modulateFMorAM(this.cubeSignal, baseGeom, isFM);
        this.cubeSignal.attachChild(baseGeom.clone());
        this.cubeSignal.setUserData("CarrierShape", this.cubeSignal.getChild(0).getName());
        this.cubeSignal.setUserData("isFM", isFM);
        
        this.pyramidSignal = new Node();
        this.pyramidSignal.attachChild(carrier[0].clone());
        ModulationCommon.modulateFMorAM(this.pyramidSignal, baseGeom, isFM);
        this.pyramidSignal.attachChild(baseGeom.clone());
        this.pyramidSignal.setUserData("CarrierShape", this.pyramidSignal.getChild(0).getName());
        this.pyramidSignal.setUserData("isFM", isFM);
       
        this.dodecagoneSignal = new Node();
        this.dodecagoneSignal.attachChild(carrier[0].clone());
        ModulationCommon.modulateFMorAM(this.dodecagoneSignal, baseGeom, isFM);
        this.dodecagoneSignal.attachChild(baseGeom.clone());
        this.dodecagoneSignal.setUserData("CarrierShape", this.dodecagoneSignal.getChild(0).getName());
        this.dodecagoneSignal.setUserData("isFM", isFM);
        
        this.autoGenParticle = this.cubeSignal;
       
    }
}
