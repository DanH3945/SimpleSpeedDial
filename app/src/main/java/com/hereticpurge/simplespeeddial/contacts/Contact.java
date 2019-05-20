package com.hereticpurge.simplespeeddial.contacts;

import java.util.Map;
import java.util.TreeMap;

public class Contact {

    private String id;
    private String name;
    private String lookupKey;
    private Map<String, String> numbers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addPhoneNumber(String numberType, String phoneNumber) {
        if (numbers == null) {
            numbers = new TreeMap<>();
        }

        if (!numbers.containsKey(phoneNumber)) {
            numbers.put(numberType, phoneNumber);
        }
    }

    public Map<String, String> getPhoneNumbers() {
        return numbers;
    }

    public String getLookupKey() {
        return lookupKey;
    }

    public void setLookupKey(String lookupKey) {
        this.lookupKey = lookupKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;

        Contact contact = (Contact) obj;

        return contact.getName().equals(name);
    }
}
