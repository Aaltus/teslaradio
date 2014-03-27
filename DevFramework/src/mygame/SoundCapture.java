/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

/**
 *
 * @author Alexandre Hamel
 * 
 * This class contains all the models and the animations related to sound capture
 * 
 */
public class SoundCapture extends Scenario {

    private final static String TAG = "Capture";

    private Spatial tambour;

    public SoundCapture(AssetManager assetManager)
    {
        super(assetManager);

        loadStaticAnimatedObjects();
    }

    /**
     * Loading the models from the asset manager and attaching it to the
     * Node containing the unmovable objects in the scene.
     */
    @Override
    protected void loadStaticAnimatedObjects()
    {
        // Load a model from test_data (OgreXML + material + texture)
        tambour = assetManager.loadModel("Models/Tambour.j3o");
        tambour.setName("Tambour");
                
        unmovableObjects.attachChild(tambour);
    }

    /**
     * Loading the models from the asset manager and attaching it to the
     * Node containing the movable objects in the scene.
     */
    @Override
    protected void loadMovableAnimatedObjects()
    {

    }

    /**
     * Initiation of the tambour object.
     */
    private void initTambour()
    {
        tambour.scale(10.0f, 10.0f, 10.0f);
        Quaternion rotateTambourX=new Quaternion();
        rotateTambourX.fromAngleAxis(3.14f/2.0f,new Vector3f(1.0f,0.0f,0.0f));
        Quaternion rotateTambourZ=new Quaternion();
        rotateTambourZ.fromAngleAxis(3.14f, new Vector3f(0.0f,0.0f,1.0f));
        Quaternion rotateTambourY=new Quaternion();
        rotateTambourY.fromAngleAxis(3.14f,new Vector3f(0.0f,1.0f,0.0f));

        rotateTambourX.mult(rotateTambourZ);
        Quaternion rotateTambourXZ=rotateTambourZ.mult(rotateTambourX);
        Quaternion rotateTambourXYZ = rotateTambourXZ.mult(rotateTambourY);

        //tambour.rotate(rotateTambourXYZ);

        //3.14/2.,new Vector3f(1.0.,0.0,1.0)));
        tambour.rotate(0.0f, -3.0f, 0.0f);
        tambour.setLocalTranslation(0.0f, 0.0f, 0.0f);
        
        Material sphereMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        tambour.setMaterial(sphereMat);
       
        this.attachChild(unmovableObjects);
    }

    /**
     * Initialisation of all the objects within the scene.
     */
    public void initAll()
    {
        initTambour();
    }

    @Override
    public void onAnimCycleDone(AnimControl animControl, AnimChannel animChannel, String s) {
        // ...do nothing
    }

    @Override
    public void onAnimChange(AnimControl animControl, AnimChannel animChannel, String s) {
        // ...do nothing
    }


}