package com.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;

import java.util.Locale;

/**
 * Static function to save language locale to sharedpreferences and
 * to restart activity
 *
 * @author jimbojd72
 */
public class LanguageLocaleChanger {

    /*
    Default language
     */
    public static final String DEFAULT_LANGUAGE = "en";

    private static final String TAG = "VuforiaJMEActivity";

    /**
    Save new language to sharedpreferences an restart the current activity
     */
    public static void reloadAppWithNewLanguage(Activity currentActivity, String languageToLoad){

        //Save it to sharedpreferences
        saveLanguageLocaleToSharedPreferences(currentActivity,languageToLoad);

        reloadAppWithCurrentLanguage(currentActivity);
    }

    /**
    Restart current activity with the current language in shared preference
     */
    public static void reloadAppWithCurrentLanguage(Activity currentActivity){

        //Change language and reset activity
        loadLanguageLocaleInActivity(currentActivity);

        //refresh current activity
        reloadActivity(currentActivity);
    }

    /**
    Change current activity app locale language
     */
    public static void loadLanguageLocaleInActivity(Activity currentActivity){

        String languageToLoad = loadLanguageLocaleFromSharedPreferences(currentActivity);

        Log.i(TAG, "loadLanguageLocaleInActivity with : " + languageToLoad);


        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        currentActivity.getBaseContext().getResources().updateConfiguration(config,
                currentActivity.getBaseContext().getResources().getDisplayMetrics());

    }

    /**
    Save to sharepreferences "language" string the specified languageToLoad value
     */
    private static void saveLanguageLocaleToSharedPreferences(Activity currentActivity, String languageToLoad){

        Log.i(TAG, "saveLanguageLocaleToSharedPreferences : " + languageToLoad);

        SharedPreferences languagepref = currentActivity.getSharedPreferences("language", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = languagepref.edit();
        editor.putString("language",languageToLoad );
        editor.commit();

    }

    /**
    Return the String value from sharepreferences "language" string.
    It also create a default a the value is not existing.
     */
    public static String loadLanguageLocaleFromSharedPreferences(Activity currentActivity){

        SharedPreferences languagepref = currentActivity.getSharedPreferences("language", Context.MODE_PRIVATE);
        String languageToLoad = languagepref.getString("language", null);

        if (languageToLoad == null){

            languageToLoad = DEFAULT_LANGUAGE;
            saveLanguageLocaleToSharedPreferences(currentActivity,languageToLoad);
            languageToLoad = loadLanguageLocaleFromSharedPreferences(currentActivity);

        }

        return languageToLoad;

    }

    /**
        Restart the activity
     */
    private static void reloadActivity(Activity activity){

        //since we are mainly using activityfragment, the recreae method exist
        //currentActivity.finish();
        //currentActivity.startActivity(currentActivity.getIntent());

        Log.i(TAG, "reloadActivity");

        if (activity != null) {
            if (Build.VERSION.SDK_INT >= 11) {
                activity.recreate();
            } else {
                Intent intent = activity.getIntent();
                activity.overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.finish();

                activity.overridePendingTransition(0, 0);
                activity.startActivity(intent);
            }
        }
    }


}
