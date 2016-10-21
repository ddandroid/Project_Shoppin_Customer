package com.shoppin.customer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shoppin.customer.R;
import com.shoppin.customer.activity.NavigationDrawerActivity;

import butterknife.ButterKnife;

/**
 * Created by ubuntu on 15/8/16.
 */

public class UnderDevelopmentFragment extends BaseFragment {

    private static final String TAG = UnderDevelopmentFragment.class.getSimpleName();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        layoutView = inflater.inflate(R.layout.fragment_home, null);
        layoutView = inflater.inflate(R.layout.fragment_under_development, container, false);
        ButterKnife.bind(this, layoutView);


        return layoutView;
    }

    @Override
    public void updateFragment() {
        super.updateFragment();
        if (getActivity() != null && getActivity() instanceof NavigationDrawerActivity) {
            ((NavigationDrawerActivity) getActivity()).setToolbarTitle("Under Development");
        }
    }
}
