/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

import com.ar4android.vuforiaJME.AppGetter;
import com.galimatias.teslaradio.subject.SongEnum;
import com.galimatias.teslaradio.world.effects.NoiseControl;
import com.galimatias.teslaradio.world.effects.SoundControl;
import com.jme3.scene.Node;
import com.jme3.scene.control.Control;
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
        SoundControl song1 = new SoundControl("Sounds/SongClassic.ogg",false,1);
        SoundControl song2 = new SoundControl("Sounds/SongElek.ogg",false,1);
        SoundControl song3 = new SoundControl("Sounds/SongRock.ogg",false,1);
        
        songMap = new EnumMap<SongEnum,SoundControl>(SongEnum.class);
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
        
        song1.setEnabled(false);
        song2.setEnabled(false);
        song3.setEnabled(false);
        
        noise.updateNoiseLevel(0);
    }
    
    public Node getAudioNode(){
        return audioNode;
    }
    
    public NoiseControl getNoiseControl(){
       return (NoiseControl) songMap.get(SongEnum.NOISE);
    }
    
    public void setNewSong(SongEnum value){
        SoundControl current = (SoundControl)songMap.get(SongEnum.SELECTED);
        current.stopSound();
        current.setEnabled(false);
        
        SoundControl newSound = (SoundControl) songMap.get(value);
        newSound.playSound(true);
        newSound.setEnabled(true);
        
        songMap.put(SongEnum.SELECTED, newSound);
        
    }
    
    public void stopSong(){
        ((SoundControl)songMap.get(SongEnum.SELECTED)).stopSound();
        ((SoundControl)songMap.get(SongEnum.NOISE)).stopSound();
    }
     
    public void playSong(){
        ((SoundControl)songMap.get(SongEnum.SELECTED)).playSound(true);
        ((SoundControl)songMap.get(SongEnum.NOISE)).playSound(true);
    }
    
    public void playSong(SongEnum value){
        setNewSong(value);
        ((SoundControl)songMap.get(SongEnum.NOISE)).playSound(true);
    }
    
    
}
