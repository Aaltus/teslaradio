package com.ar4android.vuforiaJME;

import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jean-Christophe on 2014-09-10.
 */
public class TrackableManager {

    private Node mRootNode;
    private Node mTrackableA = new Node("TrackableA");
    private Node mTrackableB = new Node("TrackableB");
    private List<Node> mNodeList = new ArrayList<Node>();

    public List<Node> getNodeList(){return this.mNodeList;}
    public Node getRootNode(){return mRootNode;}

    public TrackableManager(Node rootNode)
    {
        this.mRootNode = rootNode;


    }
    public void init()
    {
        this.mNodeList.add(0, mTrackableA);
        this.mNodeList.add(1, mTrackableB);
        this.mTrackableA.addControl(new TrackableControl());
        this.mTrackableB.addControl(new TrackableControl());
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

    public void updateVisibility(Integer id, Boolean isVisible)
    {
        Boolean previousState = this.mNodeList.get(id).getControl(TrackableControl.class).getIsVisible();

        if(previousState != isVisible);
        {
            if(isVisible)
            {
                this.mRootNode.attachChild(this.mNodeList.get(id));
            }
            else
            {
                this.mRootNode.detachChild(this.mNodeList.get(id));
            }
            this.mNodeList.get(id).getControl(TrackableControl.class).setIsVisible(isVisible);
        }

    }

    private void updateDistance()
    {
        this.mTrackableA.getControl(TrackableControl.class).updateDistance(
                this.mTrackableB.getControl(TrackableControl.class).getPosition());

        this.mTrackableB.getControl(TrackableControl.class).updateDistance(
                this.mTrackableA.getControl(TrackableControl.class).getPosition());

    }
}
