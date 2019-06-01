package com.hereticpurge.simplespeeddial.contacts;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.hereticpurge.simplespeeddial.R;
import com.hereticpurge.simplespeeddial.image.ImageHelper;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ContactsViewer {

    public interface ContactsCallback {
        void onResponse(List<Contact> contactList);
    }

    private static List<Contact> contactList;

    public ContactsViewer() {
        contactList = new ArrayList<>();
    }

    public static ContactsViewer getInstance() {
        return new ContactsViewer();
    }

    private Cursor getContactsCursor(Context context) {
        return context.getContentResolver()
                .query(Phone.CONTENT_URI,
                        new String[]{Phone._ID, Phone.DISPLAY_NAME, Phone.NUMBER, Phone.TYPE, Phone.LOOKUP_KEY},
                        null,
                        null,
                        Phone.DISPLAY_NAME + " ASC");
    }

    public void getContacts(final Context context, final ContactsCallback contactsCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                contactsCallback.onResponse(readContacts(context));
            }
        }).start();
    }

    private List<Contact> readContacts(Context context) {

        try (Cursor cursor = getContactsCursor(context)) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                String id = null;
                String contactName = null;
                String numberType = null;
                String phoneNumber = null;
                Uri lookupUri = null;

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    Timber.d("Column Name: %s -- Column Data: %s", cursor.getColumnName(i), cursor.getString(i));

                    switch (cursor.getColumnName(i)) {
                        case "_id":
                            id = cursor.getString(i);
                            continue;

                        case "display_name":
                            contactName = cursor.getString(i);
                            break;

                        case "data1":
                            phoneNumber = cursor.getString(i);
                            break;

                        case "data2":
                            int typeLabelResource = Phone.getTypeLabelResource(cursor.getInt(i));
                            numberType = context.getString(typeLabelResource);

                        case "lookup":
                            if (id != null) {
                                lookupUri = ContactsContract.Contacts.getLookupUri(Long.parseLong(id), cursor.getString(i));
                            }

                    }
                }

                Contact contact = new Contact();
                contact.setName(contactName);

                if (contactList.contains(contact)) {
                    contactList.get(contactList.indexOf(contact)).addPhoneNumber(numberType, phoneNumber);

                } else {

                    contact.setId(id);
                    contact.addPhoneNumber(numberType, phoneNumber);
                    contact.setLookupUri(lookupUri);

                    contactList.add(contact);
                }
            }
        }

        return contactList;
    }

    public Bitmap getContactPhoto(Context context, Uri lookupUri) {

        Bitmap photoBitmap = null;

        if (lookupUri != null) {
            InputStream photoStream = ContactsContract
                    .Contacts
                    .openContactPhotoInputStream(context.getContentResolver(), lookupUri);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(photoStream);
            photoBitmap = BitmapFactory.decodeStream(bufferedInputStream);
        }

        return photoBitmap;
    }

    public Bitmap getContactPhotoRounded(Context context, Uri lookupUri) {

        Bitmap photoBitmap = getContactPhoto(context, lookupUri);

        if (photoBitmap != null) {
            int pixels = context.getResources().getDimensionPixelSize(R.dimen.widget_photo_rounding_pixels);
            photoBitmap = ImageHelper.getRoundedCornerBitmap(photoBitmap, pixels);
        }

        return photoBitmap;
    }
}
