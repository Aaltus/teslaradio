package com.galimatias.teslaradio.world.Scenarios;

import com.ar4android.vuforiaJME.AppListener;
import com.galimatias.teslaradio.subject.ScenarioEnum;
import com.jme3.asset.AssetManager;
import com.jme3.input.controls.TouchListener;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * Created by jimbojd72 on 9/3/14.
 */
public class ScenarioManager  implements TouchListener {

    //List<Scenario> allScenario      = new ArrayList<Scenario>();
    private ScenarioGroup currentScenario = null;
    private ScenarioList  scenarioList    = new ScenarioList();
    private Node rootNode;

    private ScenarioGroup getCurrentScenario() {
        return currentScenario;
    }

    private void setCurrentScenario(ScenarioGroup currentScenario) {

        detachCurrentScenario();
        this.currentScenario = currentScenario;
        attachCurrentScenario();
    }

    private void detachCurrentScenario()
    {
        if(getCurrentScenario() != null){
            for(Scenario scenario : getCurrentScenario().getScenarios() )
            {
                rootNode.detachChild(scenario);
            }
        }
    }

    private void attachCurrentScenario()
    {
        for(Scenario scenario : getCurrentScenario().getScenarios() )
        {
            rootNode.attachChild(scenario);
        }
    }

    AppListener appListener;


    //TODO: MODIFY THIS TO RECEIVE A LIST<NODE> TO ATTACH THE SECNARIO TO THE RIGHT TRACKABLE/NODE
    public void setNextScenario(){
        if(getCurrentScenario().getIndex()+1 < scenarioList.size())
        {
            setCurrentScenario(scenarioList.getScenarioByIndex(getCurrentScenario().getIndex() + 1));
        }
    }
    public void setPreviousScenario(){
        if(getCurrentScenario().getIndex()-1 >= 0)
        {
            setCurrentScenario(scenarioList.getScenarioByIndex(getCurrentScenario().getIndex() - 1));
        }
    }
    public void setScenario(ScenarioEnum scenarioEnum){
        
        setCurrentScenario(scenarioList.getScenarioListByEnum(scenarioEnum));
        
    }

    public void simpleUpdate(float tpf){

        for(Scenario scenario : getCurrentScenario().getScenarios() )
        {
            if (scenario.simpleUpdate(tpf))
            {
                appListener.toggleInformativeMenuCallback(scenarioList.getScenarioEnumFromScenarioList(getCurrentScenario().getScenarios()));
            }
        }
//        if (allScenario.get(0).simpleUpdate(tpf))
//        {
//            appListener.toggleInformativeMenuCallback(ScenarioEnum.SOUNDCAPTURE);
//        }
    };



    public ScenarioManager(Node node, AssetManager assetManager, Camera cam, AppListener appListener)
    {
        this.appListener = appListener;
        this.rootNode = node;

        //Init SoundCapture scenario
        Scenario soundCapture = new SoundCapture(assetManager, cam);
        soundCapture.scale(20.0f);
        soundCapture.setName("SoundCapture");
        Quaternion rot = new Quaternion();
        rot.fromAngleAxis(3.14f / 2, new Vector3f(1.0f, 0.0f, 0.0f));
        soundCapture.rotate(rot);
        //this.rootNode.attachChild(soundCapture);
        //allScenario.add(soundCapture);
        List<Scenario> soundCaptureList = new ArrayList<Scenario>();
        soundCaptureList.add(soundCapture);
        scenarioList.addScenario(ScenarioEnum.SOUNDCAPTURE,soundCaptureList);


        //Only for debugging purpose deactivate it please.
        scenarioList.addScenario(ScenarioEnum.AMMODULATION,new ArrayList<Scenario>());
        scenarioList.addScenario(ScenarioEnum.FMMODULATION,new ArrayList<Scenario>());



        setCurrentScenario(scenarioList.getScenarioListByEnum(ScenarioEnum.AMMODULATION));


    }

    //See https://github.com/latestpost/JMonkey3-Android-Examples/blob/master/src/jmeproject/innovationtech/co/uk/Game7.java
    //For example
    @Override
    public void onTouch(String name, TouchEvent touchEvent, float v)
    {
        //Log.d(TAG,"Action on screen");
        for(Scenario scenario : getCurrentScenario().getScenarios() )
        {
            scenario.onScenarioTouch(name, touchEvent, v);
        }
        //allScenario.get(0).onScenarioTouch(name, touchEvent, v);
    }


    class ScenarioGroup
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

    }

    class ScenarioList
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
