package com.arvoice.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TaskListModel {

    @SerializedName("data")
    @Expose
    public List<TaskItem> data;

    public List<TaskItem> getData() {
        return data;
    }

    public void setData(List<TaskItem> data) {
        this.data = data;
    }

    public static class TaskItem implements Parcelable {

        @SerializedName("taskId")
        @Expose
        public Integer taskId;

        @SerializedName("taskName")
        @Expose
        public String taskName;

        @SerializedName("description")
        @Expose
        public String description;

        @SerializedName("status")
        @Expose
        public String status;

        @SerializedName("dueDate")
        @Expose
        public String dueDate;

        @SerializedName("assignedTo")
        @Expose
        public String assignedTo;

        @SerializedName("assignedToEmail")
        @Expose
        public String assignedToEmail;

        @SerializedName("lead")
        @Expose
        public Lead lead;

        @SerializedName("comments")
        @Expose
        public String comments;

        @SerializedName("createdTime")
        @Expose
        public String createdTime;

        @SerializedName("modifiedTime")
        @Expose
        public String modifiedTime;

        @SerializedName("lastViewed")
        @Expose
        public String lastViewed;

        @SerializedName("car")
        @Expose
        public String car;

        protected TaskItem(Parcel in) {
            if (in.readByte() == 0) {
                taskId = null;
            } else {
                taskId = in.readInt();
            }
            taskName = in.readString();
            description = in.readString();
            status = in.readString();
            dueDate = in.readString();
            assignedTo = in.readString();
            assignedToEmail = in.readString();
            lead = in.readParcelable(Lead.class.getClassLoader());
            comments = in.readString();
            createdTime = in.readString();
            modifiedTime = in.readString();
            lastViewed = in.readString();
            car = in.readString();
        }

        public static final Creator<TaskItem> CREATOR = new Creator<TaskItem>() {
            @Override
            public TaskItem createFromParcel(Parcel in) {
                return new TaskItem(in);
            }

            @Override
            public TaskItem[] newArray(int size) {
                return new TaskItem[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (taskId == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(taskId);
            }
            dest.writeString(taskName);
            dest.writeString(description);
            dest.writeString(status);
            dest.writeString(dueDate);
            dest.writeString(assignedTo);
            dest.writeString(assignedToEmail);
            dest.writeParcelable(lead, flags);
            dest.writeString(comments);
            dest.writeString(createdTime);
            dest.writeString(modifiedTime);
            dest.writeString(lastViewed);
            dest.writeString(car);
        }

        @Override
        public int describeContents() {
            return 0;
        }
    }

    public static class Lead implements Parcelable {

        @SerializedName("leadId")
        @Expose
        public Integer leadId;

        @SerializedName("leadOwner")
        @Expose
        public String leadOwner;

        @SerializedName("contactName")
        @Expose
        public String contactName;

        @SerializedName("mobileNumber")
        @Expose
        public String mobileNumber;

        @SerializedName("emailAddress")
        @Expose
        public String emailAddress;

        @SerializedName("leadStage")
        @Expose
        public String leadStage;

        @SerializedName("expectedRevenue")
        @Expose
        public Double expectedRevenue;

        @SerializedName("leadDate")
        @Expose
        public String leadDate;

        @SerializedName("leadOwnerEmail")
        @Expose
        public String leadOwnerEmail;

        @SerializedName("sourceInfo")
        @Expose
        public SourceInfo sourceInfo;

        @SerializedName("leadInfoExtnAttr")
        @Expose
        public LeadInfoExtnAttr leadInfoExtnAttr;

        protected Lead(Parcel in) {
            if (in.readByte() == 0) {
                leadId = null;
            } else {
                leadId = in.readInt();
            }
            leadOwner = in.readString();
            contactName = in.readString();
            mobileNumber = in.readString();
            emailAddress = in.readString();
            leadStage = in.readString();
            if (in.readByte() == 0) {
                expectedRevenue = null;
            } else {
                expectedRevenue = in.readDouble();
            }
            leadDate = in.readString();
            leadOwnerEmail = in.readString();
            sourceInfo = in.readParcelable(SourceInfo.class.getClassLoader());
            leadInfoExtnAttr = in.readParcelable(LeadInfoExtnAttr.class.getClassLoader());
        }

        public static final Creator<Lead> CREATOR = new Creator<Lead>() {
            @Override
            public Lead createFromParcel(Parcel in) {
                return new Lead(in);
            }

            @Override
            public Lead[] newArray(int size) {
                return new Lead[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (leadId == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(leadId);
            }
            dest.writeString(leadOwner);
            dest.writeString(contactName);
            dest.writeString(mobileNumber);
            dest.writeString(emailAddress);
            dest.writeString(leadStage);
            if (expectedRevenue == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(expectedRevenue);
            }
            dest.writeString(leadDate);
            dest.writeString(leadOwnerEmail);
            dest.writeParcelable(sourceInfo, flags);
            dest.writeParcelable(leadInfoExtnAttr, flags);
        }

        @Override
        public int describeContents() {
            return 0;
        }
    }

    public static class SourceInfo implements Parcelable {

        @SerializedName("sourceInfoId")
        @Expose
        public Integer sourceInfoId;

        @SerializedName("leadSource")
        @Expose
        public String leadSource;

        protected SourceInfo(Parcel in) {
            if (in.readByte() == 0) {
                sourceInfoId = null;
            } else {
                sourceInfoId = in.readInt();
            }
            leadSource = in.readString();
        }

        public static final Creator<SourceInfo> CREATOR = new Creator<SourceInfo>() {
            @Override
            public SourceInfo createFromParcel(Parcel in) {
                return new SourceInfo(in);
            }

            @Override
            public SourceInfo[] newArray(int size) {
                return new SourceInfo[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (sourceInfoId == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(sourceInfoId);
            }
            dest.writeString(leadSource);
        }

        @Override
        public int describeContents() {
            return 0;
        }
    }

    public static class LeadInfoExtnAttr implements Parcelable {

        @SerializedName("description")
        @Expose
        public String description;

        @SerializedName("nextFollow-upOn")
        @Expose
        public String nextFollowUpOn;

        @SerializedName("nextFollow-upNotes")
        @Expose
        public String nextFollowUpNotes;

        protected LeadInfoExtnAttr(Parcel in) {
            description = in.readString();
            nextFollowUpOn = in.readString();
            nextFollowUpNotes = in.readString();
        }

        public static final Creator<LeadInfoExtnAttr> CREATOR = new Creator<LeadInfoExtnAttr>() {
            @Override
            public LeadInfoExtnAttr createFromParcel(Parcel in) {
                return new LeadInfoExtnAttr(in);
            }

            @Override
            public LeadInfoExtnAttr[] newArray(int size) {
                return new LeadInfoExtnAttr[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(description);
            dest.writeString(nextFollowUpOn);
            dest.writeString(nextFollowUpNotes);
        }

        @Override
        public int describeContents() {
            return 0;
        }
    }
}