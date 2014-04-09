package com.galimatias.teslaradio.subject;

import android.app.Activity;
import com.galimatias.teslaradio.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class SubjectContent {




    /**
     * An array of sample (dummy) items.
     */
    public static List<SubjectItem> ITEMS = new ArrayList<SubjectItem>();


    public static Map<Integer, SubjectItem> ITEM_MAP = new HashMap<Integer, SubjectItem>();

    static {
        //Jonathan Desmarais: Add 7 static sample items.
        // THIS HERE YOU WILL ADD CATEGORY OF INFORMATIVE MENU AND SUBPAGES
//        addItem(new SubjectItem("1", "Capture du Son", new int[]{R.layout.informative_info_detail_test2}));
//        addItem(new SubjectItem("2", "Modulation AM", new int[]{R.layout.informative_info_detail_test, R.layout.informative_info_detail_test}));
//        addItem(new SubjectItem("3", "Modulation FM", new int[]{R.layout.informative_info_detail_test, R.layout.informative_info_detail_test, R.layout.informative_info_detail_test}));
//        addItem(new SubjectItem("4", "Transmission d'information", new int[]{R.layout.informative_info_detail_test, R.layout.informative_info_detail_test, R.layout.informative_info_detail_test}));
//        addItem(new SubjectItem("5", "Radio Réceptrice", new int[]{R.layout.informative_info_detail_test, R.layout.informative_info_detail_test, R.layout.informative_info_detail_test,R.layout.informative_info_detail_test}));
//        addItem(new SubjectItem("6", "Reference", new int[]{R.layout.informative_info_detail_test, R.layout.informative_info_detail_test}));
//        addItem(new SubjectItem("7", "Qui sommes nous ?", new int[]{R.layout.informative_info_detail_test2}));
    }

    public static void addAllItems(Activity activity){

        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.SOUNDCAPTURE.ordinal(), activity.getString(R.string.sound_capture_title), new int[]{R.layout.sound_capture_page1,R.layout.sound_capture_page2}));
        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.AMMODULATION.ordinal(), activity.getString(R.string.modulation_am_title), new int[]{R.layout.informative_info_detail_test, R.layout.informative_info_detail_test}));
        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.FMMODULATION.ordinal(), activity.getString(R.string.modulation_fm_title), new int[]{R.layout.informative_info_detail_test, R.layout.informative_info_detail_test, R.layout.informative_info_detail_test}));
        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.TRANSMIT.ordinal(), activity.getString(R.string.transmit_title), new int[]{R.layout.informative_info_detail_test, R.layout.informative_info_detail_test, R.layout.informative_info_detail_test}));
        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.RECEPTION.ordinal(), activity.getString(R.string.reception_title), new int[]{R.layout.informative_info_detail_test, R.layout.informative_info_detail_test, R.layout.informative_info_detail_test, R.layout.informative_info_detail_test}));
        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.REFERENCE.ordinal(), activity.getString(R.string.reference_title), new int[]{R.layout.informative_info_detail_test, R.layout.informative_info_detail_test}));
        SubjectContent.addItem(new SubjectContent.SubjectItem(ScenarioEnum.ABOUTUS.ordinal(), activity.getString(R.string.about_us_title), new int[]{R.layout.informative_info_detail_test2}));

    }


    public static void addItem(SubjectItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static void removeAllItems(){

        ITEMS.clear();
        ITEM_MAP.clear();
    }

    /**
     * A subject item representing a piece of content.
     * It receive an string id, an title and a list
     * of XML layout (as R.Layout.name int).
     */
    public static class SubjectItem {
        public int id;
        public String title;
        public int[] listXml;

        public SubjectItem(int id, String title, int[] listXml) {
            this.id = id;
            this.listXml = listXml;
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
