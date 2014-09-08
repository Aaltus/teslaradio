package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.subject.ScenarioEnum;

/**
 * Created by jimbojd72 on 9/8/14.
 */
public interface IScenarioSwitcher {
    //TODO: MODIFY THIS TO RECEIVE A LIST<NODE> TO ATTACH THE SCENARIO TO THE RIGHT TRACKABLE/NODE
    void setNextScenario();

    void setPreviousScenario();

    void setScenarioByEnum(ScenarioEnum scenarioEnum);
}
