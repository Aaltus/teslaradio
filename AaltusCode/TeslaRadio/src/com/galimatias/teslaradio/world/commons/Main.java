package com.galimatias.teslaradio.world.commons;

import com.galimatias.teslaradio.world.Scenarios.ScreenState;
import com.galimatias.teslaradio.world.Scenarios.DevFrameworkMainState;
import com.ar4android.vuforiaJME.AppGetter;
import com.galimatias.teslaradio.world.Scenarios.ScenarioManager;
import com.galimatias.teslaradio.world.Scenarios.StateSwitcher;
import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.utils.AppLogger;

public class Main extends SimpleApplication implements StateSwitcher
{
    
    private ScenarioManager scenarioManager;
    private DevFrameworkMainState mainState;
    private DirectionalLight sun;
    private DirectionalLight back;
    private DirectionalLight front;
    private AmbientLight ambient;
    
    public static void main(String[] args) 
    {
        Main app = new Main();
        app.start();
    }
    private ScreenState startScreenState;
    
    @Override
    public void simpleInitApp() 
    {
        AppLogger.getInstance().setLogLvl(AppLogger.LogLevel.ALL);
        AppGetter.setInstance(this);
        
        initLights();
        
        mainState = new DevFrameworkMainState(this, this);
        scenarioManager = new ScenarioManager(this,
                ScenarioManager.ApplicationType.DESKTOP,
                mainState.getNodeList(),
                cam,
                null);
        
        
        inputManager.setCursorVisible(true);
        startScreenState = new ScreenState(this, this);
        this.getStateManager().attach(startScreenState);
        
        
        
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
    
    

    @Override
    public void startGame() {
        
    }

    @Override
    public void startTutorial() {
        
        startScreenState.closeStartMenu();
        
        this.getStateManager().attach(scenarioManager);
        this.getStateManager().attach(mainState);
        
    }

    @Override
    public void startCredits() {
        
    }

    @Override
    public void endGame() {
        this.stop();
    }

    @Override
    public void openStartScreen() {
        
        if(this.getStateManager().hasState(mainState)){
            this.getStateManager().detach(mainState);
        }
        if(this.getStateManager().hasState(scenarioManager)){
           this.getStateManager().detach(scenarioManager);
        }
        startScreenState.openStartMenu();
    }
    
    //Remove screen.
    public void initLights()        
    {
        // You must add a light to make the model visible
        sun = new DirectionalLight();
        sun.setDirection(new Vector3f(0.f, 0.f, -1.0f));

        // You must add a light to make the model visible
        back = new DirectionalLight();
        back.setDirection(new Vector3f(0.f, -1.f, 1.0f));

        front = new DirectionalLight();
        front.setDirection(new Vector3f(0.f, 1.f, 1.0f));

        /** A white ambient light source. */
        ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        
        rootNode.addLight(sun);
        rootNode.addLight(back);
        rootNode.addLight(front);
        rootNode.addLight(ambient);
    }
}
