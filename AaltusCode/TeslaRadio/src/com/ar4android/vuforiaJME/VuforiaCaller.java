package com.ar4android.vuforiaJME;

/**
 * Created by jimbojd72 on 10/21/14.
 */
public interface VuforiaCaller extends VuforiaCallback, QCARInterface, ITrackerUpdater {

    /** Native tracker initialization and deinitialization. */
    public int initTracker();
    public void deinitTracker();

    /** Native functions to load and destroy tracking data. */
    public int loadTrackerData();
    public int destroyTrackerData();

    /** Native sample initialization. */
    public void onQCARInitializedNative(int loggerLvl);

    /** Native methods for starting and stopping the camera. */
    public void startCamera();
    public void stopCamera();

    /** Native method for setting / updating the projection matrix
     * for AR content rendering */
    public void setProjectionMatrix();

    /** Native function to initialize the application. */
    public void initApplicationNative(int width, int height);

    /** Native function to deinitialize the application.*/
    public void deinitApplicationNative();

    /** Tells native code whether we are in portait or landscape mode */
    public void setActivityPortraitMode(boolean isPortrait);

    /** Tells native code to switch dataset as soon as possible*/
    public void switchDatasetAsap();

    public boolean autofocus();
    public boolean setFocusMode(int mode);

    /** Activates the Flash */
    public boolean activateFlash(boolean flash);

}
