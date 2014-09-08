package com.galimatias.teslaradio.subject;

import android.app.Activity;
import android.util.SparseArray;
import com.galimatias.teslaradio.R;

import java.util.ArrayList;
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

    /**
     * Add a all the subject content to the list with the language specified
     * @param activity
     */
    public static void addAllItems(Activity activity)
    {

        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.SOUNDCAPTURE.ordinal(), activity.getString(R.string.sound_capture_title), new int[]{R.layout.sound_capture_page1,R.layout.sound_capture_page2},ScenarioEnum.SOUNDCAPTURE));
        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.AMMODULATION.ordinal(), activity.getString(R.string.modulation_am_title), new int[]{R.layout.informative_info_detail_test, R.layout.informative_info_detail_test},ScenarioEnum.AMMODULATION));
        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.FMMODULATION.ordinal(), activity.getString(R.string.modulation_fm_title), new int[]{R.layout.informative_info_detail_test, R.layout.informative_info_detail_test, R.layout.informative_info_detail_test},ScenarioEnum.FMMODULATION));
        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.TRANSMIT.ordinal(), activity.getString(R.string.transmit_title), new int[]{R.layout.informative_info_detail_test, R.layout.informative_info_detail_test, R.layout.informative_info_detail_test},ScenarioEnum.TRANSMIT));
        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.RECEPTION.ordinal(), activity.getString(R.string.reception_title), new int[]{R.layout.informative_info_detail_test, R.layout.informative_info_detail_test, R.layout.informative_info_detail_test, R.layout.informative_info_detail_test},ScenarioEnum.RECEPTION));
        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.REFERENCE.ordinal(), activity.getString(R.string.reference_title), new int[]{R.layout.informative_info_detail_test, R.layout.informative_info_detail_test},ScenarioEnum.REFERENCE));
        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.ABOUTUS.ordinal(), activity.getString(R.string.about_us_title), new int[]{R.layout.informative_info_detail_test2},ScenarioEnum.ABOUTUS));

    }

    /**
     * Add individual item to the list
     * @param item
     */
    public static void addItem(SubjectItem item)
    {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * Remove all the items of the lists
     */
    public static void removeAllItems()
    {
        ITEMS.clear();
        ITEM_MAP.clear();
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
        private int[] listXml;
        private ScenarioEnum scenarioEnum;

        public SubjectItem(int id, String title, int[] listXml, ScenarioEnum scenarioEnum)
        {
            this.id = id;
            this.listXml = listXml;
            this.title = title;
            this.scenarioEnum = scenarioEnum;
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

        public int[] getListXml() {
            return listXml;
        }

        public ScenarioEnum getScenarioEnum() {
            return scenarioEnum;
        }
    }
}
