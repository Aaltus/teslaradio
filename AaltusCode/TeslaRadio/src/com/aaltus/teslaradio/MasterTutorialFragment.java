package com.aaltus.teslaradio;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.ImageButton;
import com.utils.AppLogger;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * Created by jimbojd72 on 11/8/2014.
 */
public class MasterTutorialFragment extends DialogFragment implements
        View.OnClickListener,
        ViewPager.OnPageChangeListener {

    private static final String POSITION = "subject_content_position";
    //private static final String ITEM_DETAIL_FRAGMENT_TAG = "SUBJECT_TAG";
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    static private int[] mLayouts = {R.layout.master_tutorial_page1, R.layout.master_tutorial_page2, R.layout.master_tutorial_page3, R.layout.master_tutorial_page4};

    private static final String TAG = MasterTutorialFragment.class.getSimpleName();
    private Button nextButton;
    private Button previousButton;


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

        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.MyDialog);

        Log.i(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView");

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View view = inflater.inflate(R.layout.master_tutorial_layout, container,false);

        nextButton         = (Button)view.findViewById(R.id.master_tutorial_next_button);
        nextButton.setOnClickListener(this);
        previousButton     = (Button)view.findViewById(R.id.master_tutorial_previous_button);
        previousButton.setOnClickListener(this);
        view.findViewById(R.id.master_tutorial_skip_button).setOnClickListener(this);

        /*
        View cancelButton     = view.findViewById(R.id.master_tutorial_cancel_button);
        cancelButton.setOnClickListener(this);
        View continueButton     = view.findViewById(R.id.master_tutorial_ok_button);
        continueButton.setOnClickListener(this);
        */

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) view.findViewById(R.id.master_tutorial_pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        //mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setOffscreenPageLimit(1);
        mPager.setOnPageChangeListener(this);
        //Attach page indicator to the ViewPager
        CirclePageIndicator mIndicator = (CirclePageIndicator)view.findViewById(R.id.master_tutorial_indicator);
        mIndicator.setViewPager(mPager);


        return view;
    }

    /**
     * Reload the activity with the new language when clicked
     * @param v
     */
    public void onClick(View v){

        int id = v.getId();

        switch (id){

            /*
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
                */
            case R.id.master_tutorial_next_button:
                if(mPager.getCurrentItem() == mLayouts.length-1){
                    this.onMasterTutorialListener.onContinueEvent();
                }
                else{
                    mPager.setCurrentItem(mPager.getCurrentItem()+1);
                }
                break;
            case R.id.master_tutorial_previous_button:
                if(mPager.getCurrentItem() == 0){

                }
                else{
                    mPager.setCurrentItem(mPager.getCurrentItem()-1);
                }
                break;
            case R.id.master_tutorial_skip_button:
                this.onMasterTutorialListener.onContinueEvent();
                break;
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        if(i < mLayouts.length-1){
            nextButton.setText(getActivity().getText(R.string.master_tutorial_next_button));
        }
        else{
            nextButton.setText(getActivity().getText(R.string.master_tutorial_end_button));
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    /*
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

    }
    */

    private static class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }




        @Override
        public Fragment getItem(int position) {
            AppLogger.getInstance().i(TAG,"Position:"+position);
            return ScreenSlidePageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return mLayouts.length;
        }
    }



    private static class ScreenSlidePageFragment extends Fragment {

        private int position;
        private static final String ARG_ITEM_ID = "item_id";

        static ScreenSlidePageFragment newInstance(int num) {

            ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
            Bundle arguments = new Bundle();
            arguments.putInt(ScreenSlidePageFragment.ARG_ITEM_ID, num);
            fragment.setArguments(arguments);

            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            position = getArguments() != null ? getArguments().getInt(ARG_ITEM_ID) : -1;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            AppLogger.getInstance().i(TAG,"Position ScreenSlide:"+position);
            ViewGroup rootView = (ViewGroup) inflater.inflate(
                    mLayouts[position], container, false);

            //rootView.setOnClickListener(this);

            return rootView;
        }

    }


}
