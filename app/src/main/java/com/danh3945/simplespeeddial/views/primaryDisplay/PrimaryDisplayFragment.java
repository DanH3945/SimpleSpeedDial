package com.danh3945.simplespeeddial.views.primaryDisplay;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.danh3945.simplespeeddial.R;
import com.danh3945.simplespeeddial.database.QuickContact;
import com.danh3945.simplespeeddial.views.contactList.ContactListFragment;
import com.danh3945.simplespeeddial.views.currentSpeedDial.CurrentSpeedDialFragment;

import timber.log.Timber;

public class PrimaryDisplayFragment extends Fragment {

    public static final String TAG = "SpeedDialPrimaryDisplayFragment";
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 11567;

    AutoCompleteTextView numberTypeAutoTextview;
    EditText nameEditText;
    EditText numberEditText;

    Button mContactListBtn;

    public static Fragment createInstance() {
        return new PrimaryDisplayFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.d("Creating View for %s", TAG);
        View view = inflater.inflate(R.layout.primary_display_layout, container, false);

        mContactListBtn = view.findViewById(R.id.primary_display_contact_list_btn);
        mContactListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if we have permission to read contacts
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    // We do not have permission so ask for it. Get the result in the
                    // onRequestPermissionResult method.
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                } else {
                    // We do have permission so load the contacts display fragment
                    loadContactListFragment();
                }

            }
        });

        Button currentSpeedListBtn = view.findViewById(R.id.primary_display_contact_current_speed_list_btn);
        currentSpeedListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCurrentSpeedListFragment();
            }
        });

        String[] numberTypes = getResources().getStringArray(R.array.number_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_singlechoice, numberTypes);

        numberTypeAutoTextview = view.findViewById(R.id.primary_display_quick_add_number_type);
        numberTypeAutoTextview.setAdapter(adapter);

        nameEditText = view.findViewById(R.id.primary_display_quick_add_name_et);
        numberEditText = view.findViewById(R.id.primary_display_quick_add_number_et);

        Button quickAddButton = view.findViewById(R.id.primary_display_quick_add_button);
        quickAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSpeedDial();
            }
        });

        return view;
    }

    private void addSpeedDial() {
        String name = nameEditText.getText().toString();
        String number = numberEditText.getText().toString();
        String numberType = numberTypeAutoTextview.getText().toString();

        AlertDialog.Builder responseDialog = new AlertDialog.Builder(getContext());

        if (name.equals("")) {
            responseDialog.setMessage("Please enter a valid name");
            responseDialog.show();
            return;
        }

        if (number.equals("")) {
            responseDialog.setMessage("Please enter a valid number");
            responseDialog.show();
            return;
        }

        if (numberType.equals("")) {
            responseDialog.setMessage("Please enter a valid number type");
            responseDialog.show();
            return;
        }

        QuickContact quickContact = new QuickContact();
        quickContact.setName(name);
        quickContact.setNumber(number);
        quickContact.setNumberType(numberType);

        quickContact.addToSpeedDial(getContext());

        nameEditText.getText().clear();
        numberEditText.getText().clear();
        numberTypeAutoTextview.getText().clear();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Callback from the permissions request made above.
        // Switch is used incase we add more permission requests in the future.
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // We got permission from the user so load the contacts display fragment
                    loadContactListFragment();
                }
                // We didn't get permission so stay where we are and don't load the contact fragment.
        }
    }

    private void loadContactListFragment() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_fragment_container, ContactListFragment.getInstance(), ContactListFragment.TAG);
        ft.addToBackStack(ContactListFragment.TAG);
        ft.commit();
        fm.executePendingTransactions();
    }

    private void loadCurrentSpeedListFragment() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_fragment_container, CurrentSpeedDialFragment.getInstance(), CurrentSpeedDialFragment.TAG);
        ft.addToBackStack(CurrentSpeedDialFragment.TAG);
        ft.commit();
        fm.executePendingTransactions();
    }


}
