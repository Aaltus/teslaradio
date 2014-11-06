package com.galimatias.teslaradio;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import com.ar4android.vuforiaJME.ITutorialSwitcher;
import com.galimatias.teslaradio.subject.ScenarioEnum;
import com.galimatias.teslaradio.subject.SubjectContent;

/**
 * Created by jimbojd72 on 11/3/2014.
 */
public class TutorialFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = TutorialFragment.class.getSimpleName();

    private Animation shakeAnim;
    private AnimationDrawable speakAnim;

    private enum CharacterAction{

        CALM,
        SHAKING,
        SPEAKING

    }

    private void setCharacterAction(CharacterAction characterAction){

        //Clean up current application
        getCharacterView().clearAnimation();


        //setAnimation
        switch (characterAction){

            case CALM:
                setCalmAnimation();
                break;

            case SHAKING:
                setShakeAnimation();
                break;

            case SPEAKING:
                setSpeakAnimation();
                break;

        }

    }

    private ITutorialSwitcher tutorialSwitcher;
    private boolean wantToSpeak = true;

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
        shakeAnim = AnimationUtils.loadAnimation(this.getActivity(), R.anim.shake);
        characterButton.startAnimation(shakeAnim);

        //speakAnim = AnimationUtils.loadAnimation(this.getActivity(),R.drawable.tesla_speak_anim);

        View viewFlipper = myView.findViewById(R.id.view_flipper);

        characterButton.setOnClickListener(this);
        viewFlipper.setOnClickListener(this);

        return myView;
    }

    private ImageView getCharacterView() {
        return (ImageView)getView().findViewById(R.id.character_tutorial_button);
    }

    private void setShakeAnimation() {
        ImageView characterButton = getCharacterView();
        //Animation shakeAnim = AnimationUtils.loadAnimation(this.getActivity(), R.anim.shake);
        characterButton.startAnimation(shakeAnim);
    }

    private void setSpeakAnimation() {
        ImageView characterButton = getCharacterView();
        characterButton.setBackgroundResource(R.drawable.tesla_speak_anim);
        AnimationDrawable animation = (AnimationDrawable) characterButton.getBackground();
        animation.start();
    }

    private void setCalmAnimation() {
        ImageView characterButton = getCharacterView();
        characterButton.setBackgroundResource(R.drawable.tesla);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.character_tutorial_button:
                setBubbleViewVisibility(!(getView().findViewById(R.id.bubble_root_view).getVisibility() == View.VISIBLE));
                break;
            case R.id.view_flipper:
                ViewFlipper viewFlipper = getViewFlipper();
                if(viewFlipper.getDisplayedChild() == viewFlipper.getChildCount()-1)
                {
                    setBubbleViewVisibility(false);
                }
                else
                {
                    viewFlipper.showNext();
                    setTutorialMenuCallback(viewFlipper.getDisplayedChild());
                }
                break;

        }
    }

    private ViewFlipper getViewFlipper(){
        return (ViewFlipper)getView().findViewById(R.id.view_flipper);
    }

    private void setBubbleViewVisibility(boolean showBubble) {
        View view = (View)getView().findViewById(R.id.bubble_root_view);
        ViewFlipper viewFlipper = getViewFlipper();
        if(view.getVisibility() == View.GONE && showBubble){
            view.setVisibility(View.VISIBLE);
            if(viewFlipper.getChildCount() > 0){
                int index = 0;
                viewFlipper.setDisplayedChild(index);
                this.setTutorialMenuCallback(index);
            }
            setCharacterAction(CharacterAction.SPEAKING);
        }
        else if (view.getVisibility() == View.VISIBLE && !showBubble){
            view.setVisibility(View.GONE);
            setTutorialMenuCallback(-1);
            setCharacterAction(CharacterAction.CALM);

        }
    }

    public void setBubbleCategory(ScenarioEnum scenarioEnum){

        if(scenarioEnum == null)
        {
            setBubbleViewVisibility(false);
        }
        else
        {
            setCharacterAction(CharacterAction.SHAKING);
            ViewFlipper viewFlipper = getViewFlipper();
            viewFlipper.removeAllViews();
            int[] listXmlString = SubjectContent.ENUM_MAP.get(scenarioEnum).getListStringIdTutorial();
            if (listXmlString != null) {
                for (int i = 0; i < listXmlString.length; i++) {
                    TextView textView = new TextView(this.getActivity());
                    textView.setText(this.getActivity().getText(listXmlString[i]));
                    textView.setTextColor(Color.BLACK);
                    viewFlipper.addView(textView);
                }
            }
        }
    }
}