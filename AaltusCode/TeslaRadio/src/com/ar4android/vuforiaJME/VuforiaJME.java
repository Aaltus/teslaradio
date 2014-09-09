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

import com.galimatias.teslaradio.world.Scenarios.IScenarioManager;
import com.galimatias.teslaradio.world.Scenarios.ScenarioManager;
import com.jme3.app.SimpleApplication;
import com.jme3.input.controls.TouchTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.utils.AppLogger;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class VuforiaJME extends SimpleApplication {

	private static final String TAG = VuforiaJME.class.getName();

    // Enable this value to get multiple trackables
    private static final int DEBUG_NTargets = 1;

    private IScenarioManager scenarioManager;
    public IScenarioManager getScenarioManager() {
        return scenarioManager;
    }

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
	private boolean mNewCameraFrameAvailable = false;
// The virtual world object, it is in fact the scene
//    private World virtualWorld;
//    private Spatial ninja;
//    private Node scotty;

    private float mForegroundCamFOVY = 30;

	// for animation	
	// The controller allows access to the animation sequences of the model
//	private AnimControl mAniControl;
	// the channel is used to run one animation sequence at a time
//	private AnimChannel mAniChannel;
  
	Camera videoBGCam;
	Camera fgCam;

    private Node trackableA = new Node("TrackableA");
    private Node trackableB = new Node("TrackableB");
    private Node trackableA_fixeAngle = new Node("trackableA_fixeAngle");
    private Node trackableB_fixeAngle = new Node("trackableB_fixeAngle");	
	
    private Boolean isNodeVisibleA = new Boolean(false);
    private Boolean isNodeVisibleB = new Boolean(false);

    private List<Node> nodeList = new ArrayList<Node>();

    private Matrix3f rotMatrix;

    /** Native function for initializing the renderer. */
    public native void initTracking(int width, int height);

    /** Native function to update the renderer. */
    public native void updateTracking();

    
	public static void main(String[] args) {

        VuforiaJME app = new VuforiaJME();
		app.start();
	}

    //A Applistener that we will be using for callback
    public AppListener appListener;

    //A way to register to the appListener
    public void setAppListener(AppListener appListener)
    {
        this.appListener = appListener;
    }

	// The default method used to initialize your JME application.
	@Override
	public void simpleInitApp()
    {
        // Where the AppLogger is called for the first time and the log level is set
        AppLogger.getInstance().setLogLvl(AppLogger.LogLevel.DEBUG);

        AppLogger.getInstance().i(TAG, "simpleInitApp");

		// Do not display statistics or frames per second	
		setDisplayStatView(true);
		setDisplayFps(true);

		// We use custom viewports - so the main viewport does not need to contain the rootNode
		viewPort.detachScene(rootNode);
		
		initTracking(settings.getWidth(), settings.getHeight());
		initVideoBackground(settings.getWidth(), settings.getHeight());
		initBackgroundCamera();

        initForegroundCamera(mForegroundCamFOVY);

		initForegroundScene();

        appListener.onFinishSimpleInit();
	}

	// This function creates the geometry, the viewport and the virtual camera
	// needed for rendering the incoming Android camera frames in the scene
	// graph
	public void initVideoBackground(int screenWidth, int screenHeight) {

        AppLogger.getInstance().d(TAG, "* initVideoBackground with width : " + Integer.toString(screenWidth) + " height: " + Integer.toString(screenHeight));

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

	public void initBackgroundCamera() {
		// Create a custom virtual camera with orthographic projection

        int settingsWidth = settings.getWidth();
        int settingsHeight = settings.getHeight();
        AppLogger.getInstance().d(TAG, "* initBackgroundCamera with width : " + Integer.toString(settingsWidth) + " height: " + Integer.toString(settingsHeight));
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

    public void initForegroundScene() {

        AppLogger.getInstance().d(TAG, "initForegroundScene");

        initLights();

        rootNode.attachChild(trackableA);
        rootNode.attachChild(trackableB);
        trackableA.attachChild(trackableA_fixeAngle);
        trackableB.attachChild(trackableB_fixeAngle);

        nodeList.add(trackableA_fixeAngle);
        nodeList.add(trackableB_fixeAngle);
        scenarioManager = new ScenarioManager(nodeList, assetManager, fgCam, appListener, renderManager);

        //TODO: Move in scenario Manager
        //Correction for BUG TR-176
        //The problem was that the 3d modules was in RAM but was not forwarded to the GPU.
        //So the first time that the we were seeing a model, the vidoe was stagerring to load everything.
        renderManager.preloadScene(rootNode);

        inputManager.addMapping("Touch", new TouchTrigger(0)); // trigger 1: left-button click
        inputManager.addListener(scenarioManager, new String[]{"Touch"});

	}




    public void initForegroundCamera(float fovY) {

        int settingsWidth = settings.getWidth();
        int settingsHeight = settings.getHeight();
        AppLogger.getInstance().d(TAG, "initForegroundCamera with width : " + Integer.toString(settings.getWidth()) + " height: " + Integer.toString(settings.getHeight()));
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

    private void initLights(){


        // You must add a light to make the model visible
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(0.f, 0.f, -1.0f));
        rootNode.addLight(sun);

        // You must add a light to make the model visible
        DirectionalLight back = new DirectionalLight();
        back.setDirection(new Vector3f(0.f, -1.f, 1.0f));
        rootNode.addLight(back);

        DirectionalLight front = new DirectionalLight();
        front.setDirection(new Vector3f(0.f, 1.f, 1.0f));
        rootNode.addLight(front);

        /** A white ambient light source. */
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);

    }

	public void setCameraPerspectiveNative(float fovY,float aspectRatio) {
        // Log.d(TAG,"Update Camera Perspective..");

        //Log.d(TAG,"setCameraPerspectiveNative with fovY : " + Float.toString(fovY) + " aspectRatio: " + Float.toString(aspectRatio) );
        fgCam.setFrustumPerspective(fovY,aspectRatio, 1.f, 100000.f);
	}
	
	public void setCameraViewportNative(float viewport_w,float viewport_h,float size_x,float size_y) {
		 //Log.d(TAG,"Update Camera Viewport..");

        AppLogger.getInstance().d(TAG, "setCameraViewportNative with viewport_w : " + Float.toString(viewport_w) + " viewport_h: " + Float.toString(viewport_h));
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
	    float viewportPosition_x =  (((int)(settings.getWidth()  - viewport_w)) / 2);//+0
	    float viewportPosition_y =  (((int)(settings.getHeight() - viewport_h)) / 2);//+0
	    //float viewportSize_x = viewport_w;//2560
	    //float viewportSize_y = viewport_h;//1920

	    //transform in normalized coordinate
	    viewportPosition_x =  viewportPosition_x/viewport_w;
	    viewportPosition_y =  viewportPosition_y/viewport_h;
	    //viewportSize_x = viewportSize_x/viewport_w;
	    //viewportSize_y = viewportSize_y/viewport_h;
	       
		//adjust for viewport start (modify video quad)
		mVideoBGGeom.setLocalTranslation(-0.5f*newWidth+viewportPosition_x,-0.5f*newHeight+viewportPosition_y,0.f);
		//adust for viewport size (modify video quad)
		mVideoBGGeom.setLocalScale(newWidth, newHeight, 1.f);
	}
	

	public void setCameraPoseNative(float cam_x,float cam_y,float cam_z, int id) {
         AppLogger.getInstance().d(TAG, "Update Camera Pose..");

        //         AppLogger.getInstance().d(TAG, "Coordinates : x = " + Float.toString(cam_x) + " y = "
        //                 + Float.toString(cam_y) + " z = " + Float.toString(cam_z));

        // Set the foreground camera position
        fgCam.setLocation(new Vector3f(0.0f, 0.0f, 0.0f));

        // AppLogger.getInstance().d(TAG, "Trackable ID : " + id);

        if (DEBUG_NTargets == 1) {

            if (id == 0)
            {
                trackableA.setLocalTranslation(new Vector3f(-cam_x, -cam_y, cam_z));
            }
            else if (id == 1)
            {
                trackableB.setLocalTranslation(new Vector3f(-cam_x, -cam_y, cam_z));
            }

        }
        else
        {
            rootNode.setLocalTranslation(new Vector3f(-cam_x, -cam_y, cam_z));
        }

	}
	
	public void setCameraOrientationNative(float cam_right_x,float cam_right_y,float cam_right_z,
			float cam_up_x,float cam_up_y,float cam_up_z,float cam_dir_x,float cam_dir_y,float cam_dir_z, int id) {
		 
		//AppLogger.getInstance().d(TAG,"Update Orientation Pose..");

        //        AppLogger.getInstance().d(TAG, "direction : x = " + Float.toString(cam_dir_x) + " y = "
        //                + Float.toString(cam_dir_y) + " z = " + Float.toString(cam_dir_z));

        //left,up,direction
        fgCam.setAxes(
                new Vector3f(1.0f, 0.0f, 0.0f),
                new Vector3f(0.0f, 1.0f, 0.0f),
                new Vector3f(0.0f, 0.0f, 1.0f));

        // Adding the world rotation
        rotMatrix = new Matrix3f( cam_right_x, cam_up_x , -cam_dir_x ,
                                  cam_right_y, cam_up_y, -cam_dir_y,
                                  -cam_right_z , -cam_up_z , cam_dir_z);

        // calculate the angle of rotation of the trackable relative to the AB vector
        Vector3f vx = new Vector3f(cam_right_x, cam_up_x, -cam_dir_x);
        Vector3f vectorAB = new Vector3f(trackableB.getLocalTranslation().subtract(trackableA.getLocalTranslation()));
        double angleX = Math.atan2(vectorAB.normalize().y,vectorAB.normalize().x) - Math.atan2(vx.normalize().y,vx.normalize().x);
        if(angleX < 0)
            angleX = angleX + 2*3.141592654;
        float angleX_f = (float) angleX;  // TODO : This is the angle to return as input to the scenario manager
        Quaternion qAngle = new Quaternion();
        qAngle.fromAngleAxis(-angleX_f,new Vector3f(0,0,1));


        if (DEBUG_NTargets == 1) {

            if (id == 0)
            {
                trackableA.setLocalRotation(rotMatrix);
                trackableA_fixeAngle.setLocalRotation(qAngle);
            }
            else if (id == 1)
            {
                trackableB.setLocalRotation(rotMatrix);
                trackableB_fixeAngle.setLocalRotation(qAngle);
            }

        }
        else
        {
            rootNode.setLocalRotation(rotMatrix);
        }
	}
	public void setTrackableVisibleNative(int id, int isTrackableVisible)
    {
        //TODO: Attack/Detach node depending on visibility status
        if (id == 0 && isTrackableVisible == 0)
        {
            if (isNodeVisibleA)
            {
                getScenarioManager().setIsNodeVisible(!isNodeVisibleA);
                getScenarioManager().updateNodeList(nodeList, 0);
                isNodeVisibleA = false;
            }
            else if (!isNodeVisibleA)
            {
                // Do nothing, we don't see the trackable and Node has to be invisible.
            }
            else
            {
                // Do nothing...
            }
        }
        else if (id == 0 && isTrackableVisible == 1)
        {
            if (isNodeVisibleA)
            {
                // Do nothing, we can see the trackable and the Node has spawned.
            }
            else if (!isNodeVisibleA)
            {
                getScenarioManager().setIsNodeVisible(!isNodeVisibleA);
                getScenarioManager().updateNodeList(nodeList, 0);
                isNodeVisibleA = true;
            }
            else
            {
                // Do nothing...
            }
        }
        else if (id == 1 && isTrackableVisible == 0)
        {
            if (isNodeVisibleB)
            {
                getScenarioManager().setIsNodeVisible(!isNodeVisibleB);
                getScenarioManager().updateNodeList(nodeList, 1);
                isNodeVisibleB = false;
            }
            else if (!isNodeVisibleB)
            {
                // Do nothing, we don't see the trackable and Node has to be invisible.
            }
            else
            {
                // Do nothing...
            }
        }
        else if (id == 1 && isTrackableVisible == 1)
        {
            if (isNodeVisibleB)
            {
                // Do nothing, we can see the trackable and the Node has spawned.
            }
            else if (!isNodeVisibleB)
            {
                getScenarioManager().setIsNodeVisible(!isNodeVisibleB);
                getScenarioManager().updateNodeList(nodeList, 1);
                isNodeVisibleB = true;
            }
            else
            {
                // Do nothing...
            }
        }
    }

    // This method retrieves the preview images from the Android world and puts them into a JME image.
    public void setVideoBGTexture(final Image image) {
        if (!mSceneInitialized) {
            return;
        }
        mCameraImage = image;
        mNewCameraFrameAvailable = true;
    }

    @Override
    public void simpleUpdate(float tpf) {

        updateTracking();

        if (mNewCameraFrameAvailable) {
            mCameraTexture.setImage(mCameraImage);
            mvideoBGMat.setTexture("ColorMap", mCameraTexture);
        }

        // mCubeGeom.rotate(new Quaternion(1.f, 0.f, 0.f, 0.01f));
        mVideoBGGeom.updateLogicalState(tpf);
        mVideoBGGeom.updateGeometricState();


        scenarioManager.simpleUpdate(tpf);


        // Update the world depending on what is in focus
        //virtualWorld.UpdateFocus(fgCam,focusableObjects);
        //virtualWorld.UpdateViewables(rootNode,focusableObjects);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        // TODO: add render code
    }


}
