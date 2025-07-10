package com.arvoice.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CarListModel {

    @SerializedName("data")
    @Expose
    private List<CarItem> data;

    public List<CarItem> getData() {
        return data;
    }

    public void setData(List<CarItem> data) {
        this.data = data;
    }

    public static class CarItem implements Parcelable {

        @SerializedName("carName")
        @Expose
        private String carName;

        public CarItem() {
        }

        protected CarItem(Parcel in) {
            carName = in.readString();
        }

        public static final Creator<CarItem> CREATOR = new Creator<CarItem>() {
            @Override
            public CarItem createFromParcel(Parcel in) {
                return new CarItem(in);
            }

            @Override
            public CarItem[] newArray(int size) {
                return new CarItem[size];
            }
        };

        public String getCarName() {
            return carName;
        }

        public void setCarName(String carName) {
            this.carName = carName;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(carName);
        }

        @Override
        public int describeContents() {
            return 0;
        }
    }
}
