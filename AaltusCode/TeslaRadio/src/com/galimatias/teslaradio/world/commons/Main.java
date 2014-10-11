package com.galimatias.teslaradio.world.commons;

import com.galimatias.teslaradio.world.Scenarios.DevFrameworkMainState;
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
public class Main extends SimpleApplication
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

    
    
    @Override
    public void simpleInitApp() 
    {
        AppLogger.getInstance().setLogLvl(AppLogger.LogLevel.ALL);
        AppGetter.setInstance(this);
        
        initLights();
        mainState = new DevFrameworkMainState(this, flyCam);
        this.getStateManager().attach(mainState);
        scenarioManager = new ScenarioManager(this,ScenarioManager.ApplicationType.DESKTOP, mainState.getNodeList(), cam, null);
        this.getStateManager().attach(scenarioManager);
        //this.getStateManager().detach(mainState);
        //this.getStateManager().detach(mainState);
        //this.getStateManager().detach(scenarioManager);
        //this.getStateManager().attach(scenarioManager);
        
        
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
