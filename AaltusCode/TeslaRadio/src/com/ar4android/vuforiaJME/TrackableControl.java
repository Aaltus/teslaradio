package com.ar4android.vuforiaJME;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.FastMath;
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

    protected Node       mFixedAngleChild = null;
    protected Matrix3f   mRotationMatrix = new Matrix3f(0f,0f,0f,0f,0f,0f,0f,0f,0f);
    protected Quaternion mChildRotation = new Quaternion(0f,0f,0f,0f);
    protected Vector3f   mPosition = new Vector3f(0f,0f,0f);
    protected Vector3f   mVx = new Vector3f(0f,0f,0f);
    protected boolean    mIsVisible = false;

    public TrackableControl()
    {
        this.mFixedAngleChild = new Node("Node");
    }

    public Vector3f getPosition(){return this.mPosition;}
    public Node     getFixedAngleChild(){return this.mFixedAngleChild;}

    public void updatePosition( Vector3f newPosition)
    {
        this.mPosition.set(newPosition);
    }
    public void updateRotationMatrix(Matrix3f newMatrix, Vector3f newVx)
    {
        this.mRotationMatrix.set(newMatrix);
        this.mVx.set(newVx);

    }
    public void updateDistance(Vector3f distance, boolean vectorIsInverted)
    {
        Vector3f vectorAB;
        if(vectorIsInverted) {
            vectorAB = new Vector3f(distance.subtract(this.mPosition));
        }else {
            vectorAB = new Vector3f(this.mPosition.subtract(distance));
        }

        double angleX = Math.atan2(vectorAB.normalize().y,vectorAB.normalize().x) - Math.atan2(this.mVx.normalize().y,this.mVx.normalize().x);
        if(angleX < 0)
        {
            angleX += 2*Math.PI;
        }
        this.mChildRotation.fromAngleAxis((float)-angleX + FastMath.PI, new Vector3f(0,0,1));

        for(Spatial spatial : this.mFixedAngleChild.getChildren())
        {
            spatial.setUserData("angleX",(float)angleX);
        }
    }
    public void setIsVisible(Boolean isVisible)
    {
        this.mIsVisible = isVisible;
    }

    public boolean getIsVisible()
    {
        return this.mIsVisible;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        this.spatial = spatial;
        this.mFixedAngleChild.setName(this.spatial.getName().concat("_fixedAngleChild"));
        ((Node)this.spatial).attachChild(this.mFixedAngleChild);
    }

    @Override
    protected void controlUpdate(float v) {
        this.spatial.setLocalRotation(this.mRotationMatrix);
        this.spatial.setLocalTranslation(this.mPosition);

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
