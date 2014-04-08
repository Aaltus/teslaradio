package com.galimatias.teslaradio.world.effects;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
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
        text.setSize(updatedSize);
        text.setText(updatedText);
        text.setColor(updatedColor);
        this.lookAt(cam.getLocation(), cam.getUp());
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
                
        float boxDepth = 0.5f;
        Box rect = new Box(width-10.0f, height-5.0f, boxDepth);
        Geometry geomRect = new Geometry("textBox", rect);
        geomRect.move(width / 2 + 5.0f, -height / 2, -2 * boxDepth - 20.0f);

        Material boxMat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        boxMat.setColor("Color", new ColorRGBA((float)0x21/0xFF,(float)0x21/0xFF,(float)0x21/0xFF,(float)0xCC/0xFF));
        boxMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geomRect.setMaterial(boxMat);

        text.setQueueBucket(Bucket.Transparent);
        geomRect.setQueueBucket(Bucket.Transparent);
        
        this.move(translation);
        this.rotate(rotation);
        text.setColor(color);

        this.attachChild(geomRect);
        this.attachChild(text);
    }
}
