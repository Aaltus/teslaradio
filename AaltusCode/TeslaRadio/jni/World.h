#pragma once

#include "AaltusTrackable.h"

#include <QCAR/QCAR.h>
#include <QCAR/DataSet.h>
#include <QCAR/Tool.h>
#include <QCAR/ImageTracker.h>
#include <QCAR/TrackerManager.h>

#include <map>
#include <list>

#ifdef __cplusplus
extern "C"
{
#endif
typedef struct CWorld {} CWorld;

CWorld* CinitWorld();
int CinitTracker(CWorld* cworld);
int CdeInitTracker(CWorld* cworld);
int CloadTrackers(CWorld* cworld);
int CdestroyTrackerData(CWorld* cworld);

#ifdef __cplusplus
}
#endif

class World: public CWorld{
public:
    World();
    ~World();
    int initializeTracker();
    int deInitTracker();
    int loadTrackers();
    void destroyTrackerData();
    void setOrigin(const char* name);
    AaltusTrackable* getTrackable(const char* name);
    AaltusTrackable* getTrackable(std::string name);
private:
    std::map<std::string, AaltusTrackable> _aaltusMap;
};