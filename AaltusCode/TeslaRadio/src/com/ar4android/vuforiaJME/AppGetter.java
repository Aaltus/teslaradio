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

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by jimbojd72 on 9/23/14.
 */
public class AppGetter {
    private static AppGetter instance;
    private SimpleApplication app;
    private ScheduledThreadPoolExecutor executor;

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
    
    
    
    public static void setInstance(SimpleApplication app) {

        if(instance == null)
        {
            instance = new AppGetter(app);
            AppLogger.getInstance().i(AppGetter.class.getSimpleName(),"Initialize: " + AppGetter.class.getSimpleName());
            instance.executor = new ScheduledThreadPoolExecutor(4);
        }
        else{
            throw new RuntimeException("Can't initialized again an " + AppGetter.class.getSimpleName());
        }
    }

    public static AppGetter getInstance() {

        return instance;
    }

    private AppGetter(SimpleApplication app) {
        this.app = app;
    }
}
