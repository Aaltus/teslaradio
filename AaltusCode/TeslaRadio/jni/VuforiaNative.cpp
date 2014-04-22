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
#include <android/log.h>

//#define LOG_TAG "VuforiaNative"
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG,__VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , LOG_TAG,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO   , LOG_TAG,__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN   , LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , LOG_TAG,__VA_ARGS__)


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


#include "MathUtils.h"

#ifdef __cplusplus
extern "C"
{
#endif

// Screen dimensions:
unsigned int screenWidth        = 0;
unsigned int screenHeight       = 0;

// Indicates whether screen is in portrait (true) or landscape (false) mode
bool isActivityInPortraitMode   = false;

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

                // Added the release of the byte array buffer before deleting the reference. Seen this on a website...
                env->ReleaseByteArrayElements(pixelArray, (jbyte*)pixels, JNI_ABORT);
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
    
    // Initialize the image tracker:
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    QCAR::Tracker* tracker = trackerManager.initTracker(QCAR::ImageTracker::getClassType());
    if (tracker == NULL)
    {
        LOGE("Failed to initialize ImageTracker.");
        return 0;
    }

    LOGI("Successfully initialized ImageTracker.");
    return 1;
}


JNIEXPORT void JNICALL
Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_deinitTracker(JNIEnv *, jobject)
{
    LOGI("Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_deinitTracker");

    // Deinit the image tracker:
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    trackerManager.deinitTracker(QCAR::ImageTracker::getClassType());
}


JNIEXPORT int JNICALL
Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_loadTrackerData(JNIEnv *, jobject)
{
    LOGI("Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_loadTrackerData");
    
    // Get the image tracker:
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    QCAR::ImageTracker* imageTracker = static_cast<QCAR::ImageTracker*>(
                    trackerManager.getTracker(QCAR::ImageTracker::getClassType()));
    if (imageTracker == NULL)
    {
        LOGE("Failed to load tracking data set because the ImageTracker has not"
            " been initialized.");
        return 0;
    }

    //TODO Fix this to be generic
    // Create the data sets:
    dataSetStonesAndChips = imageTracker->createDataSet();
    dataSetLena           = imageTracker->createDataSet();

    if (dataSetStonesAndChips == 0 || dataSetLena == 0)
    {
        LOGE("Failed to create a new tracking data.");
        return 0;
    }

    // Load the data sets:
    if (!dataSetStonesAndChips->load("VuforiaJME.xml", QCAR::DataSet::STORAGE_APPRESOURCE) ||
            !dataSetLena->load("JME2.xml", QCAR::DataSet::STORAGE_APPRESOURCE) )
    {
        LOGE("Failed to load data set.");
        return 0;
    }

    // Activate the data set:
    if (!imageTracker->activateDataSet(dataSetStonesAndChips) ||
            !imageTracker->activateDataSet(dataSetLena))
    {
        LOGE("Failed to activate data set.");
        return 0;
    }

    LOGI("Successfully loaded and activated data set.");
    return 1;
}


JNIEXPORT int JNICALL
Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_destroyTrackerData(JNIEnv *, jobject)
{
    LOGI("Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_destroyTrackerData");

    // Get the image tracker:
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    QCAR::ImageTracker* imageTracker = static_cast<QCAR::ImageTracker*>(
        trackerManager.getTracker(QCAR::ImageTracker::getClassType()));
    if (imageTracker == NULL)
    {
        LOGE("Failed to destroy the tracking data set because the ImageTracker has not"
            " been initialized.");
        return 0;
    }
    //TODO: Fix this to be generic
    if (dataSetStonesAndChips != 0)
    {
        if (imageTracker->getActiveDataSet() == dataSetStonesAndChips &&
            !imageTracker->deactivateDataSet(dataSetStonesAndChips))
        {
            LOGE("Failed to destroy the tracking data set StonesAndChips because the data set "
                "could not be deactivated.");
            return 0;
        }

        if (!imageTracker->destroyDataSet(dataSetStonesAndChips))
        {
            LOGE("Failed to destroy the tracking data set StonesAndChips.");
            return 0;
        }

        LOGI("Successfully destroyed the data set StonesAndChips.");
        dataSetStonesAndChips = 0;
    }

    if (dataSetLena != 0)
        {
            if (imageTracker->getActiveDataSet() == dataSetLena &&
                !imageTracker->deactivateDataSet(dataSetLena))
            {
                LOG("Failed to destroy the tracking data set Lena because the data set "
                    "could not be deactivated.");
                return 0;
            }

            if (!imageTracker->destroyDataSet(dataSetLena))
            {
                LOG("Failed to destroy the tracking data set Lena.");
                return 0;
            }

            LOG("Successfully destroyed the data set Lena.");
            dataSetLena = 0;
        }

    return 1;
}


JNIEXPORT void JNICALL
Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_onQCARInitializedNative(JNIEnv *, jobject)
{
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
    //LOG("Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_GLRenderer_renderFrame");

    // Get the state from QCAR and mark the beginning of a rendering section
    QCAR::State state = QCAR::Renderer::getInstance().begin();
    

    // Explicitly render the Video Background
  //  QCAR::Renderer::getInstance().drawVideoBackground();

  //  if(QCAR::Renderer::getInstance().getVideoBackgroundConfig().mReflection == QCAR::VIDEO_BACKGROUND_REFLECTION_ON)


   jclass activityClass = env->GetObjectClass(obj);
   jmethodID attachScenarios = env->GetMethodID(activityClass,"attachScenarios", "(Z)V");
   bool seeTrackables = false;

    //Jonathan Desmarais: Check if we have a trackable result
    if (state.getNumTrackableResults() > 0){

        seeTrackables = true;

        // Did we find any trackables this frame?
        for(int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++)
        {


            // Get the trackable:
            const QCAR::TrackableResult* result = state.getTrackableResult(tIdx);

            //Stuplid code to simply log to trackable name
            const QCAR::Trackable& trackable = result->getTrackable();
            const char* trackableNameChar = trackable.getName();
            const char* loggingPrefix = "UpdateTracking: Find trackable: ";
            char logTrackableName[75];
            strcpy(logTrackableName,loggingPrefix);
            strcat(logTrackableName,trackableNameChar);
            const char * trackableToPrint = (const char *)logTrackableName;
            LOGD(trackableToPrint);


            QCAR::Matrix44F modelViewMatrix =
                QCAR::Tool::convertPose2GLMatrix(result->getPose());

            //get the camera transformation
            QCAR::Matrix44F inverseMV = MathUtil::Matrix44FInverse(modelViewMatrix);
            //QCAR::Matrix44F invTranspMV = modelViewMatrix;
            QCAR::Matrix44F invTranspMV = MathUtil::Matrix44FTranspose(inverseMV);

            //get position
            float cam_x = invTranspMV.data[12];
            float cam_y = invTranspMV.data[13];
            float cam_z = invTranspMV.data[14];

            //get rotation
            float cam_right_x = invTranspMV.data[0];
            float cam_right_y = invTranspMV.data[1];
            float cam_right_z = invTranspMV.data[2];
            float cam_up_x = invTranspMV.data[4];
            float cam_up_y = invTranspMV.data[5];
            float cam_up_z = invTranspMV.data[6];
            float cam_dir_x = invTranspMV.data[8];
            float cam_dir_y = invTranspMV.data[9];
            float cam_dir_z = invTranspMV.data[10];

            //get perspective transformation
            float nearPlane = 1.0f;
            float farPlane = 1000.0f;
            const QCAR::CameraCalibration& cameraCalibration =
                                        QCAR::CameraDevice::getInstance().getCameraCalibration();

            QCAR::VideoBackgroundConfig config = QCAR::Renderer::getInstance().getVideoBackgroundConfig();

            float viewportWidth = config.mSize.data[0];
            float viewportHeight = config.mSize.data[1];

            QCAR::Vec2F size = cameraCalibration.getSize();
            QCAR::Vec2F focalLength = cameraCalibration.getFocalLength();
            float fovRadians = 2 * atan(0.5f * (size.data[1] / focalLength.data[1]));
            float fovDegrees = fovRadians * 180.0f / M_PI;
            float aspectRatio=(size.data[0]/size.data[1]);

            //adjust for screen vs camera size distorsion
            float viewportDistort=1.0;

            if (viewportWidth != screenWidth)
            {
                LOGW("updateTracking viewportWidth != screenWidth");
                viewportDistort = viewportWidth / (float) screenWidth;
                fovDegrees=fovDegrees*viewportDistort;
                aspectRatio=aspectRatio/viewportDistort;

            }

            if (viewportHeight != screenHeight)
            {
                LOGW("updateTracking viewportHeight != screenHeight");
                viewportDistort = viewportHeight / (float) screenHeight;
                fovDegrees=fovDegrees/viewportDistort;
                aspectRatio=aspectRatio*viewportDistort;
            }

            //JNIEnv *env;
            //jvm->AttachCurrentThread((void **)&env, NULL);





            jmethodID setCameraPerspectiveMethod = env->GetMethodID(activityClass,"setCameraPerspectiveNative", "(FF)V");
            env->CallVoidMethod(obj,setCameraPerspectiveMethod,fovDegrees,aspectRatio);

            // jclass activityClass = env->GetObjectClass(obj);
            jmethodID setCameraViewportMethod = env->GetMethodID(activityClass,"setCameraViewportNative", "(FFFF)V");
            env->CallVoidMethod(obj,setCameraViewportMethod,viewportWidth,viewportHeight,cameraCalibration.getSize().data[0],cameraCalibration.getSize().data[1]);

            //JNIEnv *env;
            //jvm->AttachCurrentThread((void **)&env, NULL);

           // jclass activityClass = env->GetObjectClass(obj);
            jmethodID setCameraPoseMethod = env->GetMethodID(activityClass,"setCameraPoseNative", "(FFF)V");
            env->CallVoidMethod(obj,setCameraPoseMethod,cam_x,cam_y,cam_z);

            //jclass activityClass = env->GetObjectClass(obj);
            jmethodID setCameraOrientationMethod = env->GetMethodID(activityClass,"setCameraOrientationNative", "(FFFFFFFFF)V");
            env->CallVoidMethod(obj,setCameraOrientationMethod,cam_right_x,cam_right_y,cam_right_z,
                    cam_up_x,cam_up_y,cam_up_z,cam_dir_x,cam_dir_y,cam_dir_z);

           // jvm->DetachCurrentThread();

           // LOG("Got tracking...");

        }
    }

    else
    {
        seeTrackables = false;
    }
    env->CallVoidMethod(obj,attachScenarios,seeTrackables);

    QCAR::Renderer::getInstance().end();
}


void configureVideoBackground()
{
    LOGI("configureVideoBackground");

    // Get the default video mode:
    QCAR::CameraDevice& cameraDevice = QCAR::CameraDevice::getInstance();
    QCAR::VideoMode videoMode = cameraDevice.getVideoMode(QCAR::CameraDevice::MODE_DEFAULT);

    //Jonathan Desmarais I try to use the optimize speed video config but it was successful
    //QCAR::VideoMode videoMode = cameraDevice.getVideoMode(QCAR::CameraDevice::MODE_OPTIMIZE_SPEED);


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

    LOGI("Configure Video Background : Video (%d,%d), Screen (%d,%d), mSize (%d,%d)", videoMode.mWidth, videoMode.mHeight, screenWidth, screenHeight, config.mSize.data[0], config.mSize.data[1]);

    // Set the config:
    QCAR::Renderer::getInstance().setVideoBackgroundConfig(config);
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
Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_startCamera(JNIEnv *,
                                                                         jobject)
{
    LOGI("Java_com_ar4android_vuforiaJME_VuforiaJMEActivity_startCamera");
    
    // Select the camera to open, set this to QCAR::CameraDevice::CAMERA_FRONT 
    // to activate the front camera instead.
    QCAR::CameraDevice::CAMERA camera = QCAR::CameraDevice::CAMERA_DEFAULT;

    // Initialize the camera:
    if (!QCAR::CameraDevice::getInstance().init(camera))
        return;

    // Configure the video background
    configureVideoBackground();

    // Select the default mode:
    if (!QCAR::CameraDevice::getInstance().selectVideoMode(
                                QCAR::CameraDevice::MODE_DEFAULT))
        return;

  //  QCAR::VideoMode videoMode = cameraDevice.
   //                      getVideoMode(QCAR::CameraDevice::MODE_OPTIMIZE_QUALITY);

    // Start the camera:
    if (!QCAR::CameraDevice::getInstance().start())
        return;

    QCAR::setFrameFormat(QCAR::RGB565, true);
    // Uncomment to enable flash
    //if(QCAR::CameraDevice::getInstance().setFlashTorchMode(true))
    //	LOG("IMAGE TARGETS : enabled torch");

    // Uncomment to enable infinity focus mode, or any other supported focus mode
    // See CameraDevice.h for supported focus modes
    //if(QCAR::CameraDevice::getInstance().setFocusMode(QCAR::CameraDevice::FOCUS_MODE_INFINITY))
    //	LOG("IMAGE TARGETS : enabled infinity focus");

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
    const QCAR::CameraCalibration& cameraCalibration =
                                QCAR::CameraDevice::getInstance().getCameraCalibration();
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
