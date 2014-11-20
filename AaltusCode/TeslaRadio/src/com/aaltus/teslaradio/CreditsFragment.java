package com.aaltus.teslaradio;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;

/**
 * Created by Batcave on 14-11-19.
 */
public class CreditsFragment extends Fragment {

    private static final String TAG = CreditsFragment.class.getSimpleName();

    public CreditsFragment() {
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

        View view = inflater.inflate(R.layout.credits_layout, container,false);

        return view;
    }

}
