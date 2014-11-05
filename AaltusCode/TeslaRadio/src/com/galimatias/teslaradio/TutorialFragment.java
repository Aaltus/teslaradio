package com.galimatias.teslaradio;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ViewFlipper;
import com.ar4android.vuforiaJME.ITutorialSwitcher;
import com.galimatias.teslaradio.subject.ScenarioEnum;
import com.galimatias.teslaradio.subject.SubjectContent;
import com.utils.AppLogger;

/**
 * Created by jimbojd72 on 11/3/2014.
 */
public class TutorialFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = TutorialFragment.class.getSimpleName();

    private ITutorialSwitcher tutorialSwitcher;
    public void setTutorialSwitcher(ITutorialSwitcher tutorialSwitcher) {
        this.tutorialSwitcher = tutorialSwitcher;
    }

    private void setTutorialMenuCallback(int index){

        if(tutorialSwitcher != null){
            tutorialSwitcher.setTutorialIndex(index);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View myView = inflater.inflate(R.layout.tutorial_layout, null, false);

        View bubbleView = myView.findViewById(R.id.bubble_root_view);
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
                ViewFlipper viewFlipper = getViewFlipper();
                if(viewFlipper.getDisplayedChild() == viewFlipper.getChildCount()-1)
                {
                    toggleBubbleViewVisibility();
                }
                else {
                    viewFlipper.showNext();
                    setTutorialMenuCallback(viewFlipper.getDisplayedChild());
                }


                break;

        }
    }

    private ViewFlipper getViewFlipper(){
        return (ViewFlipper)getView().findViewById(R.id.view_flipper);
    }

    private void toggleBubbleViewVisibility() {
        View view = (View)getView().findViewById(R.id.bubble_root_view);
        ViewFlipper viewFlipper = getViewFlipper();
        if(view.getVisibility() == View.GONE){
            view.setVisibility(View.VISIBLE);
            if(viewFlipper.getChildCount() > 0){
                int index = 0;
                viewFlipper.setDisplayedChild(index);
                this.setTutorialMenuCallback(index);
            }
            setShakeAnimation(false);
        }
        else{
            view.setVisibility(View.GONE);
            setTutorialMenuCallback(-1);
            setShakeAnimation(true);
        }
    }

    public void setBubbleCategory(ScenarioEnum scenarioEnum){

        //AppLogger.getInstance().d(TAG, "setBubbleCategory with scenarioEnum: " + scenarioEnum);
        ViewFlipper viewFlipper = getViewFlipper();
        viewFlipper.removeAllViews();
        int[] listXmlString =  SubjectContent.ENUM_MAP.get(scenarioEnum).getListStringIdTutorial();
        //AppLogger.getInstance().d(TAG, "setBubbleCategory with scenarioEnum: " + scenarioEnum);
        if(listXmlString != null) {
            for (int i = 0; i < listXmlString.length; i++) {
                //AppLogger.getInstance().d(TAG, "Removing view :" + i);
                TextView textView = new TextView(this.getActivity());
                textView.setText(this.getActivity().getString(listXmlString[i]));
                viewFlipper.addView(textView);
            }
        }
    }
}