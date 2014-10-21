package com.ar4android.vuforiaJME;

/**
 * Created by jimbojd72 on 10/21/14.
 */
public class VuforiaCallerNative implements VuforiaCaller {

    VuforiaCallback vuforiaCallback;

    public VuforiaCallerNative(VuforiaCallback vuforiaCallback)
    {
        this.vuforiaCallback = vuforiaCallback;
    }

    /** Native tracker initialization and deinitialization. */
    public native int initTracker();
    public native void deinitTracker();

    /** Native functions to load and destroy tracking data. */
    public native int loadTrackerData();
    public native void destroyTrackerData();

    /** Native sample initialization. */
    public native void onQCARInitializedNative(int loggerLvl);

    /** Native methods for starting and stopping the camera. */
    public native void startCamera();
    public native void stopCamera();

    /** Native method for setting / updating the projection matrix
     * for AR content rendering */
    public native void setProjectionMatrix();

    /** Native function to initialize the application. */
    public native void initApplicationNative(int width, int height);

    /** Native function to deinitialize the application.*/
    public native void deinitApplicationNative();

    /** Tells native code whether we are in portait or landscape mode */
    public native void setActivityPortraitMode(boolean isPortrait);

    /** Tells native code to switch dataset as soon as possible*/
    public native void switchDatasetAsap();

    public native boolean autofocus();
    public native boolean setFocusMode(int mode);

    /** Activates the Flash */
    public native boolean activateFlash(boolean flash);

    @Override
    public void setRGB565CameraImage(byte[] buffer, int width, int height) {

        if(vuforiaCallback != null)
        {
            vuforiaCallback.setRGB565CameraImage(buffer,width,height);
        }

    }


}
