package com.ns.stellarjet.drawer;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ns.stellarjet.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompletedBookingsFragment extends Fragment {


    public CompletedBookingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_completed_bookings, container, false);
    }

}