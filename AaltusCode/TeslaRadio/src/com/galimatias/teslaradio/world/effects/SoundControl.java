/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.ar4android.vuforiaJME.AppGetter;
import com.jme3.audio.AudioNode;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;


/**
 *
 * @author Jean-Christophe
 */
public class SoundControl extends AbstractControl {

    protected AudioNode  audio;
    protected float volume;
    protected String volumeUsrData;
    
    public SoundControl(String wavPath, boolean isStream, float volume){
        this.audio = new AudioNode(AppGetter.getAssetManager(),wavPath,isStream);
        this.audio.setVolume(volume);
        this.audio.setPositional(false);
        this.volume = volume;
        this.volumeUsrData = AppGetter.USR_AUDIO_SCALE;
    }
    /**
     * Start sound
     * @param isLoop Sound will loop
     * 
     */
    public void playSound(boolean isLoop){
       
        if(isLoop){
            this.audio.setLooping(isLoop);
        }
        this.audio.play();
    }
    public void stopSound(){
        this.audio.stop();     
    }
    
   
    public void updateVolume(float volume){
        this.spatial.setUserData(AppGetter.USR_AUDIO_SCALE, volume);
    }
    @Override
    protected void controlUpdate(float tpf) {
       
      if(this.spatial.getUserData(AppGetter.USR_NEW_WAVE_TOGGLED) ){
           float scale = this.spatial.getUserData(AppGetter.USR_NEXT_WAVE_SCALE);
           this.audio.setVolume(scale * (this.volume ) );
           this.audio.playInstance();
           this.spatial.setUserData(AppGetter.USR_NEW_WAVE_TOGGLED, false);
       }
      
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
   }
    
     @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial); //To change body of generated methods, choose Tools | Templates.
        this.spatial.setUserData(AppGetter.USR_NEW_WAVE_TOGGLED, false);
        this.spatial.setUserData(AppGetter.USR_NOISE_LEVEL, 0f);
        this.spatial.setUserData(AppGetter.USR_NEXT_WAVE_SCALE, 0f);
        this.spatial.setUserData(AppGetter.USR_SCALE, 1f);
        this.spatial.setUserData(AppGetter.USR_AUDIO_SCALE, 1f);
        
        ((Node)this.spatial).attachChild(audio);
        
  
    }
     
     
             
}
