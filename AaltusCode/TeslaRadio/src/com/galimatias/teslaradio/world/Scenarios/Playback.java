/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.ar4android.vuforiaJME.AppGetter;
import com.galimatias.teslaradio.world.effects.AirParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.ParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.TextBox;
import com.jme3.font.BitmapFont;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Batcave
 */
public class Playback extends Scenario {
    
    private Node soundEmitter = new Node();
    
    private TextBox titleTextBox;
    private String titleText = "Envoi du son";
    private float titleTextSize = 0.5f;
    
    Playback(Camera Camera, Spatial destinationHandle) {
        
        super(Camera, destinationHandle);
        
        this.cam = Camera;
        this.destinationHandle = destinationHandle;
        
        loadUnmovableObjects();
        loadMovableObjects();
    }

    @Override
    protected void loadUnmovableObjects() {
        scene = (Node) AppGetter.getAssetManager().loadModel("Models/Playback/Speaker.j3o");
        scene.setName("Playback");
        this.attachChild(scene);        
        
        Material mat1 = new Material(assetManager, 
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", new ColorRGBA(1, 0, 1, 0.5f));
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        soundEmitter.addControl(new AirParticleEmitterControl(null, 20f, 13.0f, mat1, AirParticleEmitterControl.AreaType.DOME));
        soundEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);
    }

    @Override
    protected void loadMovableObjects() {
        
    }

    @Override
    public void restartScenario() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onScenarioTouch(String name, TouchEvent touchEvent, float v) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected boolean simpleUpdate(float tpf) {
        
        return false;
    }

    @Override
    public void setGlobalSpeed(float speed) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onAudioEvent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Spatial getInputHandle() {
        return soundEmitter;
    }

    @Override
    protected void initTitleBox() {
        
        boolean lookAtCamera = false;
        boolean showDebugBox = false;
        float textBoxWidth = 5.2f;
        float textBoxHeight = 0.8f;

        ColorRGBA titleTextColor = new ColorRGBA(1f, 1f, 1f, 1f);
        ColorRGBA titleBackColor = new ColorRGBA(0.1f, 0.1f, 0.1f, 0.5f);
        TextBox titleTextBox = new TextBox(assetManager,
                                    titleText, 
                                    titleTextSize,
                                    titleTextColor,
                                    titleBackColor,
                                    textBoxWidth,
                                    textBoxHeight,
                                    "titleText", 
                                    BitmapFont.Align.Center.Center,
                                    showDebugBox,
                                    lookAtCamera);

        //move the text on the ground without moving
        Vector3f titleTextPosition = new Vector3f(0f, 0.25f, 6f);
        titleTextBox.rotate((float) -Math.PI / 2, 0, 0);

        titleTextBox.move(titleTextPosition);
        this.attachChild(titleTextBox);
    }

    @Override
    public void signalEndOfPath(Geometry caller, float magnitude) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void initPatternGenerator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
