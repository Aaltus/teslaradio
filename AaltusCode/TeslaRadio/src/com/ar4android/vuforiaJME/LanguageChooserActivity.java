package com.ar4android.vuforiaJME;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import com.galimatias.teslaradio.LanguageDialogFragment;

/**
 * Created by jimbojd72 on 10/26/2014.
 */
public class LanguageChooserActivity extends FragmentActivity {
    private static final String LANGUAGE_ACTIVITY_DIALOG_FRAGMENT_TAG = "DIALOG_FRAGMENT";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if(pref.getBoolean("activity_executed", false)){
            Intent intent = new Intent(this, VuforiaJMEActivity.class);
            startActivity(intent);
            finish();
        } else {
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("activity_executed", true);
            ed.commit();
            FragmentManager fm = getSupportFragmentManager();//getSupportFragmentManager();
            LanguageDialogFragment languageDialogFragment = new LanguageDialogFragment();
            languageDialogFragment.setCancelable(false);
            languageDialogFragment.setDialogClosable(false);
            languageDialogFragment.show(fm, LANGUAGE_ACTIVITY_DIALOG_FRAGMENT_TAG);
        }



        


    }
}