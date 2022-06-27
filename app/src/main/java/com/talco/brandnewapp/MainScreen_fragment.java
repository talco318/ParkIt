package com.talco.brandnewapp;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainScreen_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainScreen_fragment extends Fragment {

    public MainScreen_fragment() {
        // Required empty public constructor
    }

    public static MainScreen_fragment newInstance(String param1, String param2) {
        MainScreen_fragment fragment = new MainScreen_fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_main_screen_fragment, container, false);

        Button addParking_button = view.findViewById(R.id.add_parking_button);

        addParking_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity secActivity = (MainActivity) getActivity();
                //logout func
            }
        });

        return view;    }
}