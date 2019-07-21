package com.danh3945.simplespeeddial.views.contactList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danh3945.simplespeeddial.R;

import timber.log.Timber;

public class ContactListFragment extends Fragment {

    public static final String TAG = "ContactListFragment";

    private RecyclerView mRecyclerView;
    private ContactListRecyclerAdapter mContactListRecyclerAdapter;

    private ContactListRecyclerAdapter.ContactListResultCallback mCallback;

    public static Fragment createInstance() {
        return new ContactListFragment();
    }

    public static Fragment createInstanceForResult(ContactListRecyclerAdapter.ContactListResultCallback callback) {
        ContactListFragment fragment = new ContactListFragment();
        fragment.mCallback = callback;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_recycler_layout, container, false);
        Timber.d("Creating View for %s", TAG);

        mRecyclerView = view.findViewById(R.id.contact_recycler_view);

        if (mCallback == null) {
            mContactListRecyclerAdapter = new ContactListRecyclerAdapter(getContext(), mRecyclerView);
        } else {
            mContactListRecyclerAdapter = new ContactListRecyclerAdapter(getContext(), mRecyclerView, mCallback);
        }

        mRecyclerView.setAdapter(mContactListRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }
}
