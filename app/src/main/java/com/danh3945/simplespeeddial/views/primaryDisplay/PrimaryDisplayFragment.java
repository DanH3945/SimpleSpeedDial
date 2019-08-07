package com.danh3945.simplespeeddial.views.primaryDisplay;

import android.app.AlertDialog;
import android.content.Context;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.danh3945.simplespeeddial.R;
import com.danh3945.simplespeeddial.database.SpeedDialObject;
import com.danh3945.simplespeeddial.views.contactList.ContactListFragment;
import com.danh3945.simplespeeddial.views.currentSpeedDial.CurrentSpeedDialFragment;

import timber.log.Timber;

public class PrimaryDisplayFragment extends Fragment {

    public static final String TAG = "SpeedDialPrimaryDisplayFragment";

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
                loadFragment(ContactListFragment.createInstance(), ContactListFragment.TAG);
            }
        });

        Button currentSpeedListBtn = view.findViewById(R.id.primary_display_contact_current_speed_list_btn);
        currentSpeedListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(CurrentSpeedDialFragment.createInstance(), CurrentSpeedDialFragment.TAG);
            }
        });

        String[] numberTypes = getResources().getStringArray(R.array.number_types);

        numberTypeAutoTextview = view.findViewById(R.id.primary_display_quick_add_number_type);
        if (getContext() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_singlechoice, numberTypes);
            numberTypeAutoTextview.setAdapter(adapter);
        }

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
            loadFragment(CurrentSpeedDialFragment.createInstance(), CurrentSpeedDialFragment.TAG);
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

        SpeedDialObject speedDialObject = SpeedDialObject.createObject(name, number, numberType);

        speedDialObject.addToLargeWidgetSpeedDial(getContext());

        nameEditText.getText().clear();
        numberEditText.getText().clear();
        numberTypeAutoTextview.getText().clear();
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
