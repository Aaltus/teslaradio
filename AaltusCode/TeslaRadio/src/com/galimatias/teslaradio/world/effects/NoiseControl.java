/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.ar4android.vuforiaJME.AppGetter;

/**
 *
 * @author Jean-Christophe
 */
public class NoiseControl extends SoundControl {
   
    public NoiseControl(){
        super("Sounds/noise.ogg",false,1);
        this.volumeUsrData = AppGetter.USR_NOISE_LEVEL;
    }
    
     public void updateNoiseLevel(float noiseLevel){
        this.spatial.setUserData(AppGetter.USR_AUDIO_SCALE, 1 - noiseLevel);
        this.spatial.setUserData(AppGetter.USR_NOISE_LEVEL,noiseLevel);  
        this.volume = noiseLevel;
    }
}
