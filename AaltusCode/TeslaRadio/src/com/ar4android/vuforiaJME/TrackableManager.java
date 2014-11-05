package com.ar4android.vuforiaJME;

import com.jme3.animation.Track;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jean-Christophe on 2014-09-10.
 */
public class TrackableManager extends AbstractControl {

    private final int TRACKABLE_NUMBER=2;
    private Node mTrackableA = new Node("TrackableA");
    private Node mTrackableB = new Node("TrackableB");
    private List<Node> mNodeList = new ArrayList<Node>();
    private List<Node> mChildNodeList = new ArrayList<Node>();
    private boolean[]  mTrackableState = new boolean[TRACKABLE_NUMBER];

    public List<Node> getScenarioNodeList()
    {
        return this.mChildNodeList;
    }

    public TrackableManager()
    {
        this.mTrackableA.addControl(new TrackableControl());
        this.mTrackableB.addControl(new TrackableControl());
        this.mNodeList.add(0, mTrackableA);
        this.mNodeList.add(1, mTrackableB);
        this.mChildNodeList.add(0,this.mTrackableA.getControl(TrackableControl.class).getFixedAngleChild());
        this.mChildNodeList.add(1,this.mTrackableB.getControl(TrackableControl.class).getFixedAngleChild());
    }

    public void updatePosition(Integer id, Vector3f position)
    {
        this.mNodeList.get(id).getControl(TrackableControl.class).updatePosition(position);

        if(!this.mNodeList.get((id+1)%TRACKABLE_NUMBER).getControl(TrackableControl.class).getIsVisible()) {
            // if the trackable is not in view
            if(id == 0) {
                this.mNodeList.get(1).getControl(TrackableControl.class).updatePosition(position.add((this.mNodeList.get(id).getControl(TrackableControl.class).getRotation().mult(Vector3f.UNIT_X)).mult(1600)));
            }
            else
            {
                this.mNodeList.get(0).getControl(TrackableControl.class).updatePosition(position.add((this.mNodeList.get(id).getControl(TrackableControl.class).getRotation().mult(Vector3f.UNIT_X)).mult(-1600)));
            }
        }
    }

    public void updateRotationMatrix(Integer id, Matrix3f rotMatrix, Vector3f vx)
    {
        this.mNodeList.get(id).getControl(TrackableControl.class).updateRotationMatrix(rotMatrix,vx);

        if(!this.mNodeList.get((id+1)%TRACKABLE_NUMBER).getControl(TrackableControl.class).getIsVisible()) {
            this.mNodeList.get((id+1)%TRACKABLE_NUMBER).getControl(TrackableControl.class).updateRotationMatrix(rotMatrix,vx);
        }

        this.updateDistance();
    }

    public void updateVisibility(int id, boolean isVisible)
    {

            this.mTrackableState[id] = isVisible;

    }

    private void updateDistance()
    {
        this.mTrackableA.getControl(TrackableControl.class).updateDistance(
                this.mTrackableB.getControl(TrackableControl.class).getPosition(),
                false); //is inverse? (false)

        this.mTrackableB.getControl(TrackableControl.class).updateDistance(
                this.mTrackableA.getControl(TrackableControl.class).getPosition(),
                true); // is inverse? (true)

    }

    @Override
    public  void setSpatial(Spatial spatial)
    {
        this.spatial = spatial;
    }
    @Override
    protected void controlUpdate(float v) {
        for(int i = 0; i < TRACKABLE_NUMBER;i++)
        {
            Node n = this.mNodeList.get(i);
            TrackableControl tb = n.getControl(TrackableControl.class);
            boolean isVisible = this.mTrackableState[i];
            boolean previousState = tb.getIsVisible();
            if (previousState != isVisible) {
              tb.setIsVisible(isVisible);
              if (isVisible) {

                  // attach to node if no more attach
                  if(!tb.getIsAttach()) {
                      ((Node) this.spatial).attachChild(n);
                      ((Node) this.spatial).attachChild(this.mNodeList.get((i+1)%TRACKABLE_NUMBER));

                      tb.setIsAttach(true);
                      this.mNodeList.get((i+1)%TRACKABLE_NUMBER).getControl(TrackableControl.class).setIsAttach(false);
                  }

              }else {

                  // detach only if no more trackable in sight
                  if(!this.mNodeList.get((i+1)%TRACKABLE_NUMBER).getControl(TrackableControl.class).getIsVisible()) {
                      ((Node) this.spatial).detachChild(n);
                      ((Node) this.spatial).detachChild(this.mNodeList.get((i+1)%TRACKABLE_NUMBER));

                      tb.setIsAttach(false);
                      this.mNodeList.get((i+1)%TRACKABLE_NUMBER).getControl(TrackableControl.class).setIsAttach(false);
                  }
              }

            }

        }
    }

    @Override
    protected void controlRender(RenderManager renderManager, ViewPort viewPort) {

    }
}
