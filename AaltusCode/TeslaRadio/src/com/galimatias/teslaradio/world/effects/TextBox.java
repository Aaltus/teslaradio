package com.galimatias.teslaradio.world.effects;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 * Created by Alexandre Hamel on 4/8/14.
 */
public class TextBox extends Node {

    private BitmapText text;
    private BitmapFont guiFont;
    private AssetManager assetManager;

    public TextBox(AssetManager assetManager)
    {
        this.assetManager = assetManager;
        this.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Helvetica.fnt");
        text = new BitmapText(guiFont, false);
    }

    public void simpleUpdate(String updatedText,
                             float updatedSize,
                             ColorRGBA updatedColor,
                             Camera cam)
    {
        if(updatedText != null)
            text.setText(updatedText);
        if(updatedSize != 0.0f)
            text.setSize(updatedSize);
        if(updatedColor != null)
            text.setColor(updatedColor);
        
        this.lookAt(cam.getLocation(), Vector3f.UNIT_Y);
    }

    public void initDefaultText(String textToDisplay,
                         float size,
                         Vector3f translation,
                         ColorRGBA color)
    {
        text.setText(textToDisplay);
        text.setLineWrapMode(LineWrapMode.Word);
        text.setSize(size);
        text.setColor(color);
        
        float width = 100.0f;
        float height = text.getHeight();

        Rectangle fontRect = new Rectangle(0.0f,0.0f,width,height);
        text.setBox(fontRect);
        text.setLocalTranslation(-(width/2.0f),0.0f,0.0f);
        text.setAlignment(BitmapFont.Align.Center);
        text.setQueueBucket(Bucket.Transparent);
        
        this.move(translation);
        this.rotate(-45, 45, -45);

        this.attachChild(text);
    }
}
