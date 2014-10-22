package com.ar4android.vuforiaJME;

import android.app.Activity;

/**
 * Created by jimbojd72 on 10/21/14.
 */
public interface QCARInterface {

    public int QCARinit();

    public void QCARdeinit();

    public void QCARonPause();

    public void QCARonResume();

    public boolean QCARisInitialized();

    public void QCARsetInitParameters(Activity activity, int mQCARFlags);

}
