package com.galimatias.teslaradio.world.Scenarios;

import com.jme3.app.state.AppState;

/**
 * Created by jimbojd72 on 11/6/2014.
 */
public interface IStartScreen extends StartScreenController {

    void openStartMenu();

    void closeStartMenu();

    boolean isStartMenuShown();
}
