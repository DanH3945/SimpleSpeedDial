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

import timber.log.Timber;

public class ContactListFragment extends Fragment {

    public static String TAG = "ContactListFragment";

    private RecyclerView mRecyclerView;
    private ContactListRecyclerAdapter mContactListRecyclerAdapter;

    public static Fragment getInstance() {
        return new ContactListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_recycler_layout, container, false);
        Timber.d("Creating View for %s", TAG);

        mRecyclerView = view.findViewById(R.id.contact_recycler_view);
        mContactListRecyclerAdapter = new ContactListRecyclerAdapter(getContext(), mRecyclerView);

        mRecyclerView.setAdapter(mContactListRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }
}
