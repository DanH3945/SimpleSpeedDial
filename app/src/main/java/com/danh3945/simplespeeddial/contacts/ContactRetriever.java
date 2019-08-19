package com.danh3945.simplespeeddial.contacts;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ContactRetriever {

    public interface ContactsCallback {
        void onResponse(List<Contact> contactList);
    }

    private ContactRetriever() { }

    public static ContactRetriever getInstance() {
        return new ContactRetriever();
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

        List<Contact> contactList = new ArrayList<>();

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
                        case Phone._ID:
                            id = cursor.getString(i);
                            continue;

                        case Phone.DISPLAY_NAME:
                            contactName = cursor.getString(i);
                            break;

                        case Phone.NUMBER:
                            phoneNumber = cursor.getString(i);
                            break;

                        case Phone.TYPE:
                            int typeLabelResource = Phone.getTypeLabelResource(cursor.getInt(i));
                            numberType = context.getString(typeLabelResource);

                        case Phone.LOOKUP_KEY:
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
}
