#pragma once

#include "AaltusTrackable.h"

#include <QCAR/QCAR.h>
#include <QCAR/DataSet.h>
#include <QCAR/Tool.h>
#include <QCAR/ImageTracker.h>
#include <QCAR/TrackerManager.h>

#include <map>
#include <list>
namespace aaltus{
class World{
public:
	World();
	virtual ~World();

	int initializeTracker();
	int deInitTracker();
	int loadTrackers();
	void destroyTrackerData();
	AaltusTrackable* getTrackable(const char* name);
	AaltusTrackable* getTrackable(std::string name);
	
	void			 setOrigin(const char* name);
	
	
private:
	std::map<std::string, AaltusTrackable> _aaltusMap;

	
};
}//namespace aaltus