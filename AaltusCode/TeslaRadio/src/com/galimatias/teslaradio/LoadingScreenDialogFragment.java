package com.galimatias.teslaradio;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.*;
// ...


/**
    Simple custom dialog fragment to show language options
 */
public class LoadingScreenDialogFragment extends DialogFragment {


    private static final String TAG = LoadingScreenDialogFragment.class.getSimpleName();
    private boolean dialogClosable = true;
    public void setDialogClosable(boolean dialogClosable) {
        this.dialogClosable = dialogClosable;
    }

    public LoadingScreenDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.MyDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView");

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        View view = inflater.inflate(R.layout.language_dialog_layout, container);


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
