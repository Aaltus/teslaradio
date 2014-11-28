/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaltus.teslaradio.world.effects;

import com.aaltus.teslaradio.world.Scenarios.Scenario;
import com.ar4android.vuforiaJME.AppGetter;
import com.jme3.cinematic.MotionPath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
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
    private MotionPath path;
    private boolean pathIsReverse;
    
    // distance and speed on path
    private float distanceTraveled = 0;
    private float speed;
    
    // position vector
    private Vector3f posVector = new Vector3f();
    private Vector3f startPos = new Vector3f();
    private Quaternion currentLocalRotation = new Quaternion();
    private Quaternion startOrientation = new Quaternion();
    private boolean toDetachAtEndOfTranslation = false;

    public ScenarioTranslationAnimControl(List<Node> trackables, float speed){

        this.trackables = trackables;
        this.speed = speed;
        
        this.path = new MotionPath();
    }
    
    /** translation from node_i to node_i+1
     *
     * @param currentNodeIndex
     */
    public void startTranslationNext(int currentNodeIndex){
        
        this.toDetachAtEndOfTranslation = false;
        this.endNode = this.trackables.get(currentNodeIndex);
        
        if(currentNodeIndex < this.trackables.size()-1) {
            this.startNode = this.trackables.get(currentNodeIndex+1);
            this.pathIsReverse = false;
        }
        else{
            // from exterior path
            this.startNode = this.trackables.get(currentNodeIndex-1);
            this.pathIsReverse = true;
        } 
        
        startTranslationCommon();
    }

    /** translation from node_i to node_i-1
     *  
     * @param currentNodeIndex
     */
    public void startTranslationPrevious(int currentNodeIndex){
        
        this.toDetachAtEndOfTranslation = false;
        this.endNode = this.trackables.get(currentNodeIndex);
        
        if(currentNodeIndex > 0){
            this.startNode = this.trackables.get(currentNodeIndex-1);
            this.pathIsReverse = false;
        }
        else{
            // from exterior path
            this.startNode = this.trackables.get(currentNodeIndex+1);
            this.pathIsReverse = true;
        }
                    
        startTranslationCommon();
    }
    
    public void startDestructionTranslationPrevious() {
        
        this.toDetachAtEndOfTranslation = true;
        
        this.startNode = this.trackables.get(1);
        this.endNode = this.trackables.get(0);
        
        this.pathIsReverse = true;    
        
        startTranslationCommon();

    }

    public void startDestructionTranslationNext() {
        
        this.toDetachAtEndOfTranslation = true;
        
        this.startNode = this.trackables.get(0);
        this.endNode = this.trackables.get(1);
        
        this.pathIsReverse = true;
        
        startTranslationCommon();
    }    
    
    
    
    public void startTranslationCommon(){

        // set the start position of the scenario
        this.spatial.setLocalTranslation(getStartPositionVector(pathIsReverse));

        if(!this.pathIsReverse) {
            
            // reset to the good scaling in the eventuality the previous translation was not finish
            this.spatial.setLocalScale(AppGetter.getWorldScalingDefault());
            
            
            if(!(((Scenario) ((Node) this.spatial).getChild(0)).getNeedFixedScenario()) ){
                this.startOrientation = (this.endNode.getParent().getWorldRotation().inverse()).mult(this.startNode.getParent().getWorldRotation());
            }
            else{
                this.startOrientation = (Quaternion.IDENTITY);
            }
        }
        else
        {
            this.startOrientation = Quaternion.IDENTITY;
            this.spatial.setLocalScale(0);
        }

        this.spatial.setLocalRotation(this.startOrientation);
        
        // enable the control updates
        this.setEnabled(true);
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        
        // move scenario only if translation is on
        updatePath();
        updatePosition(tpf);
    }
    
    private void updatePosition(float tpf){    
        // compute the new distance traveled in the wire
        distanceTraveled += tpf*speed*AppGetter.getWorldScalingDefault();
            
        // make sure the end of path is not reached
        if(distanceTraveled < path.getLength()){
                
            // find the current position on path from the distance traveled
            path.getSpline().interpolate(path.getWayPointIndexForDistance(distanceTraveled).y,(int) (path.getWayPointIndexForDistance(distanceTraveled).x), posVector);
            this.spatial.setLocalTranslation(posVector);

            // get the relative rotation
            currentLocalRotation = Quaternion.ZERO;
            if(!this.pathIsReverse) {
                this.spatial.setLocalRotation(this.currentLocalRotation.slerp(this.startOrientation, Quaternion.IDENTITY, this.distanceTraveled / this.path.getLength()));
            }
            // set scenario scale when comming from outside (0 to 1 of scaling animation)
            else{
                if(!this.toDetachAtEndOfTranslation){
                    this.spatial.setLocalScale(distanceTraveled/path.getLength()*AppGetter.getWorldScalingDefault());
                }else{
                    this.spatial.setLocalScale((1-distanceTraveled/path.getLength())*AppGetter.getWorldScalingDefault());
                }
            }

        }
        else
        {
            // stop the translation by deactivating the control
            distanceTraveled = 0;
            this.spatial.setLocalTranslation(Vector3f.ZERO);
            this.spatial.setLocalRotation(Quaternion.IDENTITY);
            this.setEnabled(false);
            
            // detach scenario if it goes to outside
            if(this.toDetachAtEndOfTranslation == true){
                this.spatial.getParent().detachChild(this.spatial);
            }
        }        
    }
    
    private void updatePath(){

        if(this.startNode != null){

            // get the relative position of the destination in this referential
            startPos = getStartPositionVector(pathIsReverse);
                    
            // remove last waypoints and add new one
            this.path.clearWayPoints();
            
            if(!this.toDetachAtEndOfTranslation){
                this.path.addWayPoint(startPos);
                this.path.addWayPoint(Vector3f.ZERO);                
            }else{
                this.path.addWayPoint(Vector3f.ZERO);
                this.path.addWayPoint(startPos);                
            }

        }
    }

    private Vector3f getStartPositionVector(boolean isPathReverse){
        Vector3f startPosition;
        
        // path between 2 trackable
        if(!isPathReverse){
            startPosition = this.endNode.getWorldRotation().inverse().mult((this.startNode.getWorldTranslation().subtract(endNode.getWorldTranslation())).divide(endNode.getWorldScale()));
        }
        
        // path to the exterior
        else if(this.toDetachAtEndOfTranslation){
            startPosition = this.startNode.getWorldRotation().inverse().mult((this.startNode.getWorldTranslation().subtract(endNode.getWorldTranslation())).divide(endNode.getWorldScale()));
        }
        
        // path from the exterior
        else{
            startPosition = this.endNode.getWorldRotation().inverse().mult((this.endNode.getWorldTranslation().subtract(startNode.getWorldTranslation())).divide(endNode.getWorldScale()));
        }

        return startPosition;
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
       
    }

}
