package com.hereticpurge.simplespeeddial;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hereticpurge.simplespeeddial.contacts.Contact;
import com.hereticpurge.simplespeeddial.contacts.ContactsViewer;

import java.util.List;

import timber.log.Timber;

public class PrimaryDisplayFragment extends Fragment {

    public static final String TAG = "SpeedDialPrimaryDisplayFragment";

    public static Fragment createInstance() {
        return new PrimaryDisplayFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.primary_display_layout, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ContactsViewer contactsViewer = new ContactsViewer();
        List<Contact> contactList = contactsViewer.getContacts(getActivity().getApplicationContext());

        for (Contact contact : contactList) {
            Timber.d("Contact name is %s", contact.getName());
            for (String s : contact.getPhoneNumbers()) {
                Timber.d("Number: %s ", s);
            }
        }
    }
}
