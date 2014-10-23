package com.ar4android.vuforiaJME;

/**
 * Created by jimbojd72 on 10/22/14.
 */
public interface ICameraUpdater {

    public void setCameraPerspectiveNative(float fovY,float aspectRatio) ;

    public void setCameraViewportNative(float viewport_w,float viewport_h,float size_x,float size_y) ;

    public void setCameraPoseNative(float cam_x,float cam_y,float cam_z, int id);

    public void setCameraOrientationNative(float cam_right_x,float cam_right_y,float cam_right_z,
                                           float cam_up_x,float cam_up_y,float cam_up_z,float cam_dir_x,float cam_dir_y,float cam_dir_z, int id) ;
    public void setTrackableVisibleNative(int id, int isTrackableVisible);
}
