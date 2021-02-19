package com.example.maimyou.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.maimyou.R;

import static com.example.maimyou.Activities.DashBoardActivity.fragmentIndex;

public class FragmentHome extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentIndex = 3;
        return inflater.inflate(R.layout.fragmen_dashboard, container, false);
    }
}