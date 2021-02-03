package com.example.maimyou.Classes;

public class SubjectReviews {
    String SubjectName, SubjectCode, SubjectRate, SubjectUsers,Category;
    boolean isFinished;

    public String getSubjectName() {
        return SubjectName;
    }

    public String getSubjectCode() {
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

    public SubjectReviews(Object subjectName,Object category, String subjectCode, String subjectRate, String subjectUsers, boolean isFinished) {
        if(subjectName!=null) {
            this.SubjectName = subjectName.toString().trim();
        }else{
            this.SubjectName = "";
        }
        if(category!=null) {
            this.Category = category.toString().trim();
        }else{
            this.Category = "";
        }
        this.SubjectCode = subjectCode;
        this.SubjectRate = subjectRate;
        this.SubjectUsers = subjectUsers;
        this.isFinished = isFinished;
    }
}
