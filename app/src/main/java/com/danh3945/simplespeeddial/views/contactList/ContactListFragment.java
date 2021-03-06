package com.danh3945.simplespeeddial.views.contactList;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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

    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 5100;

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

        if (hasReadContactPermission()) {
            finishInit();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        return view;
    }

    private void finishInit() {
        if (mCallback == null) {
            mContactListRecyclerAdapter = new ContactListRecyclerAdapter(getContext(), mRecyclerView);
        } else {
            mContactListRecyclerAdapter = new ContactListRecyclerAdapter(getContext(), mRecyclerView, mCallback);
        }

        mRecyclerView.setAdapter(mContactListRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private boolean hasReadContactPermission() {
        return ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // We got permission from the user so finish loading
                    finishInit();
                }
                // We didn't get permission so stay where we are and don't load the contact fragment.
        }
    }
}
