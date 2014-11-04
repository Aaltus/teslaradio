package com.galimatias.teslaradio;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by jimbojd72 on 11/3/2014.
 */
public class TutorialFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View myView = inflater.inflate(R.layout.tutorial_layout, null, false);

        TextView textView = (TextView)myView.findViewById(R.id.tutorial_speech_textview);
        //textView.setText("Love cats.");

        //Get the rootView of the activity. This view is on the direct parent
        //to the android jme opengl view

        //ViewGroup rootView = (ViewGroup) getActivity().findViewById(android.R.id.content);

        //Inflate and add the top level layout to the rootview
        //LayoutInflater factory = LayoutInflater.from(getActivity());

        //rootView.addView(myView);

        //Setup the ListFragment
        /*FragmentManager fm = getChildFragmentManager();//sgetSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = new ItemListFragment();
        ft.hide(fragment);
        ft.replace(R.id.item_list_fragment_vuforia, fragment, ITEM_LIST_FRAGMENT_TAG);
        ft.commit();
        fm.executePendingTransactions(); //TO do it quickly instead of waiting for commit()
        */

        return myView;
    }

}