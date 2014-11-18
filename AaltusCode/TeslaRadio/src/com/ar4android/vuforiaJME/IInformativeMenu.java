package com.ar4android.vuforiaJME;

import com.aaltus.teslaradio.subject.ScenarioEnum;

/**
 * Created by jimbojd72 on 11/8/2014.
 */
public interface IInformativeMenu {

    public void toggleInformativeMenuCallback(ScenarioEnum scenarioEnum);

    void hideInformativeMenu();

    void showInformativeMenu();
}
