package com.hereticpurge.simplespeeddial.contacts;

import java.util.ArrayList;
import java.util.List;

public class Contact {

    private String name;
    private List<String> numbers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addPhoneNumber(String phoneNumber) {
        if (numbers == null) {
            numbers = new ArrayList<>();
        }

        if (!numbers.contains(phoneNumber)) {
            numbers.add(phoneNumber);
        }
    }

    public List<String> getPhoneNumbers() {
        return numbers;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;

        Contact contact = (Contact) obj;

        return contact.getName().equals(name);
    }
}
