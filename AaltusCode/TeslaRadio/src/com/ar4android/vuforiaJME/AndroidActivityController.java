package com.ar4android.vuforiaJME;

import com.aaltus.teslaradio.subject.ScenarioEnum;
import com.aaltus.teslaradio.world.Scenarios.IStartScreen;

/**
 * Created by jimbojd72 on 9/3/14.
 */
public interface AndroidActivityController extends IStartScreen,
        IProgressBarScreen,
        IInformativeMenu,
        ITrackingController,
        ITutorialMenu,
        ITrackableAlertToast{

    public void dismissAndroidSplashScreen();

    public void quitAndroidActivity();


}
