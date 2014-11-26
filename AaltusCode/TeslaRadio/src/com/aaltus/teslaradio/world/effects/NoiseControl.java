/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaltus.teslaradio.world.effects;

import com.ar4android.vuforiaJME.AppGetter;
import static com.aaltus.teslaradio.world.effects.IBackgroundSoundCounter.counter;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jean-Christophe
 */
public class NoiseControl extends BackgroundSoundControl{
   
    private int currentKey = 0;
    Random rand = new Random();
    public NoiseControl(){
        super("Sounds/noise.ogg");
        this.volumeUsrData = AppGetter.USR_NOISE_LEVEL;
        counter.drainPermits();
    }
    
     public void updateNoiseLevel(float noiseLevel){
        counter.drainPermits();
        this.spatial.setUserData(AppGetter.USR_AUDIO_SCALE, 1 - noiseLevel);
        this.spatial.setUserData(AppGetter.USR_NOISE_LEVEL,noiseLevel);  
        this.volume = noiseLevel;
        counter.release(2);
    }
     
     @Override
     public void updateVolume(float volume){
         super.updateVolume(volume);
         counter.drainPermits();
         counter.release(2);
     }
     
     public float getNoiseLevel(){
         return this.spatial.getUserData(AppGetter.USR_NOISE_LEVEL);
     }
}
