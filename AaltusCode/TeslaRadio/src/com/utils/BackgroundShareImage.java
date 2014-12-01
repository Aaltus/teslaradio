package com.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import com.aaltus.teslaradio.R;

import java.io.OutputStream;

/**
 * Created by jimbojd72 on 12/1/2014.
 */
public class BackgroundShareImage extends AsyncTask<Integer, Uri, Uri> {

    private Context context;
    private static String TAG = BackgroundShareImage.class.getSimpleName();

    public BackgroundShareImage(Context context){
        this.context = context;
    }

    // Decode image in background.
    @Override
    protected Uri doInBackground(Integer... params) {

        Integer resourceId = params[0];
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), resourceId);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);

        OutputStream outstream;
        boolean isImageLoaded = false;
        try {
            outstream = context.getContentResolver().openOutputStream(uri);
            icon.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
            outstream.close();
            isImageLoaded = true;
        } catch (Exception e) {
            AppLogger.getInstance().e(TAG,e.getMessage());
        }

        return isImageLoaded ? uri : null;
    }


    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Uri uriImage) {

        if(uriImage != null) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");
            share.putExtra(Intent.EXTRA_STREAM, uriImage);
            context.startActivity(Intent.createChooser(share, "Share Image"));
        }

    }
}
