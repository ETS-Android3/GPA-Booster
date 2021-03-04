package com.example.maimyou.Classes;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DisplayCourse {
    String trimesterTitle;
    String Grade, Code, Subject, Hours, preRequest, Elective;
    String TotalHours;
    int colour, mode = 0;

    public String getElective() {
        return Elective;
    }

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
        this.mode = 1;
    }

    public DisplayCourse(String elective, String grade, String code, String subject, String hours, String preRequest, Trimester trim, ArrayList<String> codes, double totalHours, ArrayList<String> codesC, ArrayList<String> namesC) {
        this.Elective = elective;
        this.Grade = grade;
        this.Code = code;
        this.Subject = subject;
        this.Hours = hours;
        this.preRequest = stylePreRequest(preRequest, trim, codes, totalHours, codesC, namesC);
        this.mode = 2;
    }

    public String stylePreRequest(String input, Trimester trim, ArrayList<String> codes, double totalHours, ArrayList<String> codesC, ArrayList<String> namesC) {
        try {
            String green = "#006400", red = "#8b0000";
            StringBuilder outPut = new StringBuilder();
            String[] arr = input.split(",", -1);
            for (String str : arr) {
                str = str.trim();
                if (codes.contains(str)) {
                    if (getAveCGPA(trim.getSubjects().get(codes.indexOf(str)).getSubjectGades()) > 0) {
                        outPut.append("<font color='").append(green).append("'>").append(trim.getSubjects().get(codes.indexOf(str)).getSubjectCodes()).append(" ").append(trim.getSubjects().get(codes.indexOf(str)).getSubjectNames()).append("</font>").append("<br>");
                    } else {
                        outPut.append("<font color='").append(red).append("'>").append(trim.getSubjects().get(codes.indexOf(str)).getSubjectCodes()).append(" ").append(trim.getSubjects().get(codes.indexOf(str)).getSubjectNames()).append("</font>").append("<br>");
                    }
                } else {
                    if (str.toLowerCase().contains("credit hours")) {
                        if (hoursRequired(str) <= totalHours) {
                            outPut.append("<font color='").append(green).append("'>").append(str).append("</font>").append("<br>");
                        } else {
                            outPut.append("<font color='").append(red).append("'>").append(str).append("</font>").append("<br>");
                        }
                    } else if (codesC.contains(str)) {
                        outPut.append("<font color='").append(red).append("'>").append(codesC.get(codesC.indexOf(str))).append(" ").append(namesC.get(codesC.indexOf(str))).append("</font>").append("<br>");
                    } else {
                        outPut.append("<font color='").append(red).append("'>").append(str).append("</font>").append("<br>");
                    }
                }
            }
            if (outPut.toString().isEmpty()) {
                return input;
            }
            if (outPut.toString().endsWith("<br>")) {
                return outPut.substring(0, outPut.toString().length() - 4);
            }
            return outPut.toString();
        } catch (
                Exception ignored) {
            return input;
        }

    }

    public double hoursRequired(String str) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return getDouble(m.group().trim());
        } else {
            return 3000;
        }
    }

    public double getDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception ignored) {
            return 3000;
        }
    }

    public String getSubjectName(DataSnapshot parentSnapshot, String code) {
        if (parentSnapshot.child("Subjects").child(code).child("SubjectName").exists()) {
            try {
                return Objects.requireNonNull(parentSnapshot.child("Subjects").child(code).child("SubjectName").getValue()).toString();
            } catch (Exception ignored) {
                return "";
            }
        }
        return "";
    }

    public DisplayCourse(String totalHours) {
        this.TotalHours = totalHours;
        this.mode = 3;
    }

    public double getAveCGPA(String grade) {
        double marks = 0, gpa;
        if (grade.length() < 3) {
            if (grade.toLowerCase().contains("a")) {
                marks = 80;
            } else if (grade.toLowerCase().contains("b")) {
                marks = 65;
            } else if (grade.toLowerCase().contains("c")) {
                marks = 50;
            } else {
                marks = 0;
            }
        }
        if (grade.toLowerCase().contains("+")) {
            marks += 5;
        } else if (grade.toLowerCase().contains("-")) {
            marks -= 5;
        }
        marks += 2.5d;

        if (marks >= 80) {
            gpa = 4;
        } else if (80 > marks && marks >= 50) {
            gpa = ((marks - 50) / 30) * 2 + 2;
        } else {
            gpa = 0;
        }
        return gpa;
    }

    public DisplayCourse() {
    }
}
