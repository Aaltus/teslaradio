#include "World.h"

using namespace std;

#ifdef __cplusplus
extern "C"
{
#endif
CWorld* CinitWorld()
{
    return new World();
}
CWorld* CdeleteWorld(CWorld* world)
{
    World* w = (World*) world;
    delete w;
    return NULL;
}

int CinitTracker(CWorld* cworld)
{
    World* w = (World*) cworld;
    return w->initializeTracker();
}
int CdeInitTracker(CWorld* cworld)
{
     World* w = (World*) cworld;
     return w->deInitTracker();
}
int CloadTrackers(CWorld* cworld)
{
     World* w = (World*) cworld;
     return w->loadTrackers();
}
int CdestroyTrackerData(CWorld* cworld)
{
     World* w = (World*) cworld;
     w->destroyTrackerData();

     return 1;
}
#ifdef __cplusplus
}
#endif
World::World()
{
	AaltusTrackable chips(0, "Chips");
	pair<string, AaltusTrackable> chipsPair(chips.getName(), chips);
	_aaltusMap.insert(chipsPair);
	
	AaltusTrackable stones(1,"Stones");
	pair<string, AaltusTrackable> stonesPair(stones.getName(), stones);
	_aaltusMap.insert(stonesPair);
}
World::~World()
{

}
int World::initializeTracker()
{

 	   // Initialize the image tracker:
 	QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
 	QCAR::Tracker* tracker = trackerManager.initTracker(QCAR::ImageTracker::getClassType());
    if (tracker == NULL)
 	{
 	 	LOGE("Failed to initialize ImageTracker.");
 	    return 0;
 	}
 	LOGI("Successfully initialized ImageTracker.");
 	return 1;
}
int World::deInitTracker()
 {


 	    // Deinit the image tracker:
 	 QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
 	 trackerManager.deinitTracker(QCAR::ImageTracker::getClassType());
}
int World::loadTrackers()
{
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    QCAR::ImageTracker* imageTracker = static_cast<QCAR::ImageTracker*>(
                        trackerManager.getTracker(QCAR::ImageTracker::getClassType()));
    if (imageTracker == NULL)
    {
        LOGE("Failed to load tracking data set because the ImageTracker has not"
            " been initialized.");
        return 0;
    }

    for(map<string,AaltusTrackable>::iterator itr = _aaltusMap.begin(); itr != _aaltusMap.end(); itr++)
    {

    	if( itr->second.initializeDataSet(imageTracker) == 0 )
    	{

    	    return 0;
    	}

    }

    return 1;
}

void World::destroyTrackerData()
{
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    QCAR::ImageTracker* imageTracker = static_cast<QCAR::ImageTracker*>(
            trackerManager.getTracker(QCAR::ImageTracker::getClassType()));
     for(map<string,AaltusTrackable>::iterator itr = _aaltusMap.begin(); itr != _aaltusMap.end(); itr++)
     {
        itr->second.destroyDataSet(imageTracker);
     }
}

AaltusTrackable* World::getTrackable(const char* name)
{
	string strName(name);
	return getTrackable(strName);
}

AaltusTrackable* World::getTrackable(string name)
{
	return &_aaltusMap.find(name)->second;
}

void World::setOrigin(const char* name)
{
	AaltusTrackable* origin = getTrackable(name);
	for(map<string,AaltusTrackable>::iterator itr = _aaltusMap.begin(); itr != _aaltusMap.end(); itr++)
	{
		itr->second.setOrigin(origin);
	}
}


