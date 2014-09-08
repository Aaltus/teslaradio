/* VuforiaNative - VuforiaJME Example
 *
 * Example Chapter 5
 * accompanying the book
 * "Augmented Reality for Android Application Development", Packt Publishing, 2013.
 *
 * Copyright � 2013 Jens Grubert, Raphael Grasset / Packt Publishing.
 *
 * This code is the proprietary information of Qualcomm Connected Experiences, Inc.
 * Any use of this code is subject to the terms of the License Agreement for Vuforia Software Development Kit
 * available on the Vuforia developer website.
 *
 * https://developer.vuforia.com
 *
 * This example was built from the ImageTarget example accompanying the Vuforia SDK
 * https://developer.vuforia.com/resources/sample-apps/image-targets-sample-app
 *
 * This class is based on the ImageTarget.cpp from the Vuforia ImageTarget example
 */

#include <jni.h>

#include <stdio.h>
#include <string.h>
#include <assert.h>
#include <math.h>

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

#include <QCAR/QCAR.h>
#include <QCAR/CameraDevice.h>
#include <QCAR/Renderer.h>
#include <QCAR/VideoBackgroundConfig.h>
#include <QCAR/Trackable.h>
#include <QCAR/TrackableResult.h>
#include <QCAR/Tool.h>
#include <QCAR/Tracker.h>
#include <QCAR/TrackerManager.h>
#include <QCAR/ImageTracker.h>
#include <QCAR/CameraCalibration.h>
#include <QCAR/UpdateCallback.h>
#include <QCAR/DataSet.h>
#include <QCAR/Image.h>
#include "World.h"
#include "SampleMath.h"
#include "MathUtils.h"


#define CALL_JAVA env->CallVoidMethod
#ifdef __cplusplus
extern "C"
{
#endif
CWorld* world;
// Screen dimensions:
unsigned int screenWidth        = 0;
unsigned int screenHeight       = 0;

// Indicates whether screen is in portrait (true) or landscape (false) mode
bool isActivityInPortraitMode   = false;

bool trackableFound[AALTUS_NBR_TARGET] = {false};

// The projection matrix used for rendering virtual objects:
QCAR::Matrix44F projectionMatrix;

// Constants:
static const float kObjectScale = 3.f;

QCAR::DataSet* dataSetStonesAndChips    = 0;
QCAR::DataSet* dataSetLena              = 0;
const int      numberOfDataSet          = 2;

bool switchDataSetAsap          = false;

//global variables
JavaVM* javaVM = 0;
jobject activityObj = 0;


JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM* vm,  void* reserved) {
    LOGI("JNI_OnLoad");
    javaVM = vm;
    return JNI_VERSION_1_4;
}

// Object to receive update callbacks from QCAR SDK
//1) The QCAR_onUpdate method runs in a separate thread from the renderer, so OpenGL calls will not work.
//2) The State object received in the QCAR_onUpdate method is only valid for the scope of the method.
class VuforiaJME_UpdateCallback : public QCAR::UpdateCallback
{   
    virtual void QCAR_onUpdate(QCAR::State& state)
    {

    	//from
        //https://developer.vuforia.com/forum/faq/android-how-can-i-access-camera-image
        QCAR::Image *imageRGB565 = NULL;
        QCAR::Frame frame = state.getFrame();

        for (int i = 0; i < frame.getNumImages(); ++i) {
              const QCAR::Image *image = frame.getImage(i);
              if (image->getFormat() == QCAR::RGB565) {
                  imageRGB565 = (QCAR::Image*)image;

                  break;
              }
        }

        if (imageRGB565) {
            JNIEnv* env = 0;

            if ((javaVM != 0) && (activityObj != 0) && (javaVM->GetEnv((void**)&env, JNI_VERSION_1_4) == JNI_OK)) {

                const short* pixels = (const short*) imageRGB565->getPixels();
                int width = imageRGB565->getWidth();
                int height = imageRGB565->getHeight();
                int numPixels = width * height;

                LOGD("Update video image... !OnUpdate!");
                jbyteArray pixelArray = env->NewByteArray(numPixels * 2);
                env->SetByteArrayRegion(pixelArray, 0, numPixels * 2, (const jbyte*) pixels);
                jclass javaClass = env->GetObjectClass(activityObj);
                jmethodID method = env-> GetMethodID(javaClass, "setRGB565CameraImage", "([BII)V");
                env->CallVoidMethod(activityObj, method, pixelArray, width, height);

                env->DeleteLocalRef(pixelArray);

            }
        }

    }
};

VuforiaJME_UpdateCallback updateCallback;



JNIEXPORT void JNICALL
Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_setActivityPortraitMode(JNIEnv *, jobject, jboolean isPortrait)
{
    LOGI("Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_setActivityPortraitMode");
    isActivityInPortraitMode = isPortrait;
}



JNIEXPORT void JNICALL
Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_switchDatasetAsap(JNIEnv *, jobject)
{
    switchDataSetAsap = true;
}


JNIEXPORT int JNICALL
Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_initTracker(JNIEnv *, jobject)
{
    LOGI("Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_initTracker");
    world = CinitWorld();
    return CinitTracker(world);

}


JNIEXPORT void JNICALL
Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_deinitTracker(JNIEnv *, jobject)
{
    LOGI("Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_deinitTracker");
    CdeInitTracker(world);
}


JNIEXPORT int JNICALL
Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_loadTrackerData(JNIEnv *, jobject)
{

    LOGI("Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_loadTrackerData");
    return CloadTrackers(world);

}


JNIEXPORT int JNICALL
Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_destroyTrackerData(JNIEnv *, jobject)
{
    LOGI("Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_destroyTrackerData");
    return CdestroyTrackerData(world);

}


JNIEXPORT void JNICALL
Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_onQCARInitializedNative(JNIEnv *, jobject, jint loggerLvl)
{
    logLevel = loggerLvl;

    LOGI("com_ar4android_vuforiaJME_VuforiaJMEActivity_onQCARInitializedNative registerCallback");
    // Register the update callback where we handle the data set swap:
    QCAR::registerCallback(&updateCallback);

    // Comment in to enable tracking of up to 2 targets simultaneously and
    // split the work over multiple frames:
     QCAR::setHint(QCAR::HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, numberOfDataSet);
}

// RENDERING CALL

JNIEXPORT void JNICALL
Java_com_ar4android_vuforiaJME_VuforiaJME_updateTracking(JNIEnv *env, jobject obj)
{
    jclass activityClass = env->GetObjectClass(obj);
    jmethodID setCameraPerspectiveMethod = env->GetMethodID(activityClass,"setCameraPerspectiveNative", "(FF)V");
    jmethodID setCameraViewportMethod = env->GetMethodID(activityClass,"setCameraViewportNative", "(FFFF)V");
    jmethodID setCameraPoseMethod = env->GetMethodID(activityClass,"setCameraPoseNative", "(FFFI)V");
    jmethodID setTrackableVisible  = env->GetMethodID(activityClass, "setTrackableVisible", "(II)V");
    jmethodID setCameraOrientationMethod = env->GetMethodID(activityClass,"setCameraOrientationNative", "(FFFFFFFFF)V");
    //LOG("Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_GLRenderer_renderFrame");

    // Get the state from QCAR and mark the beginning of a rendering section
    QCAR::State state = QCAR::Renderer::getInstance().begin();



    // Code pasted here to fix TR-141
    //get perspective transformation
    float nearPlane = 1.0f;
    float farPlane  = 1000.0f;
    const QCAR::CameraCalibration& cameraCalibration = QCAR::CameraDevice::getInstance().getCameraCalibration();

    QCAR::VideoBackgroundConfig config = QCAR::Renderer::getInstance().getVideoBackgroundConfig();

    float viewportWidth     = config.mSize.data[0];
    float viewportHeight    = config.mSize.data[1];

    QCAR::Vec2F size        = cameraCalibration.getSize();
    QCAR::Vec2F focalLength = cameraCalibration.getFocalLength();
    float fovRadians        = 2 * atan(0.5f * (size.data[1] / focalLength.data[1]));
    float fovDegrees        = fovRadians * 180.0f / M_PI;
    float aspectRatio       = size.data[0] / size.data[1];

    //adjust for screen vs camera size distortion
    float viewportDistort = 1.0;

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


    CALL_JAVA(obj,setCameraPerspectiveMethod,fovDegrees,aspectRatio);

    // jclass activityClass = env->GetObjectClass(obj);

    CALL_JAVA(obj,setCameraViewportMethod,viewportWidth,viewportHeight,cameraCalibration.getSize().data[0],cameraCalibration.getSize().data[1]);

    for(int i  = 0; i < AALTUS_NBR_TARGET; i++)
    {
        trackableFound[i] = false;
    }
    // Did we find any trackables this frame?
    for(int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++)
    {
        const QCAR::TrackableResult* result = state.getTrackableResult(tIdx);


        const QCAR::Trackable& trackable         = result->getTrackable();

        trackableFound[tIdx] = true;
        //register position
        AaltusTrackable* at = ((World*) world)->getTrackable(trackable.getName());
        at->setCameraPosition( QCAR::Tool::convertPose2GLMatrix(result->getPose()) );


        ((World*)world)->setOrigin(trackable.getName());
        //Update origin camera
         QCAR::Matrix44F cam = at->getInvTranspMV();
         CALL_JAVA(obj,setCameraPerspectiveMethod,fovDegrees,aspectRatio);
         CALL_JAVA(obj,setCameraOrientationMethod,
            cam.data[0],cam.data[1],cam.data[2],
            cam.data[4],cam.data[5],cam.data[6],cam.data[8],cam.data[9],cam.data[10]);


        LOGE("Updating camera");
        //Update camera
        QCAR::Vec4F vector = at->getPositionFromOrigin();

        CALL_JAVA(obj,setCameraPoseMethod,vector.data[0],vector.data[1],
        vector.data[2],at->getId());
    }

    for(int i  = 0; i < AALTUS_NBR_TARGET; i++)
    {
            CALL_JAVA(obj,setTrackableVisible,i,trackableFound[i]);
    }
    QCAR::Renderer::getInstance().end();
}


void configureVideoBackground()
{
    LOGI("configureVideoBackground");

    // Get the default video mode:
    QCAR::CameraDevice& cameraDevice = QCAR::CameraDevice::getInstance();
    QCAR::VideoMode videoMode        = cameraDevice.getVideoMode(QCAR::CameraDevice::MODE_DEFAULT);

    // Configure the video background
    QCAR::VideoBackgroundConfig config;

    config.mEnabled = false;

     //Jonathan Desmarais: I change this to optimize the FPS of the App the rendering frame
     // and the camera frame are not synchronized making the code must effective.
     //config.mSynchronous = true;
    config.mSynchronous = false;
    config.mPosition.data[0] = 0.0f;
    config.mPosition.data[1] = 0.0f;
    
    if (isActivityInPortraitMode)
    {
        //LOG("configureVideoBackground PORTRAIT");
        config.mSize.data[0] = videoMode.mHeight
                                * (screenHeight / (float)videoMode.mWidth);
        config.mSize.data[1] = screenHeight;

        if(config.mSize.data[0] < screenWidth)
        {
            LOGI("Correcting rendering background size to handle missmatch between screen and video aspect ratios.");
            config.mSize.data[0] = screenWidth;
            config.mSize.data[1] = screenWidth * 
                              (videoMode.mWidth / (float)videoMode.mHeight);
        }
    }
    else
    {
        //LOG("configureVideoBackground LANDSCAPE");
        config.mSize.data[0] = screenWidth;
        config.mSize.data[1] = videoMode.mHeight
                            * (screenWidth / (float)videoMode.mWidth);

        if(config.mSize.data[1] < screenHeight)
        {
            LOGI("Correcting rendering background size to handle missmatch between screen and video aspect ratios.");
            config.mSize.data[0] = screenHeight
                                * (videoMode.mWidth / (float)videoMode.mHeight);
            config.mSize.data[1] = screenHeight;
        }
    }

    // Set the config:
    QCAR::Renderer::getInstance().setVideoBackgroundConfig(config);

    LOGI("Configure Video Background : Video (%d,%d), Screen (%d,%d), mSize (%d,%d)", videoMode.mWidth, videoMode.mHeight, screenWidth, screenHeight, config.mSize.data[0], config.mSize.data[1]);

}


JNIEXPORT void JNICALL
Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_initApplicationNative(
                            JNIEnv* env, jobject obj, jint width, jint height)
{
    LOGI("Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_initApplicationNative");
    
    // Store screen dimensions
    screenWidth = width;
    screenHeight = height;
        
    // Handle to the activity class:

    env->GetJavaVM(&javaVM);
    activityObj = env->NewGlobalRef(obj);
    LOGI("Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_initApplicationNative finished");
}


JNIEXPORT void JNICALL
Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_deinitApplicationNative(
                                                        JNIEnv* env, jobject obj)
{
    LOGI("Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_deinitApplicationNative");


}


JNIEXPORT void JNICALL
Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_startCamera(JNIEnv *env, jobject obj)
{
    LOGI("Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_startCamera");
    
    // Select the camera to open, set this to QCAR::CameraDevice::CAMERA_FRONT 
    // to activate the front camera instead.
    QCAR::CameraDevice::CAMERA camera = QCAR::CameraDevice::CAMERA_BACK;
    QCAR::CameraDevice& cameraDevice = QCAR::CameraDevice::getInstance();

    // Initialize the camera:
    if (!cameraDevice.init(camera))
        return;

    // Configure the video background
    configureVideoBackground();

    // Select the default mode:
    if (!cameraDevice.selectVideoMode(QCAR::CameraDevice::MODE_DEFAULT))
        return;



    // Start the camera:
    if (!cameraDevice.start())
        return;

    QCAR::setFrameFormat(QCAR::RGB565, true);


    // Start the tracker:
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    QCAR::Tracker* imageTracker = trackerManager.getTracker(QCAR::ImageTracker::getClassType());
    if(imageTracker != 0)
        imageTracker->start();
}


JNIEXPORT void JNICALL
Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_stopCamera(JNIEnv *, jobject)
{
    LOGI("Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_stopCamera");

    // Stop the tracker:
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    QCAR::Tracker* imageTracker = trackerManager.getTracker(QCAR::ImageTracker::getClassType());
    if(imageTracker != 0)
        imageTracker->stop();
    
    QCAR::CameraDevice::getInstance().stop();
    QCAR::CameraDevice::getInstance().deinit();
}


JNIEXPORT void JNICALL
Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_setProjectionMatrix(JNIEnv *, jobject)
{
    LOGD("Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_setProjectionMatrix");

    // Cache the projection matrix:
    const QCAR::CameraCalibration& cameraCalibration = QCAR::CameraDevice::getInstance().getCameraCalibration();
    projectionMatrix = QCAR::Tool::getProjectionGL(cameraCalibration, 2.0f, 2500.0f);
}

// ----------------------------------------------------------------------------
// Activates Camera Flash
// ----------------------------------------------------------------------------
JNIEXPORT jboolean JNICALL
Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_activateFlash(JNIEnv*, jobject, jboolean flash)
{
    //LOGD("com_ar4android_vuforiaJME_VuforiaJMEActivity_activateFlash");
    return QCAR::CameraDevice::getInstance().setFlashTorchMode((flash==JNI_TRUE)) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_autofocus(JNIEnv*, jobject)
{
    //LOGD("com_ar4android_vuforiaJME_VuforiaJMEActivity_autofocus");
    return QCAR::CameraDevice::getInstance().setFocusMode(QCAR::CameraDevice::FOCUS_MODE_TRIGGERAUTO) ? JNI_TRUE : JNI_FALSE;
}


JNIEXPORT jboolean JNICALL
Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_setFocusMode(JNIEnv*, jobject, jint mode)
{
    //LOGD("com_ar4android_vuforiaJME_VuforiaJMEActivity_setFocusMode");
    int qcarFocusMode;

    switch ((int)mode)
    {
        case 0:
            qcarFocusMode = QCAR::CameraDevice::FOCUS_MODE_NORMAL;
            break;
        
        case 1:
            qcarFocusMode = QCAR::CameraDevice::FOCUS_MODE_CONTINUOUSAUTO;
            break;
            
        case 2:
            qcarFocusMode = QCAR::CameraDevice::FOCUS_MODE_INFINITY;
            break;
            
        case 3:
            qcarFocusMode = QCAR::CameraDevice::FOCUS_MODE_MACRO;
            break;
    
        default:
            return JNI_FALSE;
    }
    
    return QCAR::CameraDevice::getInstance().setFocusMode(qcarFocusMode) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_com_ar4android_vuforiaJME_VuforiaJME_initTracking(
                        JNIEnv* env, jobject obj, jint width, jint height)
{
    LOGI("Java_com_ar4android_vuforiaJME_VuforiaJME_initTracking");

    // Update screen dimensions
    screenWidth = width;
    screenHeight = height;

    // Reconfigure the video background
    configureVideoBackground();
}


#ifdef __cplusplus
}
#endif
