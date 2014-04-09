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
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
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
        
        this.lookAt(cam.getLocation(), cam.getUp());
    }

    public void initDefaultText(String textToDisplay,
                         float size,
                         Vector3f translation,
                         Quaternion rotation,
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
        
        height = text.getHeight();
        width = text.getLineWidth();

        float boxDepth = 0.5f;
        Box rect = new Box((width+20.0f)/2.0f, (height+20.0f)/2.0f, boxDepth);
        Geometry geomRect = new Geometry("textBox", rect);
        geomRect.move(width / 2.0f, -height / 2.0f, -2.0f * boxDepth - 20.0f);

        Material boxMat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        boxMat.setColor("Color", new ColorRGBA((float)0x21/0xFF,(float)0x21/0xFF,(float)0x21/0xFF,(float)0xCC/0xFF));
        boxMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geomRect.setMaterial(boxMat);

        text.setQueueBucket(Bucket.Transparent);
        geomRect.setQueueBucket(Bucket.Transparent);
        
        this.move(translation);
        this.rotate(rotation);

        this.attachChild(geomRect);
        this.attachChild(text);
    }
}
