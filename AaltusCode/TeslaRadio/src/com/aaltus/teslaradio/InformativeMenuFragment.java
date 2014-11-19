package com.aaltus.teslaradio;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import com.aaltus.teslaradio.subject.AudioOptionEnum;
import com.aaltus.teslaradio.world.Scenarios.ISongManager;
import com.ar4android.vuforiaJME.ITutorialSwitcher;
import com.aaltus.teslaradio.subject.ScenarioEnum;
import com.aaltus.teslaradio.subject.SubjectContent;
import com.aaltus.teslaradio.world.Scenarios.IScenarioSwitcher;
import com.utils.AppLogger;
import com.utils.LanguageLocaleChanger;
import com.utils.PagerContainer;

/**
 * Created by jimbojd72 on 4/26/14.
 */
public class InformativeMenuFragment extends Fragment implements View.OnClickListener,
        //SeekBar.OnSeekBarChangeListener,
        ItemListFragment.Callbacks,
        ItemDetailFragment.OnClickDetailFragmentListener,
        ITutorialSwitcher,
        MultiDirectionSlidingDrawer.OnDrawerOpenListener,
        MultiDirectionSlidingDrawer.OnDrawerCloseListener,
        ViewPager.OnPageChangeListener

{

    private PagerContainer mContainer;

    private IScenarioSwitcher scenarioSwitcher;
    private ViewPager pager;

    public void setScenarioSwitcher(IScenarioSwitcher scenarioSwitcher) {
        this.scenarioSwitcher = scenarioSwitcher;
    }

    private ITutorialSwitcher tutorialSwitcher;
    public void setTutorialSwitcher(ITutorialSwitcher tutorialSwitcher) {
        this.tutorialSwitcher = tutorialSwitcher;
    }

    private ISongManager songManager;
    public void setSongManager(ISongManager songManager) {
        this.songManager = songManager;
    }



    private static final String TAG = "InformativeMenuFragment";

    //name of the fragment TAG
    private final String ITEM_LIST_FRAGMENT_TAG = "ITEM_LIST_FRAGMENT_TAG";
    private final String ITEM_DETAIL_FRAGMENT_TAG = "ITEM_DETAIL_FRAGMENT_TAG";
    private final String LANGUAGE_DIALOG_FRAGMENT_TAG = "LANGUAGE_DIALOG_FRAGMENT_TAG";
    private final String TUTORIAL_FRAGMENT_TAG = "TUTORIAL_FRAGMENT_TAG";

    private MultiDirectionSlidingDrawer drawerLayout;
    //private View drawerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        AppLogger.getInstance().d(TAG, "Initialize Top Layout");
        View myView  = inflater.inflate(R.layout.vuforia_jme_overlay_layout, null, false);
        drawerLayout = (MultiDirectionSlidingDrawer)myView.findViewById(R.id.informative_menu_drawer);

        drawerLayout.setOnDrawerOpenListener(this);
        drawerLayout.setOnDrawerCloseListener(this);

        myView.findViewById(R.id.previous_scenario_button).setOnClickListener(this);
        myView.findViewById(R.id.next_scenario_button).setOnClickListener(this);
        myView.findViewById(R.id.guitar_hit_button).setOnClickListener(this);
        myView.findViewById(R.id.tambour_hit_button).setOnClickListener(this);
        myView.findViewById(R.id.ipod_song_selector_button).setOnClickListener(this);

        //Initilize the pageview
        mContainer = (PagerContainer) myView.findViewById(R.id.pager_container);
        pager = mContainer.getViewPager();
        PagerAdapter adapter = new MyPagerAdapter();
        pager.setAdapter(adapter);
        pager.setPageTransformer(true, new ZoomOutPageTransformer());
        //Necessary or the pager will only have one extra page to show
        // make this at least however many pages you can see
        pager.setOffscreenPageLimit(adapter.getCount());
        //A little space between pages
        pager.setPageMargin(15);
        //If hardware acceleration is enabled, you should also remove
        // clipping on the pager for its children.
        pager.setClipChildren(false);
        pager.setOnPageChangeListener(this);



        /*
       * In my trial experiment:
       * Without dummy OnTouchListener for the drawView to
       * consume the onTouch event, touching/clicking on
       * un-handled view on drawView will pass to the view
       * under it!
       * - Touching on the Android icon will
       * trigger the TextView("http://android-er.blogspot.com/")
       * to open the web.
       */
        /*
        drawerView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return false;
            }
        });*/

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
        TutorialFragment tutorialFragment =  new TutorialFragment();
        tutorialFragment.setTutorialSwitcher(this);
        //ft.hide(fragment);
        ft.replace(R.id.item_list_fragment_vuforia, fragment, ITEM_LIST_FRAGMENT_TAG);
        ft.replace(R.id.tutorial_fragment, tutorialFragment, TUTORIAL_FRAGMENT_TAG);

        ft.commit();
        fm.executePendingTransactions(); //TO do it quickly instead of waiting for commit()

        return myView;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        FragmentManager fm = getChildFragmentManager();//getSupportFragmentManager();

        //For the old code
        /*Button languageButton = (Button) getView().findViewById(R.id.camera_toggle_language_button);
        Button infoButton     = (Button) getView().findViewById(R.id.camera_toggle_info_button);

        //Replace the current language button to show the current choosed locale language
        int drawableToGet = 0;
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
        */

        //Hi Jimbo myself, Hope you feel great, I commented because people asked me to hide
        //Get the vertical side bar and set its propriety
        /*
        VerticalSeekBar sb = (VerticalSeekBar) getView().findViewById(R.id.seekBar1);
        if (sb != null)
        {
            sb.setMax(100);
            sb.setProgress(50);
            sb.setOnSeekBarChangeListener(this);
        }
        */


    }

    /**
     * Update the vertical side bar progress and text
     */
    /*
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
*/
    public boolean isChildFragmentShown()
    {
        return isDetailFragmentsVisible() || isListFragmentsVisible();

    }

    public boolean isListFragmentsVisible()
    {
        return drawerLayout.isOpened();
        /*
        FragmentManager fm                = getChildFragmentManager(); //getSupportFragmentManager();s
        ItemListFragment listFragment     = (ItemListFragment) fm.findFragmentByTag(ITEM_LIST_FRAGMENT_TAG);

        boolean isVisible = false;
        if(listFragment != null && listFragment.isVisible())
        {
            isVisible = true;
        }

        return isVisible;
        */

    }

    public boolean isDetailFragmentsVisible()
    {

        FragmentManager fm                = getChildFragmentManager(); //getSupportFragmentManager();s
        ItemDetailFragment detailFragment     = (ItemDetailFragment) fm.findFragmentByTag(ITEM_DETAIL_FRAGMENT_TAG);

        boolean isVisible = false;
        if(detailFragment != null && detailFragment.isVisible())
        {
            isVisible = true;
        }

        return isVisible;

    }

    public boolean isTutorialFragmentsVisible()
    {

        Fragment fragment = getTutorialFragment();
        boolean isVisible = false;
        if(fragment != null && fragment.isVisible())
        {
            isVisible = true;
        }

        return isVisible;

    }

    public void showAllChildFragments(boolean showFragment)
    {
        toggleDetailFragmentVisibility(showFragment);
        toggleItemListVisibility(showFragment);
        toggleTutorialVisibility(!showFragment);
    }

    private TutorialFragment getTutorialFragment(){
        FragmentManager fm                = getChildFragmentManager(); //getSupportFragmentManager();s
        return (TutorialFragment) fm.findFragmentByTag(TUTORIAL_FRAGMENT_TAG);
    }


    /** Action when a listfragment item is selected*/
    @Override
    public void onItemSelected(int id) {

        toggleItemListVisibility(false);
        replaceDetailFragment(id);


    }

    public void replaceDetailFragment(int id)
    {

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

        ScenarioEnum scenarioEnum = SubjectContent.ITEM_MAP.get(id).getScenarioEnum();
        scenarioSwitcher.setScenarioByEnum(scenarioEnum);

        toggleDetailFragmentVisibility(true);
        toggleTutorialVisibility(false);

    }

    public void replaceDetailFragment(ScenarioEnum scenarioEnum)
    {
        replaceDetailFragment(SubjectContent.ENUM_MAP.get(scenarioEnum).getId());
    }

    public void setTutorialMenu(ScenarioEnum scenarioEnum){
        getTutorialFragment().setBubbleCategory(scenarioEnum);
    }

    /**
     * Event called when a onClick event happen.
     * @param view
     */
    @Override
    public void onClick(View view) {


        int id = view.getId();
        Log.d(TAG,"onClick with id: " + id);

        switch (id){

            case R.id.previous_scenario_button:
                //this.scenarioSwitcher.setPreviousScenario();
                if(pager.getCurrentItem() > 0){
                    pager.setCurrentItem(pager.getCurrentItem()-1);
                }
                break;
            case R.id.next_scenario_button:
                if(pager.getCurrentItem() < pager.getChildCount()-1){
                    pager.setCurrentItem(pager.getCurrentItem()+1);
                }
                //this.scenarioSwitcher.setNextScenario();
                break;
            case R.id.guitar_hit_button:
                this.songManager.onAudioOptionTouched(AudioOptionEnum.GUITAR);
                break;
            case R.id.tambour_hit_button:
                this.songManager.onAudioOptionTouched(AudioOptionEnum.DRUM);
                break;
            case R.id.ipod_song_selector_button:
                this.songManager.onAudioOptionTouched(AudioOptionEnum.IPOD);
                break;
            /* code for when there were language button
            case R.id.camera_toggle_language_button:
                showLanguageDialog();
                break;

            case R.id.camera_toggle_info_button:
                if(isListFragmentsVisible()){
                    toggleItemListVisibility(false);
                    if(!isDetailFragmentsVisible()){
                        toggleTutorialVisibility(true);
                    }
                }
                else{
                    toggleItemListVisibility(true);
                    toggleTutorialVisibility(false);
                }
                break;
            */
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
                toggleDetailFragmentVisibility(false);
                if(!isListFragmentsVisible()){
                    toggleTutorialVisibility(true);
                }
                break;
        }
    }


    public void toggleItemListVisibility(boolean showFragment)
    {

        if(showFragment){
            drawerLayout.animateOpen();
        }
        else{
            drawerLayout.animateClose();
        }


        /*
        FragmentManager fm                = getChildFragmentManager(); //getSupportFragmentManager();s
        ItemListFragment listFragment     = (ItemListFragment) fm.findFragmentByTag(ITEM_LIST_FRAGMENT_TAG);

        if (listFragment != null)
        {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.enter_left, R.anim.exit_left);
            if (showFragment)
            {
                Log.d(TAG, "Showing list fragment");
                ft.show(listFragment);

            }
            else
            {
                Log.d(TAG, "Hiding list fragment");
                ft.hide(listFragment);
            }

            ft.commit();
        }
        */

    }

    public void toggleTutorialVisibility(boolean showFragment)
    {
        FragmentManager fm    = getChildFragmentManager(); //getSupportFragmentManager();s
        Fragment fragment     = getTutorialFragment();

        if (fragment != null)
        {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.enter_down, R.anim.exit_down);
            if (showFragment && !isTutorialFragmentsVisible())
            {
                Log.d(TAG, "Showing tutorial fragment");
                ft.show(fragment);

                /*if (scenarioEnum != null)
                {
                    //Choose a detail fragment based on the provided enum
                    listFragment.setActivatedPosition(scenarioEnum.ordinal());
                    onItemSelected(scenarioEnum.ordinal());
                }*/
            }
            else if (!showFragment && isTutorialFragmentsVisible())
            {
                Log.d(TAG, "Hiding tutorial fragment");
                /*if (scenarioEnum == null)
                {
                    ft.hide(listFragment);
                }*/
                ft.hide(fragment);
            }

            ft.commit();
        }
    }

    /**
     * Toggle the detailfragment and listfragment visibility.
     */
    public void toggleDetailFragmentVisibility(boolean showFragment){


        FragmentManager fm                = getChildFragmentManager(); //getSupportFragmentManager();s
        ItemDetailFragment fragmentDetail = (ItemDetailFragment) fm.findFragmentByTag(ITEM_DETAIL_FRAGMENT_TAG);

        if (fragmentDetail != null)
        {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
            if (showFragment)
            {
                Log.d(TAG, "Showing detail fragment");
                ft.show(fragmentDetail);

            }
            else
            {
                Log.d(TAG,"Hiding detail fragment");
                //ft.hide(fragmentDetail);
                ft.remove(fragmentDetail);

            }
            ft.commit();
        }
    }

    @Override
    public void setTutorialIndex(int index) {
        this.tutorialSwitcher.setTutorialIndex(index);
    }

    public int getCharacterWidthInPixel(){
        return getTutorialFragment().getCharacterWidthInPixel();
    }

    public int getCharacterHeightInPixel(){
        return getTutorialFragment().getCharacterHeightInPixel();
    }

    @Override
    public void onDrawerClosed() {
        if(!isDetailFragmentsVisible()){
            toggleTutorialVisibility(true);
        }
    }

    @Override
    public void onDrawerOpened() {
        toggleTutorialVisibility(false);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {}

    @Override
    public void onPageSelected(int i) {
        if(scenarioSwitcher != null){
            scenarioSwitcher.setScenarioByEnum(SubjectContent.ITEMS.get(i).getScenarioEnum());
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {}

    //Nothing special about this adapter, just throwing up colored views for demo
    private class MyPagerAdapter extends PagerAdapter {

        /*
        private IScenarioSwitcher scenarioSwitcher;
        public void setScenarioSwitcher(IScenarioSwitcher scenarioSwitcher) {
            this.scenarioSwitcher = scenarioSwitcher;
        }

        private MyPagerAdapter(){

            this(null);
        }

        private MyPagerAdapter(IScenarioSwitcher scenarioSwitcher){

            this.setScenarioSwitcher(scenarioSwitcher);
        }
        */

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            /*
            TextView view = new TextView(getActivity());
            view.setText("Item "+position);
            view.setGravity(Gravity.CENTER);
            view.setBackgroundColor(Color.argb(255, position * 50, position * 10, position * 50));
            */
            ImageView imageView;
            //if(position < SubjectContent.getPictogramCount()) {
                Integer integer = SubjectContent.ITEMS.get(position).getPictogramDrawableId();
                imageView = new ImageView(getActivity());
                imageView.setBackgroundResource(integer);
            //}

            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public int getCount() {
            return SubjectContent.getPictogramCount();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }

}