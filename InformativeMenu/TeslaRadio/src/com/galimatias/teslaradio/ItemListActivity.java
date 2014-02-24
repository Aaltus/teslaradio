package com.galimatias.teslaradio;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ItemListFragment} and the item details
 * (if present) is a {@link ItemDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link ItemListFragment.Callbacks} interface
 * to listen for item selections.
 */

//Jonathan Desmarais: We extend ActionBarActivity instead of FragmentActivity for support v7
public class ItemListActivity extends ActionBarActivity implements
	ItemListFragment.Callbacks, View.OnClickListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    AsyncTask<Integer, Void, Void> createCameraPreviewAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((ItemListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.item_list))
                    .setActivateOnItemClick(true);
        }

        int xmlIdForCameraPreview = R.id.CameraPreviewButtonView_List;
        if(savedInstanceState == null)
        {
//            Fragment newFragment = new DemoCameraFragment();
//            FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
//            ft.add(xmlIdForCameraPreview, newFragment).commit();

            new CreateCameraPreviewAsyncTask().execute(xmlIdForCameraPreview);
        }




//        DemoCameraFragment demoFragment = new DemoCameraFragment();
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.CameraPreviewButtonView, demoFragment)
//                .commit();

        // TODO: If exposing deep links into your app, handle intents here.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // use an inflater to populate the ActionBar with items
        MenuInflater inflater = getMenuInflater();

        //Jonathan Desmarais: We show use different action menu layout depending if we are on a phone or tablet
        if (mTwoPane){
            inflater.inflate(R.menu.action_bar_menu_large, menu);
        }
        else{
            inflater.inflate(R.menu.action_bar_menu, menu);
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        Log.e("TAG", "setSingleTapListener");
    }

    /**
     * Callback method from {@link ItemListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, id);
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ItemDetailActivity.class);
            detailIntent.putExtra(ItemDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    private class CreateCameraPreviewAsyncTask extends AsyncTask<Integer, Void, Void> {

        Integer fragmentId;
        protected Void doInBackground(Integer...xmlIdForCameraPreview) {
            fragmentId = xmlIdForCameraPreview[0];
            Fragment newFragment = new DemoCameraFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(fragmentId, newFragment).commit();

            return null;
        }

    }


}
