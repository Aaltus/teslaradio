package com.ar4android.vuforiaJME.java;


import android.app.Activity;
import com.ar4android.vuforiaJME.ICameraUpdater;
import com.ar4android.vuforiaJME.VuforiaCallback;
import com.ar4android.vuforiaJME.VuforiaCaller;
import com.qualcomm.vuforia.*;

import java.nio.ByteBuffer;

/**
 * Created by jimbojd72 on 10/21/14.
 */
public class VuforiaCallerJava implements VuforiaCaller, Vuforia.UpdateCallbackInterface {

    private static final String TAG = VuforiaCallerJava.class.getSimpleName();
    private final int numberOfDataSet          = 2;

    private World world;

    private int qcarVideoMode = CameraDevice.MODE.MODE_OPTIMIZE_SPEED;
    private Matrix44F projectionMatrix;
    private int screenWidth;
    private int screenHeight;
    private boolean switchDataSetAsap = false;
    // Indicates whether screen is in portrait (true) or landscape (false) mode
    private boolean isActivityInPortraitMode   = false;
    //QCAR::CameraDevice::MODE qcarVideoMode = QCAR::CameraDevice::MODE_DEFAULT;
    private boolean[] trackableFound = new boolean[numberOfDataSet];

    private VuforiaCallback vuforiaCallback;
    private ICameraUpdater iCameraUpdater;

    public VuforiaCallerJava(VuforiaCallback vuforiaCallback, ICameraUpdater iCameraUpdater)
    {
        this.vuforiaCallback = vuforiaCallback;
        this.iCameraUpdater  = iCameraUpdater;
    }

    @Override
    public void setRGB565CameraImage(byte[] buffer, int width, int height)
    {
        this.vuforiaCallback.setRGB565CameraImage(buffer,width,height);
    }

    @Override
    public int initTracker() {
        this.world = World.CinitWorld();
        return World.CinitTracker(world);
    }

    @Override
    public void deinitTracker() {
        World.CdeInitTracker(world);
    }

    @Override
    public int loadTrackerData() {
        return World.CloadTrackers(world);
    }

    @Override
    public int destroyTrackerData() {
        return World.CdestroyTrackerData(world);
    }

    @Override
    public void onQCARInitializedNative(int loggerLvl) {
        // Register the update callback where we handle the data set swap:
        Vuforia.registerCallback(this);

        // Comment in to enable tracking of up to 2 targets simultaneously and
        // split the work over multiple frames:
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, numberOfDataSet);

    }

    @Override
    public void startCamera() {

        // Select the camera to open, set this to QCAR::CameraDevice::CAMERA_FRONT
        // to activate the front camera instead.
        int camera = CameraDevice.CAMERA.CAMERA_BACK;
        CameraDevice cameraDevice = CameraDevice.getInstance();

        // Initialize the camera:
        if (!cameraDevice.init(camera))
            return;

        // Configure the video background
        configureVideoBackground();

        // Select the default mode:
        //if (!cameraDevice.selectVideoMode(QCAR::CameraDevice::MODE_DEFAULT))
        //    return;
        if (!cameraDevice.selectVideoMode(qcarVideoMode))
            return;



        // Start the camera:
        if (!cameraDevice.start())
            return;

        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);


        // Start the tracker:
        TrackerManager trackerManager = TrackerManager.getInstance();
        Tracker imageTracker = trackerManager.getTracker(ImageTracker.getClassType());
        if(imageTracker != null)
            imageTracker.start();

    }

    @Override
    public void stopCamera() {

        // Stop the tracker:
        TrackerManager trackerManager = TrackerManager.getInstance();
        Tracker imageTracker = trackerManager.getTracker(ImageTracker.getClassType());
        if(imageTracker != null)
            imageTracker.stop();

        CameraDevice.getInstance().stop();
        CameraDevice.getInstance().deinit();
    }

    @Override
    public void setProjectionMatrix() {

        // Cache the projection matrix:
        final CameraCalibration cameraCalibration = CameraDevice.getInstance().getCameraCalibration();
        projectionMatrix = Tool.getProjectionGL(cameraCalibration, 2.0f, 2500.0f);
    }

    private void configureVideoBackground(){

        //LOGI("configureVideoBackground");

        // Get the default video mode:
        CameraDevice cameraDevice = CameraDevice.getInstance();
        //QCAR::VideoMode videoMode        = cameraDevice.getVideoMode(QCAR::CameraDevice::MODE_DEFAULT);
        VideoMode videoMode        = cameraDevice.getVideoMode(qcarVideoMode);

        // Configure the video background
        //VideoBackgroundConfig config;
        VideoBackgroundConfig config = new VideoBackgroundConfig();

        //config.mEnabled = false;
        config.setEnabled(false);

        //Jonathan Desmarais: I change this to optimize the FPS of the App the rendering frame
        // and the camera frame are not synchronized making the code must effective.
        //config.mSynchronous = true;
        //config.mSynchronous = false;
        //config.mPosition.data[0] = 0.0f;
        //config.mPosition.data[1] = 0.0f;
        config.setSynchronous(false);
        config.setPosition(new Vec2I(0,0));
        //config.getPosition().getData()[0] = 0.0f;

        if (isActivityInPortraitMode)
        {
            //LOG("configureVideoBackground PORTRAIT");
            config.setSize(new Vec2I(Math.round(videoMode.getHeight()* (screenHeight / (float)videoMode.getWidth())),screenHeight));
            //config.mSize.data[0] = videoMode.mHeight
            //        * (screenHeight / (float)videoMode.mWidth);
            //config.mSize.data[1] = screenHeight;

            if(config.getSize().getData()[0] < screenWidth)
            {
                //LOGI("Correcting rendering background size to handle missmatch between screen and video aspect ratios.");
                config.getSize().getData()[0] = screenWidth;
                config.getSize().getData()[1] = Math.round(screenWidth *(videoMode.getWidth() / (float)videoMode.getHeight()));
            }
        }
        else
        {
            //LOG("configureVideoBackground LANDSCAPE");
            config.getSize().getData()[0] = screenWidth;
            config.getSize().getData()[1] = Math.round(videoMode.getHeight() * (screenWidth / (float)videoMode.getWidth()));

            if(config.getSize().getData()[1] < screenHeight)
            {
               //LOGI("Correcting rendering background size to handle missmatch between screen and video aspect ratios.");
                config.getSize().getData()[0] = Math.round(screenHeight * (videoMode.getWidth() / (float)videoMode.getHeight()));
                config.getSize().getData()[1] = screenHeight;
            }
        }

        // Set the config:
        Renderer.getInstance().setVideoBackgroundConfig(config);

        //AppLogger.getInstance().i(TAG, "Configure Video Background : Video (%d,%d), Screen (%d,%d), mSize (%d,%d)", videoMode.mWidth, videoMode.mHeight, screenWidth, screenHeight, config.mSize.data[0], config.mSize.data[1]);


    }

    @Override
    public void initApplicationNative(int width, int height) {

        // Store screen dimensions
        this.screenWidth = width;
        this.screenHeight = height;

        // Handle to the activity class:
        /*
        env->GetJavaVM(&javaVM);
        activityObj = env->NewGlobalRef(obj);
        LOGI("Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_initApplicationNative finished");
        */
    }

    @Override
    public void deinitApplicationNative() {


        //empty for the moment
    }

    @Override
    public void setActivityPortraitMode(boolean isPortrait) {
        throw new UnsupportedOperationException("setActivityPortraitMode not supported yet");
    }

    @Override
    public void switchDatasetAsap() {
        switchDataSetAsap = true;
    }

    @Override
    public boolean autofocus() {
        return CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO) ? true : false;
    }

    @Override
    public boolean setFocusMode(int mode) {
        int qcarFocusMode;

        //TODO: Refactor stupid test case here
        switch ((int)mode)
        {
            case 0:
                qcarFocusMode = CameraDevice.FOCUS_MODE.FOCUS_MODE_NORMAL;
                break;

            case 1:
                qcarFocusMode = CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO;
                break;

            case 2:
                qcarFocusMode = CameraDevice.FOCUS_MODE.FOCUS_MODE_INFINITY;
                break;

            case 3:
                qcarFocusMode = CameraDevice.FOCUS_MODE.FOCUS_MODE_MACRO;
                break;

            default:
                return false;
        }

        return CameraDevice.getInstance().setFocusMode(qcarFocusMode) ? true : false;
    }

    @Override
    public boolean activateFlash(boolean flash) {
        return CameraDevice.getInstance().setFlashTorchMode(flash==true) ? true : false;
    }

    @Override
    public void setICameraUpdate(ICameraUpdater iCameraUpdate) {
        this.iCameraUpdater = iCameraUpdate;
    }

    @Override
    public int QCARinit() {
        return Vuforia.init();
    }

    @Override
    public void QCARdeinit() {
        Vuforia.deinit();
    }

    @Override
    public void QCARonPause() {
        Vuforia.onPause();
    }

    @Override
    public void QCARonResume() {
        Vuforia.onResume();

    }

    @Override
    public boolean QCARisInitialized() {
        return Vuforia.isInitialized();

    }

    @Override
    public void QCARsetInitParameters(Activity activity, int mQCARFlags) {
        Vuforia.setInitParameters(activity,mQCARFlags);
    }

    @Override
    public void QCAR_onUpdate(State state) {

        //from
        //https://developer.vuforia.com/forum/faq/android-how-can-i-access-camera-image
        Image imageRGB565 = null;
        Frame frame = state.getFrame();

        for (int i = 0; i < frame.getNumImages(); ++i) {
            final Image image = frame.getImage(i);
            if (image.getFormat() == PIXEL_FORMAT.RGB565) {
                imageRGB565 = /*(Image)*/image;

                break;
            }
        }

        if (imageRGB565 != null) {
            //JNIEnv* env = 0;

            //if ((javaVM != 0) && (activityObj != 0) && (javaVM->GetEnv((void**)&env, JNI_VERSION_1_4) == JNI_OK)) {

                ByteBuffer pixels = imageRGB565.getPixels();
                int width = imageRGB565.getWidth();
                int height = imageRGB565.getHeight();
                int numPixels = width * height;
                this.setRGB565CameraImage(pixels.array(),width,height);
                //LOGD("Update video image... !OnUpdate!");
            /*
                jbyteArray pixelArray = env->NewByteArray(numPixels * 2);
                env->SetByteArrayRegion(pixelArray, 0, numPixels * 2, (const jbyte*) pixels);
                jclass javaClass = env->GetObjectClass(activityObj);
                jmethodID method = env-> GetMethodID(javaClass, "setRGB565CameraImage", "([BII)V");
                env->CallVoidMethod(activityObj, method, pixelArray, width, height);

                env->DeleteLocalRef(pixelArray);
                */

           // }
        }

    }

    @Override
    public void updateTracking() {
        /*
        jclass activityClass = env->GetObjectClass(obj);
        jmethodID setCameraPerspectiveMethod = env->GetMethodID(activityClass,"setCameraPerspectiveNative", "(FF)V");
        jmethodID setCameraViewportMethod = env->GetMethodID(activityClass,"setCameraViewportNative", "(FFFF)V");
        jmethodID setCameraPoseMethod = env->GetMethodID(activityClass,"setCameraPoseNative", "(FFFI)V");
        jmethodID setTrackableVisible  = env->GetMethodID(activityClass, "setTrackableVisibleNative", "(II)V");
        jmethodID setCameraOrientationMethod = env->GetMethodID(activityClass,"setCameraOrientationNative", "(FFFFFFFFFI)V");
        //LOG("Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_GLRenderer_renderFrame");
*/
        // Get the state from QCAR and mark the beginning of a rendering section
        State state = Renderer.getInstance().begin();



        // Code pasted here to fix TR-141
        //get perspective transformation
        float nearPlane = 1.0f;
        float farPlane  = 1000.0f;
        final CameraCalibration cameraCalibration = CameraDevice.getInstance().getCameraCalibration();

        VideoBackgroundConfig config = Renderer.getInstance().getVideoBackgroundConfig();

        float viewportWidth     = config.getSize().getData()[0];
        float viewportHeight    = config.getSize().getData()[1];

        Vec2F size        = cameraCalibration.getSize();
        Vec2F focalLength = cameraCalibration.getFocalLength();
        double fovRadians        = 2 * Math.atan(0.5f * (size.getData()[1] / focalLength.getData()[1]));
        double fovDegrees        = fovRadians * 180.0f / Math.PI;
        float aspectRatio        = size.getData()[0] / size.getData()[1];

        //adjust for screen vs camera size distortion
        float viewportDistort = 1.0f;

        if (viewportWidth != screenWidth)
        {
            viewportDistort = viewportWidth / (float) screenWidth;
            fovDegrees      = fovDegrees    * viewportDistort;
            aspectRatio     = aspectRatio   / viewportDistort;

        }

        if (viewportHeight != screenHeight)
        {
            viewportDistort = viewportHeight / (float) screenHeight;
            fovDegrees      = fovDegrees     / viewportDistort;
            aspectRatio     = aspectRatio    * viewportDistort;
        }

        iCameraUpdater.setCameraPerspectiveNative((float)fovDegrees,aspectRatio);
        //CALL_JAVA(obj,setCameraPerspectiveMethod,fovDegrees,aspectRatio);

        // jclass activityClass = env->GetObjectClass(obj);

        iCameraUpdater.setCameraViewportNative(viewportWidth,viewportHeight,cameraCalibration.getSize().getData()[0],cameraCalibration.getSize().getData()[1]);
        //CALL_JAVA(obj,setCameraViewportMethod,viewportWidth,viewportHeight,cameraCalibration.getSize().data[0],cameraCalibration.getSize().data[1]);

        for(int i  = 0; i < numberOfDataSet; i++)
        {
            trackableFound[i] = false;
        }
        // Did we find any trackables this frame?
        for(int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++)
        {
            final TrackableResult result = state.getTrackableResult(tIdx);


            final Trackable trackable    = result.getTrackable();


            //register position
            AaltusTrackable at = world.getTrackable(trackable.getName());
            at.setCameraPosition( Tool.convertPose2GLMatrix(result.getPose()) );
            int id = at.getId();
            trackableFound[id] = true;


            world.setOrigin(trackable.getName());
            //Update origin camera
            Matrix44F cam = at.getPoseMatrix();
            iCameraUpdater.setCameraPerspectiveNative((float)fovDegrees,aspectRatio);
            iCameraUpdater.setCameraOrientationNative(cam.getData()[0],cam.getData()[1],cam.getData()[2],
                    cam.getData()[4],cam.getData()[5],cam.getData()[6],cam.getData()[8],cam.getData()[9],cam.getData()[10], at.getId());
            /*CALL_JAVA(obj,setCameraPerspectiveMethod,fovDegrees,aspectRatio);
            CALL_JAVA(obj,setCameraOrientationMethod,
                    cam.data[0],cam.data[1],cam.data[2],
                    cam.data[4],cam.data[5],cam.data[6],cam.data[8],cam.data[9],cam.data[10], at->getId());*/


            //LOGE("Updating camera");
            //Update camera
            Vec4F vector = at.getPositionFromOrigin();
            iCameraUpdater.setCameraPoseNative(vector.getData()[0],vector.getData()[1],
                    vector.getData()[2],at.getId());
            /*CALL_JAVA(obj,setCameraPoseMethod,vector.data[0],vector.data[1],
                    vector.data[2],at->getId());*/
        }

        for(int i  = 0; i < numberOfDataSet; i++)
        {
            iCameraUpdater.setTrackableVisibleNative(i,(trackableFound[i]) ? 1 : 0);
            //CALL_JAVA(obj,setTrackableVisible,i,trackableFound[i]);
        }

        Renderer.getInstance().end();
    }

    @Override
    public void initTracking(int width, int height) {
        // Update screen dimensions
        this.screenWidth = width;
        this.screenHeight = height;

        // Reconfigure the video background
        configureVideoBackground();

    }
/*
    @Override
    public void setCameraPerspectiveNative(float fovY, float aspectRatio) {
        this.iCameraUpdater.setCameraPerspectiveNative(fovY,aspectRatio);
    }

    @Override
    public void setCameraViewportNative(float viewport_w, float viewport_h, float size_x, float size_y) {
        this.iCameraUpdater.setCameraViewportNative(viewport_w, viewport_h, size_x, size_y);
    }

    @Override
    public void setCameraPoseNative(float cam_x, float cam_y, float cam_z, int id) {
        this.iCameraUpdater.setCameraPoseNative(cam_x, cam_y, cam_z, id);
    }

    @Override
    public void setCameraOrientationNative(float cam_right_x, float cam_right_y, float cam_right_z, float cam_up_x, float cam_up_y, float cam_up_z, float cam_dir_x, float cam_dir_y, float cam_dir_z, int id) {
        this.iCameraUpdater.setCameraOrientationNative(cam_right_x, cam_right_y, cam_right_z, cam_up_x, cam_up_y, cam_up_z, cam_dir_x, cam_dir_y, cam_dir_z, id);

    }

    @Override
    public void setTrackableVisibleNative(int id, int isTrackableVisible) {
        this.iCameraUpdater.setTrackableVisibleNative(id, isTrackableVisible);
    }
    */
}
