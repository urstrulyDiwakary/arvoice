package com.arvoice.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserSettingsModel {

    @SerializedName("data")
    @Expose
    private List<UserSettingItem> data;

    public List<UserSettingItem> getData() {
        return data;
    }

    public void setData(List<UserSettingItem> data) {
        this.data = data;
    }

    public static class UserSettingItem implements Parcelable {

        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("isActive")
        @Expose
        private boolean isActive;

        public UserSettingItem() {
        }

        protected UserSettingItem(Parcel in) {
            id = in.readString();
            name = in.readString();
            isActive = in.readByte() != 0;
        }

        public static final Creator<UserSettingItem> CREATOR = new Creator<UserSettingItem>() {
            @Override
            public UserSettingItem createFromParcel(Parcel in) {
                return new UserSettingItem(in);
            }

            @Override
            public UserSettingItem[] newArray(int size) {
                return new UserSettingItem[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
            dest.writeByte((byte) (isActive ? 1 : 0));
        }

        @Override
        public int describeContents() {
            return 0;
        }

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }
    }
}
