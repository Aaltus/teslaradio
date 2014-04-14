#include "AaltusTrackable.h"

using namespace QCAR;

namespace aaltus{

AaltusTrackable::~AaltusTrackable()
{

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
        return 0;
    }
    if ( !_dataSet->load(_name.append(".xml").c_str(), QCAR::DataSet::STORAGE_APPRESOURCE) )
    {
        return 0;
    }
    if( !imageTracker->activateDataSet(_dataSet) )
    {
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
	calculatePosition();
}
void AaltusTrackable::setOrigin(AaltusTrackable* origin)
{
	_origin = origin;
	calculatePosition();
}

void AaltusTrackable::calculatePosition()
{
	Matrix44F offset = Tool::multiply(_origin->getInverseMV(), 
						SampleMath::Matrix44FTranspose(_poseMVMatrix));
	Vec4F position(0.0f,0.0f,0.0f,1.0f);
	_poseFromOrigin = SampleMath::Vec4FTransform(position,offset);
}
}//namespace aaltus