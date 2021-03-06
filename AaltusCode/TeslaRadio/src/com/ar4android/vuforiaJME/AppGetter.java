package com.ar4android.vuforiaJME;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.utils.AppLogger;
import java.util.ResourceBundle;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by jimbojd72 on 9/23/14.
 */
public class AppGetter {
    private static AppGetter instance;
    private SimpleApplication app;
    private ScheduledThreadPoolExecutor executor;
    
    /*User Data Strings*/
    /**
     * The user data tag to set that a new wave has been toggled
     */
    static public final String USR_NEW_WAVE_TOGGLED = "newWaveToggled"; 
    /**
     * Scale of the current emission node
     */
    static public final String USR_NEXT_WAVE_SCALE = "nextWaveScale";
    
    static public final String USR_NOISE_LEVEL = "nextNoiseLevel";
    
    static public final String USR_SCALE = "Scale";
    
    static public final String USR_AUDIO_SCALE="Audio Scale";
    
    static public final String USR_SOURCE_TRANSLATION="Source Translation";
    
    static public final String USR_VOLUME_UPDATE="Volume update";
    
    static public final String USR_AMPLIFICATION="Amplification level";
    
    static public final String USR_FIXED_ANGLE_CHILD="Fixed Angle child";
    
    //world scalling
    private static float worldScaling = 10;

    public static AppSettings getAppSettings()
    {
        return instance.app.getContext().getSettings();
    }
    public static AssetManager getAssetManager()
    {
        return instance.app.getAssetManager();
    }
    public static AppStateManager getStateManager()
    {
        return instance.app.getStateManager();
    }
    public static InputManager getInputManager()
    {
        return instance.app.getInputManager();
    }
    public static ScheduledThreadPoolExecutor getThreadExecutor(){return instance.executor;}
    public static BulletAppState getBulletAppState()
    {
        return instance.app.getStateManager().getState(BulletAppState.class);
    }
    public static RenderManager getRenderManager()
    {
        return instance.app.getRenderManager();
    }
        
    public static Node getGuiNode()
    {
        return instance.app.getGuiNode();
    }
    public static boolean hasRootNodeAsAncestor(Spatial node)
    {
        return node.hasAncestor(instance.app.getRootNode());
    }
    public static void attachToRootNode(Spatial spatial)
    {
        instance.app.getRootNode().attachChild(spatial);
    }
    
    public static float getWorldScalingDefault()
    {
        return worldScaling;
    }
    public static void setWorldScaleDefault(float scale)
    {
        worldScaling = scale;
    }
    
    public static void setInstance(SimpleApplication app) {

        //Jonathan Desmarais: I commented stuff here because recreating the activity will crash because of this singleton
        //Since an application is recreated when we change language, we want it to become the new instance of the activity
        //See crash TR-319
        //if(instance == null)
        //{
            instance = new AppGetter(app);
            AppLogger.getInstance().i(AppGetter.class.getSimpleName(),"Initialize: " + AppGetter.class.getSimpleName());
            instance.executor = new ScheduledThreadPoolExecutor(4);
        //}
        //else{
        //    throw new RuntimeException("Can't initialized again an " + AppGetter.class.getSimpleName());
        //}
    }
    
    public static ResourceBundle getResourceBundle(){
        return ResourceBundle.getBundle("com.aaltus.teslaradio.Bundle");
    
    }

    public static AppGetter getInstance() {

        return instance;
    }

    private AppGetter(SimpleApplication app) {
        this.app = app;
    }
    
    public void stopThreadPool(){
        this.executor.shutdownNow();
    }
}
