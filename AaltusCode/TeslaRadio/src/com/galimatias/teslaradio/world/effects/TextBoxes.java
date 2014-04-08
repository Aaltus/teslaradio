/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galimatias.teslaradio.world.effects;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

/**
 *
 * @author Alexandre Hamel
 */
public class TextBoxes extends Node{
    
    private BitmapText text;
    private BitmapFont guiFont;
    
    public TextBoxes(AssetManager assetManager)
    {
        this.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        text = new BitmapText(guiFont, false);  
    }
    
    public void simpleUpdate(String updatedText, 
                             float updatedSize,
                             ColorRGBA updatedColor,
                             Camera cam)
    {
        text.setSize(updatedSize);
        text.setText(updatedText);
        text.setColor(updatedColor);
        text.lookAt(cam.getLocation(), cam.getUp());
    }
    
    public void initText(String textToDisplay, 
                         float size, 
                         Vector3f translation, 
                         Quaternion rotation, 
                         ColorRGBA color)
    {                      
        text.setSize(size);
        text.setText(textToDisplay);
        
        float width = text.getLineWidth();
        float height = text.getLineHeight();
        Rectangle rect = new Rectangle(0.0f, size, width, height);
        text.setBox(rect);
        
        this.move(translation);
        this.rotate(rotation);
        text.setColor(color);
        text.setAlpha(0.5f);
        this.attachChild(text);
    }
}
