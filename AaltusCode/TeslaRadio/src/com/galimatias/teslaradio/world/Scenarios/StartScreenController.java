/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.Scenarios;


import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author jimbojd72
 */

public interface StartScreenController extends ScreenController {
    
    public void onStartButtonClick();
    public void onTutorialButtonClick();
    public void onCreditsButtonClick();
    public void onEndGameClick();
    
}
