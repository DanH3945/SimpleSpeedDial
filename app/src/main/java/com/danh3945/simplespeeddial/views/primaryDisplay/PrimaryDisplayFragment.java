package com.danh3945.simplespeeddial.views.primaryDisplay;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.danh3945.simplespeeddial.R;
import com.danh3945.simplespeeddial.database.SpeedDialBtn;
import com.danh3945.simplespeeddial.views.contactList.ContactListFragment;
import com.danh3945.simplespeeddial.views.currentSpeedDial.CurrentSpeedDialFragment;

import timber.log.Timber;

public class PrimaryDisplayFragment extends Fragment {

    public static final String TAG = "SpeedDialPrimaryDisplayFragment";
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 5100;

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
                    loadFragment(ContactListFragment.getInstance(), ContactListFragment.TAG);
                }

            }
        });

        Button currentSpeedListBtn = view.findViewById(R.id.primary_display_contact_current_speed_list_btn);
        currentSpeedListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(CurrentSpeedDialFragment.getInstance(), CurrentSpeedDialFragment.TAG);
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

        if (isLandscapeOriented()) {
            loadFragment(CurrentSpeedDialFragment.getInstance(), CurrentSpeedDialFragment.TAG);
        }

        return view;
    }

    private void addSpeedDial() {
        String name = nameEditText.getText().toString();
        String number = numberEditText.getText().toString();
        String numberType = numberTypeAutoTextview.getText().toString();

        AlertDialog.Builder responseDialog = new AlertDialog.Builder(getContext());

        if (name.equals("")) {
            responseDialog.setMessage(getResources().getString(R.string.primary_display_invalid_name));
            responseDialog.show();
            return;
        }

        if (number.equals("")) {
            responseDialog.setMessage(getResources().getString(R.string.primary_display_invalid_number));
            responseDialog.show();
            return;
        }

        if (numberType.equals("")) {
            responseDialog.setMessage(getResources().getString(R.string.primary_display_invalid_number_type));
            responseDialog.show();
            return;
        }

        SpeedDialBtn speedDialBtn = new SpeedDialBtn();
        speedDialBtn.setName(name);
        speedDialBtn.setNumber(number);
        speedDialBtn.setNumberType(numberType);

        speedDialBtn.addToSpeedDial(getContext());

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
                    loadFragment(ContactListFragment.getInstance(), ContactListFragment.TAG);
                }
                // We didn't get permission so stay where we are and don't load the contact fragment.
        }
    }

    private void loadFragment(Fragment fragment, String tag) {
        if (isLandscapeOriented()) {
            loadChildFragment(fragment, tag);
        } else {
            loadMainFragment(fragment, tag);
        }
    }

    private void loadMainFragment(Fragment fragment, String tag) {
        try {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.main_fragment_container, fragment, tag);
            ft.addToBackStack(tag);
            ft.commit();
            fm.executePendingTransactions();
        } catch (NullPointerException e) {
            Timber.e(e);
        }
    }

    private void loadChildFragment(Fragment fragment, String tag) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.primary_display_landscape_frame, fragment, tag);
        ft.commit();
        fm.executePendingTransactions();
    }

    private boolean isLandscapeOriented() {
        return getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}
