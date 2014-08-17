package com.alex.tvfileexplorer.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.alex.tvfileexplorer.R;

/**
 * Created by alex on 14-8-16.
 */
public class FileCategoryFragment extends Fragment{
    private static int INDEX = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgment_test,container,false);
        TextView textView = (TextView) rootView.findViewById(R.id.text);
        textView.setText(""+INDEX);
        INDEX++;
        return rootView;
//        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
