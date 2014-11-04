/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.ar4android.vuforiaJME.AppGetter;
import com.galimatias.teslaradio.world.effects.*;
import com.galimatias.teslaradio.world.observer.AutoGenObserver;
import com.galimatias.teslaradio.world.observer.EmitterObserver;
import com.jme3.font.BitmapFont;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.utils.AppLogger;
import java.util.List;

/**
 *
 * @author Barliber
 */
public final class Reception extends Scenario implements EmitterObserver, AutoGenObserver  {
    
    // Default text to be seen when scenario starts
    private String titleText = "La Réception";
     
    private TextBox titleTextBox;
    
    // Signals emitters 
    private Node outputAntenneRx;
    private Node outputModule;
    
    // Handles for the emitter positions
    private Spatial pathAntenneRx;
    private Spatial outputHandle;
    
    // Paths
    private Geometry antenneRxPath;
    
    // Wifi logo related
    int signalIntensity = 0;
    private ImageBox wifiLogoLow;
    private ImageBox wifiLogoMedium;
    private ImageBox wifiLogoFull;
    private ImageBox wifiLogoNull;
    private Node wifi = new Node();
    
    //Autogen stuff
    private Node autoGenParticle;
    private Node cubeSignal;
    private Node pyramidSignal;
    private Node dodecagoneSignal;

    private Arrows moveArrow;
    
    private Boolean isFM = true;
    private Boolean newWave = false;
    
    private float tpf = 0f;

    
    private final float maxDistance = 100.0f;
    
    private float tpfDistanceCumul = 0f;

    public Reception(ScenarioCommon sc,com.jme3.renderer.Camera Camera, Spatial destinationHandle) {
        super(sc,Camera, destinationHandle, "Sounds/reception.ogg" );
        
        this.needAutoGenIfMain = true;     
        scenarioCommon.registerObserver(this);
        this.setName("Reception");
        loadUnmovableObjects();
        loadMovableObjects();
        loadArrows();
        
    }

    @Override
    protected void loadUnmovableObjects() {
        scene = (Node) assetManager.loadModel("Models/Reception/Antenne_Rx.j3o");
        scene.setName("Reception");
        this.attachChild(scene);
        
        outputAntenneRx = new Node();
        outputModule = new Node();
        //scene rotation
        Quaternion rot = new Quaternion();
        rot.fromAngleAxis(-pi, Vector3f.UNIT_Y);
        scene.setLocalRotation(rot);
        
        initTitleBox();
        
        wifiLogoLow = new ImageBox(1.0f, 1.0f, assetManager, "Wifi Logo Low", "Models/Commons/wifi-logo_low.png", 0.0f);
        wifiLogoMedium = new ImageBox(1.0f, 1.0f, assetManager, "Wifi Logo Medium", "Models/Commons/wifi-logo_medium.png", 0.0f);
        wifiLogoFull = new ImageBox(1.0f, 1.0f, assetManager, "Wifi Logo Full", "Models/Commons/wifi-logo_full.png", 0.0f);
        wifiLogoNull = new ImageBox(1.0f, 1.0f, assetManager, "Wifi Logo Full", "Models/Commons/wifi-logo_low_low.png", 0.0f);
       
        
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
        outputAntenneRx.getControl(ParticleEmitterControl.class).registerObserver(this);
        
        scene.attachChild(outputModule);
        outputModule.setLocalTranslation(outputHandle.getLocalTranslation());
        
        if(this.destinationHandle != null){
            outputModule.addControl(new DynamicWireParticleEmitterControl(this.destinationHandle, 3.5f, null, true));
            outputModule.getControl(ParticleEmitterControl.class).setEnabled(true);
            outputModule.getControl(ParticleEmitterControl.class).registerObserver(this.destinationHandle.getControl(ParticleEmitterControl.class));

        }

        // Set names for the emitters
        outputAntenneRx.setName("OutputAntenneRx");
        outputAntenneRx.setUserData(AppGetter.USR_SOURCE_TRANSLATION, 0f);
        
        initPatternGenerator();
    }

    private void loadArrows()
    {
        moveArrow = new Arrows("move", null, assetManager, 10);
    }

    private void addWifiControl(ImageBox wifiLogo) {
        LookAtCameraControl lookAtControl = new LookAtCameraControl(Camera);
        wifiLogo.addControl(lookAtControl);
        wifiLogo.getControl(LookAtCameraControl.class).setEnabled(true);
    }

    @Override
    protected void loadMovableObjects() {
        this.spotlight = ScenarioCommon.spotlightFactory();
        
        //implement touchable
        touchable = new Node();
        touchable.setName("Touchable");
        scene.attachChild(touchable);
    }

    @Override
    public void restartScenario() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onScenarioTouch(String name, TouchEvent touchEvent, float v) {
        /*switch(touchEvent.getType()){

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

                        if (nameToCompare.equals(titleTextBox.getName()))
                        {
                            showInformativeMenu = true;
                            break;
                        }

                }
            }
            break;
        }*/
    }

    @Override
    protected boolean simpleUpdate(float tpf) {

        if (this.emphasisChange) {
            objectEmphasis();
            this.emphasisChange = false;
        }
        
        this.tpfDistanceCumul += tpf;
        if(this.tpfDistanceCumul > 0.35f){   
            this.updateDistanceStatus();
            this.tpfDistanceCumul = 0;
        }
        moveArrow.simpleUpdate(tpf);
       
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
    
    private void updateSignalIntensity(float normScale) { 
        wifi.detachAllChildren();
        
        if(normScale == 0){
            signalIntensity = 0;
        }
        else if (normScale > 0 && normScale < 0.33f) {
            signalIntensity = 1;
        } else if (normScale >= 0.33f && normScale < 0.66f) {
            signalIntensity = 2;
        } else {
            signalIntensity = 3;
        }
        if(this.backgroundSound != null){
            this.updateSoundLevel(normScale);
        }
       
    }
    
    private void updateWifiLogos(int signalIntensity) {
        
        switch(signalIntensity) {
            case 0:
                wifi.attachChild(wifiLogoNull);
                break;
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
                wifi.attachChild(wifiLogoNull);
                break;
        }
    }
    
    private void updateSoundLevel(float normScale){
        if(normScale == 0){
            this.getControl(SoundControl.class).updateNoiseLevel(1);
        }else if (normScale > 0.75f){
            this.getControl(SoundControl.class).updateNoiseLevel(0);
        }else{
            this.getControl(SoundControl.class).updateNoiseLevel(1-normScale);
        }
        
    }

    @Override
    public void emitterObserverUpdate(Spatial spatial, String notifierId) {
        if (notifierId.equals("OutputAntenneRx")) {
            
             if (outputAntenneRx != null) {
     
                Float particleScale = spatial.getUserData(AppGetter.USR_SCALE);
                
                float normScale = spatial.getWorldScale().length()/particleScale;
                
                outputModule.getControl(ParticleEmitterControl.class).emitParticle(spatial);
             }
        }
    }
    
    private void initParticlesEmitter(Node signalEmitter, Spatial handle, Geometry path, Camera cam) {
        scene.attachChild(signalEmitter);
        signalEmitter.setLocalTranslation(handle.getLocalTranslation());
        signalEmitter.addControl(new StaticWireParticleEmitterControl(path.getMesh(), 3.5f, cam));
        signalEmitter.getControl(ParticleEmitterControl.class).setEnabled(true); 
    }
    
    @Override
    protected void onFirstNodeActions(){
        super.onFirstNodeActions();
        
        scene.detachChild(wifi);
        this.detachChild(moveArrow);
    }

    @Override
    protected void onSecondNodeActions() {
        super.onSecondNodeActions();
        
        scene.attachChild(wifi);
        this.attachChild(moveArrow);
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
       titleTextBox.rotate((float) -FastMath.PI / 2, 0, 0);

       titleTextBox.move(titleTextPosition);
       this.attachChild(titleTextBox);
    }
    
    @Override
    protected void initPatternGenerator() {
        Spatial baseGeom = scenarioCommon.initBaseGeneratorParticle();
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
        
        this.getInputHandle().addControl(new PatternGeneratorControl(0.5f, autoGenParticle.clone(), 7, scenarioCommon.minBaseParticleScale, 
                                                                     scenarioCommon.maxBaseParticleScale, true));
       
        this.updateSignalIntensity(0.3f);

    }
    
     @Override
    public void autoGenObserverUpdate(Spatial newCarrier, boolean isFm) {
        this.isFM = isFm;
        Node node = new Node();
        Spatial baseGeom = scenarioCommon.initBaseGeneratorParticle();
        node.attachChild(newCarrier.clone());
        List<Spatial> lst = scenarioCommon.generateModulatedWaves(
               node , baseGeom, isFm, 10,scenarioCommon.minBaseParticleScale ,scenarioCommon.maxBaseParticleScale);
        
        this.getInputHandle().getControl(PatternGeneratorControl.class).setParticleList(lst);
    }
     
     private void updateDistanceStatus(){
         
         Vector3f wt = this.getWorldTranslation();
         wt = wt.subtract((Vector3f) this.getInputHandle().getUserData(AppGetter.USR_SOURCE_TRANSLATION));
         float distance = wt.divide(this.getWorldScale()).length();
         distance -= 8; //offset
         distance = distance < 0 ? 0 : distance;
         float signalRatio = distance / 20.0f;
         signalRatio = signalRatio > 1 ? 1 : signalRatio;
         this.updateSignalIntensity(1-signalRatio);
         this.updateWifiLogos(signalIntensity);
     }

    @Override
    protected void objectEmphasis() {
        if (this.spotlight != null) {            
            switch(this.currentObjectToEmphasisOn) {
                // Attach on microphone
                case 0:
                    this.spotlight.setLocalTranslation(scene.getChild("axis").getLocalTranslation().add(0.0f,-scene.getChild("axis").getLocalTranslation().y,0.0f));
                    this.spotlight.setLocalScale(new Vector3f(2.0f,20.0f,2.0f));
                    this.attachChild(this.spotlight);
                    break;  
                default:
                    this.detachChild(this.spotlight);
                    break;
            }
        }
    }
    
    
}
