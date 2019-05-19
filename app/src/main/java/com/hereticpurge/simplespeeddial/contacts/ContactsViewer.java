package com.hereticpurge.simplespeeddial.contacts;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ContactsViewer {

    private static List<Contact> contacts;

    public ContactsViewer() {
        contacts = new ArrayList<>();
    }

    public static ContactsViewer getInstance() {
        return new ContactsViewer();
    }

    private Cursor getContactsCursor(Context context) {
        return context.getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[] {Phone._ID, Phone.DISPLAY_NAME, Phone.NUMBER}, null, null,  Phone.DISPLAY_NAME + " ASC");
    }


    // Todo this should probably run on it's own thread.
    // Fix me
    public List<Contact> getContacts(Context context) {
        Cursor cursor = getContactsCursor(context);
        cursor.moveToFirst();

        try {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Contact contact = new Contact();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    Timber.d("Column Name: %s -- Column Data: %s", cursor.getColumnName(i), cursor.getString(i));
                    switch (cursor.getColumnName(i)) {
                        case "_id":
                            // we aren't using the id column so we just continue to the next loop
                            continue;

                        case "display_name":
                            contact.setName(cursor.getString(i));
                            break;

                        case "data1":
                            // data1 is the phone number column
                            if (contacts.contains(contact)) {
                                // If the contact already exists in the contact list we just add
                                // the phone number to the list of numbers
                                int index = contacts.indexOf(contact);
                                contacts.get(index).addPhoneNumber(cursor.getString(i));
                            } else {
                                // If the contact isn't in the list we add the listed number
                                // to the contact then add the whole contact to the list
                                contact.addPhoneNumber(cursor.getString(i));
                                contacts.add(contact);
                            }
                            break;
                    }
                }
            }
        } finally {
            cursor.close();
        }

        return contacts;
    }
}
