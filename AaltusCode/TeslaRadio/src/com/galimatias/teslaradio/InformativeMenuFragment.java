package com.galimatias.teslaradio;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import com.galimatias.teslaradio.subject.ScenarioEnum;
import com.utils.LanguageLocaleChanger;
import com.utils.VerticalSeekBar;

/**
 * Created by jimbojd72 on 4/26/14.
 */
public class InformativeMenuFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener,
        ItemListFragment.Callbacks, ItemDetailFragment.OnClickDetailFragmentListener{


    private static final String TAG = "InformativeMenuFragment";

    //name of the fragment TAG
    private final String ITEM_LIST_FRAGMENT_TAG = "ITEM_LIST_FRAGMENT_TAG";
    private final String ITEM_DETAIL_FRAGMENT_TAG = "ITEM_DETAIL_FRAGMENT_TAG";
    private final String LANGUAGE_DIALOG_FRAGMENT_TAG = "LANGUAGE_DIALOG_FRAGMENT_TAG";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(TAG, "Initialize Top Layout");
        View myView = inflater.inflate(R.layout.vuforia_jme_overlay_layout, null);

        //Get the rootView of the activity. This view is on the direct parent
        //to the android jme opengl view

        //ViewGroup rootView = (ViewGroup) getActivity().findViewById(android.R.id.content);

        //Inflate and add the top level layout to the rootview
        //LayoutInflater factory = LayoutInflater.from(getActivity());

        //rootView.addView(myView);

        //Setup the ListFragment
        FragmentManager fm = getChildFragmentManager();//sgetSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = new ItemListFragment();
        ft.hide(fragment);
        ft.replace(R.id.item_list_fragment_vuforia, fragment, ITEM_LIST_FRAGMENT_TAG);
        ft.commit();
        fm.executePendingTransactions(); //TO do it quickly instead of waiting for commit()

        return myView;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        FragmentManager fm = getChildFragmentManager();//getSupportFragmentManager();

        Button languageButton = (Button) getView().findViewById(R.id.camera_toggle_language_button);
        Button infoButton     = (Button) getView().findViewById(R.id.camera_toggle_info_button);

        //Replace the current language button to show the current choosed locale language
        int drawableToGet = 0;
        //LayerDrawable languageButtonLayerDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.layer_list_language);
        String currentLanguage = LanguageLocaleChanger.loadLanguageLocaleFromSharedPreferences(getActivity());
        if (currentLanguage.equals("fr"))
        {
            drawableToGet = R.drawable.ic_action_language_fr;
        }
        else if (currentLanguage.equals("es"))
        {
            drawableToGet = R.drawable.ic_action_language_es;
        }
        else if (currentLanguage.equals("en"))
        {
            drawableToGet = R.drawable.ic_action_language_en;
        }
        else if (currentLanguage.equals("de"))
        {
            drawableToGet = R.drawable.ic_action_language_de;
        }
        languageButton.setBackgroundResource(drawableToGet);

        //Make the listfragment activable
        //((ItemListFragment) fm.findFragmentByTag(ITEM_LIST_FRAGMENT_TAG)).setActivateOnItemClick(true);


        languageButton.setOnClickListener(this);
        infoButton.setOnClickListener(this);

        //Get the vertical side bar and set its propriety
        VerticalSeekBar sb = (VerticalSeekBar) getView().findViewById(R.id.seekBar1);
        if (sb != null)
        {
            sb.setMax(100);
            sb.setProgress(50);
            sb.setOnSeekBarChangeListener(this);
        }


    }

    /**
     * Update the vertical side bar progress and text
     * @param v
     * @param progress
     * @param isUser
     */
    @Override
    public void onProgressChanged(SeekBar v, int progress, boolean isUser)
    {
        TextView tv = (TextView) getView().findViewById(R.id.seekbar_value_text);
        tv.setText(Integer.toString(progress)+"%");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {

    }

    public boolean isChildFragmentsHidden()
    {

        FragmentManager fm                = getChildFragmentManager(); //getSupportFragmentManager();s
        ItemListFragment listFragment     = (ItemListFragment) fm.findFragmentByTag(ITEM_LIST_FRAGMENT_TAG);

        boolean isHidden = false;
        if(listFragment != null && listFragment.isVisible())
        {
            isHidden = true;
        }

        return isHidden;

    }

    /** Action when a listfragment item is selected*/
    @Override
    public void onItemSelected(int id) {

        //Create the details fragment with the specified Id
        Bundle arguments = new Bundle();
        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, Integer.toString(id));
        ItemDetailFragment fragment = new ItemDetailFragment();
        fragment.setArguments(arguments);

        //Create the fragment with fragment transaction
        FragmentManager fm     = getChildFragmentManager();//getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
        ft.replace(R.id.item_detail_fragment_vuforia, fragment, ITEM_DETAIL_FRAGMENT_TAG).commit();
        fm.executePendingTransactions();

    }

    /**
     * Event called when a onClick event happen.
     * @param view
     */
    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id){

            case R.id.camera_toggle_language_button:
                showLanguageDialog();
                break;

            case R.id.camera_toggle_info_button:
                toggleFragmentsVisibility(null);
                break;

            default:
                break;
        }

    }

    /**
     * Show the language dialog
     */
    private void showLanguageDialog()
    {

        Log.d(TAG,"Show language dialog");
        FragmentManager fm = getFragmentManager();//getSupportFragmentManager();
        LanguageDialogFragment languageDialogFragment = new LanguageDialogFragment();
        languageDialogFragment.show(fm, LANGUAGE_DIALOG_FRAGMENT_TAG);

    }

    /**
     * Callback to receive click event of the DetailFragment
     * @param view : View that have been clicked
     */
    @Override
    public void onClickDetailFragment(View view) {

        int id = view.getId();
        Log.d(TAG, "OnClick Callback from detail fragment");


        switch (id)
        {
            //When fragment x button is clicked, close the fragment
            case R.id.item_detail_fragment_close_button:
                //Log.d(TAG, "OnClick Callback from detail fragment");
                toggleFragmentsVisibility(null);
                break;
        }
    }

    /**
     * Toggle the detailfragment and listfragment visibility.
     * @param scenarioEnum : the scenario to show
     */
    public void toggleFragmentsVisibility(ScenarioEnum scenarioEnum){


        FragmentManager fm                = getChildFragmentManager(); //getSupportFragmentManager();s
        ItemListFragment listFragment     = (ItemListFragment) fm.findFragmentByTag(ITEM_LIST_FRAGMENT_TAG);
        ItemDetailFragment fragmentDetail = (ItemDetailFragment) fm.findFragmentByTag(ITEM_DETAIL_FRAGMENT_TAG);

        if (listFragment != null)
        {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.enter_left, R.anim.exit_left);
            if (listFragment.isHidden())
            {
                Log.d(TAG, "Showing list fragment");
                ft.show(listFragment);
                if (scenarioEnum != null)
                {
                    //Choose a detail fragment based on the provided enum
                    listFragment.setActivatedPosition(scenarioEnum.ordinal());
                    onItemSelected(scenarioEnum.ordinal());
                }
            }
            else
            {
                Log.d(TAG, "Hiding list fragment");
                if (scenarioEnum == null)
                {
                    ft.hide(listFragment);
                }
            }

            ft.commit();
        }

        if (fragmentDetail != null)
        {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
            if (fragmentDetail.isHidden())
            {
                Log.d(TAG, "Showing detail fragment");
                ft.show(fragmentDetail);
            }
            else
            {
                Log.d(TAG,"Hiding detail fragment");
                if (scenarioEnum == null)
                {
                    ft.hide(fragmentDetail);
                }
            }
            ft.commit();
        }

    }

}