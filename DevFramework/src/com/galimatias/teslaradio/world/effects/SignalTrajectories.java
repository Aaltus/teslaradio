/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.List;
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
    
    /**
     * Generates a bunch of sphere like directions vectors for generating a
     * particle emittter.
     * @param startPosition : The position of the first object in the world.
     * @param endPosition : The position of the receiving object in the world.
     */
    public void setTrajectories(Vector3f startPosition, Vector3f endPosition)
    {
        trajectories.clear();
        
        int XZmaxAngle = 360;
        int YXmaxAngle = 180;
        
        Vector3f start2EndDirection = endPosition.subtract(startPosition);
        start2EndDirection.normalize();
        
        Quaternion rotationPlanXY = new Quaternion();
        Quaternion rotationPlanXZ = new Quaternion();
        Quaternion normalRotation = new Quaternion();
        
        Matrix3f rotMatrixXY = new Matrix3f();
        Matrix3f rotMatrixXZ = new Matrix3f();
        Matrix3f rotMatrixNormal = new Matrix3f();
        
        start2EndDirection.y = 0;
        trajectories.add(start2EndDirection);
        
        Vector3f normalVector = new Vector3f();
        Vector3f XZPlanVector = new Vector3f();
        
        int XYAngleIncrement = (int) ((YXmaxAngle/nbYXrotations)*2.0f*3.14f);
        int XZAngleIncrement = (int) ((XZmaxAngle/(nbDirections/nbYXrotations))*2.0f*3.14f);
        
        for(int i=0; i < nbDirections/nbYXrotations; i++)
        {                       
            rotationPlanXZ.fromAngleAxis(i*XYAngleIncrement, Vector3f.UNIT_Y);
            rotMatrixXY = rotationPlanXZ.toRotationMatrix();
            XZPlanVector = rotMatrixXY.mult(trajectories.get(i*nbYXrotations));
            
            normalRotation.fromAngleAxis(3.14f/2.0f, Vector3f.UNIT_Y);            
            rotMatrixNormal = normalRotation.toRotationMatrix();
            normalVector = rotMatrixNormal.mult(trajectories.get(i*nbYXrotations));
                        
            for(int j=0; j < nbYXrotations; j++)
            {                  
                rotationPlanXY.fromAngleAxis(j*XZAngleIncrement, normalVector);

                rotMatrixXZ = rotationPlanXY.toRotationMatrix();
                
                XZPlanVector.y = 0;
                trajectories.add(rotMatrixXZ.mult(XZPlanVector));
            }
        }
        
        trajectories.remove(0);
    }
    
    /**
     * Sets the norm of the direction vectors 
     * @param vectorNorms 
     */
    public void setVectorsNorms(List<Float> vectorNorms)
    {
        int vecNormsSize = vectorNorms.size();
        
        for(int i=0; i < vecNormsSize; i++)
        {
            for(int j=0; j < trajectories.size(); j++)
            { 
                trajectories.set(j, trajectories.get(j).mult(vectorNorms.get(j%vecNormsSize).floatValue()));
            }
        }
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
