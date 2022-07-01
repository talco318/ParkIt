package com.talco.brandnewapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class MainScreen_fragment extends Fragment {


    public MainScreen_fragment() {
        // Required empty public constructor
    }

    public static MainScreen_fragment newInstance() {
        MainScreen_fragment fragment = new MainScreen_fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("result", "main screen fragment on create ");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_screen_fragment, container, false);
        return view;
    }
}