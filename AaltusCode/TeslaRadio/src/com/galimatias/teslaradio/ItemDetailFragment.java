package com.galimatias.teslaradio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.ar4android.vuforiaJME.VideoPlayerActivity;
import com.galimatias.teslaradio.subject.SubjectContent;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * Fragment that create a ViewPager adapter
 */
public class ItemDetailFragment extends Fragment  implements View.OnClickListener{


    private static final String TAG = "ItemDetailFragment";
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private static final String POSITION = "subject_content_position";

    /**
     * The dummy content this fragment is presenting.
     */
    private SubjectContent.SubjectItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }



    /**
     * ViewPager object that we use in our fragment to switch pages
     */
    private static int[] mLayouts;

    private OnClickDetailFragmentListener listener;

    @Override
    public void onClick(View view) {

        listener.onClickDetailFragment(view);
    }

    // Define the events that the fragment will use to communicate
    public interface OnClickDetailFragmentListener {
        public void onClickDetailFragment(View view);
    }

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //We attach this fragment either to a calling activity or the parentfragment
        if (getParentFragment() == null)
        {
            if (activity instanceof OnClickDetailFragmentListener) {
                listener = (OnClickDetailFragmentListener) activity;
                Log.d(TAG,"Add calling activity as listener.");
            } else {
                throw new ClassCastException(activity.toString()
                        + " must implement MyListFragment.OnClickDetailFragmentListener");
            }
        }
        else
        {
            if (getParentFragment() instanceof OnClickDetailFragmentListener) {
                listener = (OnClickDetailFragmentListener) getParentFragment();
                Log.d(TAG,"Add calling parent fragment as listener.");
            } else {
                throw new ClassCastException(getParentFragment().toString()
                        + " must implement MyListFragment.OnClickDetailFragmentListener");
            }

        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG,"onCreate");


        setHasOptionsMenu(true);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem    = SubjectContent.ITEM_MAP.get(Integer.parseInt(getArguments().getString(ARG_ITEM_ID)));
            mLayouts = mItem.getListXml();
            Log.i(TAG,"Loading SubjectContent : " + mItem.getTitle());
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {


        Log.d(TAG,"onCreateView");
        //Get ViewPager xml layout view
        View rootView = inflater.inflate(R.layout.viewpager_container, container, false);

        TextView titleTextView= (TextView) rootView.findViewById(R.id.item_detail_fragment_title_textview);
        titleTextView.setText(mItem.getTitle());

        ImageButton cancelImageButton = (ImageButton) rootView.findViewById(R.id.item_detail_fragment_close_button);
        cancelImageButton.setOnClickListener(this);

        // Show the dummy content xml as xml
        if (mItem != null) {

            //Attach adapter to ViewPager
            ViewPager mViewPager    = (ViewPager) rootView.findViewById(R.id.pager);

            mViewPager.setAdapter(new DetailPagerAdapter(this.getChildFragmentManager()));

            //Make the viewpager load 4 offscreen page
            mViewPager.setOffscreenPageLimit(0);

            //Attach page indicator to the ViewPager
            CirclePageIndicator mIndicator = (CirclePageIndicator)rootView.findViewById(R.id.indicator);
            mIndicator.setViewPager(mViewPager);

        }




        return rootView;
    }

    public static final PageDetailFragment newInstance(int position)
    {
        PageDetailFragment f = new PageDetailFragment();
        Bundle bdl = new Bundle(2);
        bdl.putInt(POSITION, position);
        f.setArguments(bdl);
        return f;
    }

    /**
     * A simple FragmentStatePagerAdapter to return fragment to
     * the ViewPager
     */
    public class DetailPagerAdapter extends FragmentStatePagerAdapter {

        public DetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mLayouts.length;
        }

        @Override
        public Fragment getItem(int position)
        {
            return newInstance(position);
        }
    }



    /**
     * A fragment that return a fragment based on the
     * provided position.
     */
    public static class PageDetailFragment extends Fragment implements View.OnClickListener {

        private int position;




        // Empty constructor, required as per Fragment docs
        //DON'T USE THIS DIRECTLY
        public PageDetailFragment() {}


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            position = getArguments() != null ? getArguments().getInt(POSITION) : -1;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            final View v = inflater.inflate(mLayouts[position], container, false);
            v.setOnClickListener(this);

            /*
            View soundCaptureImage     = v.findViewById(R.id.sound_capture_image);
            if(soundCaptureImage != null) {
                soundCaptureImage.setOnClickListener(this);
            }
            */


            return v;
        }

        private void playVideo(int res) {
            Intent videoPlaybackActivity = new Intent(this.getActivity(), VideoPlayerActivity.class);
            //int res=this.getResources().getIdentifier(resourceName, "raw", getActivity().getPackageName());
            videoPlaybackActivity.putExtra(VideoPlayerActivity.FILE_RES_ID, res);
            this.getActivity().startActivity(videoPlaybackActivity);
        }

        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.sound_capture_image:
                    playVideo(R.raw.souris);
                    break;

            }
        }
    }
}
