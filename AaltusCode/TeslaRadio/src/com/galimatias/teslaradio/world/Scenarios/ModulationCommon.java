/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.ar4android.vuforiaJME.AppGetter;
import static com.galimatias.teslaradio.world.Scenarios.Scenario.DEBUG_ANGLE;
import com.galimatias.teslaradio.world.observer.AutoGenObserver;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains static methods used by Amplification and Modulation
 * @author Jean-Christophe
 */
public class ModulationCommon {
    
    private static List<AutoGenObserver> observerList = new ArrayList<AutoGenObserver>();
    
    public static float minBaseParticleScale = 0.25f;
    public static float maxBaseParticleScale = 0.75f;
    
    public static Geometry initBaseGeneratorParticle(){
        Geometry baseGeom;
        if (DEBUG_ANGLE) {
            Material mat1 = new Material(AppGetter.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
            //mat1.setColor("Color", new ColorRGBA(0.0f,0.0f,1.0f,0.0f));
            Texture nyan = AppGetter.getAssetManager().loadTexture("Textures/Nyan_Cat.png");
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
    public static Spatial[] initCarrierGeometries() {
        Spatial cubeCarrier;
        Spatial pyramidCarrier;
        Spatial dodecagoneCarrier;
        
        cubeCarrier = AppGetter.getAssetManager().loadModel("Models/Modulation/Cube.j3o");
        Material m = new Material(AppGetter.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        m.setTexture("ColorMap", AppGetter.getAssetManager().loadTexture("Models/Modulation/Edgemap_square.png"));
        m.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        cubeCarrier.setQueueBucket(RenderQueue.Bucket.Transparent);
        cubeCarrier.setMaterial(m);
        cubeCarrier.scale(0.3f);
        cubeCarrier.setName("CubeCarrier");
        
        pyramidCarrier = AppGetter.getAssetManager().loadModel("Models/Modulation/Tetrahedron.j3o");
        Material m2 = new Material(AppGetter.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        m2.setTexture("ColorMap", AppGetter.getAssetManager().loadTexture("Models/Modulation/Edgemap_triangle.png"));
        m2.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        pyramidCarrier.setQueueBucket(RenderQueue.Bucket.Transparent);
        pyramidCarrier.setMaterial(m2);
        pyramidCarrier.scale(0.4f);
        pyramidCarrier.setName("PyramidCarrier");
        
        dodecagoneCarrier = AppGetter.getAssetManager().loadModel("Models/Modulation/Dodecahedron.j3o");
        Material m3 = new Material(AppGetter.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        m3.setTexture("ColorMap", AppGetter.getAssetManager().loadTexture("Models/Modulation/Edgemap_square.png"));
        m3.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        dodecagoneCarrier.setQueueBucket(RenderQueue.Bucket.Transparent);
        dodecagoneCarrier.setMaterial(m3);
        dodecagoneCarrier.scale(0.35f);
        dodecagoneCarrier.setName("DodecagoneCarrier");
        
        return new Spatial[]{cubeCarrier,pyramidCarrier,dodecagoneCarrier};
    }
    
    public static void modulateFMorAM(Node clone, Spatial spatial, boolean isFm) {
        if (!isFm) {
            float scale = 1.25f;
            clone.getChild(0).setLocalScale(spatial.getLocalScale().mult(scale));
        } else {
            float scaleFactor = 1.25f;
            float midScaleValue = (ModulationCommon.minBaseParticleScale + ModulationCommon.maxBaseParticleScale)/2.0f;
            Vector3f midScale = new Vector3f(midScaleValue,midScaleValue,midScaleValue);
            
            if (spatial.getLocalScale().length() < midScale.length()) {
                scaleFactor = 0.75f;
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
            clone.scale(0.5f);
        }
    }

    
    public static void registerObserver(AutoGenObserver observer) {
        observerList.add(observer);
    }

    // observable method to notify whoever wants to know that a particle as ended his path
   
    public static void notifyObservers(Spatial newCarrier, boolean isFm) {
        for(AutoGenObserver observer : observerList)
        {
            observer.autoGenObserverUpdate(newCarrier, isFm);
        }        
    }
}
