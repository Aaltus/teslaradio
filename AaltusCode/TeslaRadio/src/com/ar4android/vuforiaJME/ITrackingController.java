package com.ar4android.vuforiaJME;

/**
 * Created by jimbojd72 on 11/8/2014.
 */
public interface ITrackingController {
    void pauseTracking();

    void startTracking();

    ITrackerUpdater getITrackerUpdater();

    void setICameraUpdater(ICameraUpdater iCameraUpdater);
}
