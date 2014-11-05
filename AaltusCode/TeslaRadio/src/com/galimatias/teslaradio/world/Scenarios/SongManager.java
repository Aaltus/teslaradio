/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.ar4android.vuforiaJME.AppGetter;
import com.galimatias.teslaradio.subject.SongEnum;
import com.galimatias.teslaradio.world.effects.NoiseControl;
import com.galimatias.teslaradio.world.effects.BackgroundSoundControl;
import com.jme3.scene.Node;

import java.util.EnumMap;

/**
 *
 * @author Jean-Christophe
 */
public class SongManager {
    private EnumMap songMap;
    private Node audioNode;
    
    
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
        songMap.put(SongEnum.SELECTED, song1);
        
        audioNode = new Node();
        audioNode.addControl(noise);
        audioNode.addControl(song1);
        audioNode.addControl(song2);
        audioNode.addControl(song3);
        
        song1.setEnabled(true);
        song2.setEnabled(false);
        song3.setEnabled(false);
        
        noise.updateNoiseLevel(0);
        this.audioNode.setUserData(AppGetter.USR_NOISE_LEVEL, 0f);
        this.audioNode.setUserData(AppGetter.USR_AUDIO_SCALE, 1f);
    }
    
    public Node getAudioNode(){
        return audioNode;
    }
    
    public NoiseControl getNoiseControl(){
       return (NoiseControl) songMap.get(SongEnum.NOISE);
    }
    
    public void setNewSong(SongEnum value){
        BackgroundSoundControl current = (BackgroundSoundControl)songMap.get(SongEnum.SELECTED);
        current.stopSound();
        current.setEnabled(false);
        
        BackgroundSoundControl newSound = (BackgroundSoundControl) songMap.get(value);
        newSound.playSound(true);
        newSound.setEnabled(true);
        
        songMap.put(SongEnum.SELECTED, newSound);
        
    }
    
    public void stopSong(){
        ((BackgroundSoundControl)songMap.get(SongEnum.SELECTED)).stopSound();
        ((BackgroundSoundControl)songMap.get(SongEnum.NOISE)).stopSound();
    }
     
    public void playSong(){
        ((BackgroundSoundControl)songMap.get(SongEnum.SELECTED)).playSound(true);
        ((BackgroundSoundControl)songMap.get(SongEnum.NOISE)).playSound(true);
    }
    
    public void playSong(SongEnum value){
        setNewSong(value);
        ((BackgroundSoundControl)songMap.get(SongEnum.NOISE)).playSound(true);
    }
    
    
}
