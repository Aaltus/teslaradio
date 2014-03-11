package com.galimatias.teslaradio.subject;

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


    public static Map<String, SubjectItem> ITEM_MAP = new HashMap<String, SubjectItem>();

    static {
        //Jonathan Desmarais: Add 7 static sample items.
        // THIS HERE YOU WILL ADD CATEGORY OF INFORMATIVE MENU AND SUBPAGES
        addItem(new SubjectItem("1", "Introduction", new int[]{R.layout.test2}));
        addItem(new SubjectItem("2", "Transmission du son", new int[]{R.layout.test, R.layout.test}));
        addItem(new SubjectItem("3", "Modulation", new int[]{R.layout.test, R.layout.test, R.layout.test}));
        addItem(new SubjectItem("4", "Transmission et antennes", new int[]{R.layout.test, R.layout.test, R.layout.test}));
        addItem(new SubjectItem("5", "Demodulation", new int[]{R.layout.test, R.layout.test, R.layout.test,R.layout.test}));
        addItem(new SubjectItem("6", "Reference", new int[]{R.layout.test, R.layout.test}));
        addItem(new SubjectItem("7", "Qui sommes nous ?", new int[]{R.layout.test2}));
    }

    private static void addItem(SubjectItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A subject item representing a piece of content.
     * It receive an string id, an title and a list
     * of XML layout (as R.Layout.name int).
     */
    public static class SubjectItem {
        public String id;
        public String title;
        public int[] listXml;

        public SubjectItem(String id, String title, int[] listXml) {
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
