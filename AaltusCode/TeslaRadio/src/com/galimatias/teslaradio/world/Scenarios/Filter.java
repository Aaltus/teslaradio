/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.galimatias.teslaradio.world.effects.DynamicWireParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.ParticleEmitterControl;
import com.galimatias.teslaradio.world.effects.PatternGeneratorControl;
import com.galimatias.teslaradio.world.effects.TextBox;
import com.galimatias.teslaradio.world.observer.EmitterObserver;
import com.jme3.font.BitmapFont;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Batcave
 */
public class Filter extends Scenario implements EmitterObserver {

    private float trackableAngle = 0;
    private float direction = 1;
    
    private Spatial filterWheel;
    
    private String titleText = "Le filtrage";
    
    //Pattern Geometry
    private Spatial micTapParticle;
    
    private Node inputEmitter = new Node();
    private Node outFilterEmitter = new Node();
    private Node outputEmitter = new Node();
    
    private Quaternion initAngleWheel = new Quaternion();
    private Quaternion endAngleWheel = new Quaternion();
    
    private int frequency = 0;
    
    private float tpfCumul = 0;
    
    Filter(Camera cam, Spatial destinationHandle) {
        
        super(cam, destinationHandle);
        
        this.needAutoGenIfMain = true;
    }
    
    @Override
    protected void loadUnmovableObjects() { 
        scene = (Node) assetManager.loadModel("Models/Modulation_Demodulation/modulation.j3o");
        scene.setName("Modulation_Demodulation");
        this.attachChild(scene);
        
        // Get the handles of the emitters
        Spatial pathInHandle = scene.getChild("Handle.Module.In");
        Spatial pathOutFilterHandle = scene.getChild("Handle.Chip.Out");
        Spatial outputHandle = scene.getChild("Handle.Module.Out");
        
        // Get the different paths
        Node wirePcb_node = (Node) scene.getChild("Path.In.Object");
        Geometry pathIn = (Geometry) wirePcb_node.getChild("Path.In.Nurbs");
        Node output_node = (Node) scene.getChild("Path.Out.Object");
        Geometry pathOut = (Geometry) output_node.getChild("Path.Out.Nurbs");
        
        initTitleBox();
        
        initStaticParticlesEmitter(inputEmitter, pathInHandle, pathIn, null);
        initStaticParticlesEmitter(outFilterEmitter, pathOutFilterHandle, pathOut, null);
        
        inputEmitter.setName("Input");
        outFilterEmitter.setName("Output Filter");
        outputEmitter.setName("Output");
        
        initPatternGenerator();
        
        scene.attachChild(outputEmitter);
        outputEmitter.setLocalTranslation(outputHandle.getLocalTranslation()); // TO DO: utiliser le object handle blender pour position
        outputEmitter.addControl(new DynamicWireParticleEmitterControl(this.destinationHandle, 3.5f, null));
        outputEmitter.getControl(ParticleEmitterControl.class).setEnabled(true);
        
        if(destinationHandle != null){
            inputEmitter.getControl(ParticleEmitterControl.class).registerObserver(this);
            outFilterEmitter.getControl(ParticleEmitterControl.class).registerObserver(outputEmitter.getControl(ParticleEmitterControl.class));
            outputEmitter.getControl(ParticleEmitterControl.class).registerObserver(this.destinationHandle.getControl(ParticleEmitterControl.class));
        }
    }

    @Override
    protected void loadMovableObjects() {
        filterWheel = scene.getChild("Button");
        
        initAngleWheel.fromAngleAxis(0f, Vector3f.UNIT_Y);
        endAngleWheel.fromAngleAxis(2f*pi/3f, Vector3f.UNIT_Y);
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
        tpfCumul += tpf;
        
        if (this.DEBUG_ANGLE) { //In Scenario class !!
            trackableAngle += direction * (pi / 9) * tpf;

            if (trackableAngle >= 2 * pi || trackableAngle <= 0) {
                //trackableAngle = 0;
                direction *= -1;
            }
        } else {
            //trackableAngle = 0;
            trackableAngle = this.getUserData("angleX");
        }
        
        checkTrackableAngle(trackableAngle);
                
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
        return inputEmitter;
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
    protected void initPatternGenerator() {
        micTapParticle = ScenariosCommon.initBaseGeneratorParticle();

        this.inputEmitter.addControl(new PatternGeneratorControl(0.5f, micTapParticle, 10, ScenariosCommon.minBaseParticleScale,
                ScenariosCommon.maxBaseParticleScale, true));    
    }

    @Override
    public void signalEndOfPath(Geometry caller, float magnitude) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void checkTrackableAngle(float trackableAngle) {

        float stepRange = 2 * pi / 3;

        if (trackableAngle >= 0 && trackableAngle < stepRange) {
            frequency = 1;
        } else if (trackableAngle >= stepRange && trackableAngle < 2 * stepRange) {
            frequency = 2;
        } else if (trackableAngle >= 2 * stepRange && trackableAngle < 3 * stepRange) {
            frequency = 3;
        }
        
        turnTunerButton(stepRange);
    }
    
    private void turnTunerButton(float stepRange) {

        Quaternion rot = new Quaternion();
        
        switch(frequency) {
            case 1:
                initAngleWheel.fromAngleAxis(0f, Vector3f.UNIT_Y);
                endAngleWheel.fromAngleAxis(stepRange, Vector3f.UNIT_Y);
                break;
            case 2:
                initAngleWheel.fromAngleAxis(stepRange, Vector3f.UNIT_Y);
                endAngleWheel.fromAngleAxis(2*stepRange, Vector3f.UNIT_Y);
                break;
            case 3:
                initAngleWheel.fromAngleAxis(2*stepRange, Vector3f.UNIT_Y);
                endAngleWheel.fromAngleAxis(3*stepRange, Vector3f.UNIT_Y);
                break;
            default:
                initAngleWheel.fromAngleAxis(0f, Vector3f.UNIT_Y);
                endAngleWheel.fromAngleAxis(stepRange, Vector3f.UNIT_Y);
                break;       
        }
        
        filterWheel.setLocalRotation(rot.slerp(initAngleWheel, endAngleWheel, tpfCumul));
    }
    
    private void filter(int frequency, Spatial spatial) {
        
        if (frequency != this.frequency) {
            
        } else {
            ((Node)spatial).detachChild(((Node)spatial).getChild(0));
        }
    }

    @Override
    public void emitterObserverUpdate(Spatial spatial, String notifierId) {
        if (notifierId.equals("Input")) {      
            filter((int)spatial.getUserData("Frequency"), spatial);
        }
    }
}
