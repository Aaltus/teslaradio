package com.galimatias.teslaradio;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * Created by jimbojd72 on 11/3/2014.
 */
public class TutorialFragment extends Fragment implements View.OnClickListener {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View myView = inflater.inflate(R.layout.tutorial_layout, null, false);

        TextView textView = (TextView)myView.findViewById(R.id.tutorial_speech_textview);

        View characterButton     = myView.findViewById(R.id.character_tutorial_button);
        Animation shakeAnim = AnimationUtils.loadAnimation(this.getActivity(), R.anim.shake);
        characterButton.startAnimation(shakeAnim);

        characterButton.setOnClickListener(this);

        return myView;
    }

    private void setShakeAnimation(boolean enabled) {
        View characterButton = getView().findViewById(R.id.character_tutorial_button);
        if(enabled) {
            Animation shakeAnim = AnimationUtils.loadAnimation(this.getActivity(), R.anim.shake);
            characterButton.startAnimation(shakeAnim);
        }
        else{
            characterButton.clearAnimation();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.character_tutorial_button:

                toggleTextViewVisibility();
                break;

        }
    }

    private void toggleTextViewVisibility() {
        TextView textView = (TextView)getView().findViewById(R.id.tutorial_speech_textview);
        if(textView.getVisibility() == View.GONE){
            textView.setVisibility(View.VISIBLE);
            setShakeAnimation(false);
        }
        else{
            textView.setVisibility(View.GONE);
            setShakeAnimation(true);
        }
    }
}