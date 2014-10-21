/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.ParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.PatternGeneratorControl;
import com.galimatias.teslaradio.world.effects.TextBox;
import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Barliber
 */

public class Demodulation extends ModulationCommon  {

    private String titleText = "La Démodulation";
    
    private Node cubeSignal;
    private Node pyramidSignal;
    private Node dodecagoneSignal;
    
    Demodulation(Camera cam, Spatial destinationHandle) {
        
        super(cam, destinationHandle, "Sounds/demodulation.ogg");
        
        loadUnmovableObjects();
        loadMovableObjects();
        loadArrows();
    }
    
    @Override
    protected void initOutputSignals() {
        
        super.initOutputSignals();
        
        this.outputSignal.detachAllChildren();
    }
    
    @Override
    public void restartScenario() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    protected void initTitleBox() {
        titleTextBox = new TextBox(assetManager, 
                                    titleText, 
                                    TEXTSIZE,
                                    TEXTCOLOR, 
                                    TEXTBOXCOLOR,
                                    TITLEWIDTH, 
                                    TITLEHEIGHT, 
                                    "titleText", 
                                    BitmapFont.Align.Center, 
                                    SHOWTEXTDEBUG, 
                                    TEXTLOOKATCAMERA);

        //move the text on the ground without moving
        Vector3f titleTextPosition = new Vector3f(0f, 0.25f, 6f);
        titleTextBox.rotate((float) -Math.PI / 2, 0, 0);

        titleTextBox.move(titleTextPosition);
        this.attachChild(titleTextBox);
    }

    @Override
    protected void initPatternGenerator() {
        
        Spatial baseGeom = ScenariosCommon.initBaseGeneratorParticle();
        Spatial[] carrier = ScenariosCommon.initCarrierGeometries();
              
        this.cubeSignal = new Node();
        this.cubeSignal.attachChild(carrier[0].clone());
        ScenariosCommon.modulateFMorAM(this.cubeSignal, baseGeom, isFM);
        this.cubeSignal.attachChild(baseGeom.clone());
        this.cubeSignal.setUserData("CarrierShape", this.cubeSignal.getChild(0).getName());
        this.cubeSignal.setUserData("isFM", isFM);
        
        this.pyramidSignal = new Node();
        this.pyramidSignal.attachChild(carrier[0].clone());
        ScenariosCommon.modulateFMorAM(this.pyramidSignal, baseGeom, isFM);
        this.pyramidSignal.attachChild(baseGeom.clone());
        this.pyramidSignal.setUserData("CarrierShape", this.pyramidSignal.getChild(0).getName());
        this.pyramidSignal.setUserData("isFM", isFM);
       
        this.dodecagoneSignal = new Node();
        this.dodecagoneSignal.attachChild(carrier[0].clone());
        ScenariosCommon.modulateFMorAM(this.dodecagoneSignal, baseGeom, isFM);
        this.dodecagoneSignal.attachChild(baseGeom.clone());
        this.dodecagoneSignal.setUserData("CarrierShape", this.dodecagoneSignal.getChild(0).getName());
        this.dodecagoneSignal.setUserData("isFM", isFM);
        
        this.micTapParticle = this.cubeSignal;
        
        this.getInputHandle().addControl(new PatternGeneratorControl(0.5f, micTapParticle.clone(), 7, ScenariosCommon.minBaseParticleScale, 
                                                                     ScenariosCommon.maxBaseParticleScale, true));
    }

    @Override
    public void emitterObserverUpdate(Spatial spatial, String notifierId) {
        if (notifierId.equals("WirePCBEmitter")) { 
            
            if (pcbAmpEmitter != null && spatial != null) {
               ((Node)spatial).getChild(1).setLocalScale(((Node)spatial).getChild(1).getWorldScale());
                outputSignal.attachChild(((Node)spatial).getChild(1));
                pcbAmpEmitter.getControl(ParticleEmitterControl.class).emitParticle(outputSignal.clone());
            }
            
        }
    }
    
    @Override
    public void autoGenObserverUpdate(Spatial newCarrier, boolean isFm) {
        this.isFM = isFm;
        this.initPatternGenerator();
        if(newCarrier.getName().equals("CubeCarrier")){
             this.getInputHandle().getControl(PatternGeneratorControl.class).setBaseParticle(this.cubeSignal);
        }
        else if(newCarrier.getName().equals("PyramidCarrier")){
            this.getInputHandle().getControl(PatternGeneratorControl.class).setBaseParticle(this.pyramidSignal);
        }
        else if(newCarrier.getName().equals("DodecagoneCarrier")){
            this.getInputHandle().getControl(PatternGeneratorControl.class).setBaseParticle(this.dodecagoneSignal);
            
        }
    }
}
               

