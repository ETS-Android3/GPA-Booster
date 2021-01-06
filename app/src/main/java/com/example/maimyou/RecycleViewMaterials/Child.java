package com.example.maimyou.RecycleViewMaterials;

import android.os.Parcel;
import android.os.Parcelable;
import com.example.maimyou.Activities.CourseStructure;

public class Child implements Parcelable {
    public final String Title;
    CourseStructure courseStructure;

    public void viewCourse(String str){
        if(courseStructure!=null){
            courseStructure.viewCourse(str.trim());
        }
    }

    public Child(String title, CourseStructure courseStructure) {
        this.Title = title;
        this.courseStructure = courseStructure;
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
