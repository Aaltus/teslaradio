package com.galimatias.teslaradio.dummy;

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
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();


    public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    static {
        //Jonathan Desmarais: Add 7 static sample items.
        addItem(new DummyItem("1", "Introduction", new int[]{R.layout.test}));
        addItem(new DummyItem("2", "Transmission du son", new int[]{R.layout.test, R.layout.test}));
        addItem(new DummyItem("3", "Modulation", new int[]{R.layout.test, R.layout.test, R.layout.test}));
        addItem(new DummyItem("4", "Transmission et antennes", new int[]{R.layout.test, R.layout.test, R.layout.test}));
        addItem(new DummyItem("5", "Demodulation", new int[]{R.layout.test, R.layout.test, R.layout.test,R.layout.test}));
        addItem(new DummyItem("6", "Reference", new int[]{R.layout.test, R.layout.test}));
        addItem(new DummyItem("7", "Qui sommes nous ?", new int[]{R.layout.test}));
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     * It receive an string id, an title and a list
     * of XML layout (as R.Layout.name int).
     */
    public static class DummyItem {
        public String id;
        public String title;
        public int[] listXml;

        public DummyItem(String id, String title, int[] listXml) {
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
