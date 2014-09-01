/* VuforiaJMEActivity - VuforiaJME Example
 * 
 * Example Chapter 5
 * accompanying the book
 * "Augmented Reality for Android Application Development", Packt Publishing, 2013.
 * 
 * Copyright © 2013 Jens Grubert, Raphael Grasset / Packt Publishing.
 * 
 * This example is dependent of the Qualcomm Vuforia SDK 
 * The Vuforia SDK is a product of Qualcomm Austria Research Center GmbH
 * 
 * https://developer.vuforia.com
 * 
 * This example was built from the ImageTarget example accompanying the Vuforia SDK
 * https://developer.vuforia.com/resources/sample-apps/image-targets-sample-app
 * 
 */

package com.ar4android.vuforiaJME;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.galimatias.teslaradio.InformativeMenuFragment;
import com.galimatias.teslaradio.R;
import com.galimatias.teslaradio.SplashscreenDialogFragment;
import com.galimatias.teslaradio.subject.ScenarioEnum;
import com.galimatias.teslaradio.subject.SubjectContent;
import com.jme3.system.android.AndroidConfigChooser.ConfigType;
import com.jme3.texture.Image;
import com.qualcomm.QCAR.QCAR;
import com.utils.LanguageLocaleChanger;

import java.nio.ByteBuffer;
import java.util.logging.Level;

//Old code
//public class VuforiaJMEActivity extends AndroidHarness {

/**
 * Center of the Android side of the application. All Android view and specific thing are here.
 * It also initialize vuforia library and jme app.
 */
public class VuforiaJMEActivity extends AndroidHarnessFragmentActivity implements VuforiaJME.AppListener {

    private static final boolean UseProfiler = false;

	private static final String TAG = VuforiaJMEActivity.class.getName();

    private final String INFORMATIVE_MENU_FRAGMENT_TAG = "INFORMATIVE_MENU_FRAGMENT_TAG";
    private final String ITEM_SPLASHSCREEN_FRAGMENT_TAG ="SPLASHSCREEN_FRAGMENT_TAG" ;
	
    // Focus mode constants:
    private static final int FOCUS_MODE_NORMAL = 0;
    private static final int FOCUS_MODE_CONTINUOUS_AUTO = 1;

    // Application status constants:
    private static final int APPSTATUS_UNINITED         = -1;
    private static final int APPSTATUS_INIT_APP         = 0;
    private static final int APPSTATUS_INIT_QCAR        = 1;
    private static final int APPSTATUS_INIT_TRACKER     = 2;
    private static final int APPSTATUS_INIT_APP_AR      = 3;
    private static final int APPSTATUS_LOAD_TRACKER     = 4;
    private static final int APPSTATUS_INIT_LAYOUT      = 5;
    private static final int APPSTATUS_INITED           = 6;
    private static final int APPSTATUS_CAMERA_STOPPED   = 7;
    private static final int APPSTATUS_CAMERA_RUNNING   = 8;

    // Name of the native dynamic libraries to load:
    private static final String NATIVE_LIB_SAMPLE = "VuforiaNative";
    private static final String NATIVE_LIB_QCAR = "Vuforia";



    // Display size of the device:
    private int mScreenWidth = 0;
    private int mScreenHeight = 0;

    // Constant representing invalid screen orientation to trigger a query:
    private static final int INVALID_SCREEN_ROTATION = -1;

    // Last detected screen rotation:
    private int mLastScreenRotation = INVALID_SCREEN_ROTATION;

    // The current application status:
    private int mAppStatus = APPSTATUS_UNINITED;

    // The async tasks to initialize the QCAR SDK:
    private InitQCARTask mInitQCARTask;
    private LoadTrackerTask mLoadTrackerTask;

    // An object used for synchronizing QCAR initialization, dataset loading and
    // the Android onDestroy() life cycle event. If the application is destroyed
    // while a data set is still being loaded, then we wait for the loading
    // operation to finish before shutting down QCAR:
    private Object mShutdownLock = new Object();

    // QCAR initialization flags:
    private int mQCARFlags = 0;

    // Contextual Menu Options for Camera Flash - Autofocus
    private boolean mFlash = false;
    private boolean mContAutofocus = false;

    // The menu item for swapping data sets:
    MenuItem mDataSetMenuItem = null;
    boolean mIsStonesAndChipsDataSetActive  = false;
    
    private RelativeLayout mUILayout;


    /** Native tracker initialization and deinitialization. */
    public native int initTracker();
    public native void deinitTracker();

    /** Native functions to load and destroy tracking data. */
    public native int loadTrackerData();
    public native void destroyTrackerData();

    /** Native sample initialization. */
    public native void onQCARInitializedNative();

    /** Native methods for starting and stopping the camera. */
    private native void startCamera();
    private native void stopCamera();

    /** Native method for setting / updating the projection matrix
     * for AR content rendering */
    private native void setProjectionMatrix();

    /** Native function to initialize the application. */
    private native void initApplicationNative(int width, int height);

    /** Native function to deinitialize the application.*/
    private native void deinitApplicationNative();

    /** Tells native code whether we are in portait or landscape mode */
    private native void setActivityPortraitMode(boolean isPortrait);

    /** Tells native code to switch dataset as soon as possible*/
    private native void switchDatasetAsap();

    private native boolean autofocus();
    private native boolean setFocusMode(int mode);

    /** Activates the Flash */
    private native boolean activateFlash(boolean flash);

    private boolean mCreatedBefore = false;

    public VuforiaJMEActivity()
    {
        // Set the application class to runs
        appClass = "com.ar4android.vuforiaJME.VuforiaJME";
        // Try ConfigType.FASTEST; or ConfigType.LEGACY if you have problems
        eglConfigType = ConfigType.BEST;

        // Exit Dialog title & messages
        exitDialogTitle   = "Exit?";
        exitDialogMessage = "Press Yes";
        // Enable verbose logging
        eglConfigVerboseLogging = false;

        // Choose screen orientation
        screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        // Invert the MouseEvents X (default = true)
        mouseEventsInvertX = true;
        // Invert the MouseEvents Y (default = true)
        mouseEventsInvertY = true;

        logger.setLevel(Level.SEVERE);
    }

    Image cameraJMEImageRGB565;
    private byte[] mPreviewBufferRGB656;
    java.nio.ByteBuffer mPreviewByteBufferRGB565;
    static boolean firstTimeGetImage=true;

    //TO BE REMOVED
//    //SoundAlert specific info
//    private int mThreshold = 10;
//    private boolean mAudioRunning = false;
//    private SoundMeter mSensor;
//    private int mHitCount = 0;
//    private int mHitCountThreshold = 5;
//    private static final int POLL_INTERVAL = 100;
//    private Handler mHandler = new Handler();
//
//    private Runnable mPollTask = new Runnable() {
//        public void run() {
//            double amp = mSensor.getAmplitude();
//
//            Log.d(TAG,"mPollTask: " + Double.toString(amp));
//            if ((amp > mThreshold)) {
//
//                VuforiaJME.onAudioEvent task = ((VuforiaJME) app). new onAudioEvent();
//                ((VuforiaJME) app).enqueue(task);
//
//            }
//            if(mAudioRunning){
//                mHandler.postDelayed(mPollTask, POLL_INTERVAL);
//            }
//        }
//    };
//
//    private void audioStart() {
//        mHitCount = 0;
//        mAudioRunning = true;
//        mSensor.start();
//        Log.i(TAG, "AudioStart");
//        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
//    }
//
//    private void audioStop() {
//        //mHandler.removeCallbacks(mSleepTask);
//        mHandler.removeCallbacks(mPollTask);
//        mSensor.stop();
//        Log.i(TAG, "AudioStop");
//        mAudioRunning = false;
//    }


    /** A helper for loading native libraries stored in "libs/armeabi*". */
    public static boolean loadLibrary(String nLibName)
    {
        try
        {
            System.loadLibrary(nLibName);
            Log.e(TAG, "Native library lib" + nLibName + ".so loaded");
            return true;
        }
        catch (UnsatisfiedLinkError ulee)
        {
            Log.e(TAG, "The library lib" + nLibName +
                            ".so could not be loaded");
        }
        catch (SecurityException se)
        {
        	Log.e(TAG, "The library lib" + nLibName +
                            ".so was not allowed to be loaded");
        }

        return false;
    }

   
    /** Static initializer block to load native libraries on start-up. */
    static
    {
        loadLibrary(NATIVE_LIB_QCAR);
        loadLibrary(NATIVE_LIB_SAMPLE);
    }



    /**
    Show the informative menu with the provided scenario to show
     */
    @Override
    public void showInformativeMenuCallback(final ScenarioEnum scenarioEnum)
    {
        Log.d(TAG,"showInformativeMenuCallback");

        //Run on UI thread
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                FragmentManager fm = getSupportFragmentManager();//getSupportFragmentManager();
                InformativeMenuFragment informativeMenuFragment =
                        (InformativeMenuFragment) fm.findFragmentByTag(INFORMATIVE_MENU_FRAGMENT_TAG);
                informativeMenuFragment.toggleFragmentsVisibility(scenarioEnum);

            }
        });
    }

    @Override
    public void onFinishSimpleInit()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                dismissSplashscreenDialog();
            }
        });

    }


    /** An async task to initialize QCAR asynchronously. */
    private class InitQCARTask extends AsyncTask<Void, Integer, Boolean>
    {
        // Initialize with invalid value:
        private int mProgressValue = -1;

        protected Boolean doInBackground(Void... params)
        {
            // Prevent the onDestroy() method to overlap with initialization:
            synchronized (mShutdownLock)
            {
                QCAR.setInitParameters(VuforiaJMEActivity.this, mQCARFlags);

                do
                {
                    // QCAR.init() blocks until an initialization step is
                    // complete, then it proceeds to the next step and reports
                    // progress in percents (0 ... 100%).
                    // If QCAR.init() returns -1, it indicates an error.
                    // Initialization is done when progress has reached 100%.
                    mProgressValue = QCAR.init();

                    // Publish the progress value:
                    publishProgress(mProgressValue);

                    // We check whether the task has been canceled in the
                    // meantime (by calling AsyncTask.cancel(true)).
                    // and bail out if it has, thus stopping this thread.
                    // This is necessary as the AsyncTask will run to completion
                    // regardless of the status of the component that
                    // started is.
                } while (!isCancelled() && mProgressValue >= 0
                         && mProgressValue < 100);

                return (mProgressValue > 0);
            }
        }

        protected void onPostExecute(Boolean result)
        {
            // Done initializing QCAR, proceed to next application
            // initialization status:
            if (result)
            {
                Log.d(TAG,"InitQCARTask::onPostExecute: QCAR " +
                              "initialization successful");

                updateApplicationStatus(APPSTATUS_INIT_TRACKER);
            }
            else
            {
            	Log.d(TAG,"InitQCARTask::onPostExecute: QCAR " +
                    "initialization failed");
            }
        }
    }

    /** An async task to load the tracker data asynchronously. */
    private class LoadTrackerTask extends AsyncTask<Void, Integer, Boolean>
    {
        protected Boolean doInBackground(Void... params)
        {
            // Prevent the onDestroy() method to overlap:
            synchronized (mShutdownLock)
            {
                // Load the tracker data set:
                return (loadTrackerData() > 0);
            }
        }

        protected void onPostExecute(Boolean result)
        {
            Log.d(TAG,"LoadTrackerTask::onPostExecute: execution " +
                        (result ? "successful" : "failed"));

            if (result)
            {
                // The stones and chips data set is now active:
                mIsStonesAndChipsDataSetActive = true;

                // Done loading the tracker, update application status:
                updateApplicationStatus(APPSTATUS_INIT_LAYOUT);
            }
            else
            {
            	
            }
        }
    }


    /** Stores screen dimensions */
    private void storeScreenDimensions()
    {
        // Query display dimensions:
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Log.d(TAG, "Store screen dimension width: " + Integer.toString(metrics.widthPixels) + " heigth: " + Integer.toString(metrics.heightPixels) );

        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
    }
    
 
    /**
     * Updates projection matrix and viewport after a screen rotation
     * change was detected.
     */
    public void updateRenderView()
    {
        int currentScreenRotation = getWindowManager().getDefaultDisplay().getRotation();
        if (currentScreenRotation != mLastScreenRotation)
        {
            // Set projection matrix if there is already a valid one:
            if (QCAR.isInitialized() && (mAppStatus == APPSTATUS_CAMERA_RUNNING))
            {
                Log.d(TAG,"VuforiaJMEActivity::updateRenderView");

                // Query display dimensions:
                storeScreenDimensions();

                // Update viewport via renderer:
                //TODO SET Screen here
               // mRenderer.updateRendering(mScreenWidth, mScreenHeight);

                // Update projection matrix:
                setProjectionMatrix();

                // Cache last rotation used for setting projection matrix:
                mLastScreenRotation = currentScreenRotation;
            }
        }
    }

    /** NOTE: this method is synchronized because of a potential concurrent
     * access by VuforiaJMEActivity::onResume() and InitQCARTask::onPostExecute(). */
    private synchronized void updateApplicationStatus(int appStatus)
    {
        // Exit if there is no change in status:
        if (mAppStatus == appStatus)
            return;

        // Store new status value:
        mAppStatus = appStatus;

        // Execute application state-specific actions:
        switch (mAppStatus)
        {
            case APPSTATUS_INIT_APP:
                // Initialize application elements that do not rely on QCAR
                // initialization:
                initApplication();

                Log.i(TAG,"In APPSTATUS_INIT_APP");
                // Proceed to next application initialization status:
                updateApplicationStatus(APPSTATUS_INIT_QCAR);

                //Working...
               // splashPicID = R.drawable.logo;
                //layoutDisplay();


                break;

            case APPSTATUS_INIT_QCAR:
                // Initialize QCAR SDK asynchronously to avoid blocking the
                // main (UI) thread.
                //
                // NOTE: This task instance must be created and invoked on the
                // UI thread and it can be executed only once!
                Log.i(TAG,"In APPSTATUS_INIT_QCAR");
                try
                {
                    mInitQCARTask = new InitQCARTask();
                    mInitQCARTask.execute();
                }
                catch (Exception e)
                {
                   Log.w(TAG,"Initializing QCAR SDK failed");
                }
                break;

            case APPSTATUS_INIT_TRACKER:
                // Initialize the ImageTracker:
                Log.i(TAG,"In APPSTATUS_INIT_TRACKER");
                if (initTracker() > 0)
                {
                    // Proceed to next application initialization status:
                    updateApplicationStatus(APPSTATUS_INIT_APP_AR);
                }
                break;

            case APPSTATUS_INIT_APP_AR:

                Log.i(TAG,"In APPSTATUS_INIT_AR");
                // Initialize Augmented Reality-specific application elements
                // that may rely on the fact that the QCAR SDK has been
                // already initialized:
            	initApplicationAR();

                // Proceed to next application initialization status:
                updateApplicationStatus(APPSTATUS_LOAD_TRACKER);
                break;

            case APPSTATUS_LOAD_TRACKER:

                Log.i(TAG,"In APPSTATUS_LOAD_TRACKER");
                // Load the tracking data set:
                //
                // NOTE: This task instance must be created and invoked on the
                // UI thread and it can be executed only once!
                try
                {
                    mLoadTrackerTask = new LoadTrackerTask();
                    mLoadTrackerTask.execute();
                }
                catch (Exception e)
                {
                    Log.w(TAG,"Loading tracking data set failed");
                }
                break;

            case APPSTATUS_INIT_LAYOUT:

                Log.i(TAG, "In APPSTATUS_INIT_LAYOUT");

                //create the layout on top of the jmonkey view to add button and fragments
                //initTopLayout();
                ViewGroup rootView        = (ViewGroup) findViewById(android.R.id.content);
                LayoutInflater factory    = LayoutInflater.from(this);
                FrameLayout frameLayout1  = new FrameLayout(this);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                frameLayout1.setLayoutParams(layoutParams);

                frameLayout1.setId(4);
                rootView.addView(frameLayout1);

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = new InformativeMenuFragment();
                ft.replace(frameLayout1.getId(), fragment, INFORMATIVE_MENU_FRAGMENT_TAG);
                ft.commit();
                fm.executePendingTransactions(); //TO do it quickly instead of waiting for commit()

                //Add the fragment with frangment transaction with framwlayoyt

                updateApplicationStatus(APPSTATUS_INITED);

                break;

            case APPSTATUS_INITED:

                Log.i(TAG,"In APPSTATUS_INITED");
                // Hint to the virtual machine that it would be a good time to
                // run the garbage collector:
                //
                // NOTE: This is only a hint. There is no guarantee that the
                // garbage collector will actually be run.
                System.gc();

                // Native post initialization:
                onQCARInitializedNative();

                // Activate the renderer:
               // mRenderer.mIsActive = true;

                // Now add the GL surface view. It is important
                // that the OpenGL ES surface view gets added
                // BEFORE the camera is started and video
                // background is configured.
                
              //  addContentView(mGlView, new LayoutParams(LayoutParams.MATCH_PARENT,
              //          LayoutParams.MATCH_PARENT));

                // Sets the UILayout to be drawn in front of the camera
              //  mUILayout.bringToFront();


                // Start the camera:
                updateApplicationStatus(APPSTATUS_CAMERA_RUNNING);

                break;

            case APPSTATUS_CAMERA_STOPPED:
                Log.i(TAG,"In APPSTATUS_CAMERA_STOPPED");
                // Call the native function to stop the camera:
                stopCamera();
                break;

            case APPSTATUS_CAMERA_RUNNING:

                Log.i(TAG,"In APPSTATUS_CAMERA_RUNNING");
                // Call the native function to start the camera:
                startCamera();

                // Sets the layout background to transparent
            //   mUILayout.setBackgroundColor(Color.TRANSPARENT);
                

                // Set continuous auto-focus if supported by the device,
                // otherwise default back to regular auto-focus mode.
                // This will be activated by a tap to the screen in this
                // application.
                if (!setFocusMode(FOCUS_MODE_CONTINUOUS_AUTO))
                {
                    mContAutofocus = false;
                    setFocusMode(FOCUS_MODE_NORMAL);
                }
                else
                {
                    mContAutofocus = true;
                }
                break;

            default:
                String errorMesasge = "Invalid application state";
                Log.e(TAG,errorMesasge);
                throw new RuntimeException(errorMesasge);
        }
    }

    /** Initialize application GUI elements that are not related to AR. */
    private void initApplication()
    {
        Log.d(TAG,"initApplication");

        // Set the screen orientation:
        // NOTE: Use SCREEN_ORIENTATION_LANDSCAPE or SCREEN_ORIENTATION_PORTRAIT
        //       to lock the screen orientation for this activity.
        int screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

        // Apply screen orientation
        setRequestedOrientation(screenOrientation);

        // Query display dimensions:
        storeScreenDimensions();

        // As long as this window is visible to the user, keep the device's
        // screen turned on and bright:
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    }



    /** Initializes AR application components. */
    private void initApplicationAR()
    {
        Log.d(TAG,"initApplicationAR");
        // Do application initialization in native code (e.g. registering
        // callbacks, etc.):
        initApplicationNative(mScreenWidth, mScreenHeight);

        /*
        // Create OpenGL ES view:
        int depthSize = 16;
        int stencilSize = 0;
        boolean translucent = QCAR.requiresAlpha();

      
        LayoutInflater inflater = LayoutInflater.from(this);
        mUILayout = (RelativeLayout) inflater.inflate(R.layout.activity_main,
                null, false);

        mUILayout.setVisibility(View.VISIBLE);
        mUILayout.setBackgroundColor(Color.BLACK);

        // Adds the inflated layout to the view
        addContentView(mUILayout, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        */
    }



    //TODO: Alex add things here.
    /**
     * Initialize an image buffer for vuforia video feed
     * @param width
     * @param height
     */
	public void initializeImageBuffer(int width,int height)
	{
        Log.d(TAG,"initializeImageBuffer");

		int bufferSizeRGB565 = width * height * 2;

		mPreviewBufferRGB656 = null;
		
		mPreviewBufferRGB656 = new byte[bufferSizeRGB565];
		
		mPreviewByteBufferRGB565 = ByteBuffer.allocateDirect(mPreviewBufferRGB656.length);
		cameraJMEImageRGB565 = new Image(Image.Format.RGB565, width,
				height, mPreviewByteBufferRGB565);
		mPreviewByteBufferRGB565.clear();
	
	}

    //TODO: Alex add things here.
    /**
     * Add an image to the buffer image video
     * @param buffer
     * @param width
     * @param height
     */
	 public void setRGB565CameraImage(byte[] buffer, int width, int height)  {
		 
		    Log.d(TAG,"setRGB565CameraImage Update Camera Image..");
		 	if (firstTimeGetImage) 
		 	{
		 		initializeImageBuffer(width,height);
		 		firstTimeGetImage=false;
		 	}

			mPreviewByteBufferRGB565.clear();
			mPreviewByteBufferRGB565.put(buffer);
			
			cameraJMEImageRGB565.setData(mPreviewByteBufferRGB565);

            // Set our camera image as the JME background
			if (app != null) {
				((com.ar4android.vuforiaJME.VuforiaJME) app)
						.setVideoBGTexture(cameraJMEImageRGB565);
			}	
		}




	// We override AndroidHarness.onCreate() to be able to add the SurfaceView
	// needed for camera preview
	@Override
	public void onCreate(Bundle savedInstanceState) {

        Log.i(TAG,"onCreate");

        //We load the language saved in the sharedpreferences to have the correct language
        LanguageLocaleChanger.loadLanguageLocaleInActivity(this);

        initLanguageSpecificStrings();

        super.onCreate(savedInstanceState);

        //Enabling Profiler
        if (UseProfiler)
        {
            Debug.startMethodTracing("traceFile");
        }

        //Set an AppListener to receive callbacks from VuforiaJME e.g. to show informative menu
        ((VuforiaJME) app).setAppListener(this);

        showSplashscreenDialog();

        // Update the application status to start initializing application:
        updateApplicationStatus(APPSTATUS_INIT_APP);

        //mSensor = new SoundMeter();

	}
	
	@Override
    public void onResume() {
        Log.i(TAG,"onResume");
    	super.onResume();
    	
    	// make sure the AndroidGLSurfaceView view is on top of the view
		// hierarchy
		//view.setZOrderOnTop(true);
		
        // QCAR-specific resume operation
        QCAR.onResume();

        // We may start the camera only if the QCAR SDK has already been
        // initialized
        if (mAppStatus == APPSTATUS_CAMERA_STOPPED)
        {
            updateApplicationStatus(APPSTATUS_CAMERA_RUNNING);
        }
        
        firstTimeGetImage=true;

        //audioStart();


	}

	@Override
	protected void onPause() {

        Log.i(TAG,"onPause");
		super.onPause();		
	

        if (mAppStatus == APPSTATUS_CAMERA_RUNNING)
        {
            updateApplicationStatus(APPSTATUS_CAMERA_STOPPED);
        }

        // Disable flash when paused
        if (mFlash)
        {
            mFlash = false;
            activateFlash(mFlash);
        }

        // QCAR-specific pause operation
        QCAR.onPause();
        
        firstTimeGetImage=true;

//        QCAR.onSurfaceCreated();
	}

    @Override
    protected void onStop()
    {
        // Stop the profiler
        if (UseProfiler)
        {
            Debug.stopMethodTracing();
        }
    }

	@Override
    protected void onDestroy()
    {
        Log.i(TAG,"onDestroy");
        super.onDestroy();
        
        // Cancel potentially running tasks
        if (mInitQCARTask != null &&
            mInitQCARTask.getStatus() != InitQCARTask.Status.FINISHED)
        {
            mInitQCARTask.cancel(true);
            mInitQCARTask = null;
        }

        if (mLoadTrackerTask != null &&
            mLoadTrackerTask.getStatus() != LoadTrackerTask.Status.FINISHED)
        {
            mLoadTrackerTask.cancel(true);
            mLoadTrackerTask = null;
        }

        // Ensure that all asynchronous operations to initialize QCAR
        // and loading the tracker datasets do not overlap:
        synchronized (mShutdownLock)
        {

            // Do application deinitialization in native code:
            deinitApplicationNative();

            // Destroy the tracking data set:
            destroyTrackerData();

            // Deinit the tracker:
            deinitTracker();

            // Deinitialize QCAR SDK:
            QCAR.deinit();
        }

        System.gc();
        
    }

    /**
     * Called when screen orientation change for example
     * @param config
     */
	@Override
    public void onConfigurationChanged(Configuration config)
    {
        Log.i(TAG,"onConfigurationChanged");
       // DebugLog.LOGD("VuforiaJMEActivity::onConfigurationChanged");
        super.onConfigurationChanged(config);

        storeScreenDimensions();

        // Invalidate screen rotation to trigger query upon next render call:
        mLastScreenRotation = INVALID_SCREEN_ROTATION;
    }

    /**
     * Init the subject content and language specific strings
     */
    private void initLanguageSpecificStrings()
    {

        exitDialogTitle = getString(R.string.exit_dialog_title);
        exitDialogMessage = getString(R.string.exit_dialog_message);

        //Empty the SubjectContent list and readd items with correct language title
        SubjectContent.removeAllItems();
        SubjectContent.addAllItems(this);

    }

    /**
     * Show the language dialog
     */
    private void showSplashscreenDialog()
    {

        Log.d(TAG,"Show splashscreen dialog");
        FragmentManager fm = getSupportFragmentManager();//getSupportFragmentManager();
        SplashscreenDialogFragment  SplashscreenDialogFragment = new  SplashscreenDialogFragment();
        SplashscreenDialogFragment.show(fm, ITEM_SPLASHSCREEN_FRAGMENT_TAG);

    }

    private void dismissSplashscreenDialog()
    {

        Log.d(TAG,"Dismiss splashscreen dialog");
        FragmentManager fm = getSupportFragmentManager();//getSupportFragmentManager();
        SplashscreenDialogFragment splashscreenDialogFragment = (SplashscreenDialogFragment) fm.findFragmentByTag(ITEM_SPLASHSCREEN_FRAGMENT_TAG);
        splashscreenDialogFragment.dismiss();


    }


}
