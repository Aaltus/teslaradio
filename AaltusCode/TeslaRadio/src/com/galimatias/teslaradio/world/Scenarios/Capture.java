package com.galimatias.teslaradio.world.Scenarios;

import android.util.Log;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * Created by Alexandre Hamel on 3/24/14.
 */
public class Capture extends Scenario {

    private final static String TAG = "Capture";

    private Spatial ninja;

    private AnimControl mAniControl;
    private AnimChannel mAniChannel;

    public Capture(AssetManager assetManager)
    {
        super(assetManager);

        Log.d(TAG, "Capture : ");
        loadStaticAnimatedObjects();
    }

    @Override
    protected void loadStaticAnimatedObjects()
    {
        // Load a model from test_data (OgreXML + material + texture)
        ninja = assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
        ninja.setName("ninja");

        initNinja();

        unmovableObjects.attachChild(ninja);
    }

    @Override
    protected void loadMovableAnimatedObjects() {

    }

    private void initNinja()
    {
        ninja.scale(5.0f, 5.0f, 5.0f);
        Quaternion rotateNinjaX=new Quaternion();
        rotateNinjaX.fromAngleAxis(3.14f/2.0f,new Vector3f(1.0f,0.0f,0.0f));
        Quaternion rotateNinjaZ=new Quaternion();
        rotateNinjaZ.fromAngleAxis(3.14f, new Vector3f(0.0f,0.0f,1.0f));
        Quaternion rotateNinjaY=new Quaternion();
        rotateNinjaY.fromAngleAxis(3.14f,new Vector3f(0.0f,1.0f,0.0f));

        rotateNinjaX.mult(rotateNinjaZ);
        Quaternion rotateNinjaXZ=rotateNinjaZ.mult(rotateNinjaX);
        Quaternion rotateNinjaXYZ = rotateNinjaXZ.mult(rotateNinjaY);

        ninja.rotate(rotateNinjaXYZ);

        //3.14/2.,new Vector3f(1.0.,0.0,1.0)));
        ninja.rotate(0.0f, -3.0f, 0.0f);
        ninja.setLocalTranslation(0.0f, 0.0f, 0.0f);

        this.attachChild(unmovableObjects);

        mAniControl = ninja.getControl(AnimControl.class);
        mAniControl.addListener(this);
        mAniChannel = mAniControl.createChannel();
        // show animation from beginning
        mAniChannel.setAnim("Walk");
        mAniChannel.setLoopMode(LoopMode.Loop);
        mAniChannel.setSpeed(1f);
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
