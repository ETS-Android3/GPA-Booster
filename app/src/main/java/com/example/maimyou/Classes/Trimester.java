package com.example.maimyou.Classes;

import java.util.ArrayList;

public class Trimester {
    public String semesterName = "";
    String gpa = "";
    String cgpa = "";
    String academicStatus = "";
    String hours = "";
    String totalHours = "";
    String totalPoint = "";
    ArrayList<subjects> subjects = new ArrayList<>();

    public ArrayList<Trimester.subjects> getSubjects() {
        return subjects;
    }


    public String getGradeFromCode(String code){
        for(int i=0;i<subjects.size();i++){
            if(isFound(subjects.get(i).subjectCodes.trim(),code.trim())){
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
        int height=0;

        public int getHeight() {
            return height;
        }

        public subjects setHeight(int height) {
            this.height = height;
            return this;
        }

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


    public void addSubject(String subjectCode, String subjectName, String subjectGade) {
        subjects.add(new subjects(subjectCode, subjectName, subjectGade));
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
}
