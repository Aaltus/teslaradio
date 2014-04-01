package com.galimatias.teslaradio.world.commons;

import com.galimatias.teslaradio.world.Scenarios.SoundCapture;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication 
{
    public static void main(String[] args) 
    {
        Main app = new Main();
        app.start();
    }

    private Spatial sceneModel;
    private SoundCapture soundCapture;
    
    
    @Override
    public void simpleInitApp() 
    {
        flyCam.setMoveSpeed(100f);
        
        sceneModel = assetManager.loadModel("Scenes/World.j3o");
        rootNode.attachChild(sceneModel);
        
        soundCapture = new SoundCapture(assetManager);
        soundCapture.initAllMovableObjects();
        soundCapture.initTrajectories(100);
                
        // Attaching the modules to the scene
        rootNode.attachChild(soundCapture);
        
        initLights();
        
        
        // Load the custom keybindings
        initKeys();
    }

    @Override
    public void simpleUpdate(float tpf) 
    {
        //TODO: add update code
        //soundCapture.AnimateCircles();
        soundCapture.simpleUpdate(tpf);
        
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
        inputManager.addMapping("Animate", new KeyTrigger(KeyInput.KEY_SPACE));
        
        // Add the names to the action listener.
        inputManager.addListener(actionListener,"Animate");
  }
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) 
        {
          if (name.equals("Animate") && !keyPressed) {
            soundCapture.drumTouchEffect();
          }
        }
    };
    
    private void initLights(){
    
    
        // You must add a light to make the model visible
        DirectionalLight back = new DirectionalLight();
        back.setDirection(new Vector3f(0.f,-1.f,1.0f));
        rootNode.addLight(back);

        DirectionalLight front = new DirectionalLight();
        front.setDirection(new Vector3f(0.f,1.f,1.0f));
        rootNode.addLight(front);

        /** A white ambient light source. */
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);
    
    }
    
}
