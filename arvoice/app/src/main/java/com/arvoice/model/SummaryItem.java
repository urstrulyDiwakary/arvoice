package com.arvoice.model;

public class SummaryItem {
    private String title;
    private String subtitle;
    private String value;
    private int templateId;
    private String colorCode;

    public SummaryItem(String title, String subtitle, String value,String colorCode,int templateId) {
        this.title = title;
        this.subtitle = subtitle;
        this.value = value;
        this.colorCode = colorCode;
        this.templateId = templateId;
    }

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getValue() { return value; }
    public String getColorCode() { return colorCode; }
    public int getTemplateId() { return templateId; }
}
