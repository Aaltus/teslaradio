/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import java.util.concurrent.Semaphore;

/**
 *
 * @author Jean-Christophe
 */
public interface IBackgroundSoundCounter {
    static Semaphore counter = new Semaphore(2);
}
