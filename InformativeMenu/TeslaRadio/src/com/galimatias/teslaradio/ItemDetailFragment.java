package com.galimatias.teslaradio;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.galimatias.teslaradio.dummy.DummyContent;
import com.utils.TextViewJustifiedUtils;
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


        private final long minimumDifferenceTime = 1000;
        private long lastUpdateTime = minimumDifferenceTime;
        private LayoutInflater mInflater;
        private Context mContext;

        SwipeAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mContext  = context;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // remove the current page as it no longer needed
            container.removeView((View) object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);


            long currentUpdateTime=System.currentTimeMillis();
            if ((currentUpdateTime - lastUpdateTime)<minimumDifferenceTime){
                container.setVisibility(View.VISIBLE);
            }
            lastUpdateTime = currentUpdateTime;
            Log.e("chat", "chat");
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // using the position parameter, inflate the proper layout, also add
            // it to the container parameter
            int currentPageRootId = mLayouts[position];

            ViewGroup pageView = (ViewGroup) mInflater.inflate(
                    currentPageRootId, container, false);

            TextViewJustifiedUtils.setTextViewJustified(pageView, mContext);
            //TextViewJustifiedUtils.setTextViewJustified(pageView.findViewById(R.id.test_size),mContext);

//            WebView mWebView = (WebView) pageView.findViewById(R.id.webview);
//
//            String text = "<html><body>" + "<p align=\"justify\">"
//                    + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas lorem orci, convallis eget interdum vitae, volutpat id magna. Aenean ullamcorper condimentum velit. Curabitur pretium lorem sit amet neque placerat congue. Vivamus eget odio pharetra, pellentesque odio nec, hendrerit est. Etiam dictum vestibulum nunc. Maecenas vel feugiat nibh. Maecenas sed massa gravida, adipiscing quam non, tincidunt felis. Phasellus neque est, fermentum ac varius in, blandit quis mauris. Curabitur a bibendum ipsum. Praesent ullamcorper convallis tellus. Nulla euismod leo a dui sagittis blandit. Etiam pretium, nisi a dignissim accumsan, magna eros dapibus est, vitae ultricies orci ipsum a sem. Ut risus quam, volutpat quis ante pharetra, consequat viverra mauris. Nunc nulla tellus, consequat in interdum et, pellentesque sit amet arcu. Vivamus rhoncus, neque a volutpat volutpat, nisl nisl eleifend nunc, non feugiat orci diam quis eros. Proin et hendrerit risus. Donec commodo porttitor pharetra. Proin sagittis velit sed consectetur hendrerit. Mauris pretium, nisl vel pellentesque elementum, erat urna dictum ipsum, quis suscipit neque neque at purus. Vivamus porttitor, neque eu feugiat ultricies, erat diam euismod libero, sed vehicula libero neque id sem. Sed vulputate, tortor pulvinar venenatis sagittis, dolor metus vulputate enim, dictum eleifend massa ipsum non orci. Donec in lectus augue. Morbi placerat ut sem sed consequat. Curabitur velit arcu, porta et euismod id, condimentum ut sem. Suspendisse vulputate tristique tortor, ac fermentum purus placerat in. Nunc dignissim, mi vitae pretium imperdiet, nisl lacus pellentesque tellus, id cursus mauris nisi nec libero. Mauris volutpat imperdiet auctor. Integer convallis iaculis lectus, sed tristique tortor ornare a. Nullam tincidunt turpis eget quam molestie faucibus. Curabitur elementum, velit sit amet pulvinar lacinia, eros leo posuere dui, vitae tincidunt lectus massa nec enim. Etiam ligula dolor, dictum lobortis elit sed, imperdiet lacinia felis. Aliquam dignissim augue scelerisque, ullamcorper libero sed, laoreet nulla. Nunc sagittis velit vitae tellus interdum vulputate. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Vivamus consectetur lorem sed mattis pellentesque. In eu auctor nisi. Pellentesque id tempus odio. Vestibulum at purus elementum, dictum quam id, mollis nunc. Suspendisse tempor velit nec fringilla cursus. Maecenas congue metus nisi, imperdiet posuere sem dignissim non. Nulla facilisi. Vivamus lectus diam, mollis eget fermentum sed, sollicitudin in elit. Etiam enim libero, aliquam nec fermentum nec, iaculis eget lorem. Cras ac consequat nisi. Phasellus feugiat vulputate nulla, in cursus est posuere euEtiam sit amet nisi erat. Sed in dui eleifend lorem congue ullamcorper a id purus. Interdum et malesuada fames ac ante ipsum primis in faucibus. Phasellus feugiat augue dapibus tempus posuere. Ut lobortis commodo nibh in venenatis. Fusce facilisis diam gravida nisl sodales, nec elementum lectus sagittis. Donec eget diam nec nisi tincidunt pretium. Donec iaculis dolor vel erat hendrerit blandit. Donec lorem quam, luctus ut justo sed, iaculis accumsan turpis. Cras non porta ligula. Aliquam hendrerit, nisl nec rutrum sodales, est velit laoreet est, sed mollis nisi elit et eros. Mauris sagittis auctor metus, quis tincidunt nisi dignissim ut. Mauris congue fermentum lectus non suscipit." +
//                    "       " + "</p> " + "</body></html>";
//            mWebView.setBackgroundColor(Color.TRANSPARENT);
//            mWebView.loadData(text, "text/html", "utf-8");

            //container.setVisibility(View.INVISIBLE);
            pageView.setVisibility(View.GONE);
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
