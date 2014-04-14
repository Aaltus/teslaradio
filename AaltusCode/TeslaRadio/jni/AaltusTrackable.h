#pragma once

#include "SampleMath.h"
#include "MathUtils.h"
#include "Math.h"
#include "AaltusCommon.h"


#include <QCAR/QCAR.h>
#include <QCAR/DataSet.h>
#include <QCAR/Tool.h>
#include <QCAR/ImageTracker.h>
#include <string>
namespace aaltus{
class AaltusTrackable {

public:
	AaltusTrackable(int id, const char* name){_id = id;_origin=this;}
	virtual ~AaltusTrackable();
	
	/**
	*Method used to get the position from the relative origin
	*@return SampleMath::Vec3F position vector
	*/
	QCAR::Vec4F getPositionFromOrigin(){return _poseFromOrigin;}
	/**
	*Method used to get the position from the device camera.
	*@see getInvTranspMV to get the full matrix with rotation
	*@return Sample::Vec3F The position vector
	*/
	QCAR::Vec3F getPositionFromCamera();
	int				  getId(){return _id;}
	std::string		  getName(){return _name;}
	QCAR::Matrix44F   getInverseMV(){return _inverseMV;}
	QCAR::Matrix44F   getInvTranspMV(){return _invTranspMV;}
	
	void              setCameraPosition(QCAR::Matrix44F modelViewMatrix);
	void              setOrigin(AaltusTrackable* origin);

	int               initializeDataSet(QCAR::ImageTracker* imageTracker);
	void              destroyDataSet(QCAR::ImageTracker* imageTracker);
	
	
	
private:
	void 				calculatePosition();
	QCAR::Matrix44F 	_poseMVMatrix;
	QCAR::Matrix44F 	_inverseMV;
	QCAR::Matrix44F 	_invTranspMV;
	QCAR::DataSet*		_dataSet;
	QCAR::Vec4F 	    _poseFromOrigin;
	AaltusTrackable*	_origin;
	std::string			_name;
	int 				_id;
};
}//namespace aaltus
	