package com.example.copia.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.copia.MainActivity;
import com.example.copia.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentRestricted extends Fragment {


    public FragmentRestricted() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_restricted, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Restricted");
        return view;
    }

}
