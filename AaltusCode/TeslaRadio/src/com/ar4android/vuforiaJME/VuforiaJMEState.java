package com.ar4android.vuforiaJME;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.utils.AppLogger;

/**
 * Created by jimbojd72 on 10/10/14.
 */
public class VuforiaJMEState extends AbstractAppState
{
    private static final String TAG = VuforiaJMEState.class.getName();


    private SimpleApplication app;
    private AssetManager assetManager;
    private AppSettings settings;
    private RenderManager renderManager;
    private Node rootNode;
    private Camera fgCam;
    private ViewPort videoBGVP;
    private boolean attachedToViewPort = false;

    public void setCamera(Camera cam){
        fgCam = cam;
    }

    private Camera videoBGCam;
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

    /** Native function to update the renderer. */
    public native void updateTracking();

    /** Native function for initializing the renderer. */
    public native void initTracking(int width, int height);

    public VuforiaJMEState(SimpleApplication app, Camera fgCam){

        this.app = app;
        this.settings = this.app.getContext().getSettings();
        this.assetManager = this.app.getAssetManager();
        this.renderManager = this.app.getRenderManager();
        this.rootNode = this.app.getRootNode();
        this.fgCam    = fgCam;

        //Initialize a state manager

        initTracking(settings.getWidth(), settings.getHeight());
        initVideoBackground(settings.getWidth(), settings.getHeight());
        initBackgroundCamera(); //thats the problem

    }




    // This function creates the geometry, the viewport and the virtual camera
    // needed for rendering the incoming Android camera frames in the scene
    // graph
    public void initVideoBackground(int screenWidth, int screenHeight) {

        AppLogger.getInstance().d(TAG, "initVideoBackground with width : " + Integer.toString(screenWidth) + " height: " + Integer.toString(screenHeight));

        // Create a Quad shape.
        Quad videoBGQuad = new Quad(1, 1, true);
        // Create a Geometry with the Quad shape
        mVideoBGGeom = new Geometry("mVideoBGGeom", videoBGQuad);
        //float newWidth = 1.f * screenWidth / screenHeight;
        // Center the Geometry in the middle of the screen.
        //
        mVideoBGGeom.setLocalTranslation(-0.5f, -0.5f, 0.f);//
        // Scale (stretch) the width of the Geometry to cover the whole screen
        // width.
        //mVideoBGGeom.setLocalScale(1.f * newWidth, 1.f, 1);
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
        videoBGVP = renderManager.createMainView("VideoBGView",videoBGCam);
        // Attach the geometry representing the video background to the
        // viewport.
        //videoBGVP.attachScene(mVideoBGGeom); //That's the problem...

        //videoBGVP.setClearFlags(true, false, false);
        //videoBGVP.setBackgroundColor(new ColorRGBA(1,0,0,1));

    }

    @Override
    public void initialize(AppStateManager stateManager, Application app){

        super.initialize(stateManager, app);

    }

    @Override
    public void cleanup(){
        super.cleanup();

    }

    @Override
    public void setEnabled(boolean enabled) {
        // Pause and unpause
        super.setEnabled(enabled);
        if(enabled){
            // we must attach the viewport only if we are attach and active
            //The reason i because the viewport is rendered even if
            videoBGVP.attachScene(mVideoBGGeom);
            this.attachedToViewPort = true;

        } else {
            videoBGVP.detachScene(mVideoBGGeom); //That's th problem...
            this.attachedToViewPort=false;
        }
    }

    public void setCameraPerspectiveNative(float fovY,float aspectRatio) {
        // Log.d(TAG,"Update Camera Perspective..");

        //Log.d(TAG,"setCameraPerspectiveNative with fovY : " + Float.toString(fovY) + " aspectRatio: " + Float.toString(aspectRatio) );
        if(fgCam != null){
            fgCam.setFrustumPerspective(fovY,aspectRatio, 1.f, 100000.f);
        }
    }

    public void setCameraViewportNative(float viewport_w,float viewport_h,float size_x,float size_y) {
        //Log.d(TAG,"Update Camera Viewport..");

        //AppLogger.getInstance().d(TAG, "setCameraViewportNative : ");

        // if(!isViewportAdjust)
        //{
        //AppLogger.getInstance().d(TAG, "setCameraViewportNative with viewport_w : " + viewport_w + " viewport_h: " + viewport_h);
        float newWidth = 1.f;
        float newHeight = 1.f;


        //if (viewport_h != settings.getHeight())
        //{

        newWidth = viewport_w/viewport_h;
        newHeight = 1.0f;
        videoBGCam.resize((int)viewport_w,(int)viewport_h,true);
        videoBGCam.setParallelProjection(true);
        //AppLogger.getInstance().d(TAG,"viewport_h != settings.getHeight()");
        //}



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
        //AppLogger.getInstance().d(TAG,"setCameraViewportNative newWidth=" + newWidth + " newHeight="+newHeight);


    }


    public void setCameraPoseNative(float cam_x,float cam_y,float cam_z, int id) {
        //AppLogger.getInstance().d(TAG, "Update Camera Pose..");

        this.rootNode.getControl(TrackableManager.class).updatePosition(id, new Vector3f(-cam_x, -cam_y, cam_z));

    }

    public void setCameraOrientationNative(float cam_right_x,float cam_right_y,float cam_right_z,
                                           float cam_up_x,float cam_up_y,float cam_up_z,float cam_dir_x,float cam_dir_y,float cam_dir_z, int id) {

        //AppLogger.getInstance().d(TAG,"Update Orientation Pose..");

        //        AppLogger.getInstance().d(TAG, "direction : x = " + Float.toString(cam_dir_x) + " y = "
        //                + Float.toString(cam_dir_y) + " z = " + Float.toString(cam_dir_z));



        // Adding the world rotation
        Matrix3f rotMatrix = new Matrix3f( cam_right_x, cam_up_x , -cam_dir_x ,
                cam_right_y, cam_up_y, -cam_dir_y,
                -cam_right_z , -cam_up_z , cam_dir_z);

        Vector3f vx = new Vector3f(cam_right_x, cam_up_x, -cam_dir_x);

        this.rootNode.getControl(TrackableManager.class).updateRotationMatrix(id, rotMatrix, vx);

    }
    public void setTrackableVisibleNative(int id, int isTrackableVisible)
    {
        boolean isVisible = false;
        if(isTrackableVisible != 0)
        {
            isVisible = true;
        }
        try{
            this.rootNode.getControl(TrackableManager.class).updateVisibility(id, isVisible);
        }catch (Exception e)
        {
            System.out.println(e);
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
    public void update(float tpf) {

        updateTracking();

        try {
            if (mNewCameraFrameAvailable) {
                mCameraTexture.setImage(mCameraImage);
                mvideoBGMat.setTexture("ColorMap", mCameraTexture);
            }
        }catch(Exception e)
        {
            System.out.println(e);
        }
        // mCubeGeom.rotate(new Quaternion(1.f, 0.f, 0.f, 0.01f));
        if(attachedToViewPort){
            mVideoBGGeom.updateLogicalState(tpf);
            mVideoBGGeom.updateGeometricState();
        }



        //iScenarioManager.simpleUpdate(tpf);


        // Update the world depending on what is in focus
        //virtualWorld.UpdateFocus(fgCam,focusableObjects);
        //virtualWorld.UpdateViewables(rootNode,focusableObjects);
    }


}
