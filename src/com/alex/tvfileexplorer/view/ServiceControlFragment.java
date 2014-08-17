package com.alex.tvfileexplorer.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.alex.tvfileexplorer.R;

/**
 * Created by alex on 14-8-16.
 */
public class ServiceControlFragment extends Fragment {
    private static final String TAG = ServiceControlFragment.class.getSimpleName();

    private Activity mActivity = null;
    private View mRootView = null;
    private TextView ipText = null;
    private TextView instructionTextPre = null;
    private TextView instructionText = null;
    private Button startStopButton = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = getActivity();
        mRootView = inflater.inflate(R.layout.service_control_fragment,container,false);
        ipText = mRootView.findViewsWithText(R.id.ip_address);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
