package com.example.maimyou.RecycleViewMaterials;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.example.maimyou.Activities.CourseStructure;

public class Child implements Parcelable {
    public final String Title;

    public Child(String title) {
        this.Title = title;
    }

    protected Child(Parcel in) {
        Title = in.readString();
    }

    public static final Creator<Child> CREATOR = new Creator<Child>() {
        @Override
        public Child createFromParcel(Parcel in) {
            return new Child(in);
        }

        @Override
        public Child[] newArray(int size) {
            return new Child[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Title);
    }
}
