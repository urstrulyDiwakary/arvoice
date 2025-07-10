package com.arvoice.model;

import java.util.List;

public class LeadCreate {
    private String name;
    private List<Group> groups;
    private String label;
    private String layoutName;

    public String getName() {
        return name;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public String getLabel() {
        return label;
    }

    public String getLayoutName() {
        return layoutName;
    }
}

