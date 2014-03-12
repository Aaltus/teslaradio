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
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;

public class VuforiaJME extends SimpleApplication implements AnimEventListener  {

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

    private float mForegroundCamFOVY = 30;

	// for animation	
	// The controller allows access to the animation sequences of the model
	private AnimControl mAniControl;
	// the channel is used to run one animation sequence at a time
	private AnimChannel mAniChannel;
  
	Camera videoBGCam;
	Camera fgCam;
    Node shootables;
    Geometry geom;
	
    /** Native function for initializing the renderer. */
    public native void initTracking(int width, int height);

    /** Native function to update the renderer. */
    public native void updateTracking();

    
	public static void main(String[] args) {
		VuforiaJME app = new VuforiaJME();
		app.start();
	}

	// The default method used to initialize your JME application.
	@Override
	public void simpleInitApp() {
		Log.e(TAG, "simpleInitApp");
		
		// Do not display statistics or frames per second	
		setDisplayStatView(false);
		setDisplayFps(false);
		
		//Logger.getLogger("").setLevel(Level.SEVERE);
		 
		
		// We use custom viewports - so the main viewport does not need to contain the rootNode
		viewPort.detachScene(rootNode);
		
		initTracking(settings.getWidth(), settings.getHeight());
		initVideoBackground(settings.getWidth(), settings.getHeight());
		initBackgroundCamera();
		
		initForegroundScene();	
		
		initForegroundCamera(mForegroundCamFOVY);

        inputManager.addMapping("Shoot", // Declare...
                new MouseButtonTrigger(0)); // trigger 1: left-button click
        inputManager.addListener(actionListener, "Shoot");

	}

    /** Defining the "Shoot" action: Determine what was hit and how to respond. */
    private ActionListener actionListener = new ActionListener() {

        public void onAction(String name, boolean keyPressed, float tpf) {
            Log.d(TAG,"Shooting.");

            if (name.equals("Shoot") && !keyPressed) {

                // 1. Reset results list.
                CollisionResults results = new CollisionResults();

                // 2. Mode 1: user touch location.
                Vector2f click2d = inputManager.getCursorPosition();
                Vector3f click3d = fgCam.getWorldCoordinates(
                        new Vector2f(click2d.x, click2d.y), 0f).clone();
                Vector3f dir = fgCam.getWorldCoordinates(
                        new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
                Ray ray = new Ray(click3d, dir);

                // 3. Collect intersections between Ray and Shootables in results list.
                shootables.collideWith(ray, results);
                // 4. Print the results
                Log.d(TAG,"----- Collisions? " + results.size() + "-----");
                for (int i = 0; i < results.size(); i++) {
                    // For each hit, we know distance, impact point, name of geometry.
                    float dist = results.getCollision(i).getDistance();
                    Vector3f pt = results.getCollision(i).getContactPoint();
                    String hit = results.getCollision(i).getGeometry().getName();

                    Log.d(TAG,"* Collision #" + i + hit);
                    //         Log.d(TAG,"  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
                }

                // 5. Use the results (we mark the hit object)
                if (results.size() > 0) {
                    // The closest collision point is what was truly hit:
                    CollisionResult closest = results.getClosestCollision();

                    if (mAniChannel.getSpeed() != 0){
                        mAniChannel.setSpeed(0.f);
                    }
                    else {

                        mAniChannel.setSpeed(1.f);

                    }
                }

                else{
                }
            }
        }
    };

	// This function creates the geometry, the viewport and the virtual camera
	// needed for rendering the incoming Android camera frames in the scene
	// graph
	public void initVideoBackground(int screenWidth, int screenHeight) {
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
		videoBGCam = new Camera(settings.getWidth(), settings.getHeight());
		videoBGCam.setViewPort(0.0f, 1.0f, 0.f, 1.0f);
		videoBGCam.setLocation(new Vector3f(0f, 0f, 1.f));
		videoBGCam.setAxes(new Vector3f(-1f,0f,0f), new Vector3f(0f,1f,0f), new Vector3f(0f,0f,-1f));
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

		// Load a model from test_data (OgreXML + material + texture)
        Spatial ninja = assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
        ninja.scale(5.0f, 5.0f, 5.0f);
        Quaternion rotateNinjaX=new Quaternion();
        rotateNinjaX.fromAngleAxis(3.14f/2.0f,new Vector3f(1.0f,0.0f,0.0f));
        Quaternion rotateNinjaZ=new Quaternion();
        rotateNinjaZ.fromAngleAxis(3.14f, new Vector3f(0.0f,0.0f,1.0f));
        Quaternion rotateNinjaY=new Quaternion();
        rotateNinjaY.fromAngleAxis(3.14f,new Vector3f(0.0f,1.0f,0.0f));

        rotateNinjaX.mult(rotateNinjaZ);
        Quaternion rotateNinjaXZ=rotateNinjaZ.mult(rotateNinjaX);
        Quaternion rotateNinjaXYZ = rotateNinjaXZ.mult(rotateNinjaY);

        ninja.rotate(rotateNinjaXYZ);
        
        //3.14/2.,new Vector3f(1.0.,0.0,1.0)));
        ninja.rotate(0.0f, -3.0f, 0.0f);
        ninja.setLocalTranslation(0.0f, 0.0f, 0.0f);

        //Jonathan: To make the ninja shootable we add it to a new node "shootable", the we add it to the root node
        shootables = new Node("Shootables");
        shootables.attachChild(ninja);
        rootNode.attachChild(shootables);
        //Jonathan: Old way (without shootable) to add the ninja to the scene
        //rootNode.attachChild(ninja);
        
        // You must add a light to make the model visible
        DirectionalLight back = new DirectionalLight();
        back.setDirection(new Vector3f(0.f,-1.f,1.0f));
        rootNode.addLight(back);

        DirectionalLight front = new DirectionalLight();
        front.setDirection(new Vector3f(0.f,1.f,1.0f));
        rootNode.addLight(front);

        mAniControl = ninja.getControl(AnimControl.class);
        mAniControl.addListener(this);
        mAniChannel = mAniControl.createChannel();
        // show animation from beginning
        mAniChannel.setAnim("Walk");
        mAniChannel.setLoopMode(LoopMode.Loop);
        mAniChannel.setSpeed(1f);
	}
	
	public void initForegroundCamera(float fovY) {

		fgCam = new Camera(settings.getWidth(), settings.getHeight());
		
		fgCam.setViewPort(0, 1.0f, 0.f,1.0f);
		fgCam.setLocation(new Vector3f(0f, 0f, 0f));
		fgCam.setAxes(new Vector3f(-1f,0f,0f), new Vector3f(0f,1f,0f), new Vector3f(0f,0f,-1f));
		fgCam.setFrustumPerspective(fovY,  settings.getWidth()/settings.getHeight(), 1000, 10000);

		ViewPort fgVP = renderManager.createMainView("ForegroundView", fgCam);
		fgVP.attachScene(rootNode);
		//color,depth,stencil
		fgVP.setClearFlags(false, true, false);
		fgVP.setBackgroundColor(new ColorRGBA(0,0,0,1));
//		fgVP.setBackgroundColor(new ColorRGBA(0,0,0,0));
	}
	
	
	 public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
		 // unused
     }

	 public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
	    // unused
	  }

	public void setCameraPerspectiveNative(float fovY,float aspectRatio) {
        // Log.d(TAG,"Update Camera Perspective..");

        fgCam.setFrustumPerspective(fovY,aspectRatio, 1.f, 100000.f);
	}
	
	public void setCameraViewportNative(float viewport_w,float viewport_h,float size_x,float size_y) {
		 //Log.d(TAG,"Update Camera Viewport..");
		
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
	
	public void setCameraPoseNative(float cam_x,float cam_y,float cam_z) {
		 Log.d(TAG,"Update Camera Pose..");

//         Log.d(TAG, "Coordinates : x = " + Float.toString(cam_x) + " y = "
//                 + Float.toString(cam_y) + " z = " + Float.toString(cam_z));

         // Set the new foreground camera position
		 fgCam.setLocation(new Vector3f(cam_x, cam_y, cam_z));

	}
	
	public void setCameraOrientationNative(float cam_right_x,float cam_right_y,float cam_right_z,
			float cam_up_x,float cam_up_y,float cam_up_z,float cam_dir_x,float cam_dir_y,float cam_dir_z) {
		 
		//Log.d(TAG,"Update Orientation Pose..");

//        Log.d(TAG, "direction : x = " + Float.toString(cam_dir_x) + " y = "
//                + Float.toString(cam_dir_y) + " z = " + Float.toString(cam_dir_z));

   		 //left,up,direction
		 fgCam.setAxes(
				 	new Vector3f(-cam_right_x,-cam_right_y,-cam_right_z),
			 		new Vector3f(-cam_up_x,-cam_up_y,-cam_up_z),
			 		new Vector3f(cam_dir_x,cam_dir_y,cam_dir_z));
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

//			mCubeGeom.rotate(new Quaternion(1.f, 0.f, 0.f, 0.01f));
			mVideoBGGeom.updateLogicalState(tpf);
			mVideoBGGeom.updateGeometricState();
			
		}

		@Override
		public void simpleRender(RenderManager rm) {
			// TODO: add render code
		}
	
}
