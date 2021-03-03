package com.example.maimyou.Classes;

public class Gradedsubjects {
    String Code, Name, Grade;
    double gpa = 0;
    boolean end = false;

    public void setEnd(boolean end) {
        this.end = end;
    }

    public boolean isEnd() {
        return end;
    }

    public String getCode() {
        return Code;
    }

    public String getName() {
        return Name;
    }

    public String getGrade() {
        return Grade;
    }

    public double getGpa() {
        return gpa;
    }

    public Gradedsubjects(String code, String name, String grade) {
        Code = code;
        Name = name;
        Grade = grade;
        if (!code.toLowerCase().contains("mpu") && !name.toLowerCase().startsWith("mpu") && !name.toLowerCase().contains("train") && !name.toLowerCase().contains("management") && !grade.toLowerCase().contains("con")) {
            gpa = getGPA(grade);
        }
    }

    public double getGPA(String grade) {
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

        if (100 >= marks && marks >= 50) {
            gpa = ((marks - 50) / 30) * 2 + 2;
        } else {
            gpa = 0;
        }
        return gpa;
    }
}
