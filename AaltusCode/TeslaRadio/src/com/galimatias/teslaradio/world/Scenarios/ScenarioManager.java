package com.galimatias.teslaradio.world.Scenarios;

import com.ar4android.vuforiaJME.AppGetter;
import com.ar4android.vuforiaJME.AppListener;
import com.galimatias.teslaradio.subject.ScenarioEnum;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.TouchTrigger;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * A IScenarioManager Implemenations
 *
 * What I try to do is have a list of (list of scenarios) that the user can change with next/previous button or
 * with an enum describing the scenario provided.
 *
 * The manager provide a direct mapping between a list of Node and the list of scenario. So
 * the first scenario will always be attached to the first Node, second to second...
 *
 * Created by jimbojd72 on 9/3/14.
 */
public class ScenarioManager  extends AbstractAppState implements IScenarioManager
{

    
    
    private static final String TOUCH_EVENT_NAME = "Touch";
    private static final String RIGHT_CLICK_MOUSE_EVENT_NAME = "Mouse";
    
    private SimpleApplication app;
    private Node guiNode;
    private Camera camera;
    private AssetManager assetManager;
    private RenderManager renderManager;
    private InputManager inputManager;
    private AppSettings settings;
    private ApplicationType applicationType;

   /**
     * An enum that provide insight to the manager to which scale/rotation it must provide to the scenario
     * created to fit in to the Android app or the JMonkey SDK app.
     */
    public enum ApplicationType {
        ANDROID, DESKTOP, ANDROID_DEV_FRAMEWORK
    }

    /**
     * The current group of scenario that is attach to the List of nodes
     */
    private ScenarioGroup currentScenario = null;
    
    /**
     * Where the pair of scenarios are saved and accessed
     */
    private ScenarioList  scenarioList = new ScenarioList();
    /**
     * The list of node that the currentScenarios will be attached to.
     */
    private List<Node> nodeList;

    public List<Node> getNodeList() {
        return nodeList;
    }

    /**
     * Callback interface to open the informativeMenu.
     */
    private AppListener appListener;
    
    private static final String NEXT_SCENARIO = "NextScenario";
    private static final String PREVIOUS_SCENARIO = "PreviousScenario";
    private static final String TEXT = "Text";
    private static final String GUITAR = "Guitar";
    private static final String DRUM = "Drum";
    private static final String MICRO = "Micro";
    private static final String FREQUENCY_SWITCH = "FrequencySwitch";
    
    public ScenarioManager(SimpleApplication app,
            ApplicationType applicationType,
            List<Node> node,
            Camera cam,
            AppListener appListener)
    {
        this.app = app;
        this.nodeList = node;
        this.appListener = appListener;
        this.applicationType = applicationType;
        this.assetManager  = this.app.getAssetManager();//AppGetter.getAssetManager();
        this.renderManager = this.app.getRenderManager();
        this.inputManager  = this.app.getInputManager();
        this.settings      = this.app.getContext().getSettings();
        this.guiNode       = this.app.getGuiNode();
        this.camera        = cam;
        AppGetter.setWorldScaleDefault(this.applicationType == ApplicationType.DESKTOP || this.applicationType == ApplicationType.ANDROID_DEV_FRAMEWORK ? 10 : 100);
        init(applicationType, nodeList, camera, appListener);
        
    }
    
    private void init(ApplicationType applicationType,
            List<Node> node,
            Camera cam,
            AppListener appListener)
    {   
        
        //This a list of all the scenario that we will rotate/scale according
        //to which environment we are in. Don't forget to add scenario in it. 
        List<Scenario> scenarios = new ArrayList<Scenario>();
        
        //Init Reception scenario
        Reception reception = new Reception(cam, null);
        reception.setName("Reception");
        scenarios.add(reception);
        
        //Init Amplification scenario
        Amplification amplification = new Amplification(cam,reception.getInputHandle());
        amplification.setName("Amplification");
        scenarios.add(amplification);
        
        //Init Modulation scenario
        Modulation modulation = new Modulation(cam, amplification.getInputHandle());
        modulation.setName("Modulation");
        scenarios.add(modulation);
        
        //Init SoundCapture scenario
        Scenario soundCapture = new SoundCapture(cam, modulation.getInputHandle());
        soundCapture.setName("SoundCapture");
        scenarios.add(soundCapture);
        
        // Init SoundEmission scenario
        SoundEmission soundEmission = new SoundEmission(cam, soundCapture.getInputHandle());
        soundEmission.setName("SoundEmission");
        scenarios.add(soundEmission);
        
        addInputMapping(this.applicationType);
        adjustScenario(this.applicationType, scenarios, renderManager);
        
        //Add first scenario
        List<Scenario> soundCaptureList = new ArrayList<Scenario>();
        soundCaptureList.add(soundEmission);
        soundCaptureList.add(soundCapture);
        scenarioList.addScenario(ScenarioEnum.SOUNDCAPTURE,soundCaptureList);
        
        //Add second scenario
        List<Scenario> modulationList = new ArrayList<Scenario>();
        modulationList.add(soundCapture);
        modulationList.add(modulation);
        scenarioList.addScenario(ScenarioEnum.AMMODULATION,modulationList);
        
        //Add third scenario
        List<Scenario> amplificationList = new ArrayList<Scenario>();
        amplificationList.add(modulation);
        amplificationList.add(amplification);
        scenarioList.addScenario(ScenarioEnum.TRANSMIT,amplificationList);
        
        //Add four scenario
        List<Scenario> receptionList = new ArrayList<Scenario>();
        receptionList.add(amplification);
        receptionList.add(reception);
        scenarioList.addScenario(ScenarioEnum.RECEPTION,receptionList);

        //Only for debugging purpose deactivate it please.
        scenarioList.addScenario(ScenarioEnum.FMMODULATION,new ArrayList<Scenario>());
      //  scenarioList.addScenario(ScenarioEnum.TRANSMIT,new ArrayList<Scenario>());
    //    scenarioList.addScenario(ScenarioEnum.RECEPTION,new ArrayList<Scenario>());

        //setCurrentScenario(scenarioList.getScenarioListByEnum(ScenarioEnum.AMMODULATION));
        setCurrentScenario(scenarioList.getScenarioListByEnum(ScenarioEnum.SOUNDCAPTURE));

        setNodeList(node);
        initGuiNode(settings, assetManager);

    }

    /**
     * Initiate an HUD on the GUI Node.
     * @param settings
     * @param assetManager 
     */
    private void initGuiNode(AppSettings settings, AssetManager assetManager) {
        final int imageWidth = settings.getWidth() / 15;
        final int imageHeight = settings.getHeight() / 10;
        
        Picture pic1 = new Picture(NEXT_SCENARIO);
        pic1.setName(NEXT_SCENARIO);
        pic1.setImage(assetManager, "Interface/arrow.png", true);
        pic1.setWidth(imageWidth);
        pic1.setHeight(imageHeight);
        Node node1 = new Node();
        node1.setName(NEXT_SCENARIO);
        node1.attachChild(pic1);
        guiNode.attachChild(node1);
        pic1.move(-imageWidth / 2, -imageHeight / 2, 0);
        //node2.rotate(0, 0, -(float)Math.PI);
        node1.move(settings.getWidth()-imageWidth/2,settings.getHeight()/2, 0);

        Picture pic2 = new Picture(PREVIOUS_SCENARIO);
        pic2.setName(PREVIOUS_SCENARIO);
        pic2.setImage(assetManager, "Interface/arrow.png", true);
        pic2.setWidth(imageWidth);
        pic2.setHeight(imageHeight);
        Node node2 = new Node();
        node2.setName(PREVIOUS_SCENARIO);
        node2.attachChild(pic2);
        guiNode.attachChild(node2);
        pic2.move(-imageWidth/2, -imageHeight/2, 0);
        node2.rotate(0, 0, -(float)Math.PI);
        node2.move(imageWidth/2,settings.getHeight()/2, 0);
    }
    
    /**
     * Make transformation to the scenario according to the application type.
     * @param applicationType
     * @param scenarios
     */
    private void adjustScenario(ApplicationType applicationType, List<Scenario> scenarios, RenderManager renderManager)
    {
        switch(applicationType)
        {
            case ANDROID:

                //This is the rotation to put a scenarion in the correct angle for VuforiaJME
                Quaternion rot = new Quaternion();
                rot.fromAngleAxis(3.14f / 2, new Vector3f(1.0f, 0.0f, 0.0f));
                //float scale = 10.0f;
                
                for(Scenario scenario : scenarios)
                {
                    //Correction for BUG TR-176
                    //The problem was that the 3d modules was in RAM but was not forwarded to the GPU.
                    //So the first time that the we were seeing a model, the vidoe was stagerring to load everything.
                    if(renderManager != null){
                        renderManager.preloadScene(scenario);
                    }
                    scenario.rotate(rot);

                    //WORLD_SCALE_DEFAULT = 100;
                    scenario.scale(AppGetter.getWorldScalingDefault());
                }
                break;
            case ANDROID_DEV_FRAMEWORK:
                

                //This is the rotation to put a scenarion in the correct angle for VuforiaJME
                //Quaternion rot = new Quaternion();
                //rot.fromAngleAxis(3.14f / 2, new Vector3f(1.0f, 0.0f, 0.0f));
                //float scale = 10.0f;
                
                for(Scenario scenario : scenarios)
                {
                    //Correction for BUG TR-176
                    //The problem was that the 3d modules was in RAM but was not forwarded to the GPU.
                    //So the first time that the we were seeing a model, the vidoe was stagerring to load everything.
                    if(renderManager != null){
                        renderManager.preloadScene(scenario);
                    }
                    //scenario.rotate(rot);

                    //WORLD_SCALE_DEFAULT = 100;
                    scenario.scale(AppGetter.getWorldScalingDefault());
                }

                break;
            case DESKTOP:
                for (Scenario scenario : scenarios) {
                    scenario.scale(AppGetter.getWorldScalingDefault());
                }
                
                break;
        }
    }

    private void addInputMapping(ApplicationType applicationType)
    {
        switch(applicationType)
        {
            case ANDROID:
            case ANDROID_DEV_FRAMEWORK:
                //Add mapping for touch input only for android device
                inputManager.addMapping(TOUCH_EVENT_NAME, new TouchTrigger(0));
                inputManager.addListener(this, new String[]{TOUCH_EVENT_NAME});
                break;
            case DESKTOP:

                // You can map one or several inputs to one named action
                inputManager.addMapping(DRUM, new KeyTrigger(KeyInput.KEY_T));
                inputManager.addMapping(GUITAR, new KeyTrigger(KeyInput.KEY_G));
                inputManager.addMapping(TEXT, new KeyTrigger(KeyInput.KEY_H));
                inputManager.addMapping(MICRO, new KeyTrigger(KeyInput.KEY_M));
                inputManager.addMapping(NEXT_SCENARIO, new KeyTrigger(KeyInput.KEY_P));
                inputManager.addMapping(PREVIOUS_SCENARIO, new KeyTrigger(KeyInput.KEY_O));
                //We add mapping for right click because left click are already implemented.
                inputManager.addMapping(RIGHT_CLICK_MOUSE_EVENT_NAME, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
                
                // Add the names to the action listener.
                inputManager.addListener(this, DRUM);
                inputManager.addListener(this, GUITAR);
                inputManager.addListener(this, TEXT);
                inputManager.addListener(this, MICRO);
                inputManager.addListener(this, NEXT_SCENARIO);
                inputManager.addListener(this, PREVIOUS_SCENARIO);
                inputManager.addListener(this, RIGHT_CLICK_MOUSE_EVENT_NAME);
                break;
        }
    }
    
    private void removeInputMapping(ApplicationType applicationType)
    {
        this.inputManager.removeListener(this);
    }
    

    private ScenarioGroup getCurrentScenario() {
        return currentScenario;
    }

    private void setCurrentScenario(ScenarioGroup currentScenario) {

        detachCurrentScenario();
        this.currentScenario = currentScenario;
        attachCurrentScenario();
    }

    /**
     * Detach all the current scenarios from its parent if possible
     */
    private void detachCurrentScenario()
    {
        if(getCurrentScenario() != null){
            for(Scenario scenario : getCurrentScenario().getScenarios() )
            {
                Node parent = scenario.getParent();
                if(parent != null){
                    parent.detachChild(scenario);
                }
            }
        }
    }

    /**
     * Attache all scenario to the Node if the node are not null.
     */
    private void attachCurrentScenario()
    {
        int count = 0;
        int size = getCurrentScenario() == null ? 0 : getCurrentScenario().getScenarios().size();
        if(getNodeList() != null){
            for(Node node : getNodeList())
            {
                if(count < size){
                    Scenario currentScenario = getCurrentScenario().getScenarios().get(count);
                    if(node != null)
                    {
                        node.attachChild(currentScenario);
                    }
                    else
                    {
                        Node parent = currentScenario.getParent();
                        if(parent != null)
                        {
                            parent.detachChild(currentScenario);
                        }
                    }
                }
                count++;
            }
        }
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
      super.initialize(stateManager, app); 
      attachCurrentScenario();
      // init stuff that is independent of whether state is PAUSED or RUNNING
      
   }
     
   @Override
    public void cleanup() {
      super.cleanup();
      detachCurrentScenario();
      removeInputMapping(this.applicationType);
      
      // unregister all my listeners, detach all my nodes, etc.../*
      
      
    }

    @Override
    public void setEnabled(boolean enabled) {
      // Pause and unpause
      super.setEnabled(enabled);
      /*if(enabled){
        // init stuff that is in use while this state is RUNNING
        this.app.getRootNode().attachChild(getX()); // modify scene graph...
        this.app.doSomethingElse();                 // call custom methods...
      } else {
        // take away everything not needed while this state is PAUSED
        ...
      }*/
    }
    
    //TODO: MODIFY THIS TO RECEIVE A LIST<NODE> TO ATTACH THE SCENARIO TO THE RIGHT TRACKABLE/NODE
    @Override
    public void setNextScenario() {
        if(hasNextScenario())
        {
            setCurrentScenario(scenarioList.getScenarioByIndex(getCurrentScenario().getIndex() + 1));
        }
    }


    @Override
    public void setPreviousScenario(){
        if(hasPreviousScenario())
        {
            setCurrentScenario(scenarioList.getScenarioByIndex(getCurrentScenario().getIndex() - 1));
        }
    }



    @Override
    public boolean hasNextScenario() {

        return getCurrentScenario().getIndex()+1 < scenarioList.size();

    }

    @Override
    public boolean hasPreviousScenario() {

        return getCurrentScenario().getIndex()-1 >= 0;

    }

    @Override
    public void setScenarioByEnum(ScenarioEnum scenarioEnum){
        
        setCurrentScenario(scenarioList.getScenarioListByEnum(scenarioEnum));
    }

    @Override
    public void setNodeList(List<Node> nodeList) {

        this.nodeList = nodeList;
        attachCurrentScenario();
    }

    /**
     * To be call to update the scenario
     * @param tpf
     */
    @Override
    public void update(float tpf){

        for(Scenario scenario : getCurrentScenario().getScenarios() )
        {
            if (scenario.simpleUpdate(tpf) && appListener != null)
            {
                appListener.toggleInformativeMenuCallback(scenarioList.getScenarioEnumFromScenarioList(getCurrentScenario().getScenarios()));
            }
        }
    };

    /**
     * To pass down on touch event to the scenario.
     * @param name
     * @param touchEvent
     * @param v
     */
    @Override
    public void onTouch(String name, TouchEvent touchEvent, float v)
    {
/*
        AppLogger.getInstance().d("TouchEvent Name: " + name,
                " Touch ID: " + touchEvent.getPointerId()+
                        " Type: "+ touchEvent.getType().toString()+
                        " X : " + touchEvent.getX() +
                        " Y : " + touchEvent.getY() +
                        " dX : " + touchEvent.getDeltaX() +
                        " dY : " + touchEvent.getDeltaY());
*/
        //I add a way to switch scenario with 2 fingers, still experimental
        if(touchEvent.getPointerId() == 1 &&
                (touchEvent.getType() == TouchEvent.Type.FLING ||
                        touchEvent.getType() == TouchEvent.Type.SCROLL ||
                        touchEvent.getType() == TouchEvent.Type.MOVE))
        {
            float deltaminValueForInput = 50;
            if(touchEvent.getDeltaX() > deltaminValueForInput)
            {
                this.setNextScenario();
            }
            else if(touchEvent.getDeltaX() < -deltaminValueForInput)
            {
                this.setPreviousScenario();
            }
        }
        else{


            //We check if the event is on the GUI NODE. We pass it down to scenario otherwise.
            CollisionResults results = new CollisionResults();
            Vector2f location = new Vector2f(touchEvent.getX(),touchEvent.getY());
            Vector3f origin = new Vector3f(location.x, location.y, 0);
            Vector3f dir = new Vector3f(0f, 0f, 1f);
            Ray ray = new Ray(origin, dir);
            // 3. Collect intersections between Ray and Shootables in results list.
            guiNode.collideWith(ray, results);

            if(results.size() > 0 && touchEvent.getType() == TouchEvent.Type.DOWN)
            {
                String nameToCompare =
                        results.getClosestCollision().getGeometry().getParent().getName();
                //AppLogger.getInstance().i("Chat",nameToCompare);
                if(nameToCompare.equals(NEXT_SCENARIO))
                {
                    this.setNextScenario();
                }
                else if(nameToCompare.equals(PREVIOUS_SCENARIO))
                {
                    this.setPreviousScenario();
                }
            }
            else{
                for(Scenario scenario : getCurrentScenario().getScenarios() )
                {
                    scenario.onScenarioTouch(name, touchEvent, v);
                }
            }
        }


    }
    
    /**
     * This event is generated by the DevFramework from keyboard key or Mouse event
     * @param name
     * @param keyPressed
     * @param tpf 
     */
    @Override
    public void onAction(String name, boolean keyPressed, float tpf) {
        
        if ((name.equals(GUITAR) || name.equals(DRUM) || name.equals(MICRO)) && !keyPressed) {
            List<Scenario> scenarios = getCurrentScenario().getScenarios();
            if(scenarios != null){
                for(Scenario scenario : scenarios ){
                    if(scenario instanceof SoundEmission){
                        if(name.equals(DRUM))
                        {
                            ((SoundEmission)scenario).drumTouchEffect();
                        }
                        if(name.equals(GUITAR))
                        {
                            ((SoundEmission)scenario).guitarTouchEffect();
                        }
                    }
                    if(scenario instanceof SoundCapture){
                        if(name.equals(MICRO))
                        {
                            ((SoundCapture)scenario).microTouchEffect();
                        }
                    }
                }
            }
        }
        else if (name.equals(NEXT_SCENARIO) && !keyPressed) {
            //soundCapture.drumTouchEffect();
            //soundCapture.drumTouchEffect();
            this.setNextScenario();
        }
        else if (name.equals(PREVIOUS_SCENARIO) && !keyPressed) {
            //soundCapture.drumTouchEffect();
            //soundCapture.drumTouchEffect();
            this.setPreviousScenario();
        }
        //If it's an mouse event, we generate a touch event from it to support touch event.
        else if(name.equals(RIGHT_CLICK_MOUSE_EVENT_NAME) && !keyPressed)
        {
            onTouch(TOUCH_EVENT_NAME, new TouchEvent(TouchEvent.Type.DOWN,
                    AppGetter.getInputManager().getCursorPosition().getX(),
                    AppGetter.getInputManager().getCursorPosition().getY(),
                    0,
                    0),
                    tpf);
        }
    }

    /**
     * A class to have both an index and a list<Scenario> together in the same data structure.
     */
    private class ScenarioGroup
    {

        List<Scenario> scenarios  = null;
        Integer index             = null;

        public ScenarioGroup(List<Scenario> scenarios, int index)
        {
            this.scenarios    = scenarios;
            this.index        = index;
        }

        public List<Scenario> getScenarios() {
            return scenarios;
        }

        public Integer getIndex() {
            return index;
        }

        public Scenario getNextScenarioInGroup(Scenario scenario){
            if (scenarios.get(0) == scenario && scenarios.size() > 1){
                return scenarios.get(1);
            }
            else{
                return null;
            }
        }
    }

    /**
     * A class that will be able to map enum and scenario index internally
     */
    private class ScenarioList
    {

        private EnumMap<ScenarioEnum,List<Scenario>> enumScenarioEnumMap = new EnumMap<ScenarioEnum, List<Scenario>>(ScenarioEnum.class);
        private List<List<Scenario>> scenarioList = new ArrayList<List<Scenario>>();

        ScenarioList(){}

        public void addScenario(ScenarioEnum scenarioEnum, List<Scenario> scenarios)
        {
            enumScenarioEnumMap.put(scenarioEnum,scenarios);
            scenarioList.add(scenarios);
        }

        public ScenarioGroup getScenarioListByEnum(ScenarioEnum scenarioEnum)
        {
            List<Scenario> scenariosFound = enumScenarioEnumMap.get(scenarioEnum);

            Integer index = null;
            int count = 0;
            for(List<Scenario> listOfScenarios: scenarioList)
            {
                if(listOfScenarios == scenariosFound)
                {
                    index = count;
                }
                count++;
            }
            return new ScenarioGroup(scenariosFound, index);
        }

        public ScenarioGroup getScenarioByIndex(int index)
        {
            return new ScenarioGroup(scenarioList.get(index), index);
        }

        public int size()
        {
            if(scenarioList.size() == enumScenarioEnumMap.size())
            {
                return scenarioList.size();
            }
            else{

                throw new IllegalStateException("Scenario list ("+ scenarioList.size() +") and scenario enummap ("+ enumScenarioEnumMap.size() +") must be equal.");
            }
        }

        public Integer getIndexFromScenarioList(List<Scenario> scenarios)
        {
            int count = 0;
            Integer index = null;
            for(List<Scenario> listOfScenarios: scenarioList)
            {
                if(listOfScenarios == scenarios)
                {
                    index = count;
                }
                count++;
            }
            return index;


        }

        public ScenarioEnum getScenarioEnumFromScenarioList(List<Scenario> scenarios)
        {
            int count = 0;
            ScenarioEnum currentScenatioEnum = null;
            for (EnumMap.Entry<ScenarioEnum,List<Scenario>> entry : enumScenarioEnumMap.entrySet()) {
                ScenarioEnum key = entry.getKey();
                List<Scenario> value = entry.getValue();
                if(value == scenarios)
                {
                    currentScenatioEnum = key;
                }
            }
            return currentScenatioEnum;
        }
    }
}
