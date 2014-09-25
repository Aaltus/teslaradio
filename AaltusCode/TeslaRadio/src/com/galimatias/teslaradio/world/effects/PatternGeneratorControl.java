/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.ar4android.vuforiaJME.AppGetter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayDeque;
import java.util.ArrayList;
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
    protected ArrayDeque<Geometry> geomList = new ArrayDeque<Geometry>();
    protected List<Float>     scaleList;
    protected Geometry baseParticle;
    protected float maxScale;
    protected float minScale;
    protected int   scaleStep;
    protected int   waveIterator;
    protected Future autoPlayThread;
    
   /**
    * 
    * @param waveMinDelay The minimum delay accepted before sending a new wave
    * @param baseGeom The model particle
    * @param scaleStep The number of particles to go over the scale
    * @param minScale The smallest particle scacle
    * @param maxScale The biggest particle scale
    */
    public PatternGeneratorControl(float waveMinDelay, Geometry baseGeom,
            int scaleStep, float minScale, float maxScale){
        
        this.minWaveDelay = waveMinDelay;
        this.waveIterator = 0;
        this.lastCall = 0;
        this.baseParticle = baseGeom;
        
        this.scaleStep = scaleStep == 1 ? 1: scaleStep * 2 - 2;
        this.maxScale = maxScale;
        this.minScale = minScale;
        
        this.scaleList = new ArrayList<Float>();
        float step = (maxScale - minScale) / scaleStep;
        for(int i = 0; i < scaleStep;i++)
        {
            this.scaleList.add(this.minScale + i * step);
        }
        for(int i = scaleStep - 2; i > 0;i--)
        {
            this.scaleList.add(this.minScale + i * step);
        }
    }
    
   /**
    * 
    * @param waveMinDelay the minimum delay accepted before sending a wave
    * @param baseGeom The base particle
    * @param magnitudes The List of Magnitudes of the particles
    */
    public PatternGeneratorControl(float waveMinDelay,Geometry baseGeom,
            List<Float> magnitudes){
        this.minWaveDelay = waveMinDelay;
        this.waveIterator = 0;
        this.lastCall = 0;
        this.baseParticle = baseGeom;
        this.scaleList = magnitudes;
        this.scaleStep = magnitudes.size();
    }
   
    /**
     * 
     * @param delay The time in second before each emission. If lower than minimum delay,
     * minimum delay will be used
     */
    public void startAutoPlay(float delay){
        if(delay < this.minWaveDelay){
            delay = this.minWaveDelay;
        }
        this.autoWaveDelay = delay;
        this.autoPlayThread = AppGetter.getThreadExecutor().scheduleAtFixedRate(
                autoPlay,0,(long) (this.autoWaveDelay * 1000),TimeUnit.MILLISECONDS);
    }
    /**
     * Start autoplay with the minmum delay being the delay
     */
    public void startAutoPlay(){
        this.startAutoPlay(this.minWaveDelay);
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
    
    /**
     * Toggle a new emission wave according to inside parameter;
     */
    public void toggleNewWave()
    {
        Geometry geom = this.baseParticle.clone();
        geom.scale(this.scaleList.get(this.waveIterator));
        //The Queue will always have a size of 1 or 0, we don't want to queue
        //more than the minimum delay
        this.geomList.clear();
        this.geomList.addLast(geom);
        if(++this.waveIterator == this.scaleStep){
            this.waveIterator = 0;
        }
    }
    /**
     * Toggle a new emission wave with the specified scale;
     * @param scale Scale of the particle
     */
    public void toggleNewWave(float scale)
    {
        Geometry geom = this.baseParticle.clone();
        geom.scale(scale);
        //The Queue will always have a size of 1 or 0, we don't want to queue
        //more than the minimum delay
        this.geomList.clear();
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
                toggleNewWave();
            }
          
    };
}
