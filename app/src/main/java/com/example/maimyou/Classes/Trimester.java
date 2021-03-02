package com.example.maimyou.Classes;

import java.util.ArrayList;

public class Trimester {
    double range = 50;
    public String semesterName = "";
    String gpa = "";
    String cgpa = "";
    String academicStatus = "";
    String hours = "";
    String hoursCore = "";
    String totalHours = "";
    String totalPoint = "";
    ArrayList<subjects> subjects = new ArrayList<>();

    public void setRange(double range) {
        this.range = range;
    }

    public ArrayList<Trimester.subjects> getSubjects() {
        return subjects;
    }

    public String getGradeFromCode(String code) {
        for (int i = 0; i < subjects.size(); i++) {
            if (isFound(subjects.get(i).subjectCodes.trim(), code.trim())) {
                return subjects.get(i).subjectGades.trim();
            }
        }
        return "";
    }

    public boolean isFound(String p, String hph) {
        return hph.contains(p);
    }

    public static class subjects {
        String subjectCodes, subjectNames, subjectGades;

        public String getSubjectCodes() {
            return subjectCodes;
        }

        public String getSubjectNames() {
            return subjectNames;
        }

        public String getSubjectGades() {
            return subjectGades;
        }

        public subjects(String subjectCode, String subjectName, String subjectGade) {
            this.subjectCodes = subjectCode;
            this.subjectNames = subjectName;
            this.subjectGades = subjectGade;
        }
    }

    public String getSemesterName() {
        return semesterName;
    }

    public String getGPA() {
        return gpa;
    }

    public String getCGPA() {
        return cgpa;
    }

    public String getAcademicStatus() {
        return academicStatus;
    }

    public String getHours() {
        return hours;
    }

    public String getTotalHours() {
        return totalHours;
    }

    public String getTotalPoint() {
        return totalPoint;
    }

    public int getSubjectSize() {
        return subjects.size();
    }

    public void addSubject(String subjectCode, String subjectName, String subjectGade) {
        subjects.add(new subjects(subjectCode, subjectName, subjectGade));
    }

    double minPoints = 0, avePoints = 0, maxPoints = 0, hoursCoreInt = 0, totalHoursInt = 0;

    public void addSubjectComputeGPA(String subjectCode, String subjectName, String subjectGrade, String subjectHours) {
        subjects.add(new subjects(subjectCode, subjectName, subjectGrade));
        if (!subjectGrade.toLowerCase().contains("con")) {
            totalHoursInt += getInt(subjectHours);
            if (!subjectCode.toLowerCase().contains("mpu") && !subjectName.toLowerCase().startsWith("mpu") && !subjectName.toLowerCase().contains("train") && !subjectName.toLowerCase().contains("management")) {
                hoursCoreInt += getInt(subjectHours);
                minPoints += getInt(subjectHours) * getMinCGPA(subjectGrade);
                avePoints += getInt(subjectHours) * getAveCGPA(subjectGrade);
                maxPoints += getInt(subjectHours) * getMaxCGPA(subjectGrade);
            }
        }
    }

    public double compTotalPoint() {
        return avePoints;
    }

    public double compMaxPoint() {
        return maxPoints;
    }

    public double compMinPoint() {
        return minPoints;
    }

    public double compTotalHours() {
        return totalHoursInt;
    }

    public double compTotalHoursCore() {
        return hoursCoreInt;
    }

    public void SetTrimesterTitle(String semesterName) {
        this.gpa = Double.toString(round(avePoints / hoursCoreInt));
        this.hours = Integer.toString((int) hoursCoreInt);
        this.hoursCore = Integer.toString((int) hoursCoreInt);
        this.academicStatus = "PASS";
        this.semesterName = semesterName;
    }

    public void setCGPA(String CGPA, String totalHours, String totalPoint) {
        this.cgpa = CGPA;
        this.totalHours = totalHours;
        this.totalPoint = totalPoint;
    }

    public Trimester() {
    }

    public Trimester(String semesterName, String GPA, String CGPA, String academicStatus, String hours, String totalHours, String totalPoint) {
        this.semesterName = semesterName;
        this.gpa = GPA;
        this.cgpa = CGPA;
        this.academicStatus = academicStatus;
        this.hours = hours;
        this.totalHours = totalHours;
        this.totalPoint = totalPoint;
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
        marks += (range / 100d) * 5d;

        if (marks >= 80) {
            gpa = 4;
        } else if (80 > marks && marks >= 50) {
            gpa = ((marks - 50) / 30) * 2 + 2;
        } else {
            gpa = 0;
        }
        return gpa;
    }

    public double getMaxCGPA(String grade) {
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
        marks += 5;
        if (marks >= 80) {
            gpa = 4;
        } else if (80 > marks && marks >= 50) {
            gpa = ((marks - 50) / 30) * 2 + 2;
        } else {
            gpa = 0;
        }
        return gpa;
    }


    public double getMinCGPA(String grade) {
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
        if (marks >= 80) {
            gpa = 4;
        } else if (80 > marks && marks >= 50) {
            gpa = ((marks - 50) / 30) * 2 + 2;
        } else {
            gpa = 0;
        }
        return gpa;
    }

    public int getInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception ignored) {
            return 0;
        }
    }

    public double round(double a) {
        return Math.round(a * 100.0) / 100.0;
    }
}
