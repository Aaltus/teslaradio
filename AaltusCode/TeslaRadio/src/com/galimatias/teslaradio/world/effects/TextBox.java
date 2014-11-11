package com.galimatias.teslaradio.world.effects;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

/**
 * Create a floating textbox that is rotating in space and face the camera
 *
 *
 * Created by Alexandre Hamel on 4/8/14.
 * Modified by Jonathan Desmarais on 4/17/14
 */
public class TextBox extends Node {

    /**
     * Jme3 BitmapText object used to show the text
     */
    private BitmapText text;
    /**
     * Jme3 BitmapFont that will applied to the text
     */
    private BitmapFont guiFont;
    /**
     * An invisible box goemetry that is on top of the text to
     * make it clickable/touchable.
     */
    private Geometry overTouchBox;
    private Geometry backgroundBox;
    
    private boolean lookAtCamera = false;
    
    public TextBox(AssetManager assetManager, 
                   String textToDisplay, 
                   float size, 
                   ColorRGBA color,  
                   ColorRGBA backgroundColor, 
                   float textBoxWidth, 
                   float textBoxHeight, 
                   String textBoxName, 
                   BitmapFont.Align alignment, 
                   boolean showBoxDebug,
                   boolean lookAtCamera)
    {
        this.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Helvetica.fnt");
        fixFont(guiFont);
        text = new BitmapText(guiFont, false);
        this.setName(textBoxName);
        init(assetManager, 
                textToDisplay, 
                size, 
                color, 
                backgroundColor, 
                textBoxWidth, 
                textBoxHeight, 
                alignment, 
                showBoxDebug, 
                lookAtCamera);
    }

    /**
     * Update always called  to update the text, color, or size of the text as runtime
     * This function also make the text always facing the camera that is passed as parameter.
     *
     * @param updatedText
     * @param updatedSize
     * @param updatedColor
     * @param cam
     * @param scenarioUpVector
     */
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
        
        if(lookAtCamera){
            this.lookAt(cam.getLocation(), scenarioUpVector);
        }
    }
    
    //Source : http://hub.jmonkeyengine.org/forum/topic/how-to-make-an-interactive-texture-like-computer-screen/page/2/
    /*
     The easiest way to solve this is with a custom GeometryComparator for the
     * transparent bucket that will always sort the panel behind
     * (earlier in the list) than the text.
     * You still need a bit of offset to prevent Z-fighting but proper sorting
     * will prevent the problem you see.
     * 
    * Another thing that will minimize it is to grab the materials from the font
    * and set alpha test on. JME, for whatever reason, hard codes BitmapFont to
    * use the unshaded.j3md file directly instead of a font.j3m or something…
    * so this has to be fixed in code. The font loader doesn’t do it but you can.

    * Here is the method I use to make fonts work right in my own code:


    void fixFont( BitmapFont font ) {
        for( int i = 0; i < font.getPageSize(); i++ ) {
            Material m = font.getPage(i);
            m.getAdditionalRenderState().setAlphaTest(true);
            m.getAdditionalRenderState().setAlphaFallOff(0.1f);
        }
    }
    * I’ve been too lazy and worried about unintended side-effects to fix
    * that in the font loader.

    * Anyway, you will still need to fix the sorting issue as outlined above…
    * but the alpha test stuff will minimize the issue if it still crops up…
    * and more importantly, it keeps the individual letters from z-fighting
    * where they overlap.
     */
    /**
     * This function fix the transparency problem on the textbox
     * @param font 
     */
    void fixFont( BitmapFont font ) {
        for( int i = 0; i < font.getPageSize(); i++ ) {
            Material m = font.getPage(i);
            m.getAdditionalRenderState().setAlphaTest(true);
            m.getAdditionalRenderState().setAlphaFallOff(0.1f);
        }
    }

    /**
     * Initialize the textbox with the provided parameters
     *
     * @param assetManager  : asset manager used for textures and fonts
     * @param textToDisplay : (String) that represent the text shown
     * @param size          : (float): Size of the text
     * @param color         : (ColorRGBA) Color of the text
     * @param textBoxWidth  : (float) TextBox width. The text will linewrap when bigged of the width
     * @param textBoxHeight : (float) TextBox width. The text will ooverflow the box when bigger than the height
     * @param alignment     : (BitmapFont.Align) : Alignment of the text in the textbox
     * @param showBoxDebug  : (Boolean) : Show a box in back of the text for debugging the box width and height
     */
    public void init(AssetManager assetManager,
                         String textToDisplay,
                         float size,
                         ColorRGBA color,
                         ColorRGBA backgroundColor,
                         float textBoxWidth,
                         float textBoxHeight,
                         BitmapFont.Align alignment,
                         boolean showBoxDebug,
                         boolean lookAtCamera)
    {
        this.lookAtCamera = lookAtCamera;
        
        //create text with the specified parameter
        text.setBox(new Rectangle(0, 0, textBoxWidth, textBoxHeight));
        text.setQueueBucket(Bucket.Translucent);
        text.setLineWrapMode(LineWrapMode.Word);
        text.setColor(color);
        text.setAlignment(alignment);
        text.setSize(size);
        text.setText(textToDisplay);
        //move the text to the center of the box to make it rotate around its center instaed of its left side
        text.move(-textBoxWidth /2.0f,0,0);
        
        //Add an invisible geometry in front of the textbox to make it clickable/touchable
        Quad quad    = new Quad(textBoxWidth, textBoxHeight);
        overTouchBox = new Geometry("quad", quad);
        
        Material boxMat;
        //Create a red box in behind  the text for debugging purpose
        if (showBoxDebug)
        {
            //Red box for debugging purpose
            boxMat = assetManager.loadMaterial("Common/Materials/RedColor.j3m");
            overTouchBox.setLocalTranslation(0, -textBoxHeight, -0.01f);
        }
        //Create a invisible geometry in front of the text to make it clickable/touchable
        else
        {
            //Create a invisible geometry
            boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            boxMat.setColor("Color", new ColorRGBA(0, 0, 0, 0f));
            overTouchBox.setLocalTranslation(0, -textBoxHeight, 0.01f);
        }
        boxMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        boxMat.getAdditionalRenderState().setDepthWrite(false);
        overTouchBox.setQueueBucket(Bucket.Translucent);
        overTouchBox.setMaterial(boxMat);
        //move the box to the center of the box to make it rotate around its center instead of its left side
        overTouchBox.move(-textBoxWidth / 2.0f, 0, 0);
        
        
        Quad backgroundQuad = new Quad(textBoxWidth, textBoxHeight);
        backgroundBox       = new Geometry("backgoundBox", backgroundQuad);
        Material backgroundBoxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        backgroundBoxMat.setColor("Color", backgroundColor);
        backgroundBoxMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        backgroundBox.setQueueBucket(Bucket.Transparent);
        backgroundBox.setMaterial(backgroundBoxMat);
        backgroundBox.setLocalTranslation(0, -textBoxHeight, -0.01f);
        backgroundBox.move(-textBoxWidth / 2.0f, 0, 0);

        //Attach both element to the textBox node
        this.attachChild(backgroundBox);
        this.attachChild(overTouchBox);
        this.attachChild(text);
    }
}
