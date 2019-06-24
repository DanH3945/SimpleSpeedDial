package com.danh3945.simplespeeddial.views.currentSpeedDial;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danh3945.simplespeeddial.R;

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
        CurrentSpeedDialRecyclerAdapter adapter = new CurrentSpeedDialRecyclerAdapter(getContext(),
                this,
                recyclerView);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }
}
