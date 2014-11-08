package com.galimatias.teslaradio;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;

/**
 * Created by jimbojd72 on 11/8/2014.
 */
public class MasterTutorialFragment extends Fragment {


    private static final String TAG = MasterTutorialFragment.class.getSimpleName();

    public MasterTutorialFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView");


        View view = inflater.inflate(R.layout.master_tutorial, container,false);


        return view;
    }

    /**
     * Reload the activity with the new language when clicked
     * @param v
     */
    private void onClick(View v){

        int id = v.getId();

        switch (id){



        }
    }

}
