package com.galimatias.teslaradio.world.effects;

import com.galimatias.teslaradio.subject.ScenarioEnum;
import com.galimatias.teslaradio.world.observer.SignalObserver;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Greenwood0 on 2014-09-22.
 */
public class PrevSignalGenerator extends Node {

    private final int delayForIntrument = 2;
    private final float period = 0.25f;
    private final float speed = 3.5f;

    private SignalEmitter emitter;
    private ArrayList<Float> drumWaveMagn;
    private ArrayList<Float> guitWaveMagn;
    private float timeElapsed;
    private boolean drumGuitToggle = false;
    private AssetManager assetManager;
    private Geometry mainParticle;
    private Camera camera;


    public PrevSignalGenerator(AssetManager assetManager, Camera cam)
    {
        this.assetManager = assetManager;
        this.camera = cam;
        /**
         * TODO: Change this to be not hardcoded
         */
        // initializing Drum wave magnitudes
        drumWaveMagn = new ArrayList<Float>();
        drumWaveMagn.add(5f);
        drumWaveMagn.add(3f);
        drumWaveMagn.add(1f);

        // Initializing wave length
        guitWaveMagn = new ArrayList<Float>();
        guitWaveMagn.add(5f);
        guitWaveMagn.add(2f);
        guitWaveMagn.add(3f);
        guitWaveMagn.add(1.5f);
        guitWaveMagn.add(1.2f);
        guitWaveMagn.add(1.0f);
        guitWaveMagn.add(0.8f);

    }

    public void setSignal(ScenarioEnum scenarioEnum, SignalObserver observer)
    {
        Quad rect = new Quad(0.1f, 0.1f);
        mainParticle = new Geometry("particul",rect);

        System.out.println(scenarioEnum);
        System.out.println(observer);
        
        switch (scenarioEnum){
            case AMMODULATION:
            case FMMODULATION:
                emitter = new SignalEmitter(observer);
                Material electricParticleMat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
                electricParticleMat.setTexture("ColorMap", assetManager.loadTexture("Textures/Electric3.png"));
                mainParticle.setMaterial(electricParticleMat);
                this.attachChild(emitter);
                break;
            case SOUNDCAPTURE:
            default:
                emitter = null;
                break;
        }

        timeElapsed = 0;
    }

    public void simpleUpdate(float tpf, Vector3f receiverHandlePos)
    {
        timeElapsed += tpf;

        if (emitter != null){
            if (delayForIntrument <= timeElapsed && receiverHandlePos != null){
                System.out.println("Signal Generator stuff");
                this.setLocalTranslation(receiverHandlePos.getX(), receiverHandlePos.getY() - 10, receiverHandlePos.getZ());
                ArrayList<Float> magnitudes = drumGuitToggle ? guitWaveMagn : drumWaveMagn;
                emitter.setWaves(magnitudes, mainParticle, null, period, speed);
                emitter.prepareEmitParticles(receiverHandlePos);
                drumGuitToggle = !drumGuitToggle;
                timeElapsed = 0f;
            }
            emitter.simpleUpdate(tpf, this.camera);
        }
        
    }

}
