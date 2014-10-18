/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.ar4android.vuforiaJME.AppGetter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;




/**
 *
 * @author Jean-Christophe
 */
public class PatternGeneratorControl extends AbstractControl {
    protected float minWaveDelay;
    protected float autoWaveDelay;
    protected float lastCall;
    protected ArrayDeque<Spatial> geomList = new ArrayDeque<Spatial>();
    protected List<Float>     scaleList;
    protected Spatial baseParticle;
    protected float maxScale;
    protected float minScale;
    protected int   scaleStep;
    protected int   waveIterator;
    protected Future autoPlayThread;
    private final boolean isRandom;
    private int particlePerAutoWave;
    private String id;
    
   /**
    * 
    * @param waveMinDelay The minimum delay accepted before sending a new wave
    * @param baseGeom The model particle
    * @param scaleStep The number of particles to go over the scale
    * @param minScale The smallest particle scacle
    * @param maxScale The biggest particle scale
    * @param isRandom If set to true, the scale will be randomly taken from the list
    */
    public PatternGeneratorControl(float waveMinDelay, Spatial baseGeom,
            int scaleStep, float minScale, float maxScale, boolean isRandom ){
        
       // this.id = id;
        this.minWaveDelay = waveMinDelay;
        this.waveIterator = 0;
        this.lastCall = 0;
        this.baseParticle = baseGeom;
        this.isRandom = isRandom;
        
        this.scaleStep = scaleStep == 1 ? 1: scaleStep * 2 - 2;
        this.maxScale = maxScale;
        this.minScale = minScale;
        
        this.scaleList = new ArrayList<Float>();
        float step = (maxScale - minScale) / scaleStep;
        for(int i = scaleStep - 1; i > 0;i--)
        {
            this.scaleList.add(this.minScale + i * step);
        }
        for(int i = 0; i < scaleStep; i++)
        {
            this.scaleList.add(this.minScale + i * step);
        }
        if(this.isRandom){
            Collections.shuffle(this.scaleList);
        }

    }
    
   /**
    * 
    * @param waveMinDelay the minimum delay accepted before sending a wave
    * @param baseGeom The base particle
    * @param magnitudes The List of Magnitudes of the particles
    * @param isRandom If set to true, the scale will be randomly taken from the list
    */
    public PatternGeneratorControl(float waveMinDelay,Spatial baseGeom,
            List<Float> magnitudes, boolean isRandom){
        this.minWaveDelay = waveMinDelay;
        this.waveIterator = 0;
        this.lastCall = 0;
        this.baseParticle = baseGeom;
        this.scaleList = magnitudes;
        this.scaleStep = magnitudes.size();
        this.isRandom = isRandom;
        if(this.isRandom){
            Collections.shuffle(this.scaleList);
        }

    }
   
    /**
     * 
     * @param delay The time in second before each emission. If lower than minimum delay,
     * minimum delay will be used
     * @param particlePerWave particle per wave
     */
    public void startAutoPlay(float delay, int particlePerWave){
        if(delay < this.minWaveDelay){
            delay = this.minWaveDelay;
        }
        this.particlePerAutoWave = particlePerWave;
        this.autoWaveDelay = delay;
        this.autoPlayThread = AppGetter.getThreadExecutor().scheduleAtFixedRate(
                autoPlay,0,(long) (1000*(this.autoWaveDelay  + particlePerWave * this.minWaveDelay)),TimeUnit.MILLISECONDS);
    }
    /**
     * Start autoplay with the minmum delay being the delay
     */
    public void startAutoPlay(int particlePerWave){
        this.startAutoPlay(this.minWaveDelay, particlePerWave);
    }
    /**
     * Stop the autoplay
     */
    public void stopAutoPlay(){
        if(this.autoPlayThread != null){
            this.autoPlayThread.cancel(true);
            this.autoPlayThread = null;
        }
    }
    
    /** Toggle a new waveform with the inside parameter and wavesPerToggle objects
    * @param wavesPerToggle 
     */
    public void toggleNewWave(int wavesPerToggle)
    {   
        {
            this.geomList.clear();
            float scale = this.scaleList.get(this.waveIterator);
            this.spatial.setUserData(AppGetter.USR_NEW_WAVE_TOGGLED, true);
            this.spatial.setUserData(AppGetter.USR_NEXT_WAVE_SCALE, scale/this.maxScale);
        }
        
        for (int i=0; i<wavesPerToggle; i++) {
            float scale = this.scaleList.get(this.waveIterator);
            if(++this.waveIterator == this.scaleStep){
                this.waveIterator = 0;
                if(this.isRandom){
                   Collections.shuffle(this.scaleList);
                }
            }

            this.toggleNewWave(scale);
        }
     }
    
    /**
     * Toggle a new emission wave with the specified scale;
     * @param scale Scale of the particle
     */
    public void toggleNewWave(float scale)
    {
        Spatial geom = this.baseParticle.clone();
        geom.scale(scale);
        Float fs = geom.getWorldScale().length();
        geom.setUserData(AppGetter.USR_SCALE, fs);
        //The Queue will always have a size of 1 or 0, we don't want to queue
        //more than the minimum delay
        this.geomList.addLast(geom);
    }
    @Override
    protected void controlUpdate(float tpf) {
         
        
        if(this.lastCall < this.minWaveDelay)
        {
            this.lastCall += tpf;
            return;
        }
        if(  this.geomList.size() > 0)
        {
            ParticleEmitterControl emitter = this.spatial.getControl(
               ParticleEmitterControl.class);
            emitter.emitParticle(this.geomList.pollFirst()); 
            
        }
        this.lastCall = 0;
            
    }
    public void setBaseParticle(Spatial newParticle){
        this.baseParticle =  newParticle;
    }
    @Override 
    protected void controlRender(RenderManager rm, ViewPort vp) {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    /**
     * Runnable method is to be called periodically during the auto play
     */
    private Runnable autoPlay = new Runnable() {

        @Override
        
        public void run() {
               try{
                   toggleNewWave(particlePerAutoWave);
               }catch(Exception e){
                   //Empty, the interrupt...
               }
            }
          
    };
}
