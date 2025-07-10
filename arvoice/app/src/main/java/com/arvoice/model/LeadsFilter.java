package com.arvoice.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LeadsFilter {
    public List<TemplateItem> getTemplates() {
        return templates;
    }

    public void setTemplates(List<TemplateItem> templates) {
        this.templates = templates;
    }

    @SerializedName("templates")
    @Expose
    private List<TemplateItem> templates;

    public List<TemplateItem> getData() {
        return templates;
    }

    public void setData(List<TemplateItem> data) {
        this.templates = data;
    }

    public static class TemplateItem {
        @SerializedName("templateId")
        @Expose
        private Integer templateId;

        public Integer getTemplateId() {
            return templateId;
        }

        public void setTemplateId(Integer templateId) {
            this.templateId = templateId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getWebFields() {
            return webFields;
        }

        public void setWebFields(String webFields) {
            this.webFields = webFields;
        }

        public String getMobileFields() {
            return mobileFields;
        }

        public void setMobileFields(String mobileFields) {
            this.mobileFields = mobileFields;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getPageId() {
            return pageId;
        }

        public void setPageId(String pageId) {
            this.pageId = pageId;
        }

        public String getSearchCriteriaList() {
            return searchCriteriaList;
        }

        public void setSearchCriteriaList(String searchCriteriaList) {
            this.searchCriteriaList = searchCriteriaList;
        }

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("webFields")
        @Expose
        private String webFields;

        @SerializedName("mobileFields")
        @Expose
        private String mobileFields;

        @SerializedName("userId")
        @Expose
        private String userId;


        @SerializedName("pageId")
        @Expose
        private String pageId;

        @SerializedName("searchCriteriaList")
        @Expose
        private String searchCriteriaList;


    }
}
