package com.aaltus.teslaradio.world.Scenarios;

import com.ar4android.vuforiaJME.AndroidActivityController;
import com.ar4android.vuforiaJME.AppGetter;
import com.ar4android.vuforiaJME.ITutorialSwitcher;
import com.aaltus.teslaradio.subject.ScenarioEnum;
import com.aaltus.teslaradio.world.effects.ScenarioTranslationAnimControl;
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
import com.utils.AppLogger;

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
public class ScenarioManager extends AbstractAppState implements IScenarioManager, ITutorialSwitcher
{
    private static final String TAG = ScenarioManager.class.getSimpleName();
    
    private static final String TOUCH_EVENT_NAME = "Touch";
    private static final String RIGHT_CLICK_MOUSE_EVENT_NAME = "Mouse";

    
    private SimpleApplication app;
    
    private Node guiNode;
    private Node localGuiNode = new Node("Local Gui Node");
    private Camera camera;
    private AssetManager assetManager;
    private RenderManager renderManager;
    private InputManager inputManager;
    private AppSettings settings;
    private ApplicationType applicationType;
    private boolean scenePreloaded =false;
    private ScenarioCommon scenarioCommon = new ScenarioCommon();
    private Node rightArrowNode;
    private Node leftArrowNode;
    
    private SongManager songManager;

    public void setApplicationType(ApplicationType applicationType){
        this.applicationType = applicationType;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
        for(Scenario scenario : scenarioList.getAllScenario()){
            scenario.setCamera(camera);
        }
    }

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
    @Override
    public void setNodeList(List<Node> nodeList) {

        this.nodeList = nodeList;
        attachCurrentScenario();
    }

    /**
     * Callback interface to open the informativeMenu.
     */
    private AndroidActivityController androidActivityController;
    
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
            AndroidActivityController androidActivityController)
    {
        this.app = app;
        this.nodeList = node;
        this.androidActivityController = androidActivityController;
        this.applicationType = applicationType;
        this.assetManager  = this.app.getAssetManager();//AppGetter.getAssetManager();
        this.renderManager = this.app.getRenderManager();
        this.inputManager  = this.app.getInputManager();
        this.settings      = this.app.getContext().getSettings();
        this.guiNode       = this.app.getGuiNode();
        this.setCamera(cam);

        init(nodeList, this.camera);
        
    }
    
    private void init(
            List<Node> node,
            Camera cam)
    {   
        initGuiNode(settings, assetManager);
        songManager = new SongManager();
        this.scenarioCommon.setNoiseControl(songManager.getNoiseControl());
        this.nodeList.get(1).attachChild(songManager.getAudioNode());
        
        //This a list of all the scenario that we will rotate/scale according
        //to which environment we are in. Don't forget to add scenario in it. 
        List<Scenario> scenarios = new ArrayList<Scenario>();
        
        // Init the playback scenario, this is the last of them! yayyyyy!
        Playback playback = new Playback(this.scenarioCommon, cam,null);
        playback.setName("Playback");
        scenarios.add(playback);
        
        //Init Demodulation scenario
        Demodulation demodulation = new Demodulation(this.scenarioCommon,cam, playback.getInputHandle());
        demodulation.setName("Demodulation");
        scenarios.add(demodulation);
        this.scenarioCommon.registerObserver(demodulation);
        
        // Init Filtering scenario
        Filter filter = new Filter(this.scenarioCommon,cam, demodulation.getInputHandle());
        filter.setName("Filter");
        scenarios.add(filter);
        
        //Init Reception scenario
        Reception reception = new Reception(this.scenarioCommon,cam, filter.getInputHandle());
        reception.setName("Reception");
        scenarios.add(reception);
        this.scenarioCommon.registerObserver(reception);
        
        //Init Amplification scenario
        Amplification amplification = new Amplification(this.scenarioCommon,cam,reception.getInputHandle());
        amplification.setName("Amplification");
        scenarios.add(amplification);
        this.scenarioCommon.registerObserver(amplification);
        
        //Init Modulation scenario
        Modulation modulation = new Modulation(this.scenarioCommon,cam, amplification.getInputHandle());
        modulation.setName("Modulation");
        scenarios.add(modulation);
        
        //Init SoundCapture scenario
        Scenario soundCapture = new SoundCapture(this.scenarioCommon,cam, modulation.getInputHandle());
        soundCapture.setName("SoundCapture");
        scenarios.add(soundCapture);
        
        // Init SoundEmission scenario
        SoundEmission soundEmission = new SoundEmission(this.scenarioCommon,cam, soundCapture.getInputHandle());
        soundEmission.setName("SoundEmission");
        scenarios.add(soundEmission);
        
        // add translation control to each scenarios
        int id = 0;
        for(Scenario scenario : scenarios){
            scenario.addControl(new ScenarioTranslationAnimControl(node, 50,id));
            id ++;
        }
        
        //Add first scenario
        List<Scenario> soundCaptureList = new ArrayList<Scenario>();
        soundCaptureList.add(soundEmission);
        soundCaptureList.add(soundCapture);
        scenarioList.addScenario(ScenarioEnum.SOUNDEMISSION,soundCaptureList);
        
        //Add second scenario
        List<Scenario> modulationList = new ArrayList<Scenario>();
        modulationList.add(soundCapture);
        modulationList.add(modulation);
        scenarioList.addScenario(ScenarioEnum.SOUNDCAPTURE,modulationList);
        
        //Add third scenario
        List<Scenario> amplificationList = new ArrayList<Scenario>();
        amplificationList.add(modulation);
        amplificationList.add(amplification);
        scenarioList.addScenario(ScenarioEnum.MODULATION,amplificationList);
        
        //Add four scenario
        List<Scenario> receptionList = new ArrayList<Scenario>();
        receptionList.add(amplification);
        receptionList.add(reception);
        scenarioList.addScenario(ScenarioEnum.TRANSMIT,receptionList);
        
        //Add fifth scenario
        List<Scenario> filterList = new ArrayList<Scenario>();
        filterList.add(reception);
        filterList.add(filter);
        scenarioList.addScenario(ScenarioEnum.RECEPTION,filterList);
        
        //Add sixth scenario
        List<Scenario> demodulationList = new ArrayList<Scenario>();
        demodulationList.add(filter);
        demodulationList.add(demodulation);
        scenarioList.addScenario(ScenarioEnum.FILTER,demodulationList);
        
        //Add last scenario
        List<Scenario> playbackList = new ArrayList<Scenario>();
        playbackList.add(demodulation);
        playbackList.add(playback);
        scenarioList.addScenario(ScenarioEnum.DEMODULATION,playbackList);
        
        

        //Only for debugging purpose deactivate it please.
        // scenarioList.addScenario(ScenarioEnum.FMMODULATION,new ArrayList<Scenario>());
        // scenarioList.addScenario(ScenarioEnum.TRANSMIT,new ArrayList<Scenario>());
        // scenarioList.addScenario(ScenarioEnum.RECEPTION,new ArrayList<Scenario>());

       
        //setCurrentScenario(scenarioList.getScenarioListByEnum(ScenarioEnum.AMMODULATION));
        
        setCurrentScenario(scenarioList.getScenarioListByEnum(ScenarioEnum.SOUNDEMISSION));

        setNodeList(node);
        
        

    }

    public void setTutorialIndex(int index){

        AppLogger.getInstance().d(TAG,"ScenarioManager Index: " + index);
        this.getCurrentScenario().getScenarios().get(0).setCurrentObjectEmphasis(index);

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
        rightArrowNode = new Node();
        rightArrowNode.setName(NEXT_SCENARIO);
        rightArrowNode.attachChild(pic1);
        localGuiNode.attachChild(rightArrowNode);
        pic1.move(-imageWidth / 2, -imageHeight / 2, 0);
        //node2.rotate(0, 0, -(float)Math.PI);
        rightArrowNode.move(settings.getWidth()-imageWidth/2,settings.getHeight()/2, 0);

        Picture pic2 = new Picture(PREVIOUS_SCENARIO);
        pic2.setName(PREVIOUS_SCENARIO);
        pic2.setImage(assetManager, "Interface/arrow.png", true);
        pic2.setWidth(imageWidth);
        pic2.setHeight(imageHeight);
        leftArrowNode = new Node();
        leftArrowNode.setName(PREVIOUS_SCENARIO);
        leftArrowNode.attachChild(pic2);
        localGuiNode.attachChild(leftArrowNode);
        pic2.move(-imageWidth/2, -imageHeight/2, 0);
        leftArrowNode.rotate(0, 0, -(float)Math.PI);
        leftArrowNode.move(imageWidth/2,settings.getHeight()/2, 0);
    }
    /*
    private void setAttachLeftArrow(boolean attach){
        if(attach){
            this.guiNode.attachChild(guiLeftArrowNode);
        }
        else{
            this.guiNode.detachChild(guiLeftArrowNode);
        }
    }
    * */
    
    /**
     * Make transformation to the scenario according to the application type.
     * @param applicationType
     * @param scenarios
     */
    private void adjustScenario(ApplicationType applicationType, List<Scenario> scenarios, RenderManager renderManager)
    {
        Quaternion rot = new Quaternion();

        switch(applicationType)
        {
            case ANDROID:
                AppGetter.setWorldScaleDefault(100);
                //This is the rotation to put a scenario in the correct angle for VuforiaJME
                rot.fromAngleAxis(3.14f / 2, new Vector3f(1.0f, 0.0f, 0.0f));
                break;
            case ANDROID_DEV_FRAMEWORK:
                AppGetter.setWorldScaleDefault(10);
                break;
            case DESKTOP:
                AppGetter.setWorldScaleDefault(10);
                break;
        }

        for(Scenario scenario : scenarios)
        {
            //Correction for BUG TR-176
            //The problem was that the 3d modules was in RAM but was not forwarded to the GPU.
            //So the first time that the we were seeing a model, the vidoe was stagerring to load everything.
            if(renderManager != null && !this.scenePreloaded){
                renderManager.preloadScene(scenario);
            }
            
            scenario.getControl(ScenarioTranslationAnimControl.class).setOffsetRotation(rot);

            //WORLD_SCALE_DEFAULT = 100;
            scenario.setLocalScale(AppGetter.getWorldScalingDefault());
        }
        this.scenePreloaded = true;
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
    
    private void removeInputMapping()
    {
        this.inputManager.removeListener(this);
    }
    

    private ScenarioGroup getCurrentScenario() {
        return currentScenario;
    }

    private void setCurrentScenario(ScenarioGroup currentScenario) {

        detachCurrentScenario();
        iniScenarioTranslation(currentScenario.getScenarios());
        this.currentScenario = currentScenario;
        attachCurrentScenario();
    }

    private void iniScenarioTranslation( List<Scenario> nextScenarios){
        
        // set translation animation
        if( (getCurrentScenario() != null) && (getCurrentScenario().getScenarios().size() >= 2) && (nextScenarios.size() >= 2) ){
            
            // scenario go to previous
            if(getCurrentScenario().getScenarios().get(0) == nextScenarios.get(1))
            {
                int index = 0;
                for(Scenario scenario : nextScenarios ){
                    scenario.getControl(ScenarioTranslationAnimControl.class).startTranslationPrevious(index);
                    index ++;
                }               
            }
            // scenario go to next 
            else if(getCurrentScenario().getScenarios().get(1) == nextScenarios.get(0))
            {
                int index = 0;
                for(Scenario scenario : nextScenarios ){
                    scenario.getControl(ScenarioTranslationAnimControl.class).startTranslationNext(index);
                    index ++;
                }               
            }
            // unknow translation = no translation
            else
            {
                // do nothing
            }

        }
    }
    
    /**
     * Detach all the current scenarios from its parent if possible
     */
    private void detachCurrentScenario()
    {
        if(getCurrentScenario() != null){
            for(Scenario scenario : getCurrentScenario().getScenarios() )
            {
                scenario.setCurrentObjectEmphasis(-1);
                scenario.notOnNodeActions();
                Node parent = scenario.getParent();
                if(parent != null){
                    parent.detachChild(scenario);
                }
            }
        }
        if(androidActivityController != null){
            androidActivityController.setTutorialMenu(null);
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
                    Scenario scenario = getCurrentScenario().getScenarios().get(count);
                    if(count == 0 ){
                        scenario.onFirstNodeActions();
                        if(scenario.getNeedsBackgroundSound()){
                            this.songManager.playSong();
                        }else{
                            this.songManager.stopSong();
                        }
                    }
                    if(count == 1){
                        scenario.onSecondNodeActions();
                    }
                    if(node != null)
                    {
                        if(node.getParent() != null){
                            node.getParent().setUserData(AppGetter.USR_FIXED_ANGLE_CHILD, scenario.getNeedFixedScenario());
                        }
                        node.attachChild(scenario);
                    }
                    else
                    {
                        Node parent = scenario.getParent();
                        if(parent != null)
                        {
                            parent.detachChild(scenario);
                        }
                    }
                }
                count++;
            }
        }

        ScenarioEnum scenarioEnum = scenarioList.getScenarioEnumFromScenarioList(getCurrentScenario().getScenarios());
        if(androidActivityController != null){
            androidActivityController.setTutorialMenu(scenarioEnum);
        }
        updateGuiNavigationArrows();
        
    }
    
    private void updateGuiNavigationArrows()
    {
        if(this.hasNextScenario()){
            this.localGuiNode.attachChild(this.rightArrowNode);
        }
        else{
            this.localGuiNode.detachChild(this.rightArrowNode);
        }
        
        if(this.hasPreviousScenario()){
                this.localGuiNode.attachChild(this.leftArrowNode);
        }
        else{
            this.localGuiNode.detachChild(this.leftArrowNode);
        }
        
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
      super.initialize(stateManager, app);
      adjustScenario(this.applicationType, this.scenarioList.getAllScenario(), renderManager);
      guiNode.attachChild(localGuiNode);
      attachCurrentScenario();
      addInputMapping(applicationType);
      // init stuff that is independent of whether state is PAUSED or RUNNING
      
   }
     
   @Override
    public void cleanup() {
      super.cleanup();
      detachCurrentScenario();
      guiNode.detachChild(localGuiNode);
      removeInputMapping();
      
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

  
    /**
     * To be call to update the scenario
     * @param tpf
     */
    @Override
    public void update(float tpf){

        for(Scenario scenario : getCurrentScenario().getScenarios() )
        {
            if (scenario.simpleUpdate(tpf) && androidActivityController != null)
            {
                androidActivityController.toggleInformativeMenuCallback(scenarioList.getScenarioEnumFromScenarioList(getCurrentScenario().getScenarios()));
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
            localGuiNode.collideWith(ray, results);

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
        private List<Scenario> allScenario = new ArrayList<Scenario>();
        public List<Scenario> getAllScenario(){
            return allScenario;
        }

        ScenarioList(){}

        public void addScenario(ScenarioEnum scenarioEnum, List<Scenario> scenarios)
        {
            enumScenarioEnumMap.put(scenarioEnum,scenarios);
            scenarioList.add(scenarios);
            for(Scenario scenario : scenarios){

                if(!allScenario.contains(scenario)){
                    allScenario.add(scenario);
                }
            }
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