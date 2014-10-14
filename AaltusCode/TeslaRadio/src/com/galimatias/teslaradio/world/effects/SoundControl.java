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
    private final float volume;
    public SoundControl(String wavPath, boolean isStream, float volume){
        this.audio = new AudioNode(AppGetter.getAssetManager(),wavPath,isStream);
        this.audio.setVolume(volume);
        this.audio.setPositional(false);
        this.volume = volume;
      
    }
    
    public void playSound(boolean isLoop){
        ((Node)this.spatial).attachChild(this.audio);
        this.audio.setLooping(isLoop);
        this.audio.play();
    }
    
    public void stopSound(){
        this.audio.stop();
        ((Node)this.spatial).detachChild(this.audio);
    }
    
   
    @Override
    protected void controlUpdate(float tpf) {
       try{
           if( (boolean) this.spatial.getUserData(AppGetter.USR_NEW_WAVE_TOGGLED) ){
               float scale = (float) this.spatial.getUserData(AppGetter.USR_NEXT_WAVE_SCALE);
               this.audio.setVolume(scale * this.volume);
               this.audio.playInstance();
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
