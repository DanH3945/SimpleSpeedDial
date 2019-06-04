package com.danh3945.simplespeeddial;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CurrentSpeedDialFragment extends Fragment {

    public static final String TAG = "CurrentSpeedDialFragment";

    public static Fragment getInstance() {
        return new CurrentSpeedDialFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.current_speed_dial_fragment_layout, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.current_speed_dial_fragment_recycler);
        recyclerView.setAdapter(new CurrentSpeedDialRecyclerAdapter(getContext(), this));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }
}
