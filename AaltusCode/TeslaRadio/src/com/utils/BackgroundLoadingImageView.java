package com.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.aaltus.teslaradio.R;

/**
 * Created by jimbojd72 on 11/26/2014.
 */
public class BackgroundLoadingImageView extends ImageView {

    private int imageId = 0;
    public int getImageId(){
        return imageId;
    }

    /**
     * @param context
     */
    public BackgroundLoadingImageView(Context context)
    {
        super(context);
        init(context, null, 0);

    }

    /**
     * @param context
     * @param attrs
     */
    public BackgroundLoadingImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs, 0);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public BackgroundLoadingImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);

    }

    private void init(Context context, AttributeSet attrs, int defStyle){

        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.BackgroundLoadingImageView, defStyle, 0 );
        imageId = a.getResourceId(R.styleable.BackgroundLoadingImageView_drawableId, 0);
        a.recycle();
    }

}
