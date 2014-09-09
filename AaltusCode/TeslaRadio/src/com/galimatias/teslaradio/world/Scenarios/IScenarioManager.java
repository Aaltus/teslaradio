package com.galimatias.teslaradio.world.Scenarios;

import com.jme3.input.controls.TouchListener;
import com.jme3.scene.Node;

import java.util.List;

/**
 * Created by jimbojd72 on 9/8/14.
 */
public interface IScenarioManager extends TouchListener, IScenarioSwitcher {

    void setNodeList(List<Node> nodeList);

    void updateNodeList(List<Node> nodeList, int idx);

    Boolean getIsNodeVisible();

    void setIsNodeVisible(Boolean isNodeVisible);

    void simpleUpdate(float tpf);
}
