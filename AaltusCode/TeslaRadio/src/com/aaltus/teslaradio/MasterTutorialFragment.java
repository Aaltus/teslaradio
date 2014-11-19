package com.aaltus.teslaradio;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;

/**
 * Created by jimbojd72 on 11/8/2014.
 */
public class MasterTutorialFragment extends Fragment implements View.OnClickListener {


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

        Log.i(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView");


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
