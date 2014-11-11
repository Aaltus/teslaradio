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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.galimatias.teslaradio.*;
import com.galimatias.teslaradio.subject.ScenarioEnum;
import com.galimatias.teslaradio.subject.SubjectContent;
import com.galimatias.teslaradio.world.Scenarios.IScenarioSwitcher;
import com.galimatias.teslaradio.world.Scenarios.ScenarioManager;
import com.galimatias.teslaradio.world.Scenarios.StartScreenController;
import com.jme3.input.event.TouchEvent;
import com.jme3.system.android.AndroidConfigChooser.ConfigType;
import com.jme3.texture.Image;
import com.qualcomm.QCAR.QCAR;
import com.utils.AppLogger;
import com.utils.LanguageLocaleChanger;

import java.nio.ByteBuffer;
import java.util.concurrent.Callable;


/**
 * Center of the Android side of the application. All Android view and specific thing are here.
 * It also initialize vuforia library and jme app.
 */
public class VuforiaJMEActivity extends AndroidHarnessFragmentActivity implements AndroidActivityController,
        IScenarioSwitcher,
        ITutorialSwitcher,
        VuforiaCallback,
        StartScreenController {

    // Boolean to use the profiler. If it's set to true, you can get the tracefile on your phone /sdcard/traceFile.trace
    private static final boolean UseProfiler = false;
	private static final String TAG = VuforiaJMEActivity.class.getName();
    private static final String START_MENU_FRAGMENT_TAG = "START_MENU_FRAGMENT_TAG";

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

    private VuforiaCaller vuforiaCaller;
    private ProgressDialog progressDialog;

    @Override
    public ITrackerUpdater getITrackerUpdater() {
        return vuforiaCaller;
    }

    @Override
    public void setICameraUpdater(ICameraUpdater iCameraUpdater) {
        this.vuforiaCaller.setICameraUpdate(iCameraUpdater);
    }

    @Override
    public void quitAndroidActivity() {
        openCloseAlertDialog();
    }

    @Override
    public void openStartMenu() {

        final StartScreenController startScreenController = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Show start screen dialog");
                FragmentManager fm = getSupportFragmentManager();
                StartScreenDialogFragment startScreenDialogFragment =
                        new StartScreenDialogFragment();
                startScreenDialogFragment.setCancelable(false);
                startScreenDialogFragment.setStartScreenController(startScreenController);
                startScreenDialogFragment.show(fm, START_MENU_FRAGMENT_TAG);
            }
        });


    }

    @Override
    public void closeStartMenu() {
        FragmentManager fm                    = getSupportFragmentManager(); //getSupportFragmentManager();s
        DialogFragment fragment     = (DialogFragment)fm.findFragmentByTag(START_MENU_FRAGMENT_TAG);
        fragment.dismiss();
        //FragmentTransaction ft = fm.beginTransaction();
        //ft.remove(fragment);
    }

    @Override
    public boolean isStartMenuShown() {
        FragmentManager fm                    = getSupportFragmentManager(); //getSupportFragmentManager();s
        Fragment fragment     = fm.findFragmentByTag(START_MENU_FRAGMENT_TAG);

        boolean isVisible = false;
        if(fragment != null && fragment.isVisible())
        {
            isVisible = true;
        }

        return isVisible;
    }

    @Override
    public void openProgressScreen(final String title) {

        final Context context = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(context); //Here I get an error: The constructor ProgressDialog(PFragment) is undefined
                progressDialog.setMessage("Loading stuff...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setTitle(title);
                //progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        });


    }

    @Override
    public void closeProgressScreen() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void setProgressBar(final int currentProgress, final String progressComment) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppLogger.getInstance().d(TAG,"setProgressBar: " + progressComment + " " + currentProgress );
                progressDialog.setMessage(progressComment);
                progressDialog.setProgress(currentProgress);
            }
        });

    }

    private void openCloseAlertDialog() {
        runOnUiThread(
            new Runnable()
            {
                public void run()
                {
                    AlertDialog dialog = new AlertDialog.Builder(VuforiaJMEActivity.this) // .setIcon(R.drawable.alert_dialog_icon)
                            .setTitle(exitDialogTitle).setPositiveButton("Yes", VuforiaJMEActivity.this).setNegativeButton("No", VuforiaJMEActivity.this).setMessage(exitDialogMessage).create();
                    dialog.show();

                }
            }
        );
    }

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
    private int mQCARFlags = QCAR.GL_20;

    // Contextual Menu Options for Camera Flash - Autofocus
    private boolean mFlash = false;
    private boolean mContAutofocus = false;

    public boolean manuallyPauseTracking = false;

    // The menu item for swapping data sets:
    private MenuItem mDataSetMenuItem = null;
    private boolean mIsStonesAndChipsDataSetActive  = false;
    private RelativeLayout mUILayout;

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


    }

    Image cameraJMEImageRGB565;
    private byte[] mPreviewBufferRGB656;
    java.nio.ByteBuffer mPreviewByteBufferRGB565;
    static boolean firstTimeGetImage=true;

    /** A helper for loading native libraries stored in "libs/armeabi*". */
    public static boolean loadLibrary(String nLibName)
    {
        try
        {
            System.loadLibrary(nLibName);
            AppLogger.getInstance().d(TAG, "Native library lib" + nLibName + ".so loaded");
            return true;
        }
        catch (UnsatisfiedLinkError ulee)
        {
            AppLogger.getInstance().e(TAG, "The library lib" + nLibName +
                    ".so could not be loaded");
        }
        catch (SecurityException se)
        {
            AppLogger.getInstance().e(TAG, "The library lib" + nLibName +
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
    public void toggleInformativeMenuCallback(final ScenarioEnum scenarioEnum)
    {
        AppLogger.getInstance().i(TAG, "showInformativeMenuCallback");

        //Run on UI thread
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                getInformativeMenuFragment().replaceDetailFragment(scenarioEnum);

            }
        });
    }

    @Override
    public void setTutorialMenu(final ScenarioEnum scenarioEnum) {
        runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        getInformativeMenuFragment().setTutorialMenu(scenarioEnum);
                    }
                }
        );
    }

    @Override
    public void pauseTracking() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                manuallyPauseTracking = true;
                pauseQCARandTasks(false);
            }
        });

    }

    @Override
    public void startTracking() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                manuallyPauseTracking = false;
                resumeQCARandTasks();
            }
        });

    }

    @Override
    public void hideInformativeMenu() {

        runOnUiThread(new Runnable()
        {
            public void run()
            {
                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment  =    getInformativeMenuFragment();

                if (fragment != null)
                {
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.hide(fragment);
                    ft.commit();
                }

            }
        });


    }

    @Override
    public void showInformativeMenu() {

        runOnUiThread(
        new Runnable()
        {
            public void run()
            {
                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment  = getInformativeMenuFragment();

                if (fragment != null)
                {
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.show(fragment);
                    ft.commit();
                }

            }
        });

    }



    public InformativeMenuFragment getInformativeMenuFragment()
    {
        FragmentManager fm = getSupportFragmentManager();//getSupportFragmentManager();
        InformativeMenuFragment informativeMenuFragment =
                (InformativeMenuFragment) fm.findFragmentByTag(INFORMATIVE_MENU_FRAGMENT_TAG);
        return informativeMenuFragment;
    }



    @Override
    public void dismissAndroidSplashScreen()
    {

        class OneShotTask implements Runnable {
            //IScenarioSwitcher scenarioSwitcher;
            //OneShotTask(IScenarioSwitcher s) { scenarioSwitcher = s; }
            //IScenarioSwitcher scenarioSwitcher;
            //OneShotTask() { scenarioSwitcher = s; }
            public void run() {
                dismissSplashscreenDialog();
                //TODO it should be a better idea to create a scenario manager in the activity and then pass it to vuforia jme.
                //InformativeMenuFragment informativeMenuFragment = getInformativeMenuFragment();
                //if(informativeMenuFragment != null)
                //{
                //    getInformativeMenuFragment().setScenarioSwitcher(scenarioSwitcher);
                //}
            }
        }

        //OneShotTask oneShotTask = new OneShotTask(this);
        //runOnUiThread(oneShotTask);
        OneShotTask oneShotTask = new OneShotTask();
        runOnUiThread(oneShotTask);

    }

    @Override
    public boolean hasNextScenario() {

        return false;
    }

    @Override
    public boolean hasPreviousScenario() {
        return false;
    }

    @Override
    public void setNextScenario() {

    }

    @Override
    public void setPreviousScenario() {

    }

    @Override
    public void setScenarioByEnum(final ScenarioEnum scenarioEnum) {
        (app).enqueue(new Callable<Object>() {
                    public Object call() throws Exception {
                        //((VuforiaJME)app).getiScenarioManager().setScenarioByEnum(scenarioEnum);
                        ScenarioManager state = app.getStateManager().getState(ScenarioManager.class);
                        if(state != null)
                        {
                            state.setScenarioByEnum(scenarioEnum);
                        }
                        return null;
                    }});
    }

    @Override
    public void setTutorialIndex(final int index) {
        (app).enqueue(new Callable<Object>() {
            public Object call() throws Exception {
                //((VuforiaJME)app).getiScenarioManager().setScenarioByEnum(scenarioEnum);
                ScenarioManager state = app.getStateManager().getState(ScenarioManager.class);
                if(state != null)
                {
                    state.setTutorialIndex(index);
                }
                return null;
            }});
    }

    @Override
    public void onStartButtonClick() {
        (app).enqueue(new Callable<Object>() {
            public Object call() throws Exception {
                ((VuforiaJME)app).onStartButtonClick();
                return null;
            }});
    }

    @Override
    public void onTutorialButtonClick() {
        (app).enqueue(new Callable<Object>() {
            public Object call() throws Exception {
                ((VuforiaJME)app).onTutorialButtonClick();
                return null;
            }});

    }

    @Override
    public void onCreditsButtonClick() {
        (app).enqueue(new Callable<Object>() {
            public Object call() throws Exception {
                ((VuforiaJME)app).onCreditsButtonClick();
                return null;
            }});

    }

    @Override
    public void onEndGameClick() {
        (app).enqueue(new Callable<Object>() {
            public Object call() throws Exception {
                ((VuforiaJME)app).onEndGameClick();
                return null;
            }});

    }

    @Override
    public void showTrackableAlertToast(final boolean showAlertToast) {
        final Context context = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(showAlertToast && !getInformativeMenuFragment().isChildFragmentShown()) {

                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_character_bubble,
                            (ViewGroup) findViewById(R.id.toast_layout_root));

                    TextView text = (TextView) layout.findViewById(R.id.toast_text);
                    text.setText(getString(R.string.toast_no_trackable_seen));

                    Toast toast = new Toast(getApplicationContext());
                    //toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                    //Old code for the toast.
                    /*
                    String text = getString(R.string.toast_no_trackable_seen);
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    */
                }
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
                vuforiaCaller.QCARsetInitParameters(VuforiaJMEActivity.this, mQCARFlags);

                do
                {
                    // QCAR.init() blocks until an initialization step is
                    // complete, then it proceeds to the next step and reports
                    // progress in percents (0 ... 100%).
                    // If QCAR.init() returns -1, it indicates an error.
                    // Initialization is done when progress has reached 100%.
                    mProgressValue = vuforiaCaller.QCARinit();

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
                AppLogger.getInstance().d(TAG, "InitQCARTask::onPostExecute: QCAR " +
                        "initialization successful");

                updateApplicationStatus(APPSTATUS_INIT_TRACKER);
            }
            else
            {
                AppLogger.getInstance().d(TAG, "InitQCARTask::onPostExecute: QCAR " +
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
                return (vuforiaCaller.loadTrackerData() > 0);
            }
        }

        protected void onPostExecute(Boolean result)
        {
            AppLogger.getInstance().d(TAG, "LoadTrackerTask::onPostExecute: execution " +
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

        AppLogger.getInstance().d(TAG, "Store screen dimension width: " + Integer.toString(metrics.widthPixels) + " heigth: " + Integer.toString(metrics.heightPixels));

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
            if (vuforiaCaller.QCARisInitialized() && (mAppStatus == APPSTATUS_CAMERA_RUNNING))
            {
                AppLogger.getInstance().d(TAG, "VuforiaJMEActivity::updateRenderView");

                // Query display dimensions:
                storeScreenDimensions();

                // Update viewport via renderer:
                //TODO SET Screen here
               // mRenderer.updateRendering(mScreenWidth, mScreenHeight);

                // Update projection matrix:
                vuforiaCaller.setProjectionMatrix();

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

                AppLogger.getInstance().i(TAG, "In APPSTATUS_INIT_APP");
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
                AppLogger.getInstance().i(TAG, "In APPSTATUS_INIT_QCAR");
                try
                {
                    mInitQCARTask = new InitQCARTask();
                    mInitQCARTask.execute();
                }
                catch (Exception e)
                {
                    AppLogger.getInstance().w(TAG, "Initializing QCAR SDK failed");
                }
                break;

            case APPSTATUS_INIT_TRACKER:
                // Initialize the ImageTracker:
                AppLogger.getInstance().i(TAG, "In APPSTATUS_INIT_TRACKER");
                if (vuforiaCaller.initTracker() > 0)
                {
                    // Proceed to next application initialization status:
                    updateApplicationStatus(APPSTATUS_INIT_APP_AR);
                }
                break;

            case APPSTATUS_INIT_APP_AR:

                AppLogger.getInstance().i(TAG, "In APPSTATUS_INIT_AR");
                // Initialize Augmented Reality-specific application elements
                // that may rely on the fact that the QCAR SDK has been
                // already initialized:
            	initApplicationAR();

                // Proceed to next application initialization status:
                updateApplicationStatus(APPSTATUS_LOAD_TRACKER);
                break;

            case APPSTATUS_LOAD_TRACKER:

                AppLogger.getInstance().i(TAG, "In APPSTATUS_LOAD_TRACKER");
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
                    AppLogger.getInstance().w(TAG, "Loading tracking data set failed");
                }
                break;

            case APPSTATUS_INIT_LAYOUT:

                AppLogger.getInstance().i(TAG, "In APPSTATUS_INIT_LAYOUT");
                initTopInformativeMenuFragment();
                hideInformativeMenu();
                updateApplicationStatus(APPSTATUS_INITED);

                break;

            case APPSTATUS_INITED:

                AppLogger.getInstance().i(TAG, "In APPSTATUS_INITED");
                // Hint to the virtual machine that it would be a good time to
                // run the garbage collector:
                //
                // NOTE: This is only a hint. There is no guarantee that the
                // garbage collector will actually be run.
                //Jonathan: I commented this see reason why here:
                //https://stackoverflow.com/questions/2414105/why-is-it-bad-practice-to-call-system-gc
                //System.gc();

                // Native post initialization:
                vuforiaCaller.onQCARInitializedNative(AppLogger.getInstance().getLogLvl().ordinal());

                // Start the camera:
                updateApplicationStatus(APPSTATUS_CAMERA_RUNNING);

                break;

            case APPSTATUS_CAMERA_STOPPED:
                AppLogger.getInstance().i(TAG, "In APPSTATUS_CAMERA_STOPPED");
                // Call the native function to stop the camera:
                vuforiaCaller.stopCamera();
                break;

            case APPSTATUS_CAMERA_RUNNING:

                AppLogger.getInstance().i(TAG, "In APPSTATUS_CAMERA_RUNNING");
                // Call the native function to start the camera:
                vuforiaCaller.startCamera();

                // Sets the layout background to transparent
            //   mUILayout.setBackgroundColor(Color.TRANSPARENT);
                

                // Set continuous auto-focus if supported by the device,
                // otherwise default back to regular auto-focus mode.
                // This will be activated by a tap to the screen in this
                // application.
                if (!vuforiaCaller.setFocusMode(FOCUS_MODE_CONTINUOUS_AUTO))
                {
                    mContAutofocus = false;
                    vuforiaCaller.setFocusMode(FOCUS_MODE_NORMAL);
                }
                else
                {
                    mContAutofocus = true;
                }
                break;

            default:
                String errorMesasge = "Invalid application state";
                AppLogger.getInstance().e(TAG, errorMesasge);
                throw new RuntimeException(errorMesasge);
        }
    }

    private void initTopInformativeMenuFragment() {
        //create the layout on top of the jmonkey view to add button and fragments
        //initTopLayout();
        ViewGroup rootView        = (ViewGroup) findViewById(android.R.id.content);
        //LayoutInflater factory    = LayoutInflater.from(this);
        FrameLayout frameLayout1  = new FrameLayout(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        frameLayout1.setLayoutParams(layoutParams);

        frameLayout1.setId(4);
        rootView.addView(frameLayout1);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        InformativeMenuFragment fragment = new InformativeMenuFragment();
        fragment.setScenarioSwitcher(this);
        fragment.setTutorialSwitcher(this);

        ft.replace(frameLayout1.getId(), fragment, INFORMATIVE_MENU_FRAGMENT_TAG);
        ft.hide(fragment);
        ft.commit();
        fm.executePendingTransactions(); //TO do it quickly instead of waiting for commit()
    }

    /** Initialize application GUI elements that are not related to AR. */
    private void initApplication()
    {
        AppLogger.getInstance().i(TAG, "initApplication");

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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        setImmersiveMode(hasFocus);
    }

    private void setImmersiveMode(boolean hasFocus) {
        if(android.os.Build.VERSION.SDK_INT >= 19) {
            if (hasFocus) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }

    /** Initializes AR application components. */
    private void initApplicationAR()
    {
        AppLogger.getInstance().d(TAG, "initApplicationAR");
        // Do application initialization in native code (e.g. registering
        // callbacks, etc.):
        vuforiaCaller.initApplicationNative(mScreenWidth, mScreenHeight);

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
        AppLogger.getInstance().d(TAG, "initializeImageBuffer with width: " + width + " and height: " + height);

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

            AppLogger.getInstance().d(TAG, "setRGB565CameraImage Update Camera Image..");
		 	if (firstTimeGetImage) 
		 	{
		 		initializeImageBuffer(width,height);
		 		firstTimeGetImage=false;
		 	}

			mPreviewByteBufferRGB565.clear();
			mPreviewByteBufferRGB565.put(buffer);
			
			cameraJMEImageRGB565.setData(mPreviewByteBufferRGB565);

            // Set our camera image as the JME background
         if(app != null) {
             (app).enqueue(new Callable<Object>() {
                 public Object call() throws Exception {
                     VuforiaJMEState state = app.getStateManager().getState(VuforiaJMEState.class);
                     if (state != null) {
                         state.setVideoBGTexture(cameraJMEImageRGB565);
                     }
                     return null;
                 }
             });
         }

		}




	// We override AndroidHarness.onCreate() to be able to add the SurfaceView
	// needed for camera preview
	@Override
	public void onCreate(Bundle savedInstanceState) {



        // Where the AppLogger is called for the first time and the log level is set
        AppLogger.getInstance().setLogLvl(AppLogger.LogLevel.ALL);

        AppLogger.getInstance().i(TAG, "onCreate");

        //We load the language saved in the sharedpreferences to have the correct language
        LanguageLocaleChanger.loadLanguageLocaleInActivity(this);

        initLanguageSpecificStrings();

        super.onCreate(savedInstanceState);

        setImmersiveMode(true);

        //Enabling Profiler
        if (UseProfiler)
        {
            Debug.startMethodTracing("traceFile");
        }

        //Set an AndroidActivityController to receive callbacks from VuforiaJME e.g. to show informative menu
        ((VuforiaJME) app).setAndroidActivityController(this);
        vuforiaCaller = new VuforiaCallerNative(this);

        showSplashscreenDialog();

        // Update the application status to start initializing application:
        updateApplicationStatus(APPSTATUS_INIT_APP);
	}
	
	@Override
    public void onResume() {
        AppLogger.getInstance().i(TAG, "onResume");
    	super.onResume();
    	
    	// make sure the AndroidGLSurfaceView view is on top of the view
		// hierarchy
		//view.setZOrderOnTop(true);
        resumeQCARandTasks();
        if(this.manuallyPauseTracking){
            pauseQCARandTasks(false);
        }


    }

    private void resumeQCARandTasks() {
        // QCAR-specific resume operation
        vuforiaCaller.QCARonResume();

        // We may start the camera only if the QCAR SDK has already been
        // initialized
        if (mAppStatus == APPSTATUS_CAMERA_STOPPED)
        {
            updateApplicationStatus(APPSTATUS_CAMERA_RUNNING);
        }

        firstTimeGetImage=true;
    }


    @Override
	protected void onPause() {

        AppLogger.getInstance().i(TAG, "onPause");
		super.onPause();

        pauseQCARandTasks(true);
	}

    private void pauseQCARandTasks(boolean isQCARPause) {
        if (mAppStatus == APPSTATUS_CAMERA_RUNNING)
        {
            updateApplicationStatus(APPSTATUS_CAMERA_STOPPED);
        }

        // Disable flash when paused
        if (mFlash)
        {
            mFlash = false;
            vuforiaCaller.activateFlash(mFlash);
        }

        // QCAR-specific pause operation
        if(isQCARPause){
            vuforiaCaller.QCARonPause();
        }

        firstTimeGetImage=true;
    }

    @Override
    protected void onStop()
    {
        // Stop the profiler
        if (UseProfiler)
        {
            Debug.stopMethodTracing();
        }
        super.onStop();
    }

	@Override
    protected void onDestroy()
    {
        AppLogger.getInstance().i(TAG, "onDestroy");
        super.onDestroy();
        destroyQCARCameraAndTasks();


    }

    private void destroyQCARCameraAndTasks() {
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
            vuforiaCaller.deinitApplicationNative();

            // Destroy the tracking data set:
            vuforiaCaller.destroyTrackerData();

            // Deinit the tracker:
            vuforiaCaller.deinitTracker();

            // Deinitialize QCAR SDK:
            vuforiaCaller.QCARdeinit();
        }
        //Jonathan: I commented this see reason why here:
        //https://stackoverflow.com/questions/2414105/why-is-it-bad-practice-to-call-system-gc
        //System.gc();
    }

    //jdesmarais: I override this because from AndroidHarness because I want to provide a way to use the back button to dismiss the UI
    @Override
    public void onTouch(String name, TouchEvent evt, float tpf) {
        if (name.equals(ESCAPE_EVENT)) {
            switch (evt.getType()) {
                case KEY_UP:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            if (getInformativeMenuFragment() != null && getInformativeMenuFragment().isChildFragmentShown())
                            {
                                getInformativeMenuFragment().showAllChildFragments(false);
                            }
                            else
                            {
                                (app).enqueue(new Callable<Object>() {
                                    public Object call() throws Exception {
                                        ((VuforiaJME)app).onBackButton();
                                        return null;
                                    }});
                            /*
                                AlertDialog dialog = new AlertDialog.Builder(VuforiaJMEActivity.this) // .setIcon(R.drawable.alert_dialog_icon)
                                        .setTitle(exitDialogTitle).setPositiveButton("Yes", VuforiaJMEActivity.this).setNegativeButton("No", VuforiaJMEActivity.this).setMessage(exitDialogMessage).create();
                                dialog.show();
                            */

                            }

                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Called when screen orientation change for example
     * @param config
     */

	@Override
    public void onConfigurationChanged(Configuration config)
    {
        AppLogger.getInstance().i(TAG, "onConfigurationChanged");
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

/*    *//**
     * Show the language dialog
     */
    private void showSplashscreenDialog()
    {

        AppLogger.getInstance().d(TAG, "Show splashscreen dialog");
        FragmentManager fm = getSupportFragmentManager();//getSupportFragmentManager();
        SplashscreenDialogFragment SplashscreenDialogFragment = new  SplashscreenDialogFragment();
        SplashscreenDialogFragment.setCancelable(false);
        SplashscreenDialogFragment.show(fm, ITEM_SPLASHSCREEN_FRAGMENT_TAG);

    }

    private void dismissSplashscreenDialog()
    {

        AppLogger.getInstance().d(TAG, "Dismiss splashscreen dialog");
        FragmentManager fm = getSupportFragmentManager();//getSupportFragmentManager();
        SplashscreenDialogFragment splashscreenDialogFragment = (SplashscreenDialogFragment) fm.findFragmentByTag(ITEM_SPLASHSCREEN_FRAGMENT_TAG);
        if(splashscreenDialogFragment != null){
            splashscreenDialogFragment.dismiss();
        }
    }


}
