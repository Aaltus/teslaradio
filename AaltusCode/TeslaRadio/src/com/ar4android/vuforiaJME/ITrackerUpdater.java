package com.ar4android.vuforiaJME;

/**
 * Created by jimbojd72 on 10/22/14.
 */
public interface ITrackerUpdater {

    /** Native function to update the renderer. */
    public void updateTracking();

    /** Native function for initializing the renderer. */
    public void initTracking(int width, int height);
}
