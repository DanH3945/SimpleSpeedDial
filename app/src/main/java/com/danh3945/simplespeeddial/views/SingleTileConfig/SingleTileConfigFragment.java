package com.danh3945.simplespeeddial.views.SingleTileConfig;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.danh3945.simplespeeddial.R;
import com.danh3945.simplespeeddial.database.LargeWidgetObject;
import com.danh3945.simplespeeddial.views.contactList.ContactListFragment;
import com.danh3945.simplespeeddial.views.contactList.ContactListRecyclerAdapter;
import com.danh3945.simplespeeddial.widget.SingleTileAppWidgetProvider;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

public class SingleTileConfigFragment extends Fragment {

    public static final String TAG = "SingleTileConfigFragment";

    private int mAppWidgetId;

    public static SingleTileConfigFragment createInstance(int appWidgetId) {
        SingleTileConfigFragment fragment = new SingleTileConfigFragment();
        fragment.mAppWidgetId = appWidgetId;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_tile_config_layout, container, false);

        loadContactList();

        return view;
    }

    private void loadContactList() {
        ContactListRecyclerAdapter.ContactListResultCallback callback = new ContactListRecyclerAdapter.ContactListResultCallback() {
            @Override
            public void clickResult(LargeWidgetObject object) {
                doSetup(object);
            }
        };

        loadFragment(ContactListFragment.createInstanceForResult(callback), ContactListFragment.TAG);
    }

    private void doSetup(LargeWidgetObject object) {
        Timber.d("Configuring single tile widget with name: %s, number: %s, numberType: %s",
                object.getName(), object.getNumber(), object.getNumberType());

        SingleTileAppWidgetProvider.setupFromConfigurationActivity(getContext(), mAppWidgetId, object);

        if (getActivity() != null) {
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            getActivity().setResult(RESULT_OK, resultValue);
            getActivity().finish();
        } else {
            Timber.d("getActivity() returned null in doSetup()");
        }
    }

    private void loadFragment(Fragment fragment, String tag) {
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
}
