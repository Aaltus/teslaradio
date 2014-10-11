/* VuforiaJME - VuforiaJME Example
 * 
 * Example Chapter 5
 * accompanying the book
 * "Augmented Reality for Android Application Development", Packt Publishing, 2013.
 * 
 * Copyright � 2013 Jens Grubert, Raphael Grasset / Packt Publishing.
 * 
 * This example is dependent of the Qualcomm Vuforia SDK 
 * The Vuforia SDK is a product of Qualcomm Austria Research Center GmbH
 * 
 * https://developer.vuforia.com
 * 
 * This example was built from the ImageTarget example accompanying the Vuforia SDK
 * https://developer.vuforia.com/resources/sample-apps/image-targets-sample-app
 * 
 */

package com.ar4android.vuforiaJME;

import com.galimatias.teslaradio.world.Scenarios.DevFrameworkMainState;
import com.galimatias.teslaradio.world.Scenarios.ScenarioManager;
import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.utils.AppLogger;

import java.util.List;

public class VuforiaJME extends SimpleApplication implements AppObservable {

	private static final String TAG = VuforiaJME.class.getName();
    private float mForegroundCamFOVY = 30;
    private Camera fgCam;

    private ScenarioManager scenarioManager;
    private VuforiaJMEState vuforiaJMEState;
    private DevFrameworkMainState mainState;

    private DirectionalLight sun;
    private DirectionalLight back;
    private DirectionalLight front;
    private AmbientLight ambient;


    public static void main(String[] args) {

        VuforiaJME app = new VuforiaJME();
		app.start();
	}

    //A Applistener that we will be using for callback
    private AndroidActivityListener androidActivityListener;

    //A way to register to the androidActivityListener
    @Override
    public void setAndroidActivityListener(AndroidActivityListener androidActivityListener)
    {
        this.androidActivityListener = androidActivityListener;
    }

	// The default method used to initialize your JME application.
	@Override
	public void simpleInitApp()
    {

        AppGetter.setInstance(this);
        // Where the AppLogger is called for the first time and the log level is set
        AppLogger.getInstance().setLogLvl(AppLogger.LogLevel.NONE);

        AppLogger.getInstance().i(TAG, "simpleInitApp");

        /*settings.setFrameRate(20);
        setSettings(settings);*/

		// Do not display statistics or frames per second	
		setDisplayStatView(true);
		setDisplayFps(true);


        /*
        Node for Jimbo, old way of initializing vuforiaJME
        // We use custom viewports - so the main viewport does not need to contain the rootNode
		viewPort.detachScene(rootNode);


		initTracking(settings.getWidth(), settings.getHeight());
		initVideoBackground(settings.getWidth(), settings.getHeight());
		initBackgroundCamera();

        initForegroundCamera(mForegroundCamFOVY);

		initForegroundScene();
         */



        initLights();

        //WARNING: IT IS IMPORTANT TO SETUP THE fgCam After the Background scene.
        //Otherwise the camera background will be OVER the 3d models.
        initBackgroundScene(null);                    //Init the background tracking
        initForegroundCamera(mForegroundCamFOVY);
        vuforiaJMEState.setCamera(fgCam);
        initForegroundScene(this.rootNode.getControl(TrackableManager.class).getScenarioNodeList(), ScenarioManager.ApplicationType.ANDROID, this.fgCam);

        //To test only our 3d model
        /*
        initBackgroundSceneDemo();
		initForegroundScene(mainState.getNodeList(), ScenarioManager.ApplicationType.ANDROID_DEV_FRAMEWORK, this.getCamera());                    // replace in the state
        if(androidActivityListener != null){
            androidActivityListener.pauseTracking();
            //androidActivityListener.startTracking();
        }*/


        androidActivityListener.onFinishSimpleInit();
	}




    public void initForegroundScene(List<Node> nodeList, ScenarioManager.ApplicationType appType, Camera cam) {

        AppLogger.getInstance().d(TAG, "initForegroundScene");

        scenarioManager = new ScenarioManager(this,
                appType,
                nodeList,
                cam,
                androidActivityListener);
        this.getStateManager().attach(scenarioManager);

	}

    public void initBackgroundScene(Camera camera) {

        // We use custom viewports - so the main viewport does not need to contain the rootNode
        viewPort.detachScene(rootNode);
        this.rootNode.addControl(new TrackableManager());

        AppLogger.getInstance().d(TAG, "initBackgroundScene");

        vuforiaJMEState = new VuforiaJMEState(this,
                camera);
        this.getStateManager().attach(vuforiaJMEState);



    }

    public void initBackgroundSceneDemo() {

        AppLogger.getInstance().d(TAG, "initBackgroundScene");

        mainState = new DevFrameworkMainState(this, flyCam);
        this.getStateManager().attach(mainState);


    }

    public void initForegroundCamera(float fovY) {

        int settingsWidth = settings.getWidth();
        int settingsHeight = settings.getHeight();
        AppLogger.getInstance().d(TAG, "initForegroundCamera with width : " + Integer.toString(settings.getWidth()) + " height: " + Integer.toString(settings.getHeight()));
		fgCam = new Camera(settingsWidth, settingsHeight);
		
		fgCam.setViewPort(0, 1.0f, 0.f, 1.0f);
		fgCam.setLocation(new Vector3f(0f, 0f, 0f));
        fgCam.setAxes(
                new Vector3f(1.0f, 0.0f, 0.0f),
                new Vector3f(0.0f, 1.0f, 0.0f),
                new Vector3f(0.0f, 0.0f, 1.0f));
		fgCam.setFrustumPerspective(fovY, settingsWidth / settingsHeight, 1000, 10000);

		ViewPort fgVP = renderManager.createMainView("ForegroundView", fgCam);
		fgVP.attachScene(rootNode);
		//color,depth,stencil
		fgVP.setClearFlags(false, true, false);
		fgVP.setBackgroundColor(new ColorRGBA(0, 0, 0, 1));
//		fgVP.setBackgroundColor(new ColorRGBA(0,0,0,0));
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

    @Override
    public void simpleUpdate(float tpf) {

    }

    @Override
    public void simpleRender(RenderManager rm) {
        // TODO: add render code
    }

}
