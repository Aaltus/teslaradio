/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaltus.teslaradio.world.Scenarios;

import com.aaltus.teslaradio.world.effects.ParticleEmitterControl;
import com.aaltus.teslaradio.world.effects.PatternGeneratorControl;
import com.aaltus.teslaradio.world.effects.TextBox;
import com.jme3.font.BitmapFont;
import com.jme3.math.Vector3f;
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
     protected boolean simpleUpdate(float tpf) {
         if(firstFrameDemodulation == true){
            switchRotation(isFM, 1);
            firstFrameDemodulation = false;
            firstFrameModulation = true;
        }
         simpleUpdateGeneral(tpf);

         return false;
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
        this.initDrumGuitarSound();
        Spatial baseGeom = scenarioCommon.initBaseGeneratorParticle();
        Spatial[] carrier = ScenarioCommon.initCarrierGeometries();
              
        Node signal = new Node();
        signal.attachChild(carrier[0].clone());
        signal.attachChild(baseGeom.clone());
        scenarioCommon.modulateFMorAM(signal, baseGeom, isFM);
        signal.setUserData("CarrierShape", signal.getChild(0).getName());
        signal.setUserData("isFM", isFM);

        
        this.getInputHandle().addControl(new PatternGeneratorControl(0.5f, signal.clone(), 7, scenarioCommon.minBaseParticleScale, 
                                                                     scenarioCommon.maxBaseParticleScale, true));  
    }

   @Override
    public void emitterObserverUpdate(Spatial spatial, String notifierId) {
        if (notifierId.equals("WirePCBEmitter")) {
            if (pcbAmpEmitter != null && spatial != null){ 
               if( selectedCarrier.getName().equals(((Node)spatial).getChild(0).getName()) 
                    && spatial.getUserData("isFM").equals(isFM)) {
                
                    ((Node)spatial).getChild(1).setLocalScale(((Node)spatial).getChild(1).getWorldScale());
                    pcbAmpEmitter.getControl(ParticleEmitterControl.class).emitParticle(((Node)spatial).getChild(1));
                    this.updateNoise(0,false);  
                }
               else{
                   this.updateNoise(1f);
               }
            }
            
        }
    }
    
    @Override
    public void autoGenObserverUpdate(Spatial newCarrier, boolean isFm) {
        //this.isFM = isFm;
        Node node = new Node();
        Spatial baseGeom = scenarioCommon.initBaseGeneratorParticle();
        node.attachChild(newCarrier.clone());
        node.attachChild(baseGeom);
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
               

