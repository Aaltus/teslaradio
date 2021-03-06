package com.aaltus.teslaradio;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import com.ar4android.vuforiaJME.ITutorialSwitcher;
import com.aaltus.teslaradio.subject.ScenarioEnum;
import com.aaltus.teslaradio.subject.SubjectContent;
import com.utils.AppLogger;

/**
 * Created by jimbojd72 on 11/3/2014.
 */
public class TutorialFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = TutorialFragment.class.getSimpleName();
    MediaPlayer mediaPlayer = null;

    private Animation shakeAnim;
    private AnimationDrawable speakAnim;
    private TextView typewriter;

    private enum CharacterAction{

        CALM,
        SHAKING,
        SPEAKING,
        ELECTRIC

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

            case ELECTRIC:
                setElectricAnimation();
                break;

        }

    }

    private ITutorialSwitcher tutorialSwitcher;
    private int nbClicks = 0;
    private long time = 0;
    private int legitClickCount = 1;
    private int nbStrings = 0;

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
        LinearLayout layout = (LinearLayout) bubbleView.findViewById(R.id.bubbleLayout);
        typewriter = (TextView)layout.findViewById(R.id.bubbleTypewriter);
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
        characterButton.setBackgroundResource(R.drawable.tesla_exclamation);
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
        characterButton.setBackgroundResource(R.drawable.tesla_norm);

    }

    private void setElectricAnimation() {
        ImageView characterButton = getCharacterView();
        characterButton.setBackgroundResource(R.drawable.tesla_electric_anim);
        AnimationDrawable animation = (AnimationDrawable) characterButton.getBackground();
        animation.start();
    }

    @Override
    public void onClick(View view) {

        if(mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(this.getActivity(), R.raw.tesla);
        }

        switch (view.getId())
        {
            case R.id.character_tutorial_button:

                checkIfEasterEgg();

                if (nbClicks == 1) {
                    time = System.currentTimeMillis();
                } else if (nbClicks > 1) {
                    long currentTime = System.currentTimeMillis();
                    long deltaTime = currentTime - time;

                    if (deltaTime >= 2000) {
                        nbClicks = 0;
                    }
                }

                if (!(getView().findViewById(R.id.bubble_root_view).getVisibility() == View.VISIBLE)) {

                    setBubbleViewVisibility(!(getView().findViewById(R.id.bubble_root_view).getVisibility() == View.VISIBLE));
                    if (getView().findViewById(R.id.bubble_root_view).getVisibility() == View.VISIBLE) {
                        mediaPlayer.start();
                    }
                } else {
                    switchBubbles();
                }
                break;
            case R.id.view_flipper:
                switchBubbles();
                break;

        }
    }

    private void checkIfEasterEgg() {
        nbClicks++;

        if (nbClicks >= 5) {
            setElectricAnimation();
        }
    }

    private ViewFlipper getViewFlipper(){
        return (ViewFlipper)getView().findViewById(R.id.view_flipper);
    }

    private void switchBubbles() {
        ViewFlipper viewFlipper = getViewFlipper();
        if(viewFlipper.getDisplayedChild() == viewFlipper.getChildCount()-1)
        {
            setBubbleViewVisibility(false);
            legitClickCount = 1;
            typewriter.setText(legitClickCount + " de " + nbStrings);
        }
        else
        {
            legitClickCount++;
            viewFlipper.showNext();
            typewriter.setText(legitClickCount + " de " + nbStrings);
            setTutorialMenuCallback(viewFlipper.getDisplayedChild());
        }
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
        } else if (view.getVisibility() == View.VISIBLE && !showBubble) {
            view.setVisibility(View.GONE);
            setTutorialMenuCallback(-1);
            setCharacterAction(CharacterAction.CALM);
        }
    }

    public void setBubbleCategory(ScenarioEnum scenarioEnum){

        if(scenarioEnum == null)
        {
            setBubbleViewVisibility(false);
            legitClickCount = 1;
        }
        else
        {
            setCharacterAction(CharacterAction.SHAKING);
            ViewFlipper viewFlipper = getViewFlipper();
            viewFlipper.removeAllViews();

            int[] listXmlString = SubjectContent.ENUM_MAP.get(scenarioEnum).getListStringIdTutorial();
            nbStrings = listXmlString.length;
            if (listXmlString != null) {
                for (int i = 0; i < nbStrings; i++) {
                    TextView textView = new TextView(this.getActivity());
                    textView.setText(this.getActivity().getText(listXmlString[i]));
                    textView.setTextColor(Color.BLACK);
                    typewriter.setText(legitClickCount + " de " + nbStrings);
                    typewriter.setTextColor(Color.BLACK);
                    viewFlipper.addView(textView);
                }
            }
        }
    }

    public int getCharacterWidthInPixel(){
        return getCharacterView().getWidth();
    }

    public int getCharacterHeightInPixel(){
        return getCharacterView().getHeight();
    }
}