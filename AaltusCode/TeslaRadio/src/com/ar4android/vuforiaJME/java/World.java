package com.ar4android.vuforiaJME.java;

import com.qualcomm.vuforia.ImageTracker;
import com.qualcomm.vuforia.Tracker;
import com.qualcomm.vuforia.TrackerManager;
import com.utils.AppLogger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jimbojd72 on 10/21/14.
 */
public class World {

    private final String TAG = AaltusTrackable.class.getSimpleName();
    private Map<String, AaltusTrackable> _aaltusMap = new HashMap<String, AaltusTrackable>();

    static public World CinitWorld()
    {
        return new World();
    }
    /*
    CWorld* CdeleteWorld(CWorld* world)
    {
        World* w = (World*) world;
        delete w;
        return NULL;
    }
    */

    /*
    int CinitTracker(CWorld* cworld)
{
    World* w = (World*) cworld;
    return w->initializeTracker();
}
    *
    * */
    static public int CinitTracker(World cworld)
    {
        World w = cworld;
        return w.initializeTracker();
    }
    static boolean CdeInitTracker(World cworld)
    {
        World w = cworld;
        return w.deInitTracker();
    }
    static int CloadTrackers(World cworld)
    {
        World w = cworld;
        return w.loadTrackers();
    }
    static int CdestroyTrackerData(World cworld)
    {
        World w = cworld;
        w.destroyTrackerData();

        return 1;
    }

        public World()
        {
        AaltusTrackable chips =  new AaltusTrackable(0, "Chips");
        //Map<String, AaltusTrackable> chipsPair = new  (chips.getName(), chips);
        _aaltusMap.put(chips.getName(),chips);

        AaltusTrackable stones = new AaltusTrackable(1,"Stones");
        //pair<string, AaltusTrackable> stonesPair(stones.getName(), stones);
        _aaltusMap.put(stones.getName(), stones);
        }

        public int initializeTracker()
        {
            // Initialize the image tracker:
            TrackerManager trackerManager = TrackerManager.getInstance();
            Tracker tracker = trackerManager.initTracker(ImageTracker.getClassType());
            if (tracker == null)
            {
                AppLogger.getInstance().e(TAG, "Failed to initialize ImageTracker.");
                return 0;
            }
                AppLogger.getInstance().i(TAG,"Successfully initialized ImageTracker.");
            return 1;
        } 
        //public int deInitTracker()
        public boolean deInitTracker()
        {
            // Deinit the image tracker:
            TrackerManager trackerManager = TrackerManager.getInstance();
            return trackerManager.deinitTracker(ImageTracker.getClassType());
        }
        public int loadTrackers()
        {
            TrackerManager trackerManager = TrackerManager.getInstance();
            ImageTracker imageTracker = (ImageTracker)trackerManager.getTracker(ImageTracker.getClassType());
            if (imageTracker == null)
            {
                AppLogger.getInstance().e(TAG,"Failed to load tracking data set because the ImageTracker has not been initialized.");
                return 0;
            }


            for (Map.Entry<String,AaltusTrackable> entry : _aaltusMap.entrySet()) {
                String key = entry.getKey();
                AaltusTrackable value = entry.getValue();
                if(value.initializeDataSet(imageTracker) == 0){
                    return 0;
                };
                // do stuff
            }

            return 1;
        }

        public void destroyTrackerData()
        {
            TrackerManager trackerManager = TrackerManager.getInstance();
            ImageTracker imageTracker = (ImageTracker)trackerManager.getTracker(ImageTracker.getClassType());
            for (Map.Entry<String,AaltusTrackable> entry : _aaltusMap.entrySet()) {
                String key = entry.getKey();
                AaltusTrackable value = entry.getValue();
                value.destroyDataSet(imageTracker);
                // do stuff
            }
        }

        public AaltusTrackable getTrackable(String name)
        {
            return _aaltusMap.get(name);
        }

        public void setOrigin(String name)
        {
            AaltusTrackable origin = getTrackable(name);
            for (Map.Entry<String,AaltusTrackable> entry : _aaltusMap.entrySet()) {
                String key = entry.getKey();
                AaltusTrackable value = entry.getValue();
                value.setOrigin(origin);
                // do stuff
            }
        }
}
