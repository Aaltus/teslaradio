package com.galimatias.teslaradio;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
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
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
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
    private ViewPager mViewPager;

    /**
     * ViewPager object that we use in our fragment to switch pages
     */
    private static int[] mLayouts;

    /**
     * PageIndicator object that we use in our fragment to show which page we are in
     */
    private CirclePageIndicator mIndicator;

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
        if (activity instanceof OnClickDetailFragmentListener) {
            listener = (OnClickDetailFragmentListener) activity;
            Log.d(TAG,"Add calling activity as listener.");
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement MyListFragment.OnClickDetailFragmentListener");
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
            mItem    = SubjectContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
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
            mViewPager    = (ViewPager) rootView.findViewById(R.id.pager);
            mViewPager.setAdapter(new SwipeAdapter(this.getActivity()));

            //Make the viewpager load 4 offscreen page
            mViewPager.setOffscreenPageLimit(4);

            //Attach page indicator to the ViewPager
            mIndicator = (CirclePageIndicator)rootView.findViewById(R.id.indicator);
            mIndicator.setViewPager(mViewPager);

        }

        return rootView;
    }


    //PagerAdapter implementation. This is the code that populate the ViewPager
    private static class SwipeAdapter extends PagerAdapter {


        private LayoutInflater mInflater;

        SwipeAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // remove the current page as it no longer needed
            container.removeView((View) object);
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // using the position parameter, inflate the proper layout, also add
            // it to the container parameter
            int currentPageRootId = mLayouts[position];

            ViewGroup pageView = (ViewGroup) mInflater.inflate(
                    currentPageRootId, container, false);

            //Uncomment this to make all the textview in the current viewgroup justified as webview
            //TextViewJustifiedUtils.setTextViewJustified(pageView, mContext);

            container.addView(pageView);

            return pageView;
        }

        @Override
        public int getCount() {
            return mLayouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        }



}
