package com.danh3945.simplespeeddial.views.contactList;

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
