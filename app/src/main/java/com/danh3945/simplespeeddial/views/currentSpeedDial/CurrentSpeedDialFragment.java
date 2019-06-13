package com.danh3945.simplespeeddial.views.currentSpeedDial;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danh3945.simplespeeddial.R;

import timber.log.Timber;

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
        CurrentSpeedDialRecyclerAdapter adapter = new CurrentSpeedDialRecyclerAdapter(getContext(), this);

        ItemTouchHelper.SimpleCallback simpleCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int position = viewHolder.getAdapterPosition();
                adapter.removeEntry(position);
                Timber.d("Swiped Position, %s", position);
            }
        };

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

        return view;
    }
}
