package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
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
    
    @Override
    public void simpleInitApp() 
    {
        
        flyCam.setMoveSpeed(200f);
        
        sceneModel = assetManager.loadModel("Scenes/World.j3o");
        rootNode.attachChild(sceneModel);
        
        // Initialisation of the modules
        World world = new World(rootNode);
        
        SoundCapture soundCapture = new SoundCapture(assetManager);
        soundCapture.initAll();
        
        // Attaching the modules to the scene
        rootNode.attachChild(soundCapture);
    }

    @Override
    public void simpleUpdate(float tpf) 
    {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) 
    {
        //TODO: add render code
    }
}
