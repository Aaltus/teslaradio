package com.aaltus.teslaradio;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.*;
import android.widget.ImageButton;
import com.aaltus.teslaradio.subject.SubjectContent;
import com.aaltus.teslaradio.world.Scenarios.StartScreenController;
import com.utils.LanguageLocaleChanger;

import java.util.ArrayList;
import java.util.List;
// ...


/**
    Simple custom dialog fragment to show language options
 */
public class StartScreenDialogFragment extends DialogFragment implements View.OnClickListener {


    private static final String TAG = StartScreenDialogFragment.class.getSimpleName();

    private boolean dialogClosable = true;
    private StartScreenController  startScreenController;
    public void setStartScreenController(StartScreenController startScreenController) {
        this.startScreenController = startScreenController;
    }

    /*public void setDialogClosable(boolean dialogClosable) {
        this.dialogClosable = dialogClosable;
    }*/

    public StartScreenDialogFragment() {
        // Empty constructor required for DialogFragment

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.MyDialog);

        /*if (getArguments() != null && getArguments().containsKey(BUNDLE_START_SCREEN_CONTROLLER_TAG)) {
            setStartScreenController((StartScreenController)getArguments().get(BUNDLE_START_SCREEN_CONTROLLER_TAG));
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView");

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        View rootView = inflater.inflate(R.layout.start_screen, container);

        List<View> listButtons= new ArrayList<View>();
        listButtons.add(rootView.findViewById(R.id.start_screen_start_button));
        listButtons.add(rootView.findViewById(R.id.start_screen_tutorial_button));
        listButtons.add(rootView.findViewById(R.id.start_screen_credits_button));
        //listButtons.add(rootView.findViewById(R.id.start_screen_exit_button));

        for (View button : listButtons){
            button.setOnClickListener(this);
        }

        return rootView;
    }

    /**
     * Reload the activity with the new language when clicked
     * @param v
     */
    public void onClick(View v){

        int id = v.getId();

        switch (id){

            case R.id.start_screen_start_button:
                if(startScreenController != null){
                    startScreenController.onStartButtonClick();
                }
                //this.dismiss();
                break;
            case R.id.start_screen_tutorial_button:
                if(startScreenController != null){
                    startScreenController.onTutorialButtonClick();
                }
                //this.dismiss();
                break;
            case R.id.start_screen_credits_button:
                if(startScreenController != null){
                    startScreenController.onCreditsButtonClick();
                }
                //this.dismiss();
                break;
            /*case R.id.start_screen_exit_button:
                if(startScreenController != null){
                    startScreenController.onEndGameClick();
                }
                //this.dismiss();
                break;
                */

        }
    }



}
