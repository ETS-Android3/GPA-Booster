package com.example.maimyou.Classes;

import android.text.Spannable;
import android.text.SpannableString;

public class SubjectReviews {
    Spannable SubjectName, SubjectCode;
    String SubjectRate, SubjectUsers, Category;
    boolean isFinished;

    public Spannable getSubjectName() {
        return SubjectName;
    }

    public Spannable getSubjectCode() {
        return SubjectCode;
    }

    public String getSubjectRate() {
        return SubjectRate;
    }

    public String getSubjectUsers() {
        return SubjectUsers;
    }

    public String getCategory() {
        return Category;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setSubjectSearch(Spannable subjectName, Spannable subjectCode) {
        SubjectName = subjectName;
        SubjectCode = subjectCode;
    }

    public SubjectReviews(Object subjectName, Object category, String subjectCode, String subjectRate, String subjectUsers, boolean isFinished) {
        if (subjectName != null) {
            this.SubjectName = new SpannableString(subjectName.toString().trim());
        } else {
            this.SubjectName = new SpannableString("");
        }
        if (category != null) {
            this.Category = category.toString().trim();
        } else {
            this.Category = "";
        }
        this.SubjectCode = new SpannableString(subjectCode);
        this.SubjectRate = subjectRate;
        this.SubjectUsers = subjectUsers;
        this.isFinished = isFinished;
    }
}
