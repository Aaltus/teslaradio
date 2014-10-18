/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.ParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.TextBox;
import com.jme3.font.BitmapFont;
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
    
    Demodulation(Camera cam, Spatial destinationHandle) {
        
        super(cam, destinationHandle);
        
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
        
        TextBox titleTextBox = new TextBox(assetManager,
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
    public void emitterObserverUpdate(Spatial spatial, String notifierId) {
        if (notifierId.equals("CarrierEmitter")) {

            //System.out.println("I am in " + notifierId);
            //changeOuputParticles(spatial, notifierId);
            
        } else if (notifierId.equals("WirePCBEmitter")) {

            //System.out.println("I am in " + notifierId);
            
            if (pcbAmpEmitter != null && spatial != null) {
                Node clone = (Node)outputSignal.clone();
                
                //ScenariosCommon.modulateFMorAM(clone, spatial, isFM);
                
                clone.attachChild(((Node)spatial).getChild(1));
                
                //System.out.println("Scaling : " + spatial.getLocalScale().toString());
                pcbAmpEmitter.getControl(ParticleEmitterControl.class).emitParticle(clone);
            }
            
        }
    }
          
}
