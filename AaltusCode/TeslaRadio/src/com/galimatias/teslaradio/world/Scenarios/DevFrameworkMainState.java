/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.ar4android.vuforiaJME.AppGetter;
import com.galimatias.teslaradio.world.Scenarios.ScenarioManager;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.utils.AppLogger;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jimbojd72
 */
public class DevFrameworkMainState extends AbstractAppState implements ActionListener {
    
    
    private static final String ScenarioB_move_X_pos = "ScenarioB_move_X_pos";
    private static final String ScenarioB_move_X_neg = "ScenarioB_move_X_neg";
    private static final String ScenarioB_move_Y_pos = "ScenarioB_move_Y_pos";
    private static final String ScenarioB_move_Y_neg = "ScenarioB_move_Y_neg";
    private static final String ScenarioB_move_Z_pos = "ScenarioB_move_Z_pos";
    private static final String ScenarioB_move_Z_neg = "ScenarioB_move_Z_neg";
    private static final String ScenarioB_rotate_Y_pos = "ScenarioB_rotate_Y_pos";
    private static final String ScenarioB_rotate_Y_neg = "ScenarioB_rotate_Y_neg";
    private static final String TOGGLE_DRAG_FLYBY_CAMERA = "toggle_cam";
    
    private SimpleApplication app;
    private Node guiNode;
    private Node rootNode;
    private FlyByCamera flyCam;
    private AssetManager assetManager;
    private RenderManager renderManager;
    private InputManager inputManager;
    private AppSettings settings;    
    private final Camera camera;
    private boolean dragMouseToMove = true;
    
    private List<Node> nodeList;
    private Node nodeA;
    private Node nodeB;
    private Geometry floor;
    private Geometry floor2;
    
    

    public List<Node> getNodeList() {
        return nodeList;
    }
    
    public DevFrameworkMainState(SimpleApplication app, FlyByCamera flyByCam)
    {
        this.app = app;
        this.assetManager  = this.app.getAssetManager();//AppGetter.getAssetManager();
        this.renderManager = this.app.getRenderManager();
        this.inputManager  = this.app.getInputManager();
        this.settings      = this.app.getContext().getSettings();
        this.guiNode       = this.app.getGuiNode();
        this.rootNode      = this.app.getRootNode();
        this.camera        = this.app.getCamera();
        this.flyCam        = flyByCam;
        
        inputManager.setCursorVisible(true);
        //mouseInput.setCursorVisible(true);
        flyCam.setDragToRotate(dragMouseToMove);
        flyCam.setMoveSpeed(100f);
        camera.setLocation(new Vector3f(-60,80,80));
        camera.lookAt(rootNode.getWorldTranslation(), Vector3f.UNIT_Y);

        //Initialized a list of nodes to attach to the scenario manager.
        nodeList = new ArrayList<Node>();
        nodeA = new Node();
        nodeB = new Node();
        
        float value = 60;
        nodeA.move(-value,0,0);
        nodeB.move(+value,0,0);
        nodeList.add(nodeA);
        nodeList.add(nodeB);
        
        this.floor = new Geometry("Floor", new Box (60,Float.MIN_VALUE,60));
        Material floorMaterial  = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        floorMaterial.setColor("Color", new ColorRGBA(0.75f,0.75f,0.75f, 1f));
        floor.setMaterial(floorMaterial);
        

        this.floor2 = new Geometry("Floor", new Box (60,Float.MIN_VALUE,60));
        Material floorMaterial2  = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        floorMaterial2.setColor("Color", ColorRGBA.Yellow);
        floor2.setMaterial(floorMaterial2);

        
        
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); 
        
        //init(applicationType, nodeList, camera, appListener);
        // init stuff that is independent of whether state is PAUSED or RUNNING
        //This is called 
        
        rootNode.attachChild(nodeA);
        rootNode.attachChild(nodeB);
        nodeB.attachChild(floor2);
        nodeA.attachChild(floor);
        addInputMapping();
        
      
   }
    
    @Override
    public void cleanup() {
      super.cleanup();
      // unregister all my listeners, detach all my nodes, etc.../*
      removeInputMapping();
    }

    @Override
    public void setEnabled(boolean enabled) {
      // Pause and unpause
      super.setEnabled(enabled);
      /*if(enabled){
        // init stuff that is in use while this state is RUNNING
        this.app.getRootNode().attachChild(getX()); // modify scene graph...
        this.app.doSomethingElse();                 // call custom methods...
      } else {
        // take away everything not needed while this state is PAUSED
        ...
      }*/
    }
    
    
    private void addInputMapping(){
        
        inputManager.addMapping(ScenarioB_move_X_neg, new KeyTrigger(KeyInput.KEY_NUMPAD1));
        inputManager.addMapping(ScenarioB_move_Z_neg, new KeyTrigger(KeyInput.KEY_NUMPAD2));
        inputManager.addMapping(ScenarioB_move_X_pos, new KeyTrigger(KeyInput.KEY_NUMPAD3));
        inputManager.addMapping(ScenarioB_move_Y_neg, new KeyTrigger(KeyInput.KEY_NUMPAD4));
        inputManager.addMapping(ScenarioB_move_Z_pos, new KeyTrigger(KeyInput.KEY_NUMPAD5));
        inputManager.addMapping(ScenarioB_rotate_Y_neg, new KeyTrigger(KeyInput.KEY_NUMPAD6));
        inputManager.addMapping(ScenarioB_move_Y_pos, new KeyTrigger(KeyInput.KEY_NUMPAD7));
        inputManager.addMapping(ScenarioB_rotate_Y_pos, new KeyTrigger(KeyInput.KEY_NUMPAD9));
        inputManager.addMapping(TOGGLE_DRAG_FLYBY_CAMERA, new KeyTrigger(KeyInput.KEY_TAB));

        // Add the names to the action listener.
        inputManager.addListener(this, ScenarioB_move_X_neg);
        inputManager.addListener(this, ScenarioB_move_Y_neg);
        inputManager.addListener(this, ScenarioB_move_X_pos);
        inputManager.addListener(this, ScenarioB_move_Z_neg);
        inputManager.addListener(this, ScenarioB_move_Y_pos);
        inputManager.addListener(this, ScenarioB_rotate_Y_neg);
        inputManager.addListener(this, ScenarioB_move_Z_pos);
        inputManager.addListener(this, ScenarioB_rotate_Y_pos);
        inputManager.addListener(this, TOGGLE_DRAG_FLYBY_CAMERA);
    
    }
    
    private void removeInputMapping(){
        
        inputManager.removeListener(this);
    }
     
   
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        Vector3f tempPosition = new Vector3f();
        Quaternion tempRotation = new Quaternion();
        
        if(!isPressed){
            
            if(name.equals(ScenarioB_move_X_pos)){
                tempPosition = nodeB.getLocalTranslation();
                nodeB.setLocalTranslation(tempPosition.x+(10), tempPosition.y, tempPosition.z);
            }
            else if(name.equals(ScenarioB_move_X_neg)){
                tempPosition = nodeB.getLocalTranslation();
                nodeB.setLocalTranslation(tempPosition.x-(10), tempPosition.y, tempPosition.z);                
            }
            else if(name.equals(ScenarioB_move_Y_pos)){
                tempPosition = nodeB.getLocalTranslation();
                nodeB.setLocalTranslation(tempPosition.x, tempPosition.y+(10), tempPosition.z);                 
            }
            else if(name.equals(ScenarioB_move_Y_neg)){
                tempPosition = nodeB.getLocalTranslation();
                nodeB.setLocalTranslation(tempPosition.x, tempPosition.y-(10), tempPosition.z);                 
            }
            else if(name.equals(ScenarioB_move_Z_pos)){
                tempPosition = nodeB.getLocalTranslation();
                nodeB.setLocalTranslation(tempPosition.x, tempPosition.y, tempPosition.z+(10));                 
            }
            else if(name.equals(ScenarioB_move_Z_neg)){
                tempPosition = nodeB.getLocalTranslation();
                nodeB.setLocalTranslation(tempPosition.x, tempPosition.y, tempPosition.z-(10));                 
            }
            else if(name.equals(ScenarioB_rotate_Y_pos)){
                tempRotation = nodeB.getLocalRotation();
                tempRotation.multLocal((new Quaternion()).fromAngles(0, 0.1f, 0));
                nodeB.setLocalRotation(tempRotation);
            }
            else if(name.equals(ScenarioB_rotate_Y_neg)){
                tempRotation = nodeB.getLocalRotation();
                tempRotation.multLocal((new Quaternion()).fromAngles(0, -0.1f, 0));
                nodeB.setLocalRotation(tempRotation);          
            }
            else if(name.equals(TOGGLE_DRAG_FLYBY_CAMERA)){
                dragMouseToMove =! dragMouseToMove;
                flyCam.setDragToRotate(dragMouseToMove);
            }
        }
    }
    
    
}
