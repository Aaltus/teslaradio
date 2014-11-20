package com.aaltus.teslaradio.subject;

import android.app.Activity;
import android.util.SparseArray;
import com.aaltus.teslaradio.R;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * Static class to provide a list of scenario and page for detail fragments
 */
public class SubjectContent {


    /**
     * An array of sample (subject) items.
     */
    public static List<SubjectItem> ITEMS = new ArrayList<SubjectItem>();
    public static SparseArray<SubjectItem> ITEM_MAP = new SparseArray<SubjectItem>();
    public static EnumMap<ScenarioEnum,SubjectItem> ENUM_MAP = new EnumMap<ScenarioEnum, SubjectItem>(ScenarioEnum.class);

    /**
     * Add a all the subject content to the list with the language specified
     * @param activity
     */
    public static void addAllItems(Activity activity)
    {
//        Example avec plusieurs pages
//        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.MODULATION.ordinal(), activity.getString(R.string.modulation_am_title), new int[]{R.layout.informative_info_detail_test, R.layout.informative_info_detail_test},ScenarioEnum.MODULATION));

        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.SOUNDEMISSION.ordinal(), activity.getString(R.string.sound_emission_title), new int[]{R.layout.sound_emission}, new int[]{R.string.sound_emission_tutorial_p1}, ScenarioEnum.SOUNDEMISSION, R.drawable.picto_1));
        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.SOUNDCAPTURE.ordinal(), activity.getString(R.string.sound_capture_title), new int[]{R.layout.sound_capture}, new int[]{R.string.sound_capture_tutorial_p1}, ScenarioEnum.SOUNDCAPTURE, R.drawable.picto_2));
        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.MODULATION.ordinal(), activity.getString(R.string.modulation_title), new int[]{R.layout.modulation}, new int[]{R.string.modulation_tutorial_p1,R.string.modulation_tutorial_p2,R.string.modulation_tutorial_p3}, ScenarioEnum.MODULATION, R.drawable.picto_3));
        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.TRANSMIT.ordinal(), activity.getString(R.string.transmit_title), new int[]{R.layout.transmission}, new int[]{R.string.transmission_tutorial_p1,R.string.transmission_tutorial_p2}, ScenarioEnum.TRANSMIT, R.drawable.picto_4));
        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.RECEPTION.ordinal(), activity.getString(R.string.reception_title), new int[]{R.layout.reception}, new int[]{R.string.reception_tutorial_p1}, ScenarioEnum.RECEPTION, R.drawable.picto_5));
        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.FILTER.ordinal(), activity.getString(R.string.filter_title), new int[]{R.layout.filter}, new int[]{R.string.filter_tutorial_p1}, ScenarioEnum.FILTER, R.drawable.picto_6));
        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.DEMODULATION.ordinal(), activity.getString(R.string.demodulation_title), new int[]{R.layout.demodulation}, new int[]{R.string.demodulation_tutorial_p1,R.string.demodulation_tutorial_p2,R.string.playback_tutorial_p1}, ScenarioEnum.DEMODULATION, R.drawable.picto_7));
        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.PLAYBACK.ordinal(), activity.getString(R.string.playback_title), new int[]{R.layout.playback}, new int[]{}, ScenarioEnum.PLAYBACK, null));
        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.REFERENCE.ordinal(), activity.getString(R.string.reference_title), new int[]{R.layout.references}, null, ScenarioEnum.REFERENCE, null));
            }

    /**
     * Add individual item to the list
     * @param item
     */
    public static void addItem(SubjectItem item)
    {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
        ENUM_MAP.put(item.getScenarioEnum(),item);
    }

    /**
     * Remove all the items of the lists
     */
    public static void removeAllItems()
    {
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    public static int getPictogramCount(){

        int count=0;
        for(SubjectItem subjectItem : ITEMS){
            if(subjectItem.getPictogramDrawableId() != null){
                count++;
            }
        }
        return count;
    }

    /**
     * A subject item representing a piece of content.
     * It receive an string id, an title and a list
     * of XML layout (as R.Layout.name int).
     */
    public static class SubjectItem

    {
        private int id;
        private String title;
        private int[] listXmlDetailmenu;
        private int[] listStringIdTutorial;
        private ScenarioEnum scenarioEnum;
        private Integer pictogramDrawableId;

        public SubjectItem(int id, String title, int[] listXmlDetailmenu, int[] listStringIdTutorial, ScenarioEnum scenarioEnum, Integer pictogramDrawableId)
        {
            this.id = id;
            this.listXmlDetailmenu = listXmlDetailmenu;
            this.listStringIdTutorial = listStringIdTutorial;
            this.title = title;
            this.scenarioEnum = scenarioEnum;
            this.pictogramDrawableId = pictogramDrawableId;
        }

        @Override
        public String toString()
        {
            return title;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public int[] getListXmlDetailMenu() {
            return listXmlDetailmenu;
        }

        public int[] getListStringIdTutorial() {
            return this.listStringIdTutorial;
        }

        public ScenarioEnum getScenarioEnum() {
            return scenarioEnum;
        }

        public Integer getPictogramDrawableId() {
            return pictogramDrawableId;
        }


    }
}
