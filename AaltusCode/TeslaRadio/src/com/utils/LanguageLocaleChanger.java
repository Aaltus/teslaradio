package com.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;

import java.util.Locale;

/**
 * Custom static language changer for the global app
 */
public class LanguageLocaleChanger {

    /*
    Default language
     */
    public static final String DEFAULT_LANGUAGE = "en";

    /*
    Save new language to sharedpreferences an restart the current activity
     */
    public static void reloadAppWithNewLanguage(Activity currentActivity, String languageToLoad){

        //Save it to sharedpreferences
        saveLanguageLocaleToSharedPreferences(currentActivity,languageToLoad);

        reloadAppWithCurrentLanguage(currentActivity);
    }

    /*
    Restart current activity with the current language in shared preference
     */
    public static void reloadAppWithCurrentLanguage(Activity currentActivity){

        //Change language and reset activity
        loadLanguageLocaleInActivity(currentActivity);

        //refresh current activity
        reloadActivity(currentActivity);
    }

    /*
    Change current activity app locale language
     */
    public static void loadLanguageLocaleInActivity(Activity currentActivity){

        String languageToLoad = loadLanguageLocaleFromSharedPreferences(currentActivity);

        Log.e("LanguageChanger", languageToLoad);


        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        currentActivity.getBaseContext().getResources().updateConfiguration(config,
                currentActivity.getBaseContext().getResources().getDisplayMetrics());

    }

    /*
    Save to sharepreferences "language" string the specified languageToLoad value
     */
    private static void saveLanguageLocaleToSharedPreferences(Activity currentActivity, String languageToLoad){

        SharedPreferences languagepref = currentActivity.getSharedPreferences("language", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = languagepref.edit();
        editor.putString("language",languageToLoad );
        editor.commit();

    }

    /*
    Return the String value from sharepreferences "language" string.
    It also create a default a the value is not existing.
     */
    private static String loadLanguageLocaleFromSharedPreferences(Activity currentActivity){

        SharedPreferences languagepref = currentActivity.getSharedPreferences("language", Context.MODE_PRIVATE);
        String languageToLoad = languagepref.getString("language", null);

        if (languageToLoad == null){

            languageToLoad = DEFAULT_LANGUAGE;
            saveLanguageLocaleToSharedPreferences(currentActivity,languageToLoad);
            languageToLoad = loadLanguageLocaleFromSharedPreferences(currentActivity);

        }

        return languageToLoad;

    }

    /*
        Restart the activity
     */
    private static void reloadActivity(Activity currentActivity){

        //since we are mainly using activityfragment, the recreae method exist
        //currentActivity.finish();
        //currentActivity.startActivity(currentActivity.getIntent());

        currentActivity.recreate();
    }


}
