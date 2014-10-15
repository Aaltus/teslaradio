/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.ar4android.vuforiaJME.AppGetter;
import static com.galimatias.teslaradio.world.Scenarios.Scenario.DEBUG_ANGLE;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Contains static methods used by Amplification and Modulation
 * @author Jean-Christophe
 */
public class ModulationCommon {
    
    public static Geometry initBaseGeneratorParticle(){
        Geometry baseGeom;
        if (DEBUG_ANGLE) {
            Material mat1 = new Material(AppGetter.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
            //mat1.setColor("Color", new ColorRGBA(0.0f,0.0f,1.0f,0.0f));
            Texture nyan = AppGetter.getAssetManager().loadTexture("Textures/Nyan_Cat.jpg");
            mat1.setTexture("ColorMap", nyan);
            mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            Quad rect = new Quad(1.0f, 1.0f);
            baseGeom = new Geometry("MicTapParticle", rect);
            baseGeom.setMaterial(mat1);            
        } else {
            Material mat1 = new Material(AppGetter.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
            mat1.setColor("Color", new ColorRGBA(0.0f,0.0f,1.0f,1.0f));
            mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            Sphere sphere = new Sphere(10, 10, 0.4f);
            baseGeom = new Geometry("MicTapParticle", sphere);
            baseGeom.setMaterial(mat1);
        }
        
        return baseGeom;
    }
    public static Geometry[] initCarrierGeometries() {
        Geometry cubeCarrier;
        Geometry pyramidCarrier;
        Geometry dodecagoneCarrier;
        
        Box cube = new Box(0.4f, 0.4f, 0.4f);
        cubeCarrier = new Geometry("CubeCarrier", cube);
        Material mat1 = new Material(AppGetter.getAssetManager(),
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", new ColorRGBA(1, 0, 1, 0.5f));
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        cubeCarrier.setMaterial(mat1);
        cubeCarrier.setQueueBucket(RenderQueue.Bucket.Transparent);
        cubeCarrier.setLocalTranslation(0.0f,0.4f,0.0f);
        
        Dome pyramid = new Dome(2, 4, 0.4f);
        pyramidCarrier = new Geometry("PyramidCarrier", pyramid);
        pyramidCarrier.setMaterial(mat1);
        pyramidCarrier.setQueueBucket(RenderQueue.Bucket.Transparent);
        pyramidCarrier.setLocalTranslation(0.0f,0.4f,0.0f);
        
        Node dodecagone = (Node) AppGetter.getAssetManager().loadModel("Models/Modulation/Dodecahedron.j3o");
        dodecagoneCarrier = (Geometry) dodecagone.getChild("Solid.0041");
        dodecagoneCarrier.scale(2.0f);
        dodecagoneCarrier.setName("DodecagoneCarrier");
        dodecagoneCarrier.setMaterial(mat1);
        dodecagoneCarrier.setQueueBucket(RenderQueue.Bucket.Transparent);
        dodecagoneCarrier.setLocalTranslation(0.0f,0.4f,0.0f);
        
        return new Geometry[]{cubeCarrier,pyramidCarrier,dodecagoneCarrier};
    }
    
    public static void modulateFMorAM(Node clone, Spatial spatial, boolean isFm) {
        if (!isFm) {
            float scale = 1.5f;
            clone.getChild(0).setLocalScale(spatial.getLocalScale().mult(scale));
        } else {
            float scaleFactor = 1.5f;
            Vector3f midScale = new Vector3f(0.5f,0.5f,0.5f);
            
            if (spatial.getLocalScale().length() < midScale.length()) {
                scaleFactor = 2.5f;
            } else {
                scaleFactor = 0.5f;
            }
            
            Vector3f scaleFM = spatial.getLocalScale().mult(new Vector3f(scaleFactor,1/scaleFactor,scaleFactor));
            
            if (scaleFM.x < spatial.getLocalScale().x || scaleFM.z < spatial.getLocalScale().z) {
                //System.out.println("Hello from too much scaling in x and z");
                scaleFM.x = spatial.getLocalScale().x;
                scaleFM.z = spatial.getLocalScale().z;
            } else if (scaleFM.y < spatial.getLocalScale().y) {
                //System.out.println("Hello from too much scaling in y");
                //System.out.println("Signal scale : " + spatial.getLocalScale().toString());
                //System.out.println("FM signal scale : " + scaleFM.toString());
                scaleFM.y = spatial.getLocalScale().y;
            }

            //System.out.println("New FM signal scale : " + scaleFM.toString());
            clone.getChild(0).setLocalScale(scaleFM);
        }
    }
}
