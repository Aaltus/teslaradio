package com.aaltus.teslaradio.world.Scenarios;

import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.TouchListener;
import com.jme3.scene.Node;

import java.util.List;

/**
 * A Scenario Manager interface that provide basic interface to change the scenario
 * and also provide a simple update.
 *
 * Created by jimbojd72 on 9/8/14.
 */
public interface IScenarioManager extends TouchListener,ActionListener, IScenarioSwitcher {

    void setNodeList(List<Node> nodeList);

    //void simpleUpdate(float tpf);
}
