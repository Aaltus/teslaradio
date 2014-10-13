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
import com.jme3.renderer.ViewPort;
import com.utils.AppLogger;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;


public class ScreenState extends AbstractAppState implements ScreenController{

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

    public ScreenState(SimpleApplication app, StateSwitcher state){
        this.app          = app;
        this.stateSwitcher= state;
        this.viewPort     = app.getViewPort();
        this.guiViewPort  = app.getGuiViewPort();
        this.assetManager = app.getAssetManager();  
        this.inputManager =  app.getInputManager();
        this.audioRenderer= app.getAudioRenderer();


        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, this.audioRenderer, viewPort);
        this.guiViewPort.addProcessor(niftyDisplay);
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/StartScreen.xml", "start", this);
        inputManager.setSimulateMouse(false); // must be false in order to the start screen to work.


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

}
