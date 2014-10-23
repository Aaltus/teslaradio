package com.ar4android.vuforiaJME;

import com.galimatias.teslaradio.subject.ScenarioEnum;

/**
 * Created by jimbojd72 on 9/3/14.
 */
public interface AndroidActivityListener {

   public void onFinishSimpleInit();

   public void toggleInformativeMenuCallback(ScenarioEnum scenarioEnum);

   public void pauseTracking();

   public void startTracking();

   public void hideInformativeMenu();

   public void showInformativeMenu();

   public ITrackerUpdater getITrackerUpdater();

   public void setICameraUpdater(ICameraUpdater iCameraUpdater);


}
