package com.galimatias.teslaradio;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.*;
import android.widget.ImageButton;
import com.utils.LanguageLocaleChanger;
// ...


/**
    Simple custom dialog fragment to show language options
 */
public class LanguageDialogFragment extends DialogFragment {


    private static final String TAG = "LanguageDialogFragment";
    private boolean dialogClosable = true;
    public void setDialogClosable(boolean dialogClosable) {
        this.dialogClosable = dialogClosable;
    }

    public LanguageDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");
        //setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog);
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
        //mEditText = (EditText) view.findViewById(R.id.txt_your_name);
        //getDialog().setTitle("Hello");


        // Watch for button clicks.
        ImageButton enButton = (ImageButton)view.findViewById(R.id.camera_toggle_en_button);
        enButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LanguageDialogFragment.this.onClick(v);
            }
        });
        ImageButton frButton = (ImageButton)view.findViewById(R.id.camera_toggle_fr_button);
        frButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LanguageDialogFragment.this.onClick(v);
            }
        });

        //SPanish and german are not implemented yet, so we don,t want them to be enabled.
        ImageButton deButton = (ImageButton)view.findViewById(R.id.camera_toggle_de_button);
        ImageButton esButton = (ImageButton)view.findViewById(R.id.camera_toggle_es_button);
        //Use deprecated function but is available since API 1
        deButton.getBackground().setAlpha(50);
        esButton.getBackground().setAlpha(50);

        /*

        deButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LanguageDialogFragment.this.onClick(v);
            }
        });
        esButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LanguageDialogFragment.this.onClick(v);
            }
        });
        */
        ImageButton cancelButton = (ImageButton) view.findViewById(R.id.camera_toggle_cancel_button);
        if(this.dialogClosable){

            cancelButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    LanguageDialogFragment.this.onClick(v);
                }
            });

        }
        else {
            ((ViewGroup)cancelButton.getParent()).removeView(cancelButton);
        }

        return view;
    }

    /**
     * Reload the activity with the new language when clicked
     * @param v
     */
    private void onClick(View v){

        int id = v.getId();

        switch (id){

            case R.id.camera_toggle_en_button:
                getDialog().dismiss();
                LanguageLocaleChanger.reloadAppWithNewLanguage(getActivity(), "en");

                break;


            case R.id.camera_toggle_fr_button:
                getDialog().dismiss();
                LanguageLocaleChanger.reloadAppWithNewLanguage(getActivity(), "fr");

                break;

            case R.id.camera_toggle_de_button:
                getDialog().dismiss();
                LanguageLocaleChanger.reloadAppWithNewLanguage(getActivity(), "de");

                break;

            case R.id.camera_toggle_es_button:
                getDialog().dismiss();
                LanguageLocaleChanger.reloadAppWithNewLanguage(getActivity(), "es");

                break;

            case R.id.camera_toggle_cancel_button:
                getDialog().dismiss();
                break;


        }
    }


}
