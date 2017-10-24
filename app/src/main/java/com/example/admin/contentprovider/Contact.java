package com.example.admin.contentprovider;

import java.util.List;

/**
 * Created by Admin on 10/23/2017.
 */

public class Contact {
    String Name;
    List<String> Numbers;

    public Contact(String name, List<String> Numbers) {
        this.Name = name;
        this.Numbers = Numbers;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public List<String> getNumbers() {
        return Numbers;
    }

    public void setNumbers(List<String> numbers) {
        Numbers = Numbers;
    }
}
