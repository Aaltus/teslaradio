package com.ar4android.vuforiaJME;

import android.app.Activity;
import com.qualcomm.QCAR.QCAR;

/**
 * Created by jimbojd72 on 10/21/14.
 */
public class VuforiaCallerNative implements VuforiaCaller, ICameraUpdater {

    VuforiaCallback vuforiaCallback;
    ICameraUpdater iCameraUpdater;

    public VuforiaCallerNative(VuforiaCallback vuforiaCallback)
    {
        this.vuforiaCallback = vuforiaCallback;
    }

    /** Native tracker initialization and deinitialization. */
    public native int initTracker();
    public native void deinitTracker();

    /** Native functions to load and destroy tracking data. */
    public native int loadTrackerData();
    public native int destroyTrackerData();

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
    public void setICameraUpdate(ICameraUpdater iCameraUpdate) {
        iCameraUpdater = iCameraUpdate;
    }

    /** Native function to update the renderer. */
    public native void updateTracking();

    /** Native function for initializing the renderer. */
    public native void initTracking(int width, int height);

    @Override
    public void setRGB565CameraImage(byte[] buffer, int width, int height) {

        if(vuforiaCallback != null)
        {
            vuforiaCallback.setRGB565CameraImage(buffer,width,height);
        }

    }


    @Override
    public int QCARinit() {
       return QCAR.init();
    }

    @Override
    public void QCARdeinit() {
        QCAR.deinit();
    }

    @Override
    public void QCARonPause() {
        QCAR.onPause();
    }

    @Override
    public void QCARonResume() {
        QCAR.onResume();

    }

    @Override
    public boolean QCARisInitialized() {
        return QCAR.isInitialized();

    }

    @Override
    public void QCARsetInitParameters(Activity activity, int mQCARFlags) {
        QCAR.setInitParameters(activity,mQCARFlags);
    }

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

}
