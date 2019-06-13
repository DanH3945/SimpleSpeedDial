package com.danh3945.simplespeeddial.views;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.ContextThemeWrapper;
import android.widget.TextView;

import com.danh3945.simplespeeddial.R;

import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.License;
import de.psdev.licensesdialog.model.Notice;
import timber.log.Timber;

public class AboutDialogFragment extends AppCompatDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog;
        if (getContext() != null) {
            // Wrapping the context in a theme to display the old style dialog box layout.
            // I just think this looks better for the layout of the app.
            ContextThemeWrapper themeWrapper = new ContextThemeWrapper(getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
            dialog = new Dialog(themeWrapper);
        } else {
            return super.onCreateDialog(savedInstanceState);
        }

        dialog.setContentView(R.layout.about_dialog_layout);
        dialog.setTitle(R.string.about_title);

        TextView mVersionText = dialog.findViewById(R.id.about_version_tv);
        try {
            int versionCode = getContext()
                    .getPackageManager()
                    .getPackageInfo(getContext().getPackageName(), 0)
                    .versionCode;

            String string = getResources().getString(R.string.about_version) + versionCode;

            mVersionText.setText(string);
        } catch (PackageManager.NameNotFoundException nnfe) {
            Timber.d("onCreateDialog: Failed to get package name");
        }

        TextView licenseDialogText = dialog.findViewById(R.id.about_license_dialog_tv);
        licenseDialogText.setOnClickListener(v -> AboutDialogFragment.this.showLicenseDialogLicense());

        TextView picassoText = dialog.findViewById(R.id.about_picasso_tv);
        picassoText.setOnClickListener(v -> showPicassoLicense());

        TextView timberText = dialog.findViewById(R.id.about_timber_tv);
        timberText.setOnClickListener(v -> showTimberLicense());

        return dialog;
    }

    private void showLicenseDialogLicense() {
        Context context = getContext();
        final String name = context.getResources().getString(R.string.about_license_dialog_title);
        final String url = context.getResources().getString(R.string.about_license_dialog_url);
        final String copyright = context.getResources().getString(R.string.about_license_dialog_copyright);
        final License license = new ApacheSoftwareLicense20();
        final Notice notice = new Notice(name, url, copyright, license);
        new LicensesDialog.Builder(context).setNotices(notice).build().showAppCompat();

    }

    private void showPicassoLicense() {
        Context context = getContext();
        final String name = context.getResources().getString(R.string.about_picasso_license_title);
        final String url = context.getResources().getString(R.string.about_picasso_url);
        final String copyright = context.getResources().getString(R.string.about_picasso_copyright);
        final License license = new ApacheSoftwareLicense20();
        final Notice notice = new Notice(name, url, copyright, license);
        new LicensesDialog.Builder(context).setNotices(notice).build().showAppCompat();
    }

    private void showTimberLicense() {
        Context context = getContext();
        final String name = context.getResources().getString(R.string.about_timber_license_title);
        final String url = context.getResources().getString(R.string.about_timber_url);
        final String copyright = context.getResources().getString(R.string.about_timber_copyright);
        final License license = new ApacheSoftwareLicense20();
        final Notice notice = new Notice(name, url, copyright, license);
        new LicensesDialog.Builder(context).setNotices(notice).build().showAppCompat();

    }
}
