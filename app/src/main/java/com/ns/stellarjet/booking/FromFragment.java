package com.ns.stellarjet.booking;


import android.os.Bundle;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ns.stellarjet.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FromFragment extends Fragment {


    public FromFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewDataBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_from, container, false);
        View mRootView = dataBinding.getRoot();
        return mRootView;
    }

}
