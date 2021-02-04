package com.example.maimyou.Classes;

public class DisplayCourseForEdit {
    String trimesterTitle;
    String Code, Subject, Grade, Hours;
    int colour, mode = 0;

    public int getMode() {
        return mode;
    }

    public String getTrimesterTitle() {
        return trimesterTitle;
    }

    public String getCode() {
        return Code;
    }

    public String getSubject() {
        return Subject;
    }

    public String getGrade() {
        return Grade;
    }

    public String getHours() {
        return Hours;
    }

    public int getColour() {
        return colour;
    }

    public DisplayCourseForEdit(String trimesterTitle, int colour) {
        this.trimesterTitle = trimesterTitle;
        this.colour = colour;
        this.mode = 1;
    }

    public DisplayCourseForEdit(String grade, String code, String subject, String hours) {
        this.Code = code;
        this.Subject = subject;
        this.Hours = hours;
        this.Grade = grade;
        this.mode = 2;
    }

    public DisplayCourseForEdit() {}
}