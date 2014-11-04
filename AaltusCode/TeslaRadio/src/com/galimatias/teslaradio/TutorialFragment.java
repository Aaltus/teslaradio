package com.galimatias.teslaradio;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

/**
 * Created by jimbojd72 on 11/3/2014.
 */
public class TutorialFragment extends Fragment implements View.OnClickListener {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View myView = inflater.inflate(R.layout.tutorial_layout, null, false);

        View bubbleView = (View)myView.findViewById(R.id.bubble_root_view);
        bubbleView.setVisibility(View.GONE);

        View characterButton     = myView.findViewById(R.id.character_tutorial_button);
        Animation shakeAnim = AnimationUtils.loadAnimation(this.getActivity(), R.anim.shake);
        characterButton.startAnimation(shakeAnim);

        View viewFlipper = myView.findViewById(R.id.view_flipper);

        characterButton.setOnClickListener(this);
        viewFlipper.setOnClickListener(this);

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
                toggleBubbleViewVisibility();
                break;
            case R.id.view_flipper:
                ViewFlipper viewFlipper = (ViewFlipper)getView().findViewById(R.id.view_flipper);
                if(viewFlipper.getDisplayedChild() == viewFlipper.getChildCount()-1)
                {
                    toggleBubbleViewVisibility();
                }
                viewFlipper.showNext();
                break;

        }
    }

    private void toggleBubbleViewVisibility() {
        View viewFlipper = (View)getView().findViewById(R.id.bubble_root_view);
        if(viewFlipper.getVisibility() == View.GONE){
            viewFlipper.setVisibility(View.VISIBLE);
            setShakeAnimation(false);
        }
        else{
            viewFlipper.setVisibility(View.GONE);
            setShakeAnimation(true);
        }
    }
}