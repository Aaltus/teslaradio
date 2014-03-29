package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
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
    private World world;
    
    @Override
    public void simpleInitApp() 
    {
        flyCam.setMoveSpeed(100f);
        
        sceneModel = assetManager.loadModel("Scenes/World.j3o");
        rootNode.attachChild(sceneModel);
        
        // Initialisation of the modules
        world = new World(rootNode);
        
        soundCapture = new SoundCapture(assetManager);
        soundCapture.initAllUnmovableObjects();
        soundCapture.initAllMovableObjects();
                
        // Attaching the modules to the scene
        rootNode.attachChild(soundCapture);
        
        // Load the custom keybindings
        initKeys();
    }

    @Override
    public void simpleUpdate(float tpf) 
    {
        //TODO: add update code
        //soundCapture.AnimateCircles();
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
            soundCapture.tambourTouchEffect();
          }
        }
    };
}
