package com.aaltus.teslaradio;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.ar4android.vuforiaJME.VideoPlayerActivity;
import com.aaltus.teslaradio.subject.SubjectContent;
import com.utils.AppLogger;
import com.utils.BackgroundLoadingImageView;
import com.viewpagerindicator.CirclePageIndicator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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
            mLayouts = mItem.getListXmlDetailMenu();
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

        rootView.findViewById(R.id.detail_picto_icon).setBackgroundResource(mItem.getPictogramDrawableId());

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

    public final PageDetailFragment newInstance(int position)
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
    public class PageDetailFragment extends Fragment implements View.OnClickListener {

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
            //v.setOnClickListener(this);

            v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    // Ensure you call it only once :
                    //v.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    if (Build.VERSION.SDK_INT < 16) {
                        v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    // Here you can get the size :)
                    startBackgroundImageProcessing((ViewGroup)v);
                }
            });


            View soundEmissionImage     = v.findViewById(R.id.sound_emission_image_preview);
            if(soundEmissionImage != null) {
                soundEmissionImage.setOnClickListener(this);
            }
            View sourisImage     = v.findViewById(R.id.souris_preview);
            if(sourisImage != null) {
                sourisImage.setOnClickListener(this);
            }
            View modulationFmImage     = v.findViewById(R.id.modulationfm_preview);
            if(modulationFmImage != null) {
                modulationFmImage.setOnClickListener(this);
            }
            View modulationAmImage    = v.findViewById(R.id.modulationam_preview);
            if(modulationAmImage != null) {
                modulationAmImage.setOnClickListener(this);
            }



            return v;
        }

        public void onViewCreated(View v, Bundle savedInstanceState) {
            super.onViewCreated(v, savedInstanceState);




        }

        private void playVideo(int res) {
            Intent videoPlaybackActivity = new Intent(this.getActivity(), VideoPlayerActivity.class);
            //int res=this.getResources().getIdentifier(resourceName, "raw", getActivity().getPackageName());
            videoPlaybackActivity.putExtra(VideoPlayerActivity.FILE_RES_ID, res);
            this.getActivity().startActivity(videoPlaybackActivity);
        }

        @Override
        public void onClick(View view) {
            //AppLogger.getInstance().d(TAG,"Testing stuff");
            switch(view.getId()) {
                case R.id.souris_preview:
                    playVideo(R.raw.souris);
                    break;
                case R.id.modulationam_preview:
                    playVideo(R.raw.modulationam);
                    break;
                case R.id.modulationfm_preview:
                    playVideo(R.raw.modulationfm);
                    break;
                case R.id.sound_emission_image_preview:
                    playVideo(R.raw.pression);
                    break;
            }
        }



    }

    private void startBackgroundImageProcessing(ViewGroup v) {
        List<BackgroundLoadingImageView> backgroundLoadingImageViewList = getCustomImageView(v);
        BitmapWorkerTask task = new BitmapWorkerTask(backgroundLoadingImageViewList);
        task.execute();
        /*List<CustomImageView> customImageViewList = getCustomImageView(v);
        AppLogger.getInstance().i(TAG, "customImageViewList:"+customImageViewList.size());
        for(int i=0; i < customImageViewList.size(); i++ )
        {
            AppLogger.getInstance().d(TAG,"ImageId:"+customImageViewList.get(i).getImageId());
            BitmapWorkerTask task = new BitmapWorkerTask(customImageViewList.get(i));
            task.execute();

        }*/
    }

    static private List<BackgroundLoadingImageView> getCustomImageView(ViewGroup parent) {
        List<BackgroundLoadingImageView> backgroundLoadingImageViewList = new ArrayList<BackgroundLoadingImageView>();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            if (child instanceof BackgroundLoadingImageView) {
                backgroundLoadingImageViewList.add((BackgroundLoadingImageView) child);
            }
            else if (child instanceof ViewGroup) {
                backgroundLoadingImageViewList.addAll(getCustomImageView((ViewGroup) child));
            }
        }
        return backgroundLoadingImageViewList;
    }



    public class BitmapWorkerTask extends AsyncTask<Void, Object, Void> {

        private final List<WeakReference<BackgroundLoadingImageView>> customImageViewList = new ArrayList<WeakReference<BackgroundLoadingImageView>>();

        public BitmapWorkerTask(List<BackgroundLoadingImageView> imageViewList) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            for(int i =0; i < imageViewList.size(); i++){
                customImageViewList.add(new WeakReference<BackgroundLoadingImageView>(imageViewList.get(i)));
            }

        }

        // Decode image in background.
        @Override
        protected Void doInBackground(Void... params) {
            //List<CustomImageView> customImageViewList = params[0];

            int data;
            ImageView imageView;
            for(int i=0; i <customImageViewList.size(); i++)
            {

                //Old way to load a big image into memory
                //int height = d.getIntrinsicHeight();
                //int width = d.getIntrinsicWidth();

                BackgroundLoadingImageView customImageView = customImageViewList.get(i).get();
                data       = customImageView.getImageId();
                Drawable d = getResources().getDrawable(data);
                int width  = customImageView.getWidth();
                int height = customImageView.getHeight();
                //AppLogger.getInstance().i(TAG,"Bitmap width:"+width+" heigth:"+height+" id:"+data);
                publishProgress(decodeSampledBitmapFromResource(getResources(), data, width, height),i);
            }
            return null;
        }

        protected void onProgressUpdate(Object...object) {
            //super.onProgressUpdate(bitmap[0]);
            Bitmap  bitmap                 = (Bitmap)object[0];
            Integer index                  = (Integer)object[1];
            WeakReference<BackgroundLoadingImageView> weakImageView= customImageViewList.get(index);

            if (customImageViewList.get(index).get() != null && bitmap != null) {
                final ImageView imageView = weakImageView.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    imageView.invalidate();
                }
            }
        }

        // Once complete, see if ImageView is still around and set bitmap.
        /*@Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    imageView.invalidate();
                }
            }
        }*/
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
