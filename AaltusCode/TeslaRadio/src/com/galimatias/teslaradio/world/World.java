/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 *
 * @author Alexandre Hamel
 */
public class World extends Node {

    //Private attributes
    private Geometry mScenarioInFocus;
    private static final String TAG = "World";

    public World(Node rootNode) {

        // You must add a light to make the model visible
        DirectionalLight back = new DirectionalLight();
        back.setDirection(new Vector3f(0.f,-1.f,1.0f));
        rootNode.addLight(back);

        DirectionalLight front = new DirectionalLight();
        front.setDirection(new Vector3f(0.f,1.f,1.0f));
        rootNode.addLight(front);
        
        /** A white ambient light source. */ 
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);
        
    }


    public void UpdateFocus(Camera fgCam, Node Scenario)
    {

        // 1. Reset results list.
        CollisionResults results = new CollisionResults();

        // 2. Mode 1: Find the location and direction of the camera and trace a ray.
        Ray ray = new Ray(fgCam.getLocation(), fgCam.getDirection());

        // 3. Collect intersections between Ray and Shootables in results list.
        Scenario.collideWith(ray, results);

        // 4. Print the results
        for (int i = 0; i < results.size(); i++)
        {
            // For each hit, we know distance, impact point, name of geometry.
            float dist = results.getCollision(i).getDistance();
            Vector3f pt = results.getCollision(i).getContactPoint();
            String hit = results.getCollision(i).getGeometry().getName();

            // Log.d(TAG,"  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
        }

        // 5. Use the results (we mark the hit object)
        if (results.size() > 0)
        {
            // The closest collision point is what was truly hit:
            CollisionResult closest = results.getClosestCollision();

            /**
             * Passing the control to the scenario via the mScenarioInFocus pointer
             * needs to be implemented. CollisionResult class will need a scenario getter
             * method for it to work. UpdateScenarioState could be called here.
             */

        // TODO: Add support for scenario type classes in CollisionResult class

            mScenarioInFocus = closest.getGeometry();
            Scenario.attachChild(mScenarioInFocus.getParent());

        }
        else
            Scenario.detachAllChildren();

    }

    public void UpdateViewables(Node rootNode, Node Scenario)
    {
        if (mScenarioInFocus != null)
        {
            rootNode.attachChild(Scenario);
        }
    }

    public void UpdateScenarioState()
    {

    }


}
