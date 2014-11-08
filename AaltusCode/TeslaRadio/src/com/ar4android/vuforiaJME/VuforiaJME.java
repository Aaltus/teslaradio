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

import com.galimatias.teslaradio.world.Scenarios.*;
import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.utils.AppLogger;

public class VuforiaJME extends SimpleApplication implements AppObservable, StateSwitcher, StartScreenController {

	private static final String TAG = VuforiaJME.class.getName();



    private IStartScreen startScreenState;

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
    private AndroidActivityController androidActivityController;

    @Override
    public void destroy() {
        super.destroy(); //To change body of generated methods, choose Tools | Templates.
        AppGetter.getInstance().stopThreadPool();
    }
    //A way to register to the androidActivityController
    @Override
    public void setAndroidActivityController(AndroidActivityController androidActivityController)
    {
        this.androidActivityController = androidActivityController;
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


        androidActivityController.dismissAndroidSplashScreen();


        //Code to unable nifty gui loading bar and start screen.
        //startScreenState = new ScreenState(this, this);
        //this.getStateManager().attach(startScreenState);
        //this.getFlyByCamera().setDragToRotate(true);
        this.androidActivityController.openProgressScreen("Loading resources...");
        startLoading();
        startScreenState = androidActivityController;
        startScreenState.openStartMenu();
        //this.openStartScreen();

        //this.androidActivityController.openStartScreen();


        initLights();

        //WARNING: IT IS IMPORTANT TO SETUP THE fgCam After the Background scene.
        //Otherwise the camera background will be OVER the 3d models.

        // We use custom viewports - so the main viewport does not need to contain the rootNode



        //Init all the things
        TrackableManager trackableManager = new TrackableManager();
        trackableManager.setiTrackableAlertToast(this.androidActivityController);
        this.rootNode.addControl(trackableManager);
        vuforiaJMEState = new VuforiaJMEState(this,this.androidActivityController.getITrackerUpdater());
        this.androidActivityController.setICameraUpdater(vuforiaJMEState);
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

        if(!startScreenState.isStartMenuShown()){
            openStartScreen();
        }
        else{
            this.androidActivityController.quitAndroidActivity();
        }
        /*
        String currentScreenName = stateManager.getState(ScreenState.class).getCurrentScreenShownName();
        if(currentScreenName.equals(ScreenState.NULL_SCREEN_ID)) {
            openStartScreen();
        }
        if(currentScreenName.equals(ScreenState.START_SCREEN_ID)) {
            this.androidActivityController.quitAndroidActivity();
        }
        */

    }

    @Override
    public void startGame() {

        androidActivityController.showInformativeMenu();

        startScreenState.closeStartMenu();

        stopDevFramework();

        startScenarioManagerForVuforiaJMEState();

        startVuforiaJMEState();

    }

    @Override
    public void startTutorial() {

        androidActivityController.showInformativeMenu();

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
        this.androidActivityController.quitAndroidActivity();
    }

    @Override
    public void dismissSplashScreen() {
        androidActivityController.dismissAndroidSplashScreen();
    }

    @Override
    public void openStartScreen() {

        androidActivityController.hideInformativeMenu();

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
                androidActivityController);
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
                androidActivityController);
        this.getStateManager().attach(scenarioManager);
    }



    private void startVuforiaJMEState() {
        androidActivityController.startTracking();
        this.getStateManager().attach(vuforiaJMEState);
        vuforiaJMEState.setEnabled(true);
    }

    private void stopVuforiaJMEState() {
        androidActivityController.pauseTracking();
        vuforiaJMEState.setEnabled(false);
        this.getStateManager().detach(vuforiaJMEState);

    }


    @Override
    public void onStartButtonClick() {
        this.startGame();
    }

    @Override
    public void onTutorialButtonClick() {
        this.startTutorial();
    }

    @Override
    public void onCreditsButtonClick() {
        this.startCredits();
    }

    @Override
    public void onEndGameClick() {
        this.endGame();
    }

    private void startLoading() {

        //Element element = nifty.getScreen(LOADIND_SCREEN_ID).findElementByName("loadingtext");
        //textRenderer = element.getRenderer(TextRenderer.class);

        ScenarioCommon scenarioCommon = new ScenarioCommon();

        androidActivityController.setProgressBar(0, "Loading " + "Sound Emission" + "...");
        SoundEmission soundEmission = new SoundEmission(scenarioCommon, null, null);
        renderManager.preloadScene(soundEmission);

        androidActivityController.setProgressBar(10, "Loading " + "Sound Capture" + "...");
        Scenario soundCapture = new SoundCapture(scenarioCommon, null, null);
        renderManager.preloadScene(soundCapture);

        androidActivityController.setProgressBar(20, "Loading " + "Amplification" + "...");
        Amplification amplification = new Amplification(scenarioCommon, null, null);
        renderManager.preloadScene(amplification);

        androidActivityController.setProgressBar(30, "Loading " + "Modulation" + "...");
        Modulation modulation = new Modulation(scenarioCommon, null, null);
        renderManager.preloadScene(modulation);

        androidActivityController.setProgressBar(40, "Loading " + "Reception" + "...");
        Reception reception = new Reception(scenarioCommon, null, null);
        renderManager.preloadScene(reception);

        androidActivityController.setProgressBar(50, "Loading " + "Demodulation" + "...");
        Demodulation demodulation = new Demodulation(scenarioCommon, null, null);
        renderManager.preloadScene(demodulation);

        androidActivityController.setProgressBar(60, "Loading " + "Filter" + "...");
        Filter filter = new Filter(scenarioCommon, null, null);
        renderManager.preloadScene(filter);

        androidActivityController.setProgressBar(70, "Loading " + "Playback" + "...");
        Playback playback = new Playback(scenarioCommon, null, null);
        renderManager.preloadScene(playback);

        androidActivityController.setProgressBar(100, "Done loading");

        /*
        try {
            this.enqueue(new Callable<Object>() {
                @Override
                public Object call() throws Exception {



                    return null;
                }
            }).get();
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } catch (ExecutionException e) {
            //e.printStackTrace();
        }
        */

        androidActivityController.closeProgressScreen();
    }

}
