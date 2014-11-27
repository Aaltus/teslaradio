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
        this.spatial.setUserData(AppGetter.USR_NOISE_LEVEL,noiseLevel);  
        this.volume = noiseLevel;
        this.updateVolume(1-noiseLevel);
    }
      public void updateNoiseLevel(float noiseLevel, boolean updateVolume){
        this.spatial.setUserData(AppGetter.USR_NOISE_LEVEL,noiseLevel);  
        this.volume = noiseLevel;
        if(updateVolume){
            this.updateVolume(1-noiseLevel);
        }
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
