/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;

/**
 *
 * @author jimbojd72
 */

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.utils.AppLogger;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Controller;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.xml.xpp3.Attributes;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;


public class ScreenState extends AbstractAppState implements ScreenController, Controller{

    private final static String TAG = ScreenState.class.getSimpleName();
    
    private SimpleApplication app;
    private StateSwitcher stateSwitcher;
    private Nifty nifty;
    private Screen screen;
    private ViewPort viewPort;
    private AssetManager assetManager;
    private ViewPort guiViewPort;
    private AudioRenderer audioRenderer;
    private InputManager inputManager;
    private ScenarioCommon scenarioCommon = new ScenarioCommon();
    
    //For the loading screen
    private boolean load = false;
    private TextRenderer textRenderer;
    private Element progressBarElement;
    private final RenderManager renderManager;
    private float frameCount = 0;

    public ScreenState(SimpleApplication app, StateSwitcher state){
        this.app          = app;
        this.stateSwitcher= state;
        this.viewPort     = app.getViewPort();
        this.guiViewPort  = app.getGuiViewPort();
        this.assetManager = app.getAssetManager();  
        this.renderManager = app.getRenderManager();
        this.inputManager =  app.getInputManager();
        this.audioRenderer= app.getAudioRenderer();


        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, this.audioRenderer, viewPort);
        
        nifty = niftyDisplay.getNifty();
        //nifty.fromXml("Interface/StartScreen.xml", "start", this);
        nifty.fromXml("Interface/StartScreen.xml", "loadlevel", this);
        //nifty.getScreen("loadlevel").findElementByName("progressbar").;
        load = true;
        inputManager.setSimulateMouse(false); // must be false in order to the start screen to work.<
        this.guiViewPort.addProcessor(niftyDisplay);


      }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app){

        super.initialize(stateManager, app);
        
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
      //rootNode.attachChild(localRootNode);
      //guiNode.attachChild(localGuiNode);
      //viewPort.setBackgroundColor(backgroundColor);
        
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
      //rootNode.detachChild(localRootNode);
      //guiNode.detachChild(localGuiNode);
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        this.nifty  = nifty;
        this.screen = screen;
        this.progressBarElement = nifty.getScreen("loadlevel").findElementByName("progressbar");
    }
    
    @Override
    public void update(float tpf){
        
        if (load) { //loading is done over many frames
            if (frameCount == 1) {

                stateSwitcher.dismissSplashScreen();
                Element element = nifty.getScreen("loadlevel").findElementByName("loadingtext");
                textRenderer = element.getRenderer(TextRenderer.class);
                
                Demodulation demodulation = new Demodulation(this.scenarioCommon,null, null);
                demodulation.setName("Demodulation");
                setProgress(0.1f, "Loading " + demodulation.getName() + "...");
                renderManager.preloadScene(demodulation);
 
            } else if (frameCount == 2) {
                //Init Reception scenario
                Reception reception = new Reception(this.scenarioCommon,null,null);
                reception.setName("Reception");
                setProgress(0.2f, "Loading " + reception.getName() + "...");
                renderManager.preloadScene(reception);
                //scenarios.add(reception);

 
            } else if (frameCount == 3) {
                
                //Init Amplification scenario
                Amplification amplification = new Amplification(this.scenarioCommon,null,null);
                amplification.setName("Amplification");
                setProgress(0.3f, "Loading " + amplification.getName() + "...");
                renderManager.preloadScene(amplification);
                //scenarios.add(amplification);
 
            } else if (frameCount == 4) {
                //Init Modulation scenario
                Modulation modulation = new Modulation(this.scenarioCommon,null, null);
                modulation.setName("Modulation");
                setProgress(0.4f, "Loading " + modulation.getName() + "...");
                renderManager.preloadScene(modulation);
                //scenarios.add(modulation);
                //this.wait(5000L);
 
            } else if (frameCount == 5) {
                //Init SoundCapture scenario
                Scenario soundCapture = new SoundCapture(this.scenarioCommon,null,null);
                soundCapture.setName("SoundCapture");
                setProgress(0.5f, "Loading " + soundCapture.getName() + "...");
                renderManager.preloadScene(soundCapture);
                //scenarios.add(soundCapture);
 
            } else if (frameCount == 6) {
                // Init SoundEmission scenario
                SoundEmission soundEmission = new SoundEmission(this.scenarioCommon,null,null);
                soundEmission.setName("SoundEmission");
                setProgress(0.6f, "Loading " + soundEmission.getName() + "...");
                renderManager.preloadScene(soundEmission);
                            
            } else if (frameCount == 7) {
                // Init PlayBack scenario
                Playback playback = new Playback(this.scenarioCommon,null,null);
                playback.setName("Playback");
                setProgress(0.7f, "Loading " + playback.getName() + "...");
                renderManager.preloadScene(playback);
                
            } else if (frameCount == 8) {
                // Init PlayBack scenario
                Filter filter = new Filter(this.scenarioCommon,null,null);
                filter.setName("Filter");
                setProgress(0.8f, "Loading " + filter.getName() + "...");
                renderManager.preloadScene(filter);
 
            } else if (frameCount == 9) {
                setProgress(1f, "Loading finished");
                this.openStartMenu();
 
            }
 
            frameCount++;
        }
    
    }
    

    @Override
    public void onStartScreen() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onEndScreen() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void onStartButtonClick(){
        AppLogger.getInstance().d(TAG, "onStartButtonClick");
        this.stateSwitcher.startGame();
    }

    public void onTutorialButtonClick(){
        AppLogger.getInstance().d(TAG, "onStartTutorialClick");
        this.stateSwitcher.startTutorial();
    }

    public void onCreditsButtonClick(){
        AppLogger.getInstance().d(TAG, "onCreditsButtonClick");
        this.stateSwitcher.startCredits();
    }

    public void onEndGameClick() {
        AppLogger.getInstance().d(TAG, "onEndGameClick");
        stateSwitcher.endGame();
    }
    
    public void openStartMenu(){
        //this.app.getFlyByCamera().setDragToRotate(true);
        nifty.gotoScreen("start");
        inputManager.setSimulateMouse(false); // must be false in order to the start screen to work.
    }
    public void closeStartMenu(){
        //this.app.getFlyByCamera().setDragToRotate(true);
        nifty.gotoScreen("null");
        inputManager.setSimulateMouse(true); // must be false in order to the start screen to work.
    }
    
    
 
    
    public void setProgress(final float progress, final String loadingText) {
        //since this method is called from another thread, we enqueue the changes to the progressbar to the update loop thread
        //this.app.enqueue(new Callable() {
 
       //     public Object call() throws Exception {
                final int MIN_WIDTH = 32;
                int pixelWidth = (int) (MIN_WIDTH + (progressBarElement.getParent().getWidth() - MIN_WIDTH) * progress);
                progressBarElement.setConstraintWidth(new SizeValue(pixelWidth + "px"));
                progressBarElement.getParent().layoutElements();
 
                textRenderer.setText(loadingText);
        //    }
        //});
    }
 

    //Methods for progress bar
    @Override
    public void bind(Nifty nifty, Screen screen, Element element, Properties parameter, Attributes controlDefinitionAttributes) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        progressBarElement = element.findElementByName("progressbar");
    }

    @Override
    public void init(Properties parameter, Attributes controlDefinitionAttributes) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onFocus(boolean getFocus) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean inputEvent(NiftyInputEvent inputEvent) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return false;
    }

}
