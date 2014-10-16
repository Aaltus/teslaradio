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
import com.jme3.scene.control.AbstractControl;


/**
 *
 * @author Jean-Christophe
 */
public class SoundControl extends AbstractControl {

    AudioNode  audio;
    AudioNode  noise;
    private final float volume;
    public SoundControl(String wavPath, boolean isStream, float volume){
        this.audio = new AudioNode(AppGetter.getAssetManager(),wavPath,isStream);
        this.noise = new AudioNode(AppGetter.getAssetManager(),"Sounds/noise.wav",isStream);
        this.noise.setVolume(0f);
        this.audio.setVolume(volume);
        this.audio.setPositional(false);
        this.volume = volume;
        
      
    }
    /**
     * Start sound
     * @param isLoop Sound will loop
     * @param noiseLevel  The noise level (1 means only noise)
     */
    public void playSound(boolean isLoop, float noiseLevel){
        ((Node)this.spatial).attachChild(this.audio);
        ((Node)this.spatial).attachChild(this.noise);
        this.audio.setLooping(isLoop);
        this.noise.setLooping(isLoop);
        this.audio.setVolume(this.volume - noiseLevel*this.volume);
        this.noise.setVolume(noiseLevel*this.volume);
        this.audio.play();
        this.noise.play();
    }
    /**
     * Start sound
     * @param isLoop Sound will loop
     * 
     */
    public void playSound(boolean isLoop){
        ((Node)this.spatial).attachChild(this.audio);
        this.audio.setLooping(isLoop);
        this.audio.play();
        this.noise.setLooping(isLoop);
        this.noise.play();
  
    }
    public void stopSound(){
        this.audio.stop();
        ((Node)this.spatial).detachChild(this.audio);
    }
    
    public void updateNoiseLevel(float noiseLevel){
        this.audio.setVolume(this.volume - noiseLevel*this.volume);
        this.noise.setVolume(noiseLevel*this.volume);
    }
   
    @Override
    protected void controlUpdate(float tpf) {
       try{
           if(this.spatial.getUserData(AppGetter.USR_NEW_WAVE_TOGGLED) ){
               float scale = this.spatial.getUserData(AppGetter.USR_NEXT_WAVE_SCALE);
               float noise = this.spatial.getUserData(AppGetter.USR_NOISE_LEVEL);
               this.audio.setVolume(scale * (this.volume - noise*this.volume));
               this.noise.setVolume(scale * (noise*this.volume));
               this.audio.playInstance();
               this.noise.playInstance();
               this.spatial.setUserData(AppGetter.USR_NEW_WAVE_TOGGLED, false);
           }
       }catch(Exception e){
           /*Empty except, called if a user data is non existant*/
           
       }
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
   }
    
}
