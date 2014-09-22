package com.galimatias.teslaradio.world.Scenarios;

import com.ar4android.vuforiaJME.AppListener;
import com.galimatias.teslaradio.subject.ScenarioEnum;
import com.galimatias.teslaradio.world.effects.PrevSignalGenerator;
import com.galimatias.teslaradio.world.observer.ParticleEmitReceiveLinker;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

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
public class ScenarioManager  implements IScenarioManager, ParticleEmitReceiveLinker {

    public static int WORLD_SCALE_DEFAULT = 100;

    /**
     * An enum that provide insight to the manager to which scale/rotation it must provide to the scenario
     * created to fit in to the Android app or the JMonkey SDK app.
     */
    public enum ApplicationType {
        ANDROID, DESKTOP
    }

    /**
     * Signal Generator that will be used to generate
     */
    private PrevSignalGenerator signalGenerator;

    /**
     * The current group of scenario that is attach to the List of nodes
     */
    private ScenarioGroup currentScenario = null;
    
    /**
     * Where the pair of scenarios are saved and accessed
     */
    private ScenarioList  scenarioList    = new ScenarioList();
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

    public ScenarioManager(ApplicationType applicationType,
            List<Node> node,
            AssetManager assetManager,
            Camera cam,
            AppListener appListener,
            RenderManager renderManager,
            InputManager inputManager)
    {
        this.appListener = appListener;

        // This will generate signals when SoundEmission is not shown
        signalGenerator = new PrevSignalGenerator(assetManager);
        
        //This a list of all the scenario that we will rotate/scale according
        //to which environment we are in. Don't forget to add scenario in it. 
        List<Scenario> scenarios = new ArrayList<Scenario>();
        
        //Init SoundCapture scenario
        Scenario soundCapture = new SoundCapture(assetManager, cam, this);
        soundCapture.setName("SoundCapture");
        scenarios.add(soundCapture);
        
        //Init SoundCapture scenario
        DummyScenario dummy = new DummyScenario(assetManager, ColorRGBA.Orange);
        scenarios.add(dummy);
        SoundEmission soundEmission = new SoundEmission(assetManager, cam, this);
        scenarios.add(soundEmission);
        
        
        adjustScenario(applicationType, scenarios, renderManager, inputManager);
        
        //Add first scenario
        List<Scenario> soundCaptureList = new ArrayList<Scenario>();
        soundCaptureList.add(soundEmission);
        soundCaptureList.add(soundCapture);
        scenarioList.addScenario(ScenarioEnum.SOUNDCAPTURE,soundCaptureList);
        
        //Add second scenario
        List<Scenario> modulationList = new ArrayList<Scenario>();
        
        //soundCaptureList.add(dummy);
        modulationList.add(soundCapture);
        modulationList.add(dummy);
        scenarioList.addScenario(ScenarioEnum.AMMODULATION,modulationList);

        //Only for debugging purpose deactivate it please.
        scenarioList.addScenario(ScenarioEnum.FMMODULATION,new ArrayList<Scenario>());
        scenarioList.addScenario(ScenarioEnum.TRANSMIT,new ArrayList<Scenario>());
        scenarioList.addScenario(ScenarioEnum.RECEPTION,new ArrayList<Scenario>());

        //setCurrentScenario(scenarioList.getScenarioListByEnum(ScenarioEnum.AMMODULATION));
        setCurrentScenario(scenarioList.getScenarioListByEnum(ScenarioEnum.SOUNDCAPTURE));

        setNodeList(node);
    }

    /**
     * Make transformation to the scenario according to the application type.
     * @param applicationType
     * @param scenarios
     */
    private void adjustScenario(ApplicationType applicationType, List<Scenario> scenarios, RenderManager renderManager, InputManager inputManager)
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

                    WORLD_SCALE_DEFAULT = 100;
                    scenario.scale(WORLD_SCALE_DEFAULT);
                }
                
                
                break;
            case DESKTOP:
                if(inputManager != null){
                        // You can map one or several inputs to one named action
                        inputManager.addMapping(DRUM, new KeyTrigger(KeyInput.KEY_T));
                        inputManager.addMapping(GUITAR, new KeyTrigger(KeyInput.KEY_G));
                        inputManager.addMapping(TEXT, new KeyTrigger(KeyInput.KEY_H));
                        inputManager.addMapping(MICRO, new KeyTrigger(KeyInput.KEY_M));
                        inputManager.addMapping(NEXT_SCENARIO, new KeyTrigger(KeyInput.KEY_P));
                        inputManager.addMapping(PREVIOUS_SCENARIO, new KeyTrigger(KeyInput.KEY_O));

                        // Add the names to the action listener.
                        inputManager.addListener(this, DRUM);
                        inputManager.addListener(this, GUITAR);
                        inputManager.addListener(this, TEXT);
                        inputManager.addListener(this, MICRO);
                        inputManager.addListener(this, NEXT_SCENARIO);
                        inputManager.addListener(this, PREVIOUS_SCENARIO);
                    }
                
                for(Scenario scenario : scenarios)
                {
                    WORLD_SCALE_DEFAULT = 10;
                    scenario.scale(WORLD_SCALE_DEFAULT);
                }
                
                break;
                
            default:
                
                break;
        }
    }


    private ScenarioGroup getCurrentScenario() {
        return currentScenario;
    }

    private void setCurrentScenario(ScenarioGroup currentScenario) {

        signalGenerator.setSignal(scenarioList.getScenarioEnumFromScenarioList(currentScenario.getScenarios()), currentScenario.getScenarios().get(0));
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
            nodeList.get(0).detachChild(signalGenerator);
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

            nodeList.get(0).attachChild(signalGenerator);

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

    //TODO: MODIFY THIS TO RECEIVE A LIST<NODE> TO ATTACH THE SCENARIO TO THE RIGHT TRACKABLE/NODE
    @Override
    public void setNextScenario() {
        if(getCurrentScenario().getIndex()+1 < scenarioList.size())
        {
            setCurrentScenario(scenarioList.getScenarioByIndex(getCurrentScenario().getIndex() + 1));
        }
    }

    @Override
    public void setPreviousScenario(){
        if(getCurrentScenario().getIndex()-1 >= 0)
        {
            setCurrentScenario(scenarioList.getScenarioByIndex(getCurrentScenario().getIndex() - 1));
        }
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
     * Method that return the next scenario's receiver handle position
     * @param caller
     * @return the handle vector
     */
    @Override
    public Vector3f GetEmitterDestinationPaths(Scenario caller) {
        Scenario nextScenario = currentScenario.getNextScenarioInGroup(caller);
        if (nextScenario != null){
            return nextScenario.getParticleReceiverHandle();
        }
        else{
            return null;
        }
    }

    @Override
    public void sendSignalToNextScenario(Scenario caller, Geometry newSignal, float magnitude) {
        Scenario nextScenario = currentScenario.getNextScenarioInGroup(caller);
        if (nextScenario != null){
            nextScenario.sendSignalToEmitter(newSignal, magnitude);
        }
    }

    /**
     * To be call to update the scenario
     * @param tpf
     */
    public void simpleUpdate(float tpf){

        signalGenerator.simpleUpdate(tpf, getCurrentScenario().getScenarios().get(0).getParticleReceiverHandle());

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
        for(Scenario scenario : getCurrentScenario().getScenarios() )
        {
            scenario.onScenarioTouch(name, touchEvent, v);
        }
    }
    
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
