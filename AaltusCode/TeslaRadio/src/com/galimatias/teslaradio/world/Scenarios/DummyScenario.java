package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.Signal;
import com.galimatias.teslaradio.world.observer.ParticleEmitReceiveLinker;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * Created by jimbojd72 on 9/9/14.
 */
public class DummyScenario extends Scenario {

    ColorRGBA color;
    Geometry box;

    public DummyScenario(AssetManager assetManager, com.jme3.renderer.Camera Camera, ParticleEmitReceiveLinker particleLinker) {
        super(assetManager, Camera, particleLinker);
    }

    @Override
    public Vector3f getParticleReceiverHandle() {
        return null;
    }

    @Override
    public void sendSignalToEmitter(Geometry newSignal, float magnitude) {
        
    }

    public DummyScenario(AssetManager assetManager, com.jme3.renderer.Camera Camera, ColorRGBA color)
    {
        super(assetManager,Camera, null);
        this.color = color;
        loadUnmovableObjects();
        loadMovableObjects();
    }

    public DummyScenario(AssetManager assetManager, ColorRGBA color)
    {
        this(assetManager, null, color);
    }

    @Override
    protected void loadUnmovableObjects() {

        Box box1 = new Box(2,0.5f,1);
        box = new Geometry("Box", box1);
        box.setLocalTranslation(new Vector3f(0,0.5f,0));
        Material mat1 = new Material(assetManager, 
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", this.color);
        box.setMaterial(mat1);
        this.attachChild(this.box);
        
    }

    @Override
    protected void loadMovableObjects() {

    }

    @Override
    public void restartScenario() {

    }

    @Override
    public void onScenarioTouch(String name, TouchEvent touchEvent, float v) {

    }

    @Override
    public boolean simpleUpdate(float tpf) {
        return false;
    }

    @Override
    public void setGlobalSpeed(float speed) {

    }

    @Override
    public void onAudioEvent() {

    }

    @Override
    public void signalEndOfPath(Geometry caller, float magnitude) {

    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
