package com.talco.brandnewapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class availableParkingsFragment extends Fragment {


    public availableParkingsFragment() {
        // Required empty public constructor
    }


    public static availableParkingsFragment newInstance(String param1, String param2) {
        availableParkingsFragment fragment = new availableParkingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_available_parkings, container, false);
    }
}