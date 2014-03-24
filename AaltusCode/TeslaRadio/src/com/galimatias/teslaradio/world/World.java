package com.galimatias.teslaradio.world;

import com.galimatias.teslaradio.world.Scenarios.Scenario;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import java.util.List;

/**
 * World: Defines the root node of the 3D World
 * Created by jean-christophelavoie on 2014-03-23.
 */
public class World extends Node {

    //Private attributes
    private List<Scenario> lstScenario;
    private Scenario mScenarioInFocus;

    public World() {
    }


    public void UpdateFocus(Vector3f newVector){

    }

    public void UpdateViewables(){

    }

    public void UpdateScenarioState(){

    }


}
