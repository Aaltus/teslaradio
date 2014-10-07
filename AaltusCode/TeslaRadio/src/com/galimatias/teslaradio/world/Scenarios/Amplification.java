/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.AirParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.ParticleEmitterControl;

import com.galimatias.teslaradio.world.effects.StaticWireParticleEmitterControl;
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
import com.jme3.scene.shape.Box;


/**
 *
 * @author Barliber
 */
public final class Amplification extends Scenario implements EmitterObserver{
    
    private final static String TAG = "Amplification";
    
      // 3D objects of the scene
    private Spatial turnAmpliButton;

    //Test 
    private Spatial generateParticle;
    private Geometry cubeTestParticle;
    
     /**
     * TODO Remove this bool and associated code in simpleUpdate when it works
     * on Android. Only for debug purposes.
     */
    private final static boolean DEBUG_ANGLE = true;
    
    
    // TextBox of the scene
    private TextBox titleTextBox;
    
    // Default text to be seen when scenario starts
    private String titleText = "L'Amplification";
    private float titleTextSize = 0.5f;
    private ColorRGBA defaultTextColor = ColorRGBA.Green;

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
    
    private Spatial destinationHandle;
    private Camera cam;
    
    private float tpfCumul = 0;
    
    public Amplification(Camera Camera, Spatial destinationHandle){
        super(Camera, destinationHandle);
        
        this.destinationHandle = destinationHandle;
        this.cam = Camera;
        loadUnmovableObjects();
        loadMovableObjects();
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
        mat2.setColor("Color", new ColorRGBA(0, 1, 1, 1f));
        mat2.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        scene.attachChild(outputModule);
        outputModule.setLocalTranslation(pathAntenneTx.getLocalTranslation()); // TO DO: utiliser le object handle blender pour position
        outputModule.addControl(new AirParticleEmitterControl(this.destinationHandle, 2, 13, mat2));
        outputModule.getControl(ParticleEmitterControl.class).registerObserver(this.destinationHandle.getControl(ParticleEmitterControl.class));
        outputModule.getControl(ParticleEmitterControl.class).setEnabled(true);
        
  //-------------------------------AirParticleEmitterControl------------------
        
        inputWireAmpli.getControl(ParticleEmitterControl.class).registerObserver(this);
        outputWireAmpli.getControl(ParticleEmitterControl.class).registerObserver(this);
        
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
    
    
    
    private void ampliButtonRotation(float ZXangle) {
        Quaternion rot = new Quaternion();
        rot.fromAngleAxis(ZXangle, Vector3f.UNIT_Y);
        turnAmpliButton.setLocalRotation(rot);
    }
    //Scale handle of the particle
    private Spatial particleAmplification(Spatial particle){
        float angle = turnAmpliButton.getLocalRotation().toAngleAxis(Vector3f.UNIT_X);
        float ampliScale = 1+ angle/(2*pi);
        particle.setLocalScale(ampliScale, ampliScale, ampliScale);
        return particle;
    }
    
     private void initParticlesEmitter(Node signalEmitter, Spatial handle, Geometry path, Camera cam) {
        scene.attachChild(signalEmitter);
        signalEmitter.setLocalTranslation(handle.getLocalTranslation()); // TO DO: utiliser le object handle blender pour position
        signalEmitter.addControl(new StaticWireParticleEmitterControl(path.getMesh(), 3.5f, cam));
        signalEmitter.getControl(ParticleEmitterControl.class).setEnabled(true); 
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
              //  focusableObjects.collideWith(ray, results);
                touchable.collideWith(ray, results);

                // 4. Print the results
                //Log.d(TAG, "----- Collisions? " + results.size() + "-----");
                for (int i = 0; i < results.size(); i++) {
                     //For each hit, we know distance, impact point, name of geometry.
                    float dist = results.getCollision(i).getDistance();
                    Vector3f pt = results.getCollision(i).getContactPoint();
                    String hit = results.getCollision(i).getGeometry().getName();

                    //Log.e(TAG, "  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
                }
                
                //Generate test particle
                inputWireAmpli.getControl(ParticleEmitterControl.class).emitParticle(newTestParticle());  
                
                // 5. Use the results (we mark the hit object)
                if (results.size() > 0)
                {

                    // The closest collision point is what was truly hit:
                    String nameToCompare = results.getClosestCollision().getGeometry().getParent().getName();

                 /*   if (nameToCompare.equals(micro.getName()))
                    {
                        this.microTouchEffect();
                    }
                    else if (nameToCompare.equals(microphoneTextBox.getName()))
                    {
                        showInformativeMenu = true;
                    }*/
                }
             }
        }
    }

    @Override
    public boolean simpleUpdate(float tpf) {
        if (DEBUG_ANGLE) {
            tpfCumul = tpf+ tpfCumul;
            ampliButtonRotation(3*pi/2);
        } else {
            ampliButtonRotation((Float)this.getUserData("angleX"));
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
         if (notifierId.equals("InputWireAmpli")) {
          //Change Scale
             outputWireAmpli.getControl(ParticleEmitterControl.class).emitParticle(particleAmplification(spatial));
         } else if(notifierId.equals("OutputWireAmpli")){
             outputModule.getControl(ParticleEmitterControl.class).emitParticle(particleAmplification(spatial));
         }
        
    }

    @Override
    public void signalEndOfPath(Geometry caller, float magnitude) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Spatial getInputHandle() {
        return inputWireAmpli;
    }
    
    public Geometry newTestParticle(){
        //Test generate particle
        Box cube = new Box(0.25f, 0.25f, 0.25f);
        Geometry particle = new Geometry("CubeCarrier", cube);
        Material mat1 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        particle.setMaterial(mat1);
        return particle;
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