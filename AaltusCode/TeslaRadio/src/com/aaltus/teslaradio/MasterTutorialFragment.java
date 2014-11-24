package com.aaltus.teslaradio;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;

/**
 * Created by jimbojd72 on 11/8/2014.
 */
public class MasterTutorialFragment extends DialogFragment implements View.OnClickListener {


    private static final String TAG = MasterTutorialFragment.class.getSimpleName();

    public interface OnMasterTutorialListener{

        public void onContinueEvent();
        public void onExitEvent();

    }

    public OnMasterTutorialListener onMasterTutorialListener;
    public void setOnMasterTutorialListener(OnMasterTutorialListener onMasterTutorialListener){
        this.onMasterTutorialListener = onMasterTutorialListener;
    }

    public MasterTutorialFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.MyDialog);

        Log.i(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView");

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View view = inflater.inflate(R.layout.master_tutorial, container,false);

        View cancelButton     = view.findViewById(R.id.master_tutorial_cancel_button);
        cancelButton.setOnClickListener(this);
        View continueButton     = view.findViewById(R.id.master_tutorial_ok_button);
        continueButton.setOnClickListener(this);


        return view;
    }

    /**
     * Reload the activity with the new language when clicked
     * @param v
     */
    public void onClick(View v){

        int id = v.getId();

        switch (id){

            case R.id.master_tutorial_cancel_button:
                if(this.onMasterTutorialListener != null){
                    this.onMasterTutorialListener.onExitEvent();
                }
                break;
            case R.id.master_tutorial_ok_button:
                if(this.onMasterTutorialListener != null){
                    this.onMasterTutorialListener.onContinueEvent();
                }
                break;

        }
    }


}
