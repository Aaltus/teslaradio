package com.galimatias.teslaradio.world.effects;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;

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
                             Camera cam,
                             Vector3f scenarioUpVector)
    {
        if(updatedText != null)
            text.setText(updatedText);
        if(updatedSize != 0.0f)
            text.setSize(updatedSize);
        if(updatedColor != null)
            text.setColor(updatedColor);
        
        this.lookAt(cam.getLocation(), scenarioUpVector);
    }

    public void initDefaultText(String textToDisplay,
                         float size,
                         Vector3f translation,
                         ColorRGBA color,
                         float width,
                         float height)
    {
        
        
        
        BitmapFont fnt = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText text = new BitmapText(fnt, false);
        text.setBox(new Rectangle(0, 0, width, height));
        text.setQueueBucket(Bucket.Transparent);
        text.setLineWrapMode(LineWrapMode.Word);
        text.setColor(color);
        text.setAlignment(BitmapFont.Align.Center);
        text.setSize( size );
        text.setText(textToDisplay);
        text.move(-width/2.0f,0,0);
        //text.setText("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.");
       
        Quad q = new Quad(width, height);
        Geometry g = new Geometry("quad", q);
        g.setLocalTranslation(0, -height, -0.0001f);
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //boxmat = assetManager.loadMaterial("Common/Materials/RedColor.j3m");
        //boxMat.setTexture("ColorMap", assetManager.loadTexture("Textures/ColoredTex/Monkey.png"));
        boxMat.setColor("Color", new ColorRGBA(0,0,0,0f));
        boxMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        g.setQueueBucket(Bucket.Transparent);
        g.setMaterial(boxMat);
        g.move(-width/2.0f,0,0);
        
        this.attachChild(g);
        this.attachChild(text);
        this.attachChild(fnt);
        this.move(translation);
        
        /*
        text.setText(textToDisplay);
        text.setLineWrapMode(LineWrapMode.Word);
        text.setSize(size);
        text.setColor(color);
        
        float width = 100.0f;
        float height = text.getHeight();

        Rectangle fontRect = new Rectangle(0.0f,0.0f,width,height);
        text.setBox(fontRect);
        //text.setLocalTranslation(-(width/2.0f),0.0f,0.0f);
        text.setAlignment(BitmapFont.Align.Center);
        text.setQueueBucket(Bucket.Transparent);
        
        //Add a box to make the textbox clickable 
        Quad q = new Quad(width, height);
        Geometry g = new Geometry("quad", q);
        g.setLocalTranslation(0, 0, -0.0001f);
        g.setMaterial(assetManager.loadMaterial("Common/Materials/RedColor.j3m"));
        this.attachChild(g);
        
        this.move(translation);

        this.attachChild(text);
        * */
    }
}
