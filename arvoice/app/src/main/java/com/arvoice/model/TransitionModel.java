package com.arvoice.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TransitionModel {

    public static class TaskItem implements Serializable {

        private static final long serialVersionUID = 1L;

        @SerializedName("id")
        @Expose
        private Integer id;

        @SerializedName("taskId")
        @Expose
        private Integer taskId;

        @SerializedName("taskName")
        @Expose
        private String taskName;

        @SerializedName("comments")
        @Expose
        private String comments;

        @SerializedName("car")
        @Expose
        private String car;

        @SerializedName("siteVisitImage")
        @Expose
        private String siteVisitImage;

        @SerializedName("assignedTo")
        @Expose
        private String assignedTo;

        @SerializedName("createdTime")
        @Expose
        private String createdTime;

        @SerializedName("modifiedTime")
        @Expose
        private String modifiedTime;

        @SerializedName("status")
        @Expose
        private String status;

        // Getters and setters
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public Integer getTaskId() { return taskId; }
        public void setTaskId(Integer taskId) { this.taskId = taskId; }

        public String getTaskName() { return taskName; }
        public void setTaskName(String taskName) { this.taskName = taskName; }

        public String getComments() { return comments; }
        public void setComments(String comments) { this.comments = comments; }

        public String getCar() { return car; }
        public void setCar(String car) { this.car = car; }

        public String getSiteVisitImage() { return siteVisitImage; }
        public void setSiteVisitImage(String siteVisitImage) { this.siteVisitImage = siteVisitImage; }

        public String getAssignedTo() { return assignedTo; }
        public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

        public String getCreatedTime() { return createdTime; }
        public void setCreatedTime(String createdTime) { this.createdTime = createdTime; }

        public String getModifiedTime() { return modifiedTime; }
        public void setModifiedTime(String modifiedTime) { this.modifiedTime = modifiedTime; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
