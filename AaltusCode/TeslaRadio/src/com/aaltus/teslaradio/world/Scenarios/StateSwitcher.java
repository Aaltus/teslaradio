/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaltus.teslaradio.world.Scenarios;

/**
 *
 * @author jimbojd72
 */
public interface StateSwitcher {
    
    public void openStartScreen();
    public void startGame();
    public void startTutorial();
    public void startCredits();
    public void endGame();
    public void dismissSplashScreen();
    
}
