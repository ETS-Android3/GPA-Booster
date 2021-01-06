package com.example.maimyou.Classes;

public class DisplayCourse {
    String trimesterTitle;
    String Grade,Code,Subject,Hours,preRequest;
    String TotalHours;
    int colour,mode=0;

    public int getMode() {
        return mode;
    }

    public String getTrimesterTitle() {
        return trimesterTitle;
    }

    public String getGrade() {
        return Grade;
    }

    public String getCode() {
        return Code;
    }

    public String getSubject() {
        return Subject;
    }

    public String getHours() {
        return Hours;
    }

    public String getPreRequest() {
        return preRequest;
    }

    public String getTotalHours() {
        return TotalHours;
    }

    public int getColour() {
        return colour;
    }

    public DisplayCourse(String trimesterTitle, int colour) {
        this.trimesterTitle = trimesterTitle;
        this.colour = colour;
        this.mode=1;
    }

    public DisplayCourse(String grade, String code, String subject, String hours, String preRequest) {
        this.Grade = grade;
        this.Code = code;
        this.Subject = subject;
        this.Hours = hours;
        this.preRequest = preRequest;
        this.mode = 2;
    }

    public DisplayCourse(String totalHours) {
        this.TotalHours = totalHours;
        this.mode=3;
    }

    public DisplayCourse() {}
}
