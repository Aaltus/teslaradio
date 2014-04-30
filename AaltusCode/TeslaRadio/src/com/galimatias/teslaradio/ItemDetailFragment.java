package com.galimatias.teslaradio;

import android.app.Activity;
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
            mLayouts = mItem.listXml;
            Log.i(TAG,"Loading SubjectContent : " + mItem.title);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {


        Log.d(TAG,"onCreateView");
        //Get ViewPager xml layout view
        View rootView = inflater.inflate(R.layout.viewpager_container, container, false);

        TextView titleTextView= (TextView) rootView.findViewById(R.id.item_detail_fragment_title_textview);
        titleTextView.setText(mItem.title);

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
            return new PageDetailFragment(position);
        }
    }

    /**
     * A fragment that return a fragment based on the
     * provided position.
     */
    public class PageDetailFragment extends Fragment {

        private int position;

        // Empty constructor, required as per Fragment docs
        //We make it private to prevent the user to initialize a fragment without a position
        private PageDetailFragment() {}

        public PageDetailFragment(int position)
        {
            this.position = position;
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            final View v = inflater.inflate(mLayouts[position], container, false);
            return v;
        }
    }
}
