#include "AaltusTrackable.h"

using namespace QCAR;



AaltusTrackable::~AaltusTrackable()
{

}

Vec4F AaltusTrackable::getPositionFromOrigin()
{
    LOGE("Trackable is %s and position is %f %f %f", _name.c_str(), _poseFromOrigin.data[0],_poseFromOrigin.data[1],_poseFromOrigin.data[2]);
    return  _poseFromOrigin;
}
Vec3F AaltusTrackable::getPositionFromCamera()
{
	Vec3F pos(_invTranspMV.data[12],_invTranspMV.data[13],_invTranspMV.data[14]);
	return pos;
}

int AaltusTrackable::initializeDataSet(QCAR::ImageTracker* imageTracker)
{

    _dataSet = imageTracker->createDataSet();
    if( !_dataSet )
    {
        LOGE("Failed to create %s dataset",_name.c_str());
        return 0;
    }
    std::string ds = _name;
    ds.append(".xml");
    if ( !_dataSet->load(ds.c_str(), QCAR::DataSet::STORAGE_APPRESOURCE) )
    {
        LOGE("Failed to load %s dataset",_name.c_str());
        return 0;
    }
    if( !imageTracker->activateDataSet(_dataSet) )
    {
         LOGE("Failed to activate %s dataset",_name.c_str());
        return 0;
    }
    return 1;
}

void AaltusTrackable::destroyDataSet(QCAR::ImageTracker* imageTracker)
{
    if ( _dataSet )
    {
        imageTracker->deactivateDataSet ( _dataSet );
        imageTracker->destroyDataSet ( _dataSet );
        _dataSet = 0;
    }
}

void AaltusTrackable::setCameraPosition(Matrix44F modelViewMatrix)
{
	_poseMVMatrix = modelViewMatrix;
	_inverseMV = SampleMath::Matrix44FInverse(_poseMVMatrix);
	_invTranspMV = MathUtil::Matrix44FTranspose(_inverseMV);
}
void AaltusTrackable::setOrigin(AaltusTrackable* origin)
{
	_origin = origin;
	calculatePosition();
}

void AaltusTrackable::calculatePosition()
{
    if(_origin == this)
    {
        _poseFromOrigin.data[0] = _invTranspMV.data[12];
        _poseFromOrigin.data[1] = _invTranspMV.data[13];
        _poseFromOrigin.data[2] = _invTranspMV.data[14];
        _poseFromOrigin.data[4] = _invTranspMV.data[15];

    }
    else
    {
	    Matrix44F offset = Tool::multiply(_origin->getInverseMV(),
						SampleMath::Matrix44FTranspose(_poseMVMatrix));
	    Vec4F position(0.0f,0.0f,0.0f,1.0f);
	    _poseFromOrigin = SampleMath::Vec4FTransform(position,offset);
    }
}
