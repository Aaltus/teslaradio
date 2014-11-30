/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaltus.teslaradio.world.Scenarios;

import com.ar4android.vuforiaJME.AppGetter;
import com.aaltus.teslaradio.subject.SongEnum;
import com.aaltus.teslaradio.world.effects.NoiseControl;
import com.aaltus.teslaradio.world.effects.BackgroundSoundControl;
import com.jme3.scene.Node;

import java.util.EnumMap;

/**
 *
 * @author Jean-Christophe
 */
public class SongManager {
    private EnumMap songMap;
    private Node audioNode; 
    private boolean audioPlaying;
    private SongEnum selectedSong;
    private boolean isInformativeMenu = false;

    public boolean isIsInformativeMenu() {
        return isInformativeMenu;
    }

    public void setIsInformativeMenu(boolean isInformativeMenu) {
        this.isInformativeMenu = isInformativeMenu;
    }
    
    
    public SongManager(){
        NoiseControl noise = new NoiseControl();
        BackgroundSoundControl song1 = new BackgroundSoundControl("Sounds/SongClassic.ogg");
        BackgroundSoundControl song2 = new BackgroundSoundControl("Sounds/SongElek.ogg");
        BackgroundSoundControl song3 = new BackgroundSoundControl("Sounds/SongRock.ogg");
        
        songMap = new EnumMap<SongEnum,BackgroundSoundControl>(SongEnum.class);
        songMap.put(SongEnum.CLASSIC, song1);
        songMap.put(SongEnum.ELEK, song2);
        songMap.put(SongEnum.ROCK,song3);
        songMap.put(SongEnum.NOISE, noise);
        selectedSong = SongEnum.ELEK;
        
        audioNode = new Node();
        audioNode.addControl(noise);
        audioNode.addControl(song1);
        audioNode.addControl(song2);
        audioNode.addControl(song3);
        
        song1.setEnabled(false);
        song2.setEnabled(true);
        song3.setEnabled(false);
        
        noise.updateNoiseLevel(0);
        this.audioNode.setUserData(AppGetter.USR_NOISE_LEVEL, 0f);
        this.audioNode.setUserData(AppGetter.USR_AUDIO_SCALE, 1f);
        
        this.audioPlaying = false;
        
   
    }
    
    public Node getAudioNode(){
        return audioNode;
    }
    
    public NoiseControl getNoiseControl(){
       return (NoiseControl) songMap.get(SongEnum.NOISE);
    }
    
    public void setNewSong(SongEnum value){
        BackgroundSoundControl current = (BackgroundSoundControl)songMap.get(this.selectedSong);
        current.stopSound();
        current.setEnabled(false);
        
        BackgroundSoundControl newSound = (BackgroundSoundControl) songMap.get(value);
        if(this.audioPlaying){
            newSound.playSound(true);
        }
        newSound.setEnabled(true);
        
       this.selectedSong = value;
        
    }
    
    public void stopSong(){
        ((BackgroundSoundControl)songMap.get(this.selectedSong)).stopSound();
        ((BackgroundSoundControl)songMap.get(SongEnum.NOISE)).stopSound();
        this.audioPlaying = false;
    }
       public void stopSong(boolean stopNoise){
        ((BackgroundSoundControl)songMap.get(this.selectedSong)).stopSound();
        if(stopNoise){
            ((BackgroundSoundControl)songMap.get(SongEnum.NOISE)).stopSound();
        }
        this.audioPlaying = false;
    }
     
    public void playSong(){
        ((BackgroundSoundControl)songMap.get(this.selectedSong)).playSound(true);
        ((BackgroundSoundControl)songMap.get(SongEnum.NOISE)).playSound(true);
        this.audioPlaying = true;
    }
    
    public void playSong(SongEnum value){
        setNewSong(value);
        ((BackgroundSoundControl)songMap.get(SongEnum.NOISE)).playSound(true);
    }
    
    public void ipodTouched(){
          if(this.audioPlaying){
              this.nextSong();
          }else{
              this.playSong();
          }
    }
    
    
    private void nextSong(){
        SongEnum newSong;
        switch(this.selectedSong){
            case ROCK:
                newSong = SongEnum.CLASSIC;
                break;
            case ELEK:
                newSong = SongEnum.ROCK;
                break;
            case CLASSIC:
                newSong = SongEnum.ELEK;
                break;
            default:
                newSong = SongEnum.ELEK;
        }
        this.setNewSong(newSong);
        
    }
    

}
