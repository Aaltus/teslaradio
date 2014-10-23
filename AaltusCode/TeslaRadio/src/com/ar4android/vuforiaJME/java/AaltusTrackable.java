package com.ar4android.vuforiaJME.java;

import android.util.Log;
import com.qualcomm.vuforia.*;
import com.utils.AppLogger;

/**
 * Created by jimbojd72 on 10/21/14.
 */
public class AaltusTrackable {

    private final String TAG = AaltusTrackable.class.getSimpleName();

    int _id;
    String _name;
    Matrix44F _poseMVMatrix = new Matrix44F();
    Matrix44F _inverseMV    = new Matrix44F();
    Matrix44F _invTranspMV  = new Matrix44F();
    DataSet   _dataSet;
    Vec4F     _poseFromOrigin = new Vec4F();
    AaltusTrackable	_origin;

    public int getId(){return _id;}
    public Matrix44F   getPoseMatrix(){return _poseMVMatrix;}
    public Matrix44F   getInverseMV(){return _inverseMV;}
    public Matrix44F   getInvTranspMV(){return _invTranspMV;}
    public String      getName(){return _name;}

    public AaltusTrackable(int id, String name){
        _id = id;
        _name=name;
        _origin=this;
    }

    public Vec4F getPositionFromOrigin()
    {
        //LOGE("Trackable is %s and position is %f %f %f", _name.c_str(), _poseFromOrigin.data[0],_poseFromOrigin.data[1],_poseFromOrigin.data[2]);
        return  _poseFromOrigin;
    }

    public Vec3F getPositionFromCamera()
    {
        Vec3F pos= new Vec3F(_poseMVMatrix.getData()[12],_poseMVMatrix.getData()[13],_poseMVMatrix.getData()[14]);
        return pos;
    }

    public int initializeDataSet(ImageTracker imageTracker)
    {

        _dataSet = imageTracker.createDataSet();
        //if( !_dataSet. )
        if( _dataSet == null )
        {
            AppLogger.getInstance().e(TAG,"Failed to create "+_name+" dataset");
            return 0;
        }
        String ds = _name;
        ds = ds+ ".xml";
        if ( !_dataSet.load(ds, STORAGE_TYPE.STORAGE_APPRESOURCE))
        {
            AppLogger.getInstance().e(TAG, "Failed to load " + _name + " dataset");
            return 0;
        }
        if( !imageTracker.activateDataSet(_dataSet) )
        {
            AppLogger.getInstance().e(TAG, "Failed to activate " + _name + " dataset");
            return 0;
        }

        for (int i = 0; i < _dataSet.getNumTrackables(); i++)
        {
            Trackable trackable = _dataSet.getTrackable(i);
            if (trackable.getName().equals(_name)) //NOT SURE ABOUT THIS
            {
                //UNCOMMENT THESE LINES TO ACTIVATE THE EXTEMDED TRACKING.
                // Start extended tracking on “chips” target
                //if (!trackable->startExtendedTracking())
                //{
                //    LOGD ("Failed to start extended tracking on %s target",_name.c_str());
                //}
            }
        }
        return 1;
    }

    void destroyDataSet(ImageTracker imageTracker)
    {
        if ( _dataSet != null )
        {
            imageTracker.deactivateDataSet(_dataSet);
            imageTracker.destroyDataSet(_dataSet);
            _dataSet = null;
        }
    }

    void setCameraPosition(Matrix44F modelViewMatrix)
    {
        _poseMVMatrix = modelViewMatrix;
        _inverseMV = SampleMath.Matrix44FInverse(_poseMVMatrix);
        //_invTranspMV = MathUtil::Matrix44FTranspose(_inverseMV);
        //_invTranspMV = SampleMath.Matrix44FTranspose(_inverseMV);
        _invTranspMV = MathUtil.Matrix44FTranspose(_inverseMV);
    }

    void setOrigin(AaltusTrackable origin)
    {
        _origin = this;// origin;
        calculatePosition();
    }

    void calculatePosition()
    {
        if(_origin == this)
        {
       /* _poseFromOrigin.data[0] = _invTranspMV.data[12];
        _poseFromOrigin.data[1] = _invTranspMV.data[13];
        _poseFromOrigin.data[2] = _invTranspMV.data[14];
        _poseFromOrigin.data[4] = _invTranspMV.data[15];*/
            //Log.d(TAG,_poseFromOrigin)
            _poseFromOrigin.getData()[0] = _poseMVMatrix.getData()[12];
            _poseFromOrigin.getData()[1] = _poseMVMatrix.getData()[13];
            _poseFromOrigin.getData()[2] = _poseMVMatrix.getData()[14];
            _poseFromOrigin.getData()[3] = _poseMVMatrix.getData()[15];

        }
        else
        {
            Matrix44F offset = Tool.multiply(_origin.getInverseMV(),
                    SampleMath.Matrix44FTranspose(_poseMVMatrix));
            Vec4F position = new Vec4F(0.0f,0.0f,0.0f,1.0f);
            _poseFromOrigin = SampleMath.Vec4FTransform(position,offset);
        }
    }
}
