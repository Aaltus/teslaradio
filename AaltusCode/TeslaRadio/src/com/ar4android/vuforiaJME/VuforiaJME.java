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
import com.galimatias.teslaradio.world.Scenarios.ScreenState;
import com.galimatias.teslaradio.world.Scenarios.StateSwitcher;
import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.utils.AppLogger;

public class VuforiaJME extends SimpleApplication implements AppObservable, StateSwitcher {

	private static final String TAG = VuforiaJME.class.getName();
    private float mForegroundCamFOVY = 30;
    private Camera fgCam;

    private ScreenState startScreenState;

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


        //To uncomments
        startScreenState = new ScreenState(this, this);
        this.getStateManager().attach(startScreenState);
        this.getFlyByCamera().setDragToRotate(true);

        //openStartScreen();



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

        // We use custom viewports - so the main viewport does not need to contain the rootNode

        this.rootNode.addControl(new TrackableManager());
        vuforiaJMEState = new VuforiaJMEState(this,null);
        //this.getStateManager().attach(vuforiaJMEState);
        initForegroundCamera(mForegroundCamFOVY);
        vuforiaJMEState.setCamera(fgCam);
        this.getStateManager().attach(vuforiaJMEState);
        vuforiaJMEState.setEnabled(true);
        //this.getStateManager().attach(vuforiaJMEState);
        /*scenarioManager = new ScenarioManager(this,
                ScenarioManager.ApplicationType.ANDROID,
                null,
                null,
                androidActivityListener);
         */

        vuforiaJMEState.setEnabled(false);
        this.getStateManager().detach(vuforiaJMEState);
        androidActivityListener.pauseTracking();
        inputManager.setSimulateMouse(false); // must be false in order to the start screen to work.



        //To test only our 3d model
        /*
        initBackgroundSceneDemo();
		initForegroundScene(mainState.getNodeList(), ScenarioManager.ApplicationType.ANDROID_DEV_FRAMEWORK, this.getCamera());                    // replace in the state
        if(androidActivityListener != null){
            androidActivityListener.pauseTracking();
            //androidActivityListener.startTracking();
        }
        */


        androidActivityListener.onFinishSimpleInit();
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
    public void startGame() {


        startScreenState.closeStartMenu();

        viewPort.detachScene(rootNode);

        this.getStateManager().detach(mainState);
        mainState = null;

        this.getStateManager().detach(scenarioManager);
        scenarioManager = new ScenarioManager(this,
                ScenarioManager.ApplicationType.ANDROID,
                this.rootNode.getControl(TrackableManager.class).getScenarioNodeList(),
                fgCam,
                androidActivityListener);
        this.getStateManager().attach(scenarioManager);

        androidActivityListener.startTracking();
        this.getStateManager().attach(vuforiaJMEState);
        vuforiaJMEState.setEnabled(true);

    }

    @Override
    public void startTutorial() {

        viewPort.attachScene(rootNode);

        startScreenState.closeStartMenu();
        inputManager.setSimulateMouse(true);


        androidActivityListener.pauseTracking();
        this.getStateManager().detach(vuforiaJMEState);
        vuforiaJMEState.setEnabled(false);

        mainState = new DevFrameworkMainState(this,this);
        mainState.setEnabled(true);
        this.getStateManager().attach(mainState);

        this.getStateManager().detach(scenarioManager);
        scenarioManager = new ScenarioManager(this,
                ScenarioManager.ApplicationType.ANDROID_DEV_FRAMEWORK,
                mainState.getNodeList(),
                this.getCamera(),
                androidActivityListener);
        this.getStateManager().attach(scenarioManager);
/*
        scenarioManager.setApplicationType(ScenarioManager.ApplicationType.ANDROID_DEV_FRAMEWORK);
        scenarioManager.setNodeList(mainState.getNodeList());
        scenarioManager.setCamera(this.getCamera());
        this.getStateManager().attach(scenarioManager);
*/


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

        //this.getFlyByCamera().setDragToRotate(true);
        this.getFlyByCamera().setDragToRotate(true);
        if(androidActivityListener != null){
            androidActivityListener.pauseTracking();
            //androidActivityListener.startTracking();
        }
        if(this.getStateManager().hasState(vuforiaJMEState)){
            this.getStateManager().detach(vuforiaJMEState);
        }
        if(this.getStateManager().hasState(scenarioManager)){
            this.getStateManager().detach(scenarioManager);
        }
        if(this.getStateManager().hasState(mainState)){
            this.getStateManager().detach(mainState);
        }
        startScreenState.openStartMenu();
    }
}
