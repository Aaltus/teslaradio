/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Spline;
import com.jme3.math.Spline.SplineType;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.mesh.IndexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.Vector;

/**
 *
 * @author Alexandre Hamel
 */
public class SignalTrajectories {
    
    private int nbDirections;
    private int nbYXrotations;
    private Vector<Vector3f> trajectories = new Vector<Vector3f>();
        
    public SignalTrajectories(int nbDirections, int nbYXrotations)
    {
        this.nbDirections = nbDirections;
        this.nbYXrotations = nbYXrotations;
    }
    
    public SignalTrajectories()
    {
        
        
    }

    /**
     * Generates a bunch of sphere like directions vectors for generating a
     * particle emittter.
     * @param startPosition : The position of the first object in the world.
     * @param endPosition : The position of the receiving object in the world.
     */
    public void setTrajectories(Vector3f startDirection, float vectorNorms)
    {
        trajectories.clear();
        
        float XZmaxAngle = 360f;
        float YXmaxAngle = 90f;
       
        startDirection.normalize();
        
        //Algo to ensure a straight beam of particles along our starting signal
        //Find the vertical angle of startDirection
        float vAngle = (float) Math.asin(startDirection.y/startDirection.length());
        
        float XYAngleIncrement = ((YXmaxAngle/nbYXrotations)*2.0f*3.141592654f/360);
        float XZAngleIncrement = ((XZmaxAngle/(nbDirections/nbYXrotations))*2.0f*3.141592654f/360);
        
        float nIncrementInVertical = (float)Math.ceil(vAngle/XYAngleIncrement);
        float skewingFactor = vAngle/(nIncrementInVertical*XYAngleIncrement);
        XYAngleIncrement = XYAngleIncrement*skewingFactor;
        
        //Index of the main beam
        int iMainBeam = (int) nIncrementInVertical;
        
        Quaternion rotationPlanXY = new Quaternion();
        Quaternion rotationPlanXZ = new Quaternion();
        Quaternion normalRotation = new Quaternion();
        
        Matrix3f rotMatrixXY;
        Matrix3f rotMatrixXZ;
        Matrix3f rotMatrixNormal;
        
        startDirection.y = 0;
        trajectories.add(startDirection);
        
        Vector3f normalVector;
        Vector3f XZPlanVector;
        
        for(int i=0; i < nbDirections/nbYXrotations; i++)
        {                       
            rotationPlanXZ.fromAngleAxis(i*XZAngleIncrement, Vector3f.UNIT_Y);
            rotMatrixXZ = rotationPlanXZ.toRotationMatrix();
            XZPlanVector = rotMatrixXZ.mult(trajectories.get(0).normalizeLocal());
            
            normalRotation.fromAngleAxis(3.14f/2.0f, Vector3f.UNIT_Y);            
            rotMatrixNormal = normalRotation.toRotationMatrix();
            normalVector = rotMatrixNormal.mult(XZPlanVector.normalizeLocal());

            for(int j=0; j < nbYXrotations; j++)
            {                  
                rotationPlanXY.fromAngleAxis(-j*XYAngleIncrement, normalVector);

                rotMatrixXY = rotationPlanXY.toRotationMatrix();
                
                trajectories.add((rotMatrixXY.mult(XZPlanVector)).normalizeLocal().mult(vectorNorms));  
            }
        }
        
        trajectories.remove(0);
        
        Collections.swap(trajectories, 0, iMainBeam);
    }
    
    private Vector3f getControlPoint(int index, Mesh bezier_mesh)
    {
        Vector3f v1 = new Vector3f();
        Vector3f pathVector = new Vector3f();
        
        VertexBuffer pb = bezier_mesh.getBuffer(VertexBuffer.Type.Position);
        IndexBuffer ib = bezier_mesh.getIndicesAsList();
        if (pb != null && pb.getFormat() == VertexBuffer.Format.Float && pb.getNumComponents() == 3){
            FloatBuffer fpb = (FloatBuffer) pb.getData();

            int vert1 = ib.get(index);
           // int vert2 = ib.get(vertIndex+1);

            BufferUtils.populateFromBuffer(v1, fpb, vert1);
           // BufferUtils.populateFromBuffer(v2, fpb, vert2);
        }else{
            throw new UnsupportedOperationException("Position buffer not set or "
                                                  + " has incompatible format");
        }                
        
        
          pathVector.set(v1);
       // pathVector.set(v2);
      //  pathVector.subtractLocal(v1);
        
        return pathVector;
        
    }
    
    public Spline getCurvedPath(Mesh bezier_mesh)
    { 
        int nbVertex = bezier_mesh.getTriangleCount();
        Vector3f[] controlPoints = new Vector3f[nbVertex*2];
        

        for(int index =0; index < (nbVertex*2); index++)
        {
            controlPoints[index] = getControlPoint(index,bezier_mesh).mult(10f);
        }

        return new Spline(SplineType.Linear,controlPoints,1,false);
    }

    /**
     * Get the trajectories that were created.
     * @return trajectories
     */
    public Vector<Vector3f> getTrajectories()
    {
        return trajectories;
    }
}
