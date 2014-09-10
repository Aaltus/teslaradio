package com.galimatias.teslaradio.world.commons;

import com.galimatias.teslaradio.world.Scenarios.DummyScenario;
import com.galimatias.teslaradio.world.Scenarios.IScenarioManager;
import com.galimatias.teslaradio.world.Scenarios.ScenarioManager;
import com.galimatias.teslaradio.world.Scenarios.SoundCapture;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
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
public class Main extends SimpleApplication 
{
    private static final String NEXT_SCENARIO = "NextScenario";
    private static final String TEXT = "Text";
    public static final String GUITAR = "Guitar";
    public static final String DRUM = "Drum";
    public static final String PREVIOUS_SCENARIO = "PreviousScenario";
    public static void main(String[] args) 
    {
        Main app = new Main();
        app.start();
    }

    private Spatial sceneModel;
    private List<Node> nodeList;
    private IScenarioManager scenarioManager;
    
    
    @Override
    public void simpleInitApp() 
    {
        AppLogger.getInstance().setLogLvl(AppLogger.LogLevel.ALL);
        
        //Initialized a list of nodes to attach to the scenario manager.
        nodeList = new ArrayList<Node>();
        Node nodeA = new Node();
        Node nodeB = new Node();
        rootNode.attachChild(nodeA);
        rootNode.attachChild(nodeB);
        float value = 60;
        nodeA.move(value,0,0);
        nodeB.move(-value,0,0);
        nodeList.add(nodeA);
        nodeList.add(nodeB);
        
        scenarioManager = new ScenarioManager(ScenarioManager.ApplicationType.DESKTOP, nodeList, assetManager, cam, null, renderManager);
        
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
        
        // Load the custom keybindings
        initKeys();
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
    
    /** Custom Keybinding: Map named actions to inputs. */
    private void initKeys() 
    {
        // You can map one or several inputs to one named action
        inputManager.addMapping(DRUM, new KeyTrigger(KeyInput.KEY_T));
        inputManager.addMapping(GUITAR, new KeyTrigger(KeyInput.KEY_G));
        inputManager.addMapping(TEXT, new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping(NEXT_SCENARIO, new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping(PREVIOUS_SCENARIO, new KeyTrigger(KeyInput.KEY_O));
        
        // Add the names to the action listener.
        inputManager.addListener(actionListener, DRUM);
        inputManager.addListener(actionListener, GUITAR);
        inputManager.addListener(actionListener, TEXT);
        inputManager.addListener(actionListener, NEXT_SCENARIO);
        inputManager.addListener(actionListener, PREVIOUS_SCENARIO);
  }
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) 
        {
          if (name.equals(GUITAR) && !keyPressed) {
              //soundCapture.drumTouchEffect();
              //soundCapture.guitarTouchEffect();
          }
          else if (name.equals(DRUM) && !keyPressed) {
              //soundCapture.drumTouchEffect();
              //soundCapture.drumTouchEffect();
          }
          else if (name.equals(NEXT_SCENARIO) && !keyPressed) {
              //soundCapture.drumTouchEffect();
              //soundCapture.drumTouchEffect();
              scenarioManager.setNextScenario();
          }
          else if (name.equals(PREVIOUS_SCENARIO) && !keyPressed) {
              //soundCapture.drumTouchEffect();
              //soundCapture.drumTouchEffect();
              scenarioManager.setPreviousScenario();
          }
        }
    };
    
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
    
}
