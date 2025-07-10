package com.arvoice.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class LeadsListModel {

    @SerializedName("content")
    @Expose
    private List<ContentItem> content;

    public List<ContentItem> getContent() {
        return content;
    }

    public void setContent(List<ContentItem> content) {
        this.content = content;
    }

    public static class ContentItem implements Parcelable {
        @SerializedName("leadId")
        @Expose
        private Integer leadId;

        @SerializedName("leadOwner")
        @Expose
        private String leadOwner;
        public String getInitials() {
            if (leadOwner == null || leadOwner.trim().isEmpty()) {
                return "";
            }

            String[] parts = leadOwner.trim().split("\\s+");
            StringBuilder initials = new StringBuilder();

            for (int i = 0; i < parts.length && i < 2; i++) {
                if (!parts[i].isEmpty()) {
                    initials.append(Character.toUpperCase(parts[i].charAt(0)));
                }
            }

            return initials.toString();
        }
        @SerializedName("contactName")
        @Expose
        private String contactName;

        @SerializedName("mobileNumber")
        @Expose
        private String mobileNumber;

        @SerializedName("alternateNumber")
        @Expose
        private String alternateNumber;

        @SerializedName("emailAddress")
        @Expose
        private String emailAddress;

        @SerializedName("leadStage")
        @Expose
        private String leadStage;

        @SerializedName("expectedRevenue")
        @Expose
        private Double expectedRevenue;

        @SerializedName("expectedClosingDate")
        @Expose
        private String expectedClosingDate;

        @SerializedName("siteVisited")
        @Expose
        private String siteVisited;

        @SerializedName("leadTitle")
        @Expose
        private String leadTitle;

        @SerializedName("category")
        @Expose
        private String category;

        @SerializedName("assignedManager")
        @Expose
        private String assignedManager;

        @SerializedName("leadDate")
        @Expose
        private String leadDate;

        @SerializedName("leadOwnerEmail")
        @Expose
        private String leadOwnerEmail;

        @SerializedName("sourceInfo")
        @Expose
        private SourceInfo sourceInfo;
        @SerializedName("wonInfo")
        @Expose
        private WONInfo wonInfo;

        public WONInfo getWonInfo() {
            return wonInfo;
        }

        @SerializedName("leadInfoExtnAttr")
        @Expose
        private LeadInfoExtnAttr leadInfoExtnAttr;

        @SerializedName("leadHistory")
        @Expose
        private LeadHistory leadHistory;
        @SerializedName("callRecordDataEntity")
        @Expose
        private transient Object callRecordDataEntity;
        @SerializedName("lastCallInfo")
        @Expose
        private transient Object lastCallInfo;

        public ContentItem() {
            // Default constructor
        }

        protected ContentItem(Parcel in) {
            if (in.readByte() == 0) {
                leadId = null;
            } else {
                leadId = in.readInt();
            }
            leadOwner = in.readString();
            contactName = in.readString();
            mobileNumber = in.readString();
            alternateNumber = in.readString();
            emailAddress = in.readString();
            leadStage = in.readString();
            if (in.readByte() == 0) {
                expectedRevenue = null;
            } else {
                expectedRevenue = in.readDouble();
            }
            expectedClosingDate = in.readString();
            siteVisited = in.readString();
            leadTitle = in.readString();
            category = in.readString();
            assignedManager = in.readString();
            leadDate = in.readString();
            leadOwnerEmail = in.readString();
            sourceInfo = in.readParcelable(SourceInfo.class.getClassLoader());
            wonInfo = in.readParcelable(WONInfo.class.getClassLoader());
            leadInfoExtnAttr = in.readParcelable(LeadInfoExtnAttr.class.getClassLoader());
            leadHistory = in.readParcelable(LeadInfoExtnAttr.class.getClassLoader());
            // transient fields are not written/read.
        }

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
            dest.writeString(alternateNumber);
            dest.writeString(emailAddress);
            dest.writeString(leadStage);
            if (expectedRevenue == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(expectedRevenue);
            }
            dest.writeString(expectedClosingDate);
            dest.writeString(siteVisited);
            dest.writeString(leadTitle);
            dest.writeString(category);
            dest.writeString(assignedManager);
            dest.writeString(leadDate);
            dest.writeString(leadOwnerEmail);
            dest.writeParcelable(sourceInfo, flags);
            dest.writeParcelable(wonInfo, flags);
            dest.writeParcelable(leadInfoExtnAttr, flags);
            dest.writeParcelable(leadHistory, flags);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<ContentItem> CREATOR = new Creator<ContentItem>() {
            @Override
            public ContentItem createFromParcel(Parcel in) {
                return new ContentItem(in);
            }

            @Override
            public ContentItem[] newArray(int size) {
                return new ContentItem[size];
            }
        };

        // Getters and Setters for each field...
        public Integer getLeadId() {
            return leadId;
        }
        public void setLeadId(Integer leadId) {
            this.leadId = leadId;
        }
        public String getLeadOwner() {
            return leadOwner;
        }
        public void setLeadOwner(String leadOwner) {
            this.leadOwner = leadOwner;
        }
        public String getContactName() {
            return contactName;
        }
        public void setContactName(String contactName) {
            this.contactName = contactName;
        }
        public String getMobileNumber() {
            return mobileNumber;
        }
        public void setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
        }
        public String getAlternateNumber() {
            return alternateNumber;
        }
        public void setAlternateNumber(String alternateNumber) {
            this.alternateNumber = alternateNumber;
        }
        public String getEmailAddress() {
            return emailAddress;
        }
        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }
        public String getLeadStage() {
            return leadStage;
        }
        public void setLeadStage(String leadStage) {
            this.leadStage = leadStage;
        }
        public Double getExpectedRevenue() {
            return expectedRevenue;
        }
        public void setExpectedRevenue(Double expectedRevenue) {
            this.expectedRevenue = expectedRevenue;
        }
        public String getExpectedClosingDate() {
            return expectedClosingDate;
        }
        public void setExpectedClosingDate(String expectedClosingDate) {
            this.expectedClosingDate = expectedClosingDate;
        }
        public String getSiteVisited() {
            return siteVisited;
        }
        public void setSiteVisited(String siteVisited) {
            this.siteVisited = siteVisited;
        }
        public String getLeadTitle() {
            return leadTitle;
        }
        public void setLeadTitle(String leadTitle) {
            this.leadTitle = leadTitle;
        }
        public String getCategory() {
            return category;
        }
        public void setCategory(String category) {
            this.category = category;
        }
        public String getAssignedManager() {
            return assignedManager;
        }
        public void setAssignedManager(String assignedManager) {
            this.assignedManager = assignedManager;
        }
        public String getLeadDate() {
            return leadDate;
        }
        public void setLeadDate(String leadDate) {
            this.leadDate = leadDate;
        }
        public String getLeadOwnerEmail() {
            return leadOwnerEmail;
        }
        public void setLeadOwnerEmail(String leadOwnerEmail) {
            this.leadOwnerEmail = leadOwnerEmail;
        }
        public SourceInfo getSourceInfo() {
            return sourceInfo;
        }

        public void setSourceInfo(SourceInfo sourceInfo) {
            this.sourceInfo = sourceInfo;
        }
        public LeadInfoExtnAttr getLeadInfoExtnAttr() {
            return leadInfoExtnAttr;
        }
        public void setLeadInfoExtnAttr(LeadInfoExtnAttr leadInfoExtnAttr) {
            this.leadInfoExtnAttr = leadInfoExtnAttr;
        }

        public LeadHistory getLeadHistory() {
            return leadHistory;
        }

        public void setLeadHistory(LeadHistory leadHistory) {
            this.leadHistory = leadHistory;
        }
// Optionally, add getInitials() if required.
    }

    public static class SourceInfo implements Parcelable {
        @SerializedName("sourceInfoId")
        @Expose
        private Integer sourceInfoId;
        @SerializedName("leadSource")
        @Expose
        private String leadSource;
        @SerializedName("campaignName")
        @Expose
        private String campaignName;
        @SerializedName("campaignTeam")
        @Expose
        private String campaignTeam;
        @SerializedName("campaignContent")
        @Expose
        private String campaignContent;
        @SerializedName("leadDate")
        @Expose
        private String leadDate;
        @SerializedName("createdBy")
        @Expose
        private String createdBy;

        // We'll skip the Map field for parceling.
        @SerializedName("sourceInfoExtnAttr")
        @Expose
        private transient Map<String, Object> sourceInfoExtnAttr;

        public SourceInfo() {
        }

        protected SourceInfo(Parcel in) {
            if (in.readByte() == 0) {
                sourceInfoId = null;
            } else {
                sourceInfoId = in.readInt();
            }
            leadSource = in.readString();
            campaignName = in.readString();
            campaignTeam = in.readString();
            campaignContent = in.readString();
            leadDate = in.readString();
            createdBy = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (sourceInfoId == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(sourceInfoId);
            }
            dest.writeString(leadSource);
            dest.writeString(campaignName);
            dest.writeString(campaignTeam);
            dest.writeString(campaignContent);
            dest.writeString(leadDate);
            dest.writeString(createdBy);
        }

        @Override
        public int describeContents() {
            return 0;
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

        // Getters and Setters
        public Integer getSourceInfoId() {
            return sourceInfoId;
        }
        public void setSourceInfoId(Integer sourceInfoId) {
            this.sourceInfoId = sourceInfoId;
        }
        public String getLeadSource() {
            return leadSource;
        }
        public void setLeadSource(String leadSource) {
            this.leadSource = leadSource;
        }
        public String getCampaignName() {
            return campaignName;
        }
        public void setCampaignName(String campaignName) {
            this.campaignName = campaignName;
        }
        public String getCampaignTeam() {
            return campaignTeam;
        }
        public void setCampaignTeam(String campaignTeam) {
            this.campaignTeam = campaignTeam;
        }
        public String getCampaignContent() {
            return campaignContent;
        }
        public void setCampaignContent(String campaignContent) {
            this.campaignContent = campaignContent;
        }
        public String getLeadDate() {
            return leadDate;
        }
        public void setLeadDate(String leadDate) {
            this.leadDate = leadDate;
        }
        public String getCreatedBy() {
            return createdBy;
        }
        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }
    }
    public static class WONInfo implements Parcelable {

        @SerializedName("wonInfoId")
        @Expose
        private Integer wonInfoId;

        @SerializedName("dealDate")
        @Expose
        private String dealDate;

        @SerializedName("dealPrice")
        @Expose
        private Double dealPrice;

        @SerializedName("dealNoOfCents")
        @Expose
        private Integer dealNoOfCents;

        @SerializedName("dealTotalValue")
        @Expose
        private Double dealTotalValue;

        @SerializedName("dealDescription")
        @Expose
        private String dealDescription;

        @SerializedName("wonInfoExtnAttr")
        @Expose
        private transient Map<String, Object> wonInfoExtnAttr; // Skipped from parceling

        public WONInfo() {}

        protected WONInfo(Parcel in) {
            if (in.readByte() == 0) {
                wonInfoId = null;
            } else {
                wonInfoId = in.readInt();
            }
            dealDate = in.readString();
            if (in.readByte() == 0) {
                dealPrice = null;
            } else {
                dealPrice = in.readDouble();
            }
            if (in.readByte() == 0) {
                dealNoOfCents = null;
            } else {
                dealNoOfCents = in.readInt();
            }
            if (in.readByte() == 0) {
                dealTotalValue = null;
            } else {
                dealTotalValue = in.readDouble();
            }
            dealDescription = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (wonInfoId == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(wonInfoId);
            }
            dest.writeString(dealDate);

            if (dealPrice == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(dealPrice);
            }

            if (dealNoOfCents == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(dealNoOfCents);
            }

            if (dealTotalValue == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(dealTotalValue);
            }

            dest.writeString(dealDescription);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<WONInfo> CREATOR = new Creator<WONInfo>() {
            @Override
            public WONInfo createFromParcel(Parcel in) {
                return new WONInfo(in);
            }

            @Override
            public WONInfo[] newArray(int size) {
                return new WONInfo[size];
            }
        };

        // --- Getters & Setters ---
        public Integer getWonInfoId() {
            return wonInfoId;
        }

        public void setWonInfoId(Integer wonInfoId) {
            this.wonInfoId = wonInfoId;
        }

        public String getDealDate() {
            return dealDate;
        }

        public void setDealDate(String dealDate) {
            this.dealDate = dealDate;
        }

        public Double getDealPrice() {
            return dealPrice;
        }

        public void setDealPrice(Double dealPrice) {
            this.dealPrice = dealPrice;
        }

        public Integer getDealNoOfCents() {
            return dealNoOfCents;
        }

        public void setDealNoOfCents(Integer dealNoOfCents) {
            this.dealNoOfCents = dealNoOfCents;
        }

        public Double getDealTotalValue() {
            return dealTotalValue;
        }

        public void setDealTotalValue(Double dealTotalValue) {
            this.dealTotalValue = dealTotalValue;
        }

        public String getDealDescription() {
            return dealDescription;
        }

        public void setDealDescription(String dealDescription) {
            this.dealDescription = dealDescription;
        }

        public Map<String, Object> getWonInfoExtnAttr() {
            return wonInfoExtnAttr;
        }

        public void setWonInfoExtnAttr(Map<String, Object> wonInfoExtnAttr) {
            this.wonInfoExtnAttr = wonInfoExtnAttr;
        }
    }

    public static class LeadHistory implements Parcelable {
        @SerializedName("leadHistoryId")
        @Expose
        private Integer leadHistoryId;
        @SerializedName("leadCreation")
        @Expose
        private String leadCreation;
        @SerializedName("modifiedTime")
        @Expose
        private String modifiedTime;
        @SerializedName("lastViewed")
        @Expose
        private String lastViewed;

        public LeadHistory() {
        }

        protected LeadHistory(Parcel in) {
            leadHistoryId = in.readInt();
            leadCreation = in.readString();
            modifiedTime = in.readString();
            lastViewed = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(leadHistoryId);
            dest.writeString(leadCreation);
            dest.writeString(modifiedTime);
            dest.writeString(lastViewed);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<LeadHistory> CREATOR = new Creator<LeadHistory>() {
            @Override
            public LeadHistory createFromParcel(Parcel in) {
                return new LeadHistory(in);
            }

            @Override
            public LeadHistory[] newArray(int size) {
                return new LeadHistory[size];
            }
        };

        public Integer getLeadHistoryId() {
            return leadHistoryId;
        }

        public void setLeadHistoryId(Integer leadHistoryId) {
            this.leadHistoryId = leadHistoryId;
        }

        public String getLeadCreation() {
            return leadCreation;
        }

        public void setLeadCreation(String leadCreation) {
            this.leadCreation = leadCreation;
        }

        public String getModifiedTime() {
            return modifiedTime;
        }

        public void setModifiedTime(String modifiedTime) {
            this.modifiedTime = modifiedTime;
        }

        public String getLastViewed() {
            return lastViewed;
        }

        public void setLastViewed(String lastViewed) {
            this.lastViewed = lastViewed;
        }
    }
    public static class LeadInfoExtnAttr implements Parcelable {
        @SerializedName("gender")
        @Expose
        private String gender;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("nextFollow-upOn")
        @Expose
        private String nextFollowUpOn;
        @SerializedName("lostDate")
        @Expose
        private String lostDate;
        @SerializedName("lostReason")
        @Expose
        private String lostReason;

        public String getLostDate() {
            return lostDate;
        }

        public void setLostDate(String lostDate) {
            this.lostDate = lostDate;
        }

        public String getLostReason() {
            return lostReason;
        }

        public void setLostReason(String lostReason) {
            this.lostReason = lostReason;
        }

        public String getLostDescription() {
            return lostDescription;
        }

        public void setLostDescription(String lostDescription) {
            this.lostDescription = lostDescription;
        }

        @SerializedName("lostDescription")
        @Expose
        private String lostDescription;

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        @SerializedName("nextFollow-upNotes")
        @Expose
        private String nextFollowUpNotes;

        public LeadInfoExtnAttr() {
        }

        protected LeadInfoExtnAttr(Parcel in) {
            gender = in.readString();
            description = in.readString();
            nextFollowUpOn = in.readString();
            nextFollowUpNotes = in.readString();
            lostDate = in.readString();
            lostReason = in.readString();
            lostDescription = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(gender);
            dest.writeString(description);
            dest.writeString(nextFollowUpOn);
            dest.writeString(nextFollowUpNotes);
            dest.writeString(lostDate);
            dest.writeString(lostReason);
            dest.writeString(lostDescription);
        }

        @Override
        public int describeContents() {
            return 0;
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

        // Getters and Setters
        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
        public String getNextFollowUpOn() {
            return nextFollowUpOn;
        }
        public void setNextFollowUpOn(String nextFollowUpOn) {
            this.nextFollowUpOn = nextFollowUpOn;
        }
        public String getNextFollowUpNotes() {
            return nextFollowUpNotes;
        }
        public void setNextFollowUpNotes(String nextFollowUpNotes) {
            this.nextFollowUpNotes = nextFollowUpNotes;
        }
    }
}
