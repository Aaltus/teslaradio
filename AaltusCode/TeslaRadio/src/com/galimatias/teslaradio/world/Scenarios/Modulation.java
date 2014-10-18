package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.Arrows;
import com.galimatias.teslaradio.world.effects.DynamicWireParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.FadeControl;
import com.galimatias.teslaradio.world.effects.ImageBox;
import com.galimatias.teslaradio.world.effects.LookAtCameraControl;
import com.galimatias.teslaradio.world.effects.ParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.PatternGeneratorControl;
import com.galimatias.teslaradio.world.effects.StaticWireParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.TextBox;
import com.galimatias.teslaradio.world.observer.EmitterObserver;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.*;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Batcave on 2014-09-09.
 */
public final class Modulation extends ModulationCommon {
    
    private final static String TAG = "Modulation";
    
    // Default text to be seen when scenario starts
    private String titleText = "La Modulation";

    
    public Modulation(Camera Camera, Spatial destinationHandle) {
        
        super(Camera, destinationHandle);
        
        loadUnmovableObjects();
        loadMovableObjects();
        loadArrows();
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
    public void signalEndOfPath(Geometry caller, float magnitude) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //convert angle for range [0 ; 2pi]
    private float angleRangeTwoPi(float angle) {
        float resultat = 0;
        if (angle >= 0) {
            resultat = angle;
        } else {
            resultat = 2 * pi + angle;
        }
        return resultat;
    }


    /**
     * Called when receiving an event from the observable emitter
     *
     * @param spatial
     * @param notifierId
     */
    @Override
    public void emitterObserverUpdate(Spatial spatial, String notifierId) {
        
        if (notifierId.equals("CarrierEmitter")) {

            //System.out.println("I am in " + notifierId);
            changeOuputParticles(spatial, notifierId);
            
        } else if (notifierId.equals("WirePCBEmitter")) {

            //System.out.println("I am in " + notifierId);
            
            if (pcbAmpEmitter != null && spatial != null) {
                Node clone = (Node)outputSignal.clone();
                
                ScenariosCommon.modulateFMorAM(clone, spatial, isFM);
                
                clone.attachChild(spatial);
                
                //System.out.println("Scaling : " + spatial.getLocalScale().toString());
                pcbAmpEmitter.getControl(ParticleEmitterControl.class).emitParticle(clone);
                clone.setUserData("CarrierShape", outputSignal.getChild(0).getName());
                clone.setUserData("isFM", isFM);
            }
            
        }
    }
}
