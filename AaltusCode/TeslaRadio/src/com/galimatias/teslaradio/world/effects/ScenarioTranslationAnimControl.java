/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.ar4android.vuforiaJME.AppGetter;
import com.jme3.cinematic.MotionPath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import java.util.List;

/**
 *
 * @author Hugo
 */
public class ScenarioTranslationAnimControl extends AbstractControl{

    // Node to create base path
    private final List<Node> trackables;
    private Node endNode;
    private Node startNode;
    
    // path
    private MotionPath path = new MotionPath();
    private boolean pathIsReverse;
    
    // distance and speed on path
    private float distanceTraveled = 0;
    private float speed;
    
    // position vector
    private Vector3f posVector = new Vector3f();
    private Vector3f startPos = new Vector3f();
    

    
    public ScenarioTranslationAnimControl(List<Node> trackables, float speed){

        this.trackables = trackables;
        this.speed = speed*AppGetter.getWorldScalingDefault();
    }
    
    /** translation from node_i to node_i+1
     *
     * @param currentNodeIndex
     */
    public void startTranslationNext(int currentNodeIndex){
        startTranslation(currentNodeIndex, false);
    }

    /** translation from node_i to node_i-1
     *  
     * @param currentNodeIndex
     */
    public void startTranslationPrevious(int currentNodeIndex){
        startTranslation(currentNodeIndex, true);
    }
    
    public void startTranslation(int currentNodeIndex, boolean isNext){
        this.endNode = this.trackables.get(currentNodeIndex);
        if(isNext){
            if(currentNodeIndex < this.trackables.size()-1) {
                this.startNode = this.trackables.get(currentNodeIndex+1);
                this.pathIsReverse = false;
            }
            else{
                // from exterior path
                this.startNode = this.trackables.get(currentNodeIndex-1);
                this.pathIsReverse = true;
            }   
        }
        else{
            if(currentNodeIndex > 0){
                this.startNode = this.trackables.get(currentNodeIndex-1);
                this.pathIsReverse = false;
            }
            else{
                // from exterior path
                this.startNode = this.trackables.get(currentNodeIndex+1);
                this.pathIsReverse = true;
            }
        }
        
        // update the path before first frame
        updatePath();
        
        // enable the control updates
        this.setEnabled(true);
        
        // set the start position of the scenario
        this.spatial.setLocalTranslation(startNode.getWorldRotation().inverse().mult((startNode.getWorldTranslation().subtract(endNode.getWorldTranslation())).divide(endNode.getWorldScale())));

    }
    
    @Override
    protected void controlUpdate(float tpf) {
        
        // move scenario only if translation is on
        updatePath();
        updatePosition(tpf);
    }
    
    private void updatePosition(float tpf){    
        // compute the new distance traveled in the wire
        distanceTraveled += tpf*speed;
            
        // make sure the end of path is not reached
        if(distanceTraveled < path.getLength()){
                
            // find the current position on path from the distance traveled
            path.getSpline().interpolate(path.getWayPointIndexForDistance(distanceTraveled).y,(int) (path.getWayPointIndexForDistance(distanceTraveled).x), posVector);
            this.spatial.setLocalTranslation(posVector.negate());
        }
        else
        {
            // stop the translation by deactivating the control
            distanceTraveled = 0;
            this.spatial.setLocalTranslation(Vector3f.ZERO);
            this.setEnabled(false);
        }        
    }
    
    private void updatePath(){
        
        // get the relative position of the destination in this referential
        if(startNode != null){
            if(!this.pathIsReverse){
                startPos = startNode.getWorldRotation().inverse().mult((startNode.getWorldTranslation().subtract(endNode.getWorldTranslation())).divide(endNode.getWorldScale()));
            }
            else{
                startPos = endNode.getWorldRotation().inverse().mult((startNode.getWorldTranslation().subtract(endNode.getWorldTranslation())).divide(endNode.getWorldScale()));
            }
            // remove last waypoints and add new one
            this.path.clearWayPoints();
            this.path.addWayPoint(startPos);
            this.path.addWayPoint(Vector3f.ZERO);
        }
    }
            
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
       
    }  
}
