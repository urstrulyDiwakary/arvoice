package com.arvoice.model;

public class Field {
    private int id;
    private String name;
    private String label;
    private String type;
    private String options;
    private int displayOrder;
    private String searchMode;
    private boolean needForMassEdit;
    private boolean visible;
    private boolean required;
    private boolean jsonKey;
    private boolean editable;
    private boolean mappingNeeded;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public String getType() {
        return type;
    }

    public String getOptions() {
        return options;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public String getSearchMode() {
        return searchMode;
    }

    public boolean isNeedForMassEdit() {
        return needForMassEdit;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isJsonKey() {
        return jsonKey;
    }

    public boolean isEditable() {
        return editable;
    }

    public boolean isMappingNeeded() {
        return mappingNeeded;
    }
}
