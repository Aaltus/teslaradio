package com.ar4android.vuforiaJME;

import com.galimatias.teslaradio.subject.ScenarioEnum;
import com.galimatias.teslaradio.world.Scenarios.IStartScreen;

/**
 * Created by jimbojd72 on 9/3/14.
 */
public interface AndroidActivityListener extends IStartScreen, IProgressBarScreen, IInformativeMenu {

    public void dismissSplashScreen();

    public void setTutorialMenu(ScenarioEnum scenarioEnum);

    public void pauseTracking();

    public void startTracking();

    public ITrackerUpdater getITrackerUpdater();

    public void setICameraUpdater(ICameraUpdater iCameraUpdater);

    public void quitActivity();


}
