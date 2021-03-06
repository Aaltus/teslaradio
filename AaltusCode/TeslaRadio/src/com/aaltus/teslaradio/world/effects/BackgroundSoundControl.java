/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaltus.teslaradio.world.effects;

import com.jme3.audio.AudioSource;
import com.utils.AppLogger;

/**
 *
 * @author Jean-Christophe
 */
public class BackgroundSoundControl extends SoundControl implements IBackgroundSoundCounter {
    
    public BackgroundSoundControl(String wavPath){
        super(wavPath,false,1);
    }
    
    @Override
    public void controlUpdate(float tpf){
        /*Fix for Android 5.0*/
      if( (this.audio.getStatus().compareTo(AudioSource.Status.Stopped))==0 &&
              this.audio.isLooping()  && this.isPlaying ) {
          this.audio.play();
      }
        if(counter.tryAcquire()){
           //  AppLogger.getInstance().e("NoiseControl", ((Float)this.spatial.getUserData(this.volumeUsrData)).toString());
          this.audio.setVolume((Float)this.spatial.getUserData(this.volumeUsrData));
        }
    }
}
