package com.aaltus.teslaradio.world.Scenarios;

import com.aaltus.teslaradio.subject.ScenarioEnum;

/**
 * Interface that can switch between scenario with a next/previous or an enum
 *
 * Created by jimbojd72 on 9/8/14.
 */
public interface IScenarioSwitcher {

    boolean hasNextScenario();

    boolean hasPreviousScenario();

    void setNextScenario();

    void setPreviousScenario();

    void setScenarioByEnum(ScenarioEnum scenarioEnum);
}
