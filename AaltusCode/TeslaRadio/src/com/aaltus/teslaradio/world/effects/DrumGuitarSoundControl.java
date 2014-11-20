/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaltus.teslaradio.world.effects;

import com.ar4android.vuforiaJME.AppGetter;
import com.aaltus.teslaradio.subject.AudioOptionEnum;
import com.jme3.audio.AudioNode;

/**
 *
 * @author Jean-Christophe
 */
public class DrumGuitarSoundControl extends SoundControl {

    private AudioNode guitar;
    private AudioNode drum;
    public DrumGuitarSoundControl() {
        super();
        this.guitar = new AudioNode(AppGetter.getAssetManager(),"Sounds/guitar.wav");
        this.drum = new AudioNode(AppGetter.getAssetManager(),"Sounds/drum_taiko.wav");
        this.audio = guitar;
        this.volume = 3;
    }
    
    public void setNextInstrument(AudioOptionEnum newInstrument){
     switch(newInstrument){
         case GUITAR:
             this.audio = guitar;
             break;
         case DRUM:
             this.audio = drum;
             break;
         default:
     }    
    }
    
    /*@Override
    protected void controlUpdate(float tpf){
        super.controlUpdate(tpf);
    }*/
}
