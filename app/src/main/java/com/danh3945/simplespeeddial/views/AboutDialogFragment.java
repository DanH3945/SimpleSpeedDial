package com.danh3945.simplespeeddial.views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.danh3945.simplespeeddial.R;

import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.License;
import de.psdev.licensesdialog.licenses.MITLicense;
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

        TextView gsonText = dialog.findViewById(R.id.about_gson_tv);
        gsonText.setOnClickListener(v -> showGsonLicense());

        TextView textDrawableText = dialog.findViewById(R.id.about_text_drawable_tv);
        textDrawableText.setOnClickListener(v -> showTextDrawableLicense());

        TextView iconsEightText = dialog.findViewById(R.id.about_icons_eight_tv);
        iconsEightText.setOnClickListener(v -> openIconsEightWebpage());

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

    private void showGsonLicense() {
        Context context = getContext();
        final String name = context.getResources().getString(R.string.about_gson_license_title);
        final String url = context.getResources().getString(R.string.about_gson_url);
        final String copyright = context.getResources().getString(R.string.about_gson_copyright);
        final License license = new ApacheSoftwareLicense20();
        final Notice notice = new Notice(name, url, copyright, license);
        new LicensesDialog.Builder(context).setNotices(notice).build().showAppCompat();
    }

    private void showTextDrawableLicense() {
        Context context = getContext();
        final String name = getResources().getString(R.string.about_text_drawable_license_title);
        final String url = getResources().getString(R.string.about_text_drawable_url);
        final String copyright = getResources().getString(R.string.about_text_drawable_copyright);
        final License license = new MITLicense();
        final Notice notice = new Notice(name, url, copyright, license);
        new LicensesDialog.Builder(context).setNotices(notice).build().showAppCompat();
    }

    private void openIconsEightWebpage() {
        String url = getResources().getString(R.string.about_icons_eight_url);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    // Todo add Dagger2 about Fragment
}
