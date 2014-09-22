package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.Signal;
import com.galimatias.teslaradio.world.effects.SignalEmitter;
import com.galimatias.teslaradio.world.effects.SignalType;
import com.galimatias.teslaradio.world.effects.TextBox;
import com.galimatias.teslaradio.world.observer.ParticleEmitReceiveLinker;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
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
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.PQTorus;
import com.jme3.scene.shape.Quad;

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
    private Boolean isFM = true;
    private Boolean switchIsToggled = false;
    
    // 3D objects of the scene
    private Spatial turnButton;
    private Spatial actionSwitch;
    
    // TextBox of the scene
    private TextBox titleTextBox;
    private TextBox digitalDisplay;
    
    // Default text to be seen when scenario starts
    private String titleText = "La Modulation";
    private float titleTextSize = 0.5f;
    private ColorRGBA defaultTextColor = ColorRGBA.Green;
    
    // Signals emitters 
    private SignalEmitter wirePcbEmitter = new SignalEmitter(this);
    private SignalEmitter carrierEmitter = new SignalEmitter(this);
    private SignalEmitter pcbAmpEmitter = new SignalEmitter(this);
    
    // The entry of the chip
    private Spatial chipEntry;
    private CollisionResults soundChipCollisions = new CollisionResults();
    
    // Handles for the emitter positions
    private Spatial pathInHandle;
    private Spatial pathCarrierHandle;
    private Spatial pathOutHandle;
    
    // Paths
    private Geometry pathIn;
    private Geometry pathCarrier;
    private Geometry pathOut;
    
    // Geometry of the carrier signals
    private Geometry cubeCarrier;
    private Geometry pyramidCarrier;
    private Geometry dodecagoneCarrier; // Really...
    private Geometry currentCarrier;
    
    //CHANGE THIS VALUE CHANGE THE PARTICULE BEHAVIOUR 
    //Setting the direction norms and the speed displacement to the trajectories
    private float VecDirectionNorms = 80f;
    private float SoundParticles_Speed = 50f;
    
    // this is PIIIIIII! (kick persian)
    private final float pi = (float) Math.PI; 

    //Angle for test purposes
    private float trackableAngle = 0;
    private int direction = 1;
    
    //Variable for switch
    private float initAngleSwitch;
    private float tpfCumul =0;
    private Quaternion rotationXSwitch = new Quaternion();

    private PhysicsSpace space;
    
    public Modulation(AssetManager assetManager, com.jme3.renderer.Camera Camera, ParticleEmitReceiveLinker particleLinker, PhysicsSpace physicSpace) {

        super(assetManager, Camera, particleLinker);

        this.space = physicSpace;
        
        loadUnmovableObjects();
        loadMovableObjects();
    }

    @Override
    protected void loadUnmovableObjects() {
        
        scene = (Node) assetManager.loadModel("Models/Modulation/modulation.j3o");
        scene.setName("Modulation");
        this.attachChild(scene);
        
        wirePcbEmitter.setSpace(space);
        
        // Get the chip entrance geometry
        chipEntry = scene.getChild("Gate.In");
        chipEntry.addControl(new GhostControl(new BoxCollisionShape(new Vector3f(0.1f,0.1f,0.1f))));
        chipEntry.getControl(GhostControl.class).setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
        space.add(chipEntry);
        
        WireBox wirebox = new WireBox(0.1f, 0.1f, 0.1f);
        wirebox.fromBoundingBox((BoundingBox) chipEntry.getWorldBound());
        
        Geometry bx = new Geometry("TheMesh", wirebox);
        Material mat_box = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_box.setColor("Color", ColorRGBA.Blue);
        bx.setMaterial(mat_box);
        bx.updateModelBound();
        bx.setLocalTranslation(chipEntry.getWorldTranslation().addLocal(0.0f, 1.0f, 0.0f)); 
        scene.attachChild(bx);
        
        // Get the handles of the emitters
        pathInHandle = scene.getChild("Handle.In");
        pathCarrierHandle = scene.getChild("Handle.Generator");
        pathOutHandle = scene.getChild("Handle.Chip.Out");
        
        // Get the different paths
        Node wirePcb_node = (Node) scene.getChild("Path.In.Object");
        pathIn = (Geometry) wirePcb_node.getChild("Path.In.Nurbs");
        Node carrier_node = (Node) scene.getChild("Path.Generator.Object");
        pathCarrier = (Geometry) carrier_node.getChild("Path.Generator.Nurbs");
        Node pcbAmp_node = (Node) scene.getChild("Path.Out.Object");
        pathOut = (Geometry) pcbAmp_node.getChild("Path.Out.Nurbs");

        initDigitalDisplay();
        
        // Initialize the emitters
        initParticlesEmitter(wirePcbEmitter, pathInHandle, pathIn);
        initParticlesEmitter(carrierEmitter, pathCarrierHandle, pathCarrier);
        initParticlesEmitter(pcbAmpEmitter, pathOutHandle, pathOut);
        
        
    }

    @Override
    public void loadMovableObjects() {
        turnButton = scene.getChild("Button");
        actionSwitch = scene.getChild("Switch");
        initAngleSwitch = actionSwitch.getLocalRotation().toAngleAxis(Vector3f.UNIT_X);
        
        initCarrierGeometries();
        
        //Assign touchable
        touchable = new Node();//(Node) scene.getParent().getChild("Touchable")
        touchable.attachChild(actionSwitch);
        scene.attachChild(touchable);  
        
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
                        }
                    }
                }
                break;
        }
    }

    @Override
    public boolean simpleUpdate(float tpf) {    
        
        trackableAngle += direction * (pi/9)* tpf;
        
        if (trackableAngle >= 2*pi || trackableAngle <= 0)
        {
            //trackableAngle = 0;
            direction *= -1;
        }
        
        wirePcbEmitter.simpleUpdate(tpf, this.Camera);
        carrierEmitter.simpleUpdate(tpf, null);
        pcbAmpEmitter.simpleUpdate(tpf, null);
        
        checkTrackableAngle(trackableAngle);
        checkModulationMode(tpf);
        
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
    public Vector3f getParticleReceiverHandle() {
        return pathInHandle.getWorldTranslation();
    }

    @Override
    public void sendSignalToEmitter(Geometry newSignal, float magnitude) {
        if (wirePcbEmitter != null) {
            // We have a new material out there! Since the Signal is now becoming "Electrical", we set the Electrical material to it

            wirePcbEmitter.prepareEmitParticles(newSignal, magnitude);
        }
    }
    
    //Dynamic move
    private void checkModulationMode(float tpf) {
        if(switchIsToggled) {
            tpfCumul = tpfCumul+ 3*tpf;
            switchRotation(isFM, tpfCumul);
           // switchRotationWithoutDynamicSwitch(isFM);
            float currAngle = actionSwitch.getLocalRotation().toAngleAxis(Vector3f.UNIT_X);
            if(currAngle >= initAngleSwitch && currAngle <= (2*pi - initAngleSwitch)){
                switchIsToggled = false;
                tpfCumul = 0;
            }
        }
    }
    /*
    //only switch
    private void checkModulationMode(float tpf) {
        switchRotationWithoutDynamicSwitch(isFM);
        switchIsToggled = false;
    }*/
    
    public void toggleModulationMode() {
        if(!switchIsToggled){   
            isFM = isFM ? false : true;
            switchIsToggled = true;   
        }
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
        mat1.setColor("Color", ColorRGBA.Green);
        pyramidCarrier.setMaterial(mat1);
        
        Node dodecagone = (Node) assetManager.loadModel("Models/Modulation/Dodecahedron.j3o");
        dodecagoneCarrier = (Geometry) dodecagone.getChild("Solid.0041");
        dodecagoneCarrier.setMaterial(mat1);
                         
    }
    
    private void initParticlesEmitter(SignalEmitter signalEmitter, Spatial handle, Geometry path) {
        
        scene.attachChild(signalEmitter);
        signalEmitter.setLocalTranslation(handle.getWorldTranslation()); // TO DO: utiliser le object handle blender pour position
        signalEmitter.setWaves(path.getMesh(), 0.25f, 3.5f);
    }
    
    private void initDigitalDisplay() {
        
        // Default configuration of the digital display
        digitalDisplay = new TextBox(assetManager, 
                                     sFM1061, 
                                     titleTextSize, 
                                     defaultTextColor, 
                                     new ColorRGBA(0.1f, 0.1f, 0.1f, 0.0f), 
                                     3.5f, 1.0f, 
                                     "DigitalDisplay", 
                                     BitmapFont.Align.Center.Center, 
                                     false, 
                                     false);
        
        // Get the digital display parameters
        scene.getChild("Display").setQueueBucket(queueBucket.Transparent);
        scene.getChild("Cube.005").setQueueBucket(queueBucket.Opaque);
        Vector3f displayPosition = scene.getChild("Display").getWorldTranslation();
        displayPosition.addLocal(-0.4f, 0.35f, 0.0f);
        
        digitalDisplay.setLocalTranslation(displayPosition);
        Quaternion rotY = new Quaternion();
        Quaternion rotZ = new Quaternion();
        rotY.fromAngleAxis(pi/2, Vector3f.UNIT_Y);
        rotZ.fromAngleAxis(-pi/2, Vector3f.UNIT_X);
        digitalDisplay.rotate(rotY);
        digitalDisplay.rotate(rotZ);
        scene.attachChild(digitalDisplay);
    }  
    
    private void turnTunerButton(float ZXangle) {
        
        Quaternion rot = new Quaternion();
        rot.fromAngleAxis(ZXangle, Vector3f.UNIT_Y);
        turnButton.setLocalRotation(rot);
    }
    
    private void changeModulation(int frequency, Boolean isFM) {
        
        if (isFM){
            switch(frequency){
                case 1:
                    digitalDisplay.simpleUpdate(sFM1061, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
               //     System.out.println(sFM1061);
                    changeElectricalParticles(1);
                    break;
                case 2:
                    digitalDisplay.simpleUpdate(sFM977, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                 //   System.out.println(sFM977);
                    changeElectricalParticles(2);
                    break;
                case 3:
                    digitalDisplay.simpleUpdate(sFM952, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                 //   System.out.println(sFM952);
                    changeElectricalParticles(3);
                    break;
                default:
                    digitalDisplay.simpleUpdate(sFM1061, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                   // System.out.println(sFM1061);
                    changeElectricalParticles(1);
                    break;
            }
        }
        else{
            switch(frequency){
                case 1:
                    digitalDisplay.simpleUpdate(sAM697, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                  //  System.out.println(sAM697);
                    changeElectricalParticles(1);
                    break;
                case 2:
                    digitalDisplay.simpleUpdate(sAM498, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                  //  System.out.println(sAM498);
                    changeElectricalParticles(2);
                    break;
                case 3:
                    digitalDisplay.simpleUpdate(sAM707, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                  //  System.out.println(sAM707);
                    changeElectricalParticles(3);
                    break;
                default :
                    digitalDisplay.simpleUpdate(sAM697, titleTextSize, defaultTextColor, Camera, Vector3f.UNIT_X);
                  // System.out.println(sAM697);
                    changeElectricalParticles(1);
                    break;
                }
        }   
    }
    
    private void changeElectricalParticles(int frequency) {
        
        switch(frequency)
        {
            case 1:
                currentCarrier = cubeCarrier;
                break;
            case 2:
                currentCarrier = pyramidCarrier;
                break;
            case 3:
                currentCarrier = dodecagoneCarrier;
                break;
        }
        
        if (carrierEmitter != null) {
            carrierEmitter.prepareEmitParticles(currentCarrier, 0.25f);
        }
        
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
    
    private void switchRotation(boolean isFM, float tpfCumul){
        if(!isFM){
            rotationXSwitch.fromAngleAxis(angleRangeTwoPi(initAngleSwitch - tpfCumul), Vector3f.UNIT_X);
            actionSwitch.setLocalRotation(rotationXSwitch);
        } else {
            rotationXSwitch.fromAngleAxis(angleRangeTwoPi(-initAngleSwitch + tpfCumul), Vector3f.UNIT_X);
            actionSwitch.setLocalRotation(rotationXSwitch);
        }
    }
    
      private void switchRotationWithoutDynamicSwitch(boolean isFM){
        if(!isFM){
            rotationXSwitch.fromAngleAxis(initAngleSwitch, Vector3f.UNIT_X);
            actionSwitch.setLocalRotation(rotationXSwitch);
        } else {
            rotationXSwitch.fromAngleAxis(-initAngleSwitch , Vector3f.UNIT_X);
            actionSwitch.setLocalRotation(rotationXSwitch);
        }
    }
    //convert angle for range [0 ; 2pi]
     private float angleRangeTwoPi(float angle){
         float resultat = 0;
            if(angle >= 0){
                resultat =  angle;
            } else {
                resultat = 2*pi + angle;
            }
        return resultat;
     }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        
        if (pcbAmpEmitter != null) {
            
            pcbAmpEmitter.prepareEmitParticles(cubeCarrier, 0.25f);
        }
    }
   
}

