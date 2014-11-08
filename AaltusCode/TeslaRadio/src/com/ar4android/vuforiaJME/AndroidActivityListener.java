package com.ar4android.vuforiaJME;

import com.galimatias.teslaradio.subject.ScenarioEnum;
import com.galimatias.teslaradio.world.Scenarios.IStartScreen;

/**
 * Created by jimbojd72 on 9/3/14.
 */
public interface AndroidActivityListener extends IStartScreen{

    public void dismissSplashScreen();

    public void toggleInformativeMenuCallback(ScenarioEnum scenarioEnum);

    public void setTutorialMenu(ScenarioEnum scenarioEnum);

    public void pauseTracking();

    public void startTracking();

    public void hideInformativeMenu();

    public void showInformativeMenu();

    public ITrackerUpdater getITrackerUpdater();

    public void setICameraUpdater(ICameraUpdater iCameraUpdater);

    public void quitActivity();



    public void openProgressScreen(String title);

    public void closeProgressScreen();

    public void setProgressBar(int currentProgress, String progressComment);


}
