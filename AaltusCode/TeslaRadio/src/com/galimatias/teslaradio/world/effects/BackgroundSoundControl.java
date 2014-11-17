/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

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
        if(counter.tryAcquire()){
          this.audio.setVolume((Float)this.spatial.getUserData(this.volumeUsrData));
        }
    }
}
