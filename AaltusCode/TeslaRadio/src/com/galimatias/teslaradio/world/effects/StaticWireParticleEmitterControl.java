/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.jme3.cinematic.MotionPath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.mesh.IndexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 *
 * @author Hugo
 */
public class StaticWireParticleEmitterControl extends ParticleEmitterControl {
    
    
    private MotionPath path;
   
    
    
    public StaticWireParticleEmitterControl(Mesh wirePathMesh, float speed)
    {
        spatialToSendFifo = new ArrayList();
        this.path = new MotionPath();
        
        //set the path from the given mesh
        setPathFromMesh(wirePathMesh);
    }

    @Override
    public void emitParticle(Spatial spatialToSend) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void pathUpdate() {
        //static path => do nothing here
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void observerUpdate(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void setPathFromMesh(Mesh bezier_mesh)
    { 
        // make sure there is no way points
        this.path.clearWayPoints();
  
        int nbVertex = bezier_mesh.getTriangleCount();
        for(int index =0; index < (nbVertex*2); index++)
        {
            this.path.addWayPoint(getControlPoint(index,bezier_mesh));
        }
    }  
    
    private Vector3f getControlPoint(int index, Mesh bezier_mesh)
    {
        Vector3f v1 = new Vector3f();
        Vector3f pathVector = new Vector3f();
        
        VertexBuffer pb = bezier_mesh.getBuffer(VertexBuffer.Type.Position);
        IndexBuffer ib = bezier_mesh.getIndicesAsList();
        if (pb != null && pb.getFormat() == VertexBuffer.Format.Float && pb.getNumComponents() == 3)
        {
            FloatBuffer fpb = (FloatBuffer) pb.getData();
            int vert1 = ib.get(index);
           // int vert2 = ib.get(vertIndex+1);
            BufferUtils.populateFromBuffer(v1, fpb, vert1);
           // BufferUtils.populateFromBuffer(v2, fpb, vert2);
        }
        else
        {
            throw new UnsupportedOperationException("Position buffer not set or "
                                                  + " has incompatible format");
        }                
        pathVector.set(v1);

        return pathVector;
    }    
}
