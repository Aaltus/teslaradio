package com.galimatias.teslaradio.world.commons;

import com.ar4android.vuforiaJME.AppGetter;
import com.galimatias.teslaradio.world.Scenarios.DummyScenario;
import com.galimatias.teslaradio.world.Scenarios.IScenarioManager;
import com.galimatias.teslaradio.world.Scenarios.Scenario;
import com.galimatias.teslaradio.world.Scenarios.ScenarioManager;
import com.galimatias.teslaradio.world.Scenarios.SoundCapture;
import com.galimatias.teslaradio.world.effects.ParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.SignalControl;
import com.galimatias.teslaradio.world.effects.DynamicWireParticleEmitterControl;
import com.jme3.app.SimpleApplication;
import com.jme3.cinematic.MotionPath;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.utils.AppLogger;
import java.util.ArrayList;
import java.util.List;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication implements ActionListener
{
    public static void main(String[] args) 
    {
        Main app = new Main();
        AppGetter.setInstance(app);
        app.start();
    }

    private Spatial sceneModel;
    private Node nodeA;
    private Node nodeB;
    private List<Node> nodeList;
    private IScenarioManager scenarioManager;
    boolean dragMouseToMove = true;
    
    private static final String ScenarioB_move_X_pos = "ScenarioB_move_X_pos";
    private static final String ScenarioB_move_X_neg = "ScenarioB_move_X_neg";
    private static final String ScenarioB_move_Y_pos = "ScenarioB_move_Y_pos";
    private static final String ScenarioB_move_Y_neg = "ScenarioB_move_Y_neg";
    private static final String ScenarioB_move_Z_pos = "ScenarioB_move_Z_pos";
    private static final String ScenarioB_move_Z_neg = "ScenarioB_move_Z_neg";
    private static final String ScenarioB_rotate_Y_pos = "ScenarioB_rotate_Y_pos";
    private static final String ScenarioB_rotate_Y_neg = "ScenarioB_rotate_Y_neg";
    private static final String TOGGLE_DRAG_FLYBY_CAMERA = "toggle_cam";
    
    
    private Node destination;
    
    @Override
    public void simpleInitApp() 
    {
        AppLogger.getInstance().setLogLvl(AppLogger.LogLevel.ALL);
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(true);
        
        mouseInput.setCursorVisible(true);
        flyCam.setDragToRotate(dragMouseToMove);
        
        //Initialized a list of nodes to attach to the scenario manager.
        nodeList = new ArrayList<Node>();
        nodeA = new Node();
        nodeB = new Node();
        rootNode.attachChild(nodeA);
        rootNode.attachChild(nodeB);
        float value = 60;
        nodeA.move(-value,0,0);
        nodeB.move(+value,0,0);
        nodeList.add(nodeA);
        nodeList.add(nodeB);
        
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
        
        scenarioManager = new ScenarioManager(ScenarioManager.ApplicationType.DESKTOP, nodeList, cam, null);
        
        flyCam.setMoveSpeed(100f);
        cam.setLocation(new Vector3f(-60,80,80));
        cam.lookAt(rootNode.getWorldTranslation(), Vector3f.UNIT_Y);
        
        //Add a floor
        Geometry floor = new Geometry("Floor", new Box (60,Float.MIN_VALUE,60));
        Material floorMaterial  = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        floorMaterial.setColor("Color", new ColorRGBA(0.75f,0.75f,0.75f, 1f));
        floor.setMaterial(floorMaterial);
        nodeA.attachChild(floor);
        
        Geometry floor2 = new Geometry("Floor", new Box (60,Float.MIN_VALUE,60));
        Material floorMaterial2  = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        floorMaterial2.setColor("Color", ColorRGBA.Yellow);
        floor2.setMaterial(floorMaterial2);
        nodeB.attachChild(floor2);
        
        //soundCapture = new SoundCapture(assetManager, this.getCamera());
        //rootNode.attachChild(soundCapture);
        
        //DummyScenario dummy = new DummyScenario(assetManager, ColorRGBA.Orange);
        //rootNode.attachChild(dummy);
        
        // Attaching the modules to the scene
        //dummy.scale(20);
        
        
        initLights();
        
    }

    @Override
    public void simpleUpdate(float tpf) 
    {
        //TODO: add update code
        scenarioManager.simpleUpdate(tpf);
    }

    @Override
    public void simpleRender(RenderManager rm) 
    {
        //TODO: add render code
    }
    
    private void initLights(){
    
        // You must add a light to make the model visible
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(0.f, 0.f, -1.0f));
        rootNode.addLight(sun);

        // You must add a light to make the model visible
        DirectionalLight back = new DirectionalLight();
        back.setDirection(new Vector3f(0.f, -1.f, 1.0f));
        rootNode.addLight(back);

        DirectionalLight front = new DirectionalLight();
        front.setDirection(new Vector3f(0.f, 1.f, 1.0f));
        rootNode.addLight(front);

        /** A white ambient light source. */
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);
    
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
