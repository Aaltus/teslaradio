package com.galimatias.teslaradio.world.Scenarios;


import com.galimatias.teslaradio.world.AnimatedObjects.AnimatedObject;
import com.galimatias.teslaradio.world.ViewState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.qualcomm.vuforia.Vec3F;

import java.util.List;


/**
 * Scenario: Defines a Scenario node that will includes multiple objects and
 * manage interaction between thems. This could be refered as a Scene
 * Created by jean-christophelavoie on 2014-03-23.
 */
public abstract class Scenario extends Node {

    /**
     * The list of AnimatedObjects that are static
     */
    private List<AnimatedObject> lstStaticObjects;
    /**
     * The List of AnimatedObjects that are moveable
     */
    private List<AnimatedObject> listMoveableObjects;

    private Vector3f mOrigin;

    private ViewState mViewState;


}
