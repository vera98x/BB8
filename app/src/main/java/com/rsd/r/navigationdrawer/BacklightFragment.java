package com.rsd.r.navigationdrawer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class BacklightFragment extends Fragment {


    public BacklightFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // De layout die bij dit Fragment hoort wordt op het scherm gezet
        return inflater.inflate(R.layout.fragment_backlight, container, false);
    }

}
