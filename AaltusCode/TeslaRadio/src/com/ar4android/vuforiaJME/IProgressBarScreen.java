package com.ar4android.vuforiaJME;

/**
 * Created by jimbojd72 on 11/8/2014.
 */
public interface IProgressBarScreen {
    void openProgressScreen(String title);

    void closeProgressScreen();

    void setProgressBar(int currentProgress, String progressComment);
}
