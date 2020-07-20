package com.rsd.r.navigationdrawer;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import static com.rsd.r.navigationdrawer.MainActivity.BT;
import static com.rsd.r.navigationdrawer.MainActivity.tVGlobStatus;
import static com.rsd.r.navigationdrawer.MainActivity.tV_velocity_Text;
import static com.rsd.r.navigationdrawer.MainActivity.velocity;


/**
 * A simple {@link Fragment} subclass.
 */
public class BluetoothFragment extends Fragment {

    public TextView tVConnection;

    public BluetoothFragment(Activity activity) {

    }


    @Override
    public void onStart()
    {
        // roept de superclasse hiervan aan omdat dit een parent constructor heeft
        super.onStart();
        // roep de getConnectionStatus aan
        // zet de tekst die is achterhaald via getConnectionStatus
        tVConnection.setText(BT.getConnectionStatus());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myFragmentView;
        // stopt de layout in een variabele
        myFragmentView = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        // haalt layout-onderdelen via id op en slaat op in een variabele
        tVConnection = (TextView) myFragmentView.findViewById(R.id.tV_connection);
        tVGlobStatus = tVConnection;
        return myFragmentView;
        // Inflate the layout for this fragment
    }

}
