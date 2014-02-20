package com.galimatias.teslaradio;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import com.galimatias.teslaradio.dummy.DummyContent;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment  {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

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

    private CirclePageIndicator mIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem    = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            mLayouts = mItem.listXml;
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        //Get ViewPager xml layout view
        View rootView = inflater.inflate(R.layout.view_pagerlayoutswipe, container, false);

        // Show the dummy content xml as xml
        if (mItem != null) {

            //Attach adapter to ViewPager
            mViewPager    = (ViewPager) rootView.findViewById(R.id.pager);
            mViewPager.setAdapter(new SwipeAdapter(this.getActivity()));

            //Attach page indicator to the ViewPager
            mIndicator = (CirclePageIndicator)rootView.findViewById(R.id.indicator);
            mIndicator.setViewPager(mViewPager);

        }

        return rootView;
    }

    //PagerAdapter implementation
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
            ViewGroup pageView = (ViewGroup) mInflater.inflate(
                    mLayouts[position], container, false);

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
