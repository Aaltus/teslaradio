package com;


import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.galimatias.teslaradio.DemoCameraFragment;

/**
 * Created by jimbojd72 on 3/11/14.
 */

public class CreateCameraPreviewAsyncTask extends AsyncTask<Object, Void, Void> {

    protected Void doInBackground(Object...params) {

        int xmlIdToPutCameraFragment = (Integer) params[0];
        FragmentManager fm = (FragmentManager) params[1];

        FragmentTransaction ft = fm.beginTransaction();
        Fragment newFragment = new DemoCameraFragment();

        ft.add(xmlIdToPutCameraFragment, newFragment).commit();
        return null;
    }

}
