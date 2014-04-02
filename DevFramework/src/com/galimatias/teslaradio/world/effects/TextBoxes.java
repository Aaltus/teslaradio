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
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
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
                             Vector3f updatedTranslation, 
                             Quaternion updatedRotation,
                             ColorRGBA updatedColor)
    {
        text.setSize(updatedSize);
        text.setText(updatedText);
        text.setLocalTranslation(updatedTranslation);
        text.rotate(updatedRotation);
        text.setColor(updatedColor);
    }
    
    public void initText(String textToDisplay, 
                         float size, 
                         Vector3f translation, 
                         Quaternion rotation, 
                         ColorRGBA color)
    {
        float width = text.getLineWidth();
        float height = text.getLineHeight();
                
        Rectangle rect = new Rectangle(translation.x, translation.y, width, height);
        
        text.setBox(rect);
        text.setSize(size);
        text.setText(textToDisplay);
        text.setLocalTranslation(translation);
        text.rotate(rotation);
        text.setColor(color);
        text.setAlpha(0.5f);
        this.attachChild(text);
    }
}
