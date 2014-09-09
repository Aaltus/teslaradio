package com.galimatias.teslaradio.world.observer;

/**
 * Created by Greenwood0 on 2014-09-04.
 */
public interface ScenarioObservable {

    public void registerObserver(ScenarioObserver observer);
    public void removeObserver(ScenarioObserver observer);
    public void notifyObservers(Object caller);
}
