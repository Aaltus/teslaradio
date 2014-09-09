package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.subject.ScenarioEnum;
import com.jme3.input.controls.TouchListener;
import com.jme3.scene.Node;

import java.util.List;

/**
 * Created by jimbojd72 on 9/8/14.
 */
public interface IScenarioManager extends TouchListener {
    //TODO: MODIFY THIS TO RECEIVE A LIST<NODE> TO ATTACH THE SCENARIO TO THE RIGHT TRACKABLE/NODE
    void setNextScenario();

    void setPreviousScenario();

    void setScenarioByEnum(ScenarioEnum scenarioEnum);

    void setNodeList(List<Node> nodeList);

    void updateNodeList(List<Node> nodeList, int idx);

    Boolean getIsNodeVisible();

    void setIsNodeVisible(Boolean isNodeVisible);

    void simpleUpdate(float tpf);
}
