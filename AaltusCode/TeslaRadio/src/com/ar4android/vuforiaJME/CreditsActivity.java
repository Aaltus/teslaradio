package com.ar4android.vuforiaJME;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.aaltus.teslaradio.CreditsFragment;
import com.aaltus.teslaradio.R;
import com.aaltus.teslaradio.world.Scenarios.IStartScreen;

/**
 * Created by Batcave on 14-11-19.
 */
public class CreditsActivity extends FragmentActivity {

    private static final String CREDITS_FRAGMENT = "CREDITS_FRAGMENT";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.credits_layout_activity);

        FragmentManager fm = getSupportFragmentManager();// getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        CreditsFragment fragment = new CreditsFragment();
        ft.add(R.id.credits_fragment_container, fragment, CREDITS_FRAGMENT);
        ft.commit();

        /*
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
        */

    }

    @Override
    public void onBackPressed() {

        Intent openMainActivity= new Intent(this, VuforiaJMEActivity.class);
        openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        openMainActivity.putExtra(VuforiaJMEActivity.CreditsBackButtonKey,true);
        this.startActivity(openMainActivity);
        this.finish();
        this.overridePendingTransition(0,0);
    }
}
