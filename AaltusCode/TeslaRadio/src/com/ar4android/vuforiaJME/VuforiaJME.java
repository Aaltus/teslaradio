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

import android.util.Log;
import com.galimatias.teslaradio.subject.ScenarioEnum;
import com.galimatias.teslaradio.world.Scenarios.SoundCapture;
import com.jme3.app.SimpleApplication;
import com.jme3.input.controls.TouchListener;
import com.jme3.input.controls.TouchTrigger;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;

import java.util.concurrent.Callable;


/**
 * This is the scenario manager of all the jme application
 *
 *It's managing the different scenarios, init the different camera and the
 *background camera texture of the main view, and it's the main entry point
 * of the jmonkey side of the app.
 */
public class VuforiaJME extends SimpleApplication  implements TouchListener{

	private static final String TAG = "VuforiaJME";
	// The geometry which will represent the video background
	private Geometry mVideoBGGeom;
	// The material which will be applied to the video background geometry.
	private Material mvideoBGMat;
	// The texture displaying the Android camera preview frames.
	private Texture2D mCameraTexture;
	// the JME image which serves as intermediate storage place for the Android
	// camera frame before the pixels get uploaded into the texture.
	private Image mCameraImage;
	// A flag indicating if the scene has been already initialized.
	private boolean mSceneInitialized = false;
	// A flag indicating if the JME Image has been already initialized.
	private boolean mVideoImageInitialized = false;
	// A flag indicating if a new Android camera image is available.
	boolean mNewCameraFrameAvailable = false;



// The virtual world object, it is in fact the scene
    private SoundCapture soundCapture;

    private float mForegroundCamFOVY = 30;

    //A camera for the background camera texture and another for the 3d foreground objects
	Camera videoBGCam;
	Camera fgCam;

    /** Native function for initializing the renderer. */
    public native void initTracking(int width, int height);

    /** Native function to update the renderer. */
    public native void updateTracking();

    
	public static void main(String[] args) {
		VuforiaJME app = new VuforiaJME();

		app.start();
	}

    //A Applistener that we will be using for callback to the Android activity
    public AppListener appListener;


    //See https://github.com/latestpost/JMonkey3-Android-Examples/blob/master/src/jmeproject/innovationtech/co/uk/Game7.java
    //Touch event on the camera background texture. The event is pass down to the next scenario.
    @Override
    public void onTouch(String name, TouchEvent touchEvent, float v)
    {

        soundCapture.onScenarioTouch(name, touchEvent, v);
    }

    interface AppListener
    {
        //Callaback for showing a informative menu with the provided menu
        public void showInformativeMenuCallback(ScenarioEnum scenarioEnum);
    }

    //A way to register to the appListener
    public void setAppListener(AppListener appListener)
    {
        this.appListener = appListener;
    }

	// The default method used to initialize your JME application.
	@Override
	public void simpleInitApp() {
		Log.i(TAG, "simpleInitApp");


		// Display statistics or frames per second. To be removed in release
		setDisplayStatView(true);
		setDisplayFps(true);
		
		// We use custom viewports - so the main viewport does not need to contain the rootNode
		viewPort.detachScene(rootNode);

        //Init everything
		initTracking(settings.getWidth(), settings.getHeight());
		initVideoBackground(settings.getWidth(), settings.getHeight());
		initBackgroundCamera();
        initForegroundCamera(mForegroundCamFOVY);
		initForegroundScene();

        //Add touchsceen event to the application
        inputManager.addMapping("Touch", new TouchTrigger(0));
        inputManager.addListener(this, new String[]{"Touch"});

	}

	// This function creates the geometry, the viewport and the virtual camera
	// needed for rendering the incoming Android camera frames in the scene
	// graph
	public void initVideoBackground(int screenWidth, int screenHeight) {

        Log.d(TAG,"* initVideoBackground with width : " + Integer.toString(screenWidth) + " height: " + Integer.toString(screenHeight) );

		// Create a Quad shape.
		Quad videoBGQuad = new Quad(1, 1, true);
		// Create a Geometry with the Quad shape
		mVideoBGGeom = new Geometry("quad", videoBGQuad);
		float newWidth = 1.f * screenWidth / screenHeight;
		// Center the Geometry in the middle of the screen.
		mVideoBGGeom.setLocalTranslation(-0.5f * newWidth, -0.5f, 0.f);//
		// Scale (stretch) the width of the Geometry to cover the whole screen
		// width.
		mVideoBGGeom.setLocalScale(1.f * newWidth, 1.f, 1);
		// Apply a unshaded material which we will use for texturing.
		mvideoBGMat = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		mVideoBGGeom.setMaterial(mvideoBGMat);
		
		// Create a new texture which will hold the Android camera preview frame
		// pixels.
		mCameraTexture = new Texture2D();
	
		mSceneInitialized = true;	
	}

    /**
     * Create a background camera to see the camera texture at the back of the scene
     */
	public void initBackgroundCamera() {

		// Create a custom virtual camera with orthographic projection
        int settingsWidth = settings.getWidth();
        int settingsHeight = settings.getHeight();
        Log.d(TAG, "* initBackgroundCamera with width : " + Integer.toString(settingsWidth) + " height: " + Integer.toString(settingsHeight));
		videoBGCam = new Camera(settingsWidth, settingsHeight);
		videoBGCam.setViewPort(0.0f, 1.0f, 0.f, 1.0f);
		videoBGCam.setLocation(new Vector3f(0f, 0f, 1.f));
		videoBGCam.setAxes(new Vector3f(-1f, 0f, 0f), new Vector3f(0f, 1f, 0f), new Vector3f(0f, 0f, -1f));
		videoBGCam.setParallelProjection(true);
		
		// Also create a custom viewport.
		ViewPort videoBGVP = renderManager.createMainView("VideoBGView",videoBGCam);
		// Attach the geometry representing the video background to the
		// viewport.
		videoBGVP.attachScene(mVideoBGGeom);
		
		//videoBGVP.setClearFlags(true, false, false);
		//videoBGVP.setBackgroundColor(new ColorRGBA(1,0,0,1));

	}

    /**
     * Init the foreground according to our need. e.g. the scenarios and 3d objects
     */
    public void initForegroundScene() {

        Log.d(TAG,"initForegroundScene" );

        initLights();

        //Init SoundCapture scenario and scale it to our need
        soundCapture = new SoundCapture(assetManager, fgCam);
        soundCapture.scale(20.0f);
        soundCapture.setName("SoundCapture");
        Quaternion rot = new Quaternion();
        rot.fromAngleAxis(3.14f / 2, new Vector3f(1.0f, 0.0f, 0.0f));
        soundCapture.rotate(rot);
        rootNode.attachChild(soundCapture);

	}

    /**
     * Init the camera that will be looking at the 3d objects
     * @param fovY : Field of view of the camera
     */
    public void initForegroundCamera(float fovY) {

        int settingsWidth = settings.getWidth();
        int settingsHeight = settings.getHeight();
        Log.d(TAG,"initForegroundCamera with width : " + Integer.toString(settings.getWidth()) + " height: " + Integer.toString(settings.getHeight()) );
		fgCam = new Camera(settingsWidth, settingsHeight);
		
		fgCam.setViewPort(0, 1.0f, 0.f, 1.0f);
		fgCam.setLocation(new Vector3f(0f, 0f, 0f));
		fgCam.setAxes(new Vector3f(-1f, 0f, 0f), new Vector3f(0f, 1f, 0f), new Vector3f(0f, 0f, -1f));
		fgCam.setFrustumPerspective(fovY, settingsWidth / settingsHeight, 1000, 10000);

		ViewPort fgVP = renderManager.createMainView("ForegroundView", fgCam);
		fgVP.attachScene(rootNode);
		//color,depth,stencil
		fgVP.setClearFlags(false, true, false);
		fgVP.setBackgroundColor(new ColorRGBA(0, 0, 0, 1));
//		fgVP.setBackgroundColor(new ColorRGBA(0,0,0,0));
	}

    /**
     * Add lights to scene to be able to see it.
     */
    private void initLights()
    {


        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(0.f, 0.f, -1.0f));
        rootNode.addLight(sun);

        DirectionalLight back = new DirectionalLight();
        back.setDirection(new Vector3f(0.f, -1.f, 1.0f));
        rootNode.addLight(back);

        DirectionalLight front = new DirectionalLight();
        front.setDirection(new Vector3f(0.f, 1.f, 1.0f));
        rootNode.addLight(front);

        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);

    }

    //TODO Add some shit here...
    /**
     * Called by native code in Vuforia...
     *
     * @param fovY
     * @param aspectRatio
     */
	public void setCameraPerspectiveNative(float fovY,float aspectRatio) {
        // Log.d(TAG,"Update Camera Perspective..");

        //Log.d(TAG,"setCameraPerspectiveNative with fovY : " + Float.toString(fovY) + " aspectRatio: " + Float.toString(aspectRatio) );
        fgCam.setFrustumPerspective(fovY,aspectRatio, 1.f, 100000.f);
	}

    /**
     * Called by native code in Vuforia...
     *
     * @param viewport_w
     * @param viewport_h
     * @param size_x
     * @param size_y
     */
	public void setCameraViewportNative(float viewport_w,float viewport_h,float size_x,float size_y) {
		 //Log.d(TAG,"Update Camera Viewport..");

        Log.d(TAG,"setCameraViewportNative with viewport_w : " + Float.toString(viewport_w) + " viewport_h: " + Float.toString(viewport_h   ));
		float newWidth = 1.f;
		float newHeight = 1.f;
		
		if (viewport_h != settings.getHeight())
		{
			newWidth = viewport_w/viewport_h;
			newHeight = 1.0f;
			videoBGCam.resize((int)viewport_w,(int)viewport_h,true);
			videoBGCam.setParallelProjection(true);
		}

		//exercise: find the similar transformation 
		//when viewport_w != settings.getWidth
		
		//Adjusting viewport: from BackgroundTextureAccess example in Qualcomm Vuforia
	    float viewportPosition_x =  (((int)(settings.getWidth()  - viewport_w)) / (int) 2);//+0
	    float viewportPosition_y =  (((int)(settings.getHeight() - viewport_h)) / (int) 2);//+0
	    float viewportSize_x = viewport_w;//2560
	    float viewportSize_y = viewport_h;//1920

	    //transform in normalized coordinate
	    viewportPosition_x =  (float)viewportPosition_x/(float)viewport_w;
	    viewportPosition_y =  (float)viewportPosition_y/(float)viewport_h;
	    viewportSize_x = viewportSize_x/viewport_w;
	    viewportSize_y = viewportSize_y/viewport_h;
	       
		//adjust for viewport start (modify video quad)
		mVideoBGGeom.setLocalTranslation(-0.5f*newWidth+viewportPosition_x,-0.5f*newHeight+viewportPosition_y,0.f);
		//adust for viewport size (modify video quad)
		mVideoBGGeom.setLocalScale(newWidth, newHeight, 1.f);
	}

    /**
     * Called by native code in Vuforia to set the new camera position based on the
     * trackable position
     *
     * @param cam_x
     * @param cam_y
     * @param cam_z
     */
	public void setCameraPoseNative(float cam_x,float cam_y,float cam_z) {
		 //Log.d(TAG,"Update Camera Pose..");

//         Log.d(TAG, "Coordinates : x = " + Float.toString(cam_x) + " y = "
//                 + Float.toString(cam_y) + " z = " + Float.toString(cam_z));

         // Set the new foreground camera position
		 fgCam.setLocation(new Vector3f(cam_x, cam_y, cam_z));

	}

    /**
     * Called by native code in Vuforia to update Camera orientation
     * @param cam_right_x
     * @param cam_right_y
     * @param cam_right_z
     * @param cam_up_x
     * @param cam_up_y
     * @param cam_up_z
     * @param cam_dir_x
     * @param cam_dir_y
     * @param cam_dir_z
     */
	public void setCameraOrientationNative(float cam_right_x,float cam_right_y,float cam_right_z,
			float cam_up_x,float cam_up_y,float cam_up_z,float cam_dir_x,float cam_dir_y,float cam_dir_z)
    {
		 
		//Log.d(TAG,"Update Orientation Pose..");

//        Log.d(TAG, "direction : x = " + Float.toString(cam_dir_x) + " y = "
//                + Float.toString(cam_dir_y) + " z = " + Float.toString(cam_dir_z));

   		 //left,up,direction
		 fgCam.setAxes(
                 new Vector3f(-cam_right_x, -cam_right_y, -cam_right_z),
                 new Vector3f(-cam_up_x, -cam_up_y, -cam_up_z),
                 new Vector3f(cam_dir_x, cam_dir_y, cam_dir_z));
	}


    /**
     * This method retrieves the preview images from the Android world and puts them into a JME image.
     * @param image
     */
    public void setVideoBGTexture(final Image image)
    {
        if (!mSceneInitialized) {
            return;
        }
        mCameraImage = image;
        mNewCameraFrameAvailable = true;
    }

    /**
     * This function is called periodically at every frame.
     * We update all the rendered image e.g. the scenario and the tracking
     * @param tpf
     */
    @Override
    public void simpleUpdate(float tpf) {

        updateTracking();

        if (mNewCameraFrameAvailable) {
            mCameraTexture.setImage(mCameraImage);
            mvideoBGMat.setTexture("ColorMap", mCameraTexture);
        }

//			mCubeGeom.rotate(new Quaternion(1.f, 0.f, 0.f, 0.01f));
        mVideoBGGeom.updateLogicalState(tpf);
        mVideoBGGeom.updateGeometricState();


        if (soundCapture.simpleUpdate(tpf))
        {
            appListener.showInformativeMenuCallback(ScenarioEnum.SOUNDCAPTURE);
        }

    }

    @Override
    public void simpleRender(RenderManager rm) {
        // TODO: add render code
    }



    public class onAudioEvent implements Callable
    {
        @Override
        public Object call() throws Exception {

            soundCapture.onAudioEvent();
            return null;
        }
    }
}
