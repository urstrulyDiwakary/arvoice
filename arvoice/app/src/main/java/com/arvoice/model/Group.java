package com.arvoice.model;

import java.util.List;

public class Group {
    private int id;
    private String name;
    private String label;
    private List<Field> fields;
    private String layout;
    private int displayOrder;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public List<Field> getFields() {
        return fields;
    }

    public String getLayout() {
        return layout;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }
}
