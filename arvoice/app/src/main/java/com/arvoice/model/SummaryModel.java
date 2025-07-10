package com.arvoice.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SummaryModel {

    @SerializedName("data")
    @Expose
    private List<SummaryItem> data;

    public List<SummaryItem> getData() {
        return data;
    }

    public void setData(List<SummaryItem> data) {
        this.data = data;
    }

    public static class SummaryItem {
        @SerializedName("id")
        @Expose
        private Integer id;

        @SerializedName("summaryId")
        @Expose
        private String summaryId;

        @SerializedName("summaryName")
        @Expose
        private String summaryName;

        @SerializedName("templateId")
        @Expose
        private Integer templateId;

        @SerializedName("summaryTemplateName")
        @Expose
        private String summaryTemplateName;

        @SerializedName("summaryTemplateBgColor")
        @Expose
        private String summaryTemplateBgColor;

        @SerializedName("summaryBy")
        @Expose
        private String summaryBy;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getSummaryId() {
            return summaryId;
        }

        public void setSummaryId(String summaryId) {
            this.summaryId = summaryId;
        }

        public String getSummaryName() {
            return summaryName;
        }

        public void setSummaryName(String summaryName) {
            this.summaryName = summaryName;
        }

        public Integer getTemplateId() {
            return templateId;
        }

        public void setTemplateId(Integer templateId) {
            this.templateId = templateId;
        }

        public String getSummaryTemplateName() {
            return summaryTemplateName;
        }

        public void setSummaryTemplateName(String summaryTemplateName) {
            this.summaryTemplateName = summaryTemplateName;
        }

        public String getSummaryTemplateBgColor() {
            return summaryTemplateBgColor;
        }

        public void setSummaryTemplateBgColor(String summaryTemplateBgColor) {
            this.summaryTemplateBgColor = summaryTemplateBgColor;
        }

        public String getSummaryBy() {
            return summaryBy;
        }

        public void setSummaryBy(String summaryBy) {
            this.summaryBy = summaryBy;
        }
    }
}
