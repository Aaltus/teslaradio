/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.ParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.PatternGeneratorControl;
import com.galimatias.teslaradio.world.effects.SoundControl;
import com.galimatias.teslaradio.world.effects.TextBox;
import com.jme3.font.BitmapFont;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.List;

/**
 *
 * @author Barliber
 */

public class Demodulation extends ModulationCommon  {

    private String titleText = "La Démodulation";
    
    private Node cubeSignal;
    private Node pyramidSignal;
    private Node dodecagoneSignal;
    public Demodulation(ScenarioCommon sc,com.jme3.renderer.Camera Camera, Spatial destinationHandle){
        super(sc,Camera, destinationHandle);
        this.setName("Demodulation");
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
        
        Spatial baseGeom = scenarioCommon.initBaseGeneratorParticle();
        Spatial[] carrier = scenarioCommon.initCarrierGeometries();   
              
        this.cubeSignal = new Node();
        this.cubeSignal.attachChild(carrier[0].clone());
        scenarioCommon.modulateFMorAM(this.cubeSignal, baseGeom, isFM);
        this.cubeSignal.attachChild(baseGeom.clone());
        this.cubeSignal.setUserData("CarrierShape", this.cubeSignal.getChild(0).getName());
        this.cubeSignal.setUserData("isFM", isFM);
        
        this.pyramidSignal = new Node();
        this.pyramidSignal.attachChild(carrier[1].clone());
        scenarioCommon.modulateFMorAM(this.pyramidSignal, baseGeom, isFM);
        this.pyramidSignal.attachChild(baseGeom.clone());
        this.pyramidSignal.setUserData("CarrierShape", this.pyramidSignal.getChild(0).getName());
        this.pyramidSignal.setUserData("isFM", isFM);
       
        this.dodecagoneSignal = new Node();
        this.dodecagoneSignal.attachChild(carrier[2].clone());
        scenarioCommon.modulateFMorAM(this.dodecagoneSignal, baseGeom, isFM);
        this.dodecagoneSignal.attachChild(baseGeom.clone());
        this.dodecagoneSignal.setUserData("CarrierShape", this.dodecagoneSignal.getChild(0).getName());
        this.dodecagoneSignal.setUserData("isFM", isFM);
        
        this.micTapParticle = this.cubeSignal;
        
        this.getInputHandle().addControl(new PatternGeneratorControl(0.5f, micTapParticle.clone(), 7, scenarioCommon.minBaseParticleScale, 
                                                                     scenarioCommon.maxBaseParticleScale, true));
    }

   @Override
    public void emitterObserverUpdate(Spatial spatial, String notifierId) {
        if (notifierId.equals("WirePCBEmitter")) {

            if (pcbAmpEmitter != null && spatial != null){ 
               if( selectedCarrier.getName().equals(((Node)spatial).getChild(0).getName()) 
                    && spatial.getUserData("isFM").equals(isFM)) {
                
                    ((Node)spatial).getChild(1).setLocalScale(((Node)spatial).getChild(1).getWorldScale());
                    outputSignal.attachChild(((Node)spatial).getChild(1));
                    pcbAmpEmitter.getControl(ParticleEmitterControl.class).emitParticle(outputSignal.clone());
                    if(!this.isFirst){
                        this.updateNoise(0f);
                    }
                }
               else{
                   this.updateNoise(1);
                 
               }
            }
            
        }
    }
    
    @Override
    public void autoGenObserverUpdate(Spatial newCarrier, boolean isFm) {
        this.isFM = isFm;
        Node node = new Node();
        Spatial baseGeom = scenarioCommon.initBaseGeneratorParticle();
        node.attachChild(newCarrier.clone());
        List<Spatial> lst = scenarioCommon.generateModulatedWaves(
               node , baseGeom, isFm, 10,scenarioCommon.minBaseParticleScale ,scenarioCommon.maxBaseParticleScale);
        
        this.getInputHandle().getControl(PatternGeneratorControl.class).setParticleList(lst);
    }
    
    @Override
    protected void objectEmphasis() {
        if (this.spotlight != null) {            
            switch(this.currentObjectToEmphasisOn) {
                // Attach on modulator
                case 0:
                    this.spotlight.setLocalTranslation(scene.getChild("Modulator").getLocalTranslation().add(0.0f,-scene.getChild("Modulator").getLocalTranslation().y,0.0f));
                    this.spotlight.setLocalScale(new Vector3f(3.0f,30.0f,3.0f));
                    scene.attachChild(this.spotlight);
                    break;  
                case 1:
                    this.spotlight.setLocalTranslation(scene.getChild("Switch").getLocalTranslation().add(0.0f,-scene.getChild("Switch").getLocalTranslation().y,0.0f));
                    this.spotlight.setLocalScale(new Vector3f(2.0f,30.0f,2.0f));
                    scene.attachChild(this.spotlight);
                    break;    
                default:
                    scene.detachChild(this.spotlight);
                    break;
            }
        }
    }

}
               

