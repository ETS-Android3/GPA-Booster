package com.example.maimyou.Classes;

public class DisplayCourseForEdit {
    String trimesterTitle;
    String Code, Subject, Grade, Hours, elective,sem;
    int colour, mode = 0;

    public String getSem() {
        return sem;
    }

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

    public String getElective() {
        return elective;
    }

    public int getColour() {
        return colour;
    }

    public DisplayCourseForEdit(String trimesterTitle, int colour) {
        this.trimesterTitle = trimesterTitle;
        this.colour = colour;
        this.mode = 1;
    }

    public DisplayCourseForEdit(String grade, String code, String subject, String hours, String elective,String sem) {
//        if(isNumeric(code)){
//            this.Code ="";
//        }else {
        this.Code = code;
//        }
        this.Subject = subject;
        this.Hours = hours;
        this.Grade = grade;
        this.elective = elective;
        this.sem=sem;
        this.mode = 2;
    }

//    public boolean isNumeric(String str) {
//        try {
//            Integer.parseInt(str);
//            return true;
//        } catch (Exception ignored) {
//            return false;
//        }
//    }

    public DisplayCourseForEdit() {
    }
}