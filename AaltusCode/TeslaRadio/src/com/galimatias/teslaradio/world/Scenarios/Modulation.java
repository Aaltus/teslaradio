package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.SignalEmitter;
import com.galimatias.teslaradio.world.effects.TextBox;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.event.TouchEvent;
import static com.jme3.input.event.TouchEvent.Type.DOWN;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Triangle;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.PQTorus;

/**
 * Created by Batcave on 2014-09-09.
 */
public class Modulation extends Scenario {

    private final static String TAG = "Modulation";
  
    private int frequency;
    
    // Values displayed on the digital screen of the PCB 3D object
    private final String sFM1061 = "106.1 FM";
    private final String sFM977 = "97.7 FM";
    private final String sFM952 = "95.2 FM";
    private final String sAM697 = "697 AM";
    private final String sAM498 = "498 AM";
    private final String sAM707 = "707 AM";
    private       Boolean isFM = true;
    
    // 3D objects of the scene
    private Spatial pcb;
    private Spatial button;
    
    // TextBox of the scene
    private TextBox titleTextBox;
    private TextBox digitalDisplay;
    
    // Default text to be seen when scenario starts
    private String titleText = "La Modulation";
    private float titleTextSize = 0.5f;
    private ColorRGBA defaultTextColor = new ColorRGBA(1f, 0f, 1f, 1f);
    
    // Signals emitters 
    private SignalEmitter electricalParticles;
    private SignalEmitter carrierParticles;
    
    // Geometry of the carrier signals
    private Geometry cubeCarrier;
    private Geometry pyramidCarrier;
    private Geometry dodecagoneCarrier; // Really...
    
    //CHANGE THIS VALUE CHANGE THE PARTICULE BEHAVIOUR 
    //Setting the direction norms and the speed displacement to the trajectories
    private float VecDirectionNorms = 80f;
    private float SoundParticles_Speed = 50f;
    
    // this is PIIIIIII! (kick persian)
    private final float pi = (float) Math.PI; 

    //Angle for test purposes
    private float trackableAngle = 0;
    private int direction = 1;
    
    public Modulation(AssetManager assetManager, com.jme3.renderer.Camera Camera /*, ScenarioObserver observer*/) {

        super(assetManager, Camera);

        loadUnmovableObjects();
        loadMovableObjects();
    }
    
    public Modulation(AssetManager assetManager) {
        this(assetManager,null /*, null */);
    }

    @Override
    protected void loadUnmovableObjects() {

       /** create a blue box at coordinates (1,-1,1) */
        Box box1 = new Box(1,1,1);
        Geometry blue = new Geometry("Box", box1);
        blue.scale(10.0f);
        blue.setLocalTranslation(new Vector3f(0,10,0));
        Material mat1 = new Material(assetManager, 
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Green);
        blue.setMaterial(mat1);
        this.attachChild(blue);
        
        initTunerButton();
    }

    @Override
    public void loadMovableObjects() {

        initCarrierGeometries();
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
                    for (int i = 0; i < results.size(); i++) {
                        // For each hit, we know distance, impact point, name of geometry.
                        float dist = results.getCollision(i).getDistance();
                        Vector3f pt = results.getCollision(i).getContactPoint();
                        String hit = results.getCollision(i).getGeometry().getName();

                        //Log.e(TAG, "  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
                    }

                    // 5. Use the results (we mark the hit object)
                    if (results.size() > 0)
                    {

                        // The closest collision point is what was truly hit:
                        CollisionResult closest = results.getClosestCollision();

                        Spatial touchedGeometry = closest.getGeometry();
                        String nameToCompare = touchedGeometry.getParent().getName();
                        
                        if (nameToCompare.equals(this.getChild("Switch").getName()))
                        {
                            toggleModulationMode();
                            break;
                        }
                    }
                }
                break;
        }
    }

    @Override
    public boolean simpleUpdate(float tpf) {
        
        //float angleX = this.getUserData("angleX");
        
        trackableAngle += direction * pi/9/2000;
        if (trackableAngle >= 2*pi || trackableAngle <= 0)
        {
            //trackableAngle = 0;
            direction *= -1;
        }
        
        checkTrackableAngle(trackableAngle);
        
        
        
        return false;
    }

    @Override
    public void setGlobalSpeed(float speed) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onAudioEvent() {

    }
    
    public void toggleModulationMode() {
        isFM = isFM ? false : true;
    }
    
    private void initCarrierGeometries() {
        
        Box cube = new Box(1,1,1);
        cubeCarrier = new Geometry("CubeCarrier", cube);
        Material mat1 = new Material(assetManager, 
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        cubeCarrier.setMaterial(mat1);

        Dome pyramid = new Dome(2, 4, 1.0f);
        pyramidCarrier = new Geometry("PyramidCarrier", pyramid);
        pyramidCarrier.setMaterial(mat1);
        
        // TODO Change this carrier
        PQTorus torus = new PQTorus(pi, pi, pi, pi, RF_BOUND, refreshFlags);
        dodecagoneCarrier = new Geometry("DodecagoneCarrier", torus);
        dodecagoneCarrier.setMaterial(mat1);
                
    }
    
    private void initEmitters() {
        
    }
    
    private void initTunerButton() {
        
        /** create a blue box at coordinates (1,-1,1) */
        Box box1 = new Box(1,1,1);
        Geometry blue = new Geometry("Box2", box1);
        blue.scale(10.0f);
        blue.setLocalTranslation(new Vector3f(0,10,40));
        Material mat1 = new Material(assetManager, 
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        blue.setMaterial(mat1);
        this.attachChild(blue);
        
    }
    
    private void turnTunerButton(float ZXangle) {
        
        Spatial Box = this.getChild("Box2");
        Quaternion rot = new Quaternion();
        rot.fromAngleAxis(ZXangle, Vector3f.UNIT_Y);
        Box.setLocalRotation(rot);
    }
    
    private void changeModulation(int frequency, Boolean isFM) {
        
        if (isFM){
            switch(frequency){
                case 1:
                    //digitalDisplay.simpleUpdate(sFM1061, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    System.out.println(sFM1061);
                    changeElectricalParticles();
                    break;
                case 2:
                    //digitalDisplay.simpleUpdate(sFM977, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    System.out.println(sFM977);
                    changeElectricalParticles();
                    break;
                case 3:
                    //digitalDisplay.simpleUpdate(sFM952, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    System.out.println(sFM952);
                    changeElectricalParticles();
                    break;
                default:
                    //digitalDisplay.simpleUpdate(sFM1061, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    System.out.println(sFM1061);
                    changeElectricalParticles();
                    break;
            }
        }
        else{
            switch(frequency){
                case 1:
                    //digitalDisplay.simpleUpdate(sAM697, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    System.out.println(sAM697);
                    changeElectricalParticles();
                    break;
                case 2:
                    //digitalDisplay.simpleUpdate(sAM498, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    System.out.println(sAM498);
                    changeElectricalParticles();
                    break;
                case 3:
                    //digitalDisplay.simpleUpdate(sAM707, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    System.out.println(sAM707);
                    changeElectricalParticles();
                    break;
                default :
                    //digitalDisplay.simpleUpdate(sAM697, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                    System.out.println(sAM697);
                    changeElectricalParticles();
                    break;
                }
        }   
    }
    
    private void changeElectricalParticles() {
        
    }
    
    private void checkTrackableAngle(float trackableAngle) {
        
        float stepRange = 2*pi/3;
        
        if (trackableAngle >= 0 && trackableAngle < stepRange  )
        {
            turnTunerButton(trackableAngle);
            changeModulation(1, isFM);
        }
        else if (trackableAngle >= stepRange && trackableAngle < 2*stepRange  )
        {
            turnTunerButton(trackableAngle);
            changeModulation(2, isFM);
        }
        else if (trackableAngle >= 2*stepRange && trackableAngle < 3*stepRange  )
        {
            turnTunerButton(trackableAngle);
            changeModulation(3, isFM);
        }
            
    }
}

