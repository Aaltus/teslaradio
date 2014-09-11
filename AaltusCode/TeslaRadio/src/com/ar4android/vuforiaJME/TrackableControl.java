package com.ar4android.vuforiaJME;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.utils.AppLogger;

import java.io.IOException;

/**
 * Created by Jean-Christophe on 2014-09-10.
 */
public class TrackableControl extends AbstractControl {

    protected Node       mTrackable = null;
    protected Node       mFixedAngleChild = null;
    protected Matrix3f   mRotationMatrix = new Matrix3f(0f,0f,0f,0f,0f,0f,0f,0f,0f);
    protected Quaternion mChildRotation = new Quaternion(0f,0f,0f,0f);
    protected Vector3f   mPosition = new Vector3f(0f,0f,0f);
    protected Vector3f   mVx = new Vector3f(0f,0f,0f);
    protected Boolean    mIsVisible = false;

    public TrackableControl()
    {

    }

    public Vector3f getPosition(){return this.mPosition;}

    public void updatePosition( Vector3f newPosition)
    {
        this.mPosition.set(newPosition);
    }
    public void updateRotationMatrix(Matrix3f newMatrix, Vector3f newVx)
    {
        this.mRotationMatrix.set(newMatrix);
        this.mVx.set(newVx);
    }
    public void updateDistance(Vector3f distance)
    {
        Vector3f vectorAB = new Vector3f(this.mPosition.subtract(distance));
        Double angleX = Math.atan2(vectorAB.normalize().y,vectorAB.normalize().x) - Math.atan2(this.mVx.normalize().y,this.mVx.normalize().x);
        if(angleX < 0)
        {
            angleX += 2*Math.PI;
        }
        this.mChildRotation.fromAngleAxis((float)-angleX, new Vector3f(0,0,1));

        for(Spatial spatial : this.mFixedAngleChild.getChildren())
        {
            spatial.setUserData("angleX",angleX);
        }
    }
    public void setIsVisible(Boolean isVisible)
    {
        this.mIsVisible = isVisible;
    }

    public Boolean getIsVisible()
    {
        return this.mIsVisible;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        this.mTrackable = (Node) spatial;
        this.mFixedAngleChild = new Node(this.mTrackable.getName().concat("_fixedAngleChild"));
        this.mTrackable.attachChild(this.mFixedAngleChild);
    }

    @Override
    protected void controlUpdate(float v) {
        this.mTrackable.setLocalRotation(this.mRotationMatrix);
        this.mTrackable.setLocalTranslation(this.mPosition);

        this.mFixedAngleChild.setLocalRotation(this.mChildRotation);
    }

    @Override
    protected void controlRender(RenderManager renderManager, ViewPort viewPort) {

    }


    @Override
    public void write(JmeExporter jmeExporter) throws IOException {

    }

    @Override
    public void read(JmeImporter jmeImporter) throws IOException {

    }
}
