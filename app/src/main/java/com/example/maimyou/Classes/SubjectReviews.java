package com.example.maimyou.Classes;

public class SubjectReviews {
    String SubjectName, SubjectCode, SubjectRate, SubjectUsers;
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

    public boolean isFinished() {
        return isFinished;
    }

    public SubjectReviews(String subjectName, String subjectCode, String subjectRate, String subjectUsers, boolean isFinished) {
        SubjectName = subjectName;
        SubjectCode = subjectCode;
        SubjectRate = subjectRate;
        SubjectUsers = subjectUsers;
        this.isFinished = isFinished;
    }
}
