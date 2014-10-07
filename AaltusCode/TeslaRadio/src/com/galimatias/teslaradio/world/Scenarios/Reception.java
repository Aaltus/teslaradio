/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.ParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.StaticWireParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.TextBox;
import com.galimatias.teslaradio.world.observer.EmitterObserver;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.input.event.TouchEvent;
import static com.jme3.input.event.TouchEvent.Type.DOWN;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Barliber
 */
public class Reception extends Scenario implements EmitterObserver  {
    
    private Camera cam;
    private Spatial destinationHandle;
    
    // TextBox of the scene
    private TextBox titleTextBox;
    
    // Default text to be seen when scenario starts
    private String titleText = "La Réception";
    private float titleTextSize = 0.5f;
    private ColorRGBA defaultTextColor = ColorRGBA.Green;

    //Test 
    private Spatial antenne;
    
     // this is PIIIIIII! (kick persian)
    private final float pi = (float) Math.PI;
    
    // Signals emitters 
    private Node inputAntenneRx = new Node();
    private Node outputAntenneRx = new Node();
    
    // Handles for the emitter positions
    private Spatial pathAntenneRx;
    
    //Test
    // Output signals
    private Geometry cubeOutputSignal;
    
    // Paths
    private Geometry antenneRxPath;
    
    
    public Reception(com.jme3.renderer.Camera Camera, Spatial destinationHandle) {
        super(Camera, destinationHandle);

        this.cam = Camera;
        this.destinationHandle = destinationHandle;

        loadUnmovableObjects();
        loadMovableObjects();
        
        //Test generate particle
        Box cube = new Box(0.25f, 0.25f, 0.25f);
        cubeOutputSignal = new Geometry("CubeCarrier", cube);
        Material mat1 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        cubeOutputSignal.setMaterial(mat1);
   
        
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
                
        pathAntenneRx = scene.getChild("Path.Sortie.001");
        
         // Get the different paths
        Node wireAntenneRx_node = (Node) scene.getChild("Path.Sortie.001");
        antenneRxPath = (Geometry) wireAntenneRx_node.getChild("NurbsPath.005");
       
        initParticlesEmitter(outputAntenneRx, pathAntenneRx, antenneRxPath, null);
        
        // Set names for the emitters  // VOir si utile dans ce module
       // inputAntenneRx.setName("InputAntenneRx");
        outputAntenneRx.setName("OutputAntenneRx");
        
        
       // inputAntenneRx.getControl(ParticleEmitterControl.class).registerObserver(this);
        outputAntenneRx.getControl(ParticleEmitterControl.class).registerObserver(this);

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
        switch (touchEvent.getType()) {
  
            //Checking for down event is very responsive
            case DOWN:

                //case TAP:
                if (name.equals("Touch")) {
                    // 1. Reset results list.
                    CollisionResults results = new CollisionResults();

                    // 2. Mode 1: user touch location.
                    //Vector2f click2d = inputManager.getCursorPosition();

                    Vector2f click2d = new Vector2f(touchEvent.getX(), touchEvent.getY());
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
                    
                    //Test generate particle
                    outputAntenneRx.getControl(ParticleEmitterControl.class).emitParticle(cubeOutputSignal);
                   
                 /*   // 5. Use the results (we mark the hit object)
                    if (results.size() > 0) {
                        // The closest collision point is what was truly hit:
                        CollisionResult closest = results.getClosestCollision();

                        Spatial touchedGeometry = closest.getGeometry();
                        String nameToCompare = touchedGeometry.getParent().getName();
    
                        if (nameToCompare.equals(this.getChild("Board.001").getName())) {
                         
                            
                        }
                    }*/
                }
                break;
        }
    }

    @Override
    public boolean simpleUpdate(float tpf) {
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
    public Spatial getInputHandle() {
        return outputAntenneRx;
    }

    @Override
    public void signalEndOfPath(Geometry caller, float magnitude) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void emitterObserverUpdate(Spatial spatial, String notifierId) {
        if (notifierId.equals("OutputModule")) {
          //Change Scale
             outputAntenneRx.getControl(ParticleEmitterControl.class).emitParticle(spatial);
         }
    }
    
     private void initParticlesEmitter(Node signalEmitter, Spatial handle, Geometry path, Camera cam) {
        scene.attachChild(signalEmitter);
        signalEmitter.setLocalTranslation(handle.getLocalTranslation()); // TO DO: utiliser le object handle blender pour position
        signalEmitter.addControl(new StaticWireParticleEmitterControl(path.getMesh(), 3.5f, cam));
        signalEmitter.getControl(ParticleEmitterControl.class).setEnabled(true); 
    }

    private void initTitleBox() {

       boolean lookAtCamera = false;
       boolean showDebugBox = false;
       float textBoxWidth = 5.2f;
       float textBoxHeight = 0.8f;

       ColorRGBA titleTextColor = new ColorRGBA(1f, 1f, 1f, 1f);
       ColorRGBA titleBackColor = new ColorRGBA(0.1f, 0.1f, 0.1f, 0.5f);
       titleTextBox = new TextBox(assetManager,
               titleText,
               titleTextSize,
               titleTextColor,
               titleBackColor,
               textBoxWidth,
               textBoxHeight,
               "titleText",
               BitmapFont.Align.Center.Center,
               showDebugBox,
               lookAtCamera);

       //move the text on the ground without moving
       Vector3f titleTextPosition = new Vector3f(0f, 0.25f, 6f);
       titleTextBox.rotate((float) -Math.PI / 2, 0, 0);

       titleTextBox.move(titleTextPosition);
       this.attachChild(titleTextBox);
   }
}