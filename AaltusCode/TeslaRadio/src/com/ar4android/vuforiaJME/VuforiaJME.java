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
import com.utils.AppLogger;

public class VuforiaJME extends SimpleApplication implements AppObservable, StateSwitcher {

	private static final String TAG = VuforiaJME.class.getName();



    private ScreenState startScreenState;

    private ScenarioManager scenarioManager;
    private VuforiaJMEState vuforiaJMEState;
    private DevFrameworkMainState mainState;

    private DirectionalLight sun;
    private DirectionalLight back;
    private DirectionalLight front;
    private AmbientLight ambient;
    private boolean viewPortAttached = true;


    public static void main(String[] args) {

        VuforiaJME app = new VuforiaJME();
		app.start();
	}

    //A Applistener that we will be using for callback
    private AndroidActivityListener androidActivityListener;

    @Override
    public void destroy() {
        super.destroy(); //To change body of generated methods, choose Tools | Templates.
        AppGetter.getInstance().stopThreadPool();
    }
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


        AppLogger.getInstance().i(TAG, "simpleInitApp");

        /*settings.setFrameRate(20);
        setSettings(settings);*/

		// Do not display statistics or frames per second	
		setDisplayStatView(true);
		setDisplayFps(true);


        //androidActivityListener.onFinishSimpleInit();


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

        //Init all the things
        this.rootNode.addControl(new TrackableManager());
        vuforiaJMEState = new VuforiaJMEState(this,this.androidActivityListener.getITrackerUpdater());
        this.androidActivityListener.setICameraUpdater(vuforiaJMEState);
        //this.getStateManager().attach(vuforiaJMEState);

        this.getStateManager().attach(vuforiaJMEState);
        vuforiaJMEState.setEnabled(true);

        stopVuforiaJMEState();




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

    public void onBackButton(){

        openStartScreen();

    }

    @Override
    public void startGame() {

        androidActivityListener.showInformativeMenu();

        startScreenState.closeStartMenu();

        stopDevFramework();

        startScenarioManagerForVuforiaJMEState();

        startVuforiaJMEState();

    }

    @Override
    public void startTutorial() {

        androidActivityListener.showInformativeMenu();

        startScreenState.closeStartMenu();

        stopScenarioManagerForVuforiaJMEState();

        stopVuforiaJMEState();

        startDevFramework();

    }

    @Override
    public void startCredits() {

    }

    @Override
    public void endGame() {
        this.stop();
    }

    @Override
    public void dismissSplashScreen() {
        androidActivityListener.onFinishSimpleInit();
    }

    @Override
    public void openStartScreen() {

        androidActivityListener.hideInformativeMenu();

        stopVuforiaJMEState();

        stopScenarioManagerForVuforiaJMEState();

        stopDevFramework();

        startScreenState.openStartMenu();
    }

    private void startScenarioManagerForVuforiaJMEState() {
        if(this.viewPortAttached){
            viewPort.detachScene(rootNode);
            this.viewPortAttached = false;
        }
        this.getStateManager().detach(scenarioManager);
        scenarioManager = new ScenarioManager(this,
                ScenarioManager.ApplicationType.ANDROID,
                this.rootNode.getControl(TrackableManager.class).getScenarioNodeList(),
                vuforiaJMEState.getCamera(),
                androidActivityListener);
        this.getStateManager().attach(scenarioManager);
    }

    private void stopScenarioManagerForVuforiaJMEState() {
        //viewPort.clearScenes();
        if(!this.viewPortAttached){
            viewPort.attachScene(rootNode);
            this.viewPortAttached = true;
        }

        this.getStateManager().detach(scenarioManager);
        scenarioManager = null;
    }



    private void startDevFramework() {

        stopDevFramework();

        mainState = new DevFrameworkMainState(this,this);
        mainState.setEnabled(true);
        this.getStateManager().attach(mainState);



        stopScenarioManagerForVuforiaJMEState();

        startScenarioManagerForDevFramework();
    }

    private void stopDevFramework() {
        //mainState.setEnabled(false);
        this.getStateManager().detach(mainState);
        mainState = null;
    }

    private void startScenarioManagerForDevFramework() {
        scenarioManager = new ScenarioManager(this,
                ScenarioManager.ApplicationType.ANDROID_DEV_FRAMEWORK,
                mainState.getNodeList(),
                this.getCamera(),
                androidActivityListener);
        this.getStateManager().attach(scenarioManager);
    }



    private void startVuforiaJMEState() {
        androidActivityListener.startTracking();
        this.getStateManager().attach(vuforiaJMEState);
        vuforiaJMEState.setEnabled(true);
    }

    private void stopVuforiaJMEState() {
        androidActivityListener.pauseTracking();
        vuforiaJMEState.setEnabled(false);
        this.getStateManager().detach(vuforiaJMEState);

    }




}
