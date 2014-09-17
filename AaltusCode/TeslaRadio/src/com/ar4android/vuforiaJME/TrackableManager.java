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
    }

    public void updateRotationMatrix(Integer id, Matrix3f rotMatrix, Vector3f vx)
    {
        this.mNodeList.get(id).getControl(TrackableControl.class).updateRotationMatrix(rotMatrix,vx);
        this.updateDistance();
    }

    public void updateVisibility(int id, boolean isVisible)
    {

            this.mTrackableState[id] = isVisible;

    }

    private void updateDistance()
    {
        this.mTrackableA.getControl(TrackableControl.class).updateDistance(
                this.mTrackableB.getControl(TrackableControl.class).getPosition());

        this.mTrackableB.getControl(TrackableControl.class).updateDistance(
                this.mTrackableA.getControl(TrackableControl.class).getPosition());

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
                    ((Node)this.spatial).attachChild(n);
              }else {
                    ((Node)this.spatial).detachChild(n);
              }
                }

        }
    }

    @Override
    protected void controlRender(RenderManager renderManager, ViewPort viewPort) {

    }
}
