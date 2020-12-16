package com.example.maimyou.Classes;

import java.util.ArrayList;

public class TrimesterCourse {
    ArrayList<String> subjectCodes = new ArrayList<>(), subjectNames = new ArrayList<>(), SubjectHours = new ArrayList<>(), trimName = new ArrayList<>();
    ArrayList<String> preRequest = new ArrayList<>();
    ArrayList<Boolean> elective = new ArrayList<>();

    void setVals(ArrayList<String> subjectCodes, ArrayList<String> subjectNames, ArrayList<String> subjectHours, ArrayList<String> trimName, ArrayList<String> preRequest, ArrayList<Boolean> elective) {
        this.subjectCodes = subjectCodes;
        this.subjectNames = subjectNames;
        this.SubjectHours = subjectHours;
        this.trimName = trimName;
        this.preRequest = preRequest;
        this.elective = elective;
    }

    public TrimesterCourse() {
    }

    public ArrayList<Boolean> getElective() {
        return elective;
    }

    public ArrayList<String> getPreRequest() {
        return preRequest;
    }

    public void addPreRequest(String code, String pre) {
        if (subjectCodes.contains(code)) {
            this.preRequest.set(subjectCodes.indexOf(code), pre);
        }
    }

    public ArrayList<String> getSubjectCodes() {
        return subjectCodes;
    }

    public ArrayList<String> getSubjectNames() {
        return subjectNames;
    }

    public ArrayList<String> getSubjectHours() {
        return SubjectHours;
    }

    public ArrayList<String> getTrimName() {
        return trimName;
    }


    public void sort() {
        ArrayList<String> subjectCodes = new ArrayList<>(), subjectNames = new ArrayList<>(), SubjectHours = new ArrayList<>(), trimName = new ArrayList<>();
        ArrayList<String> preRequest = new ArrayList<>();
        ArrayList<Boolean> elective = new ArrayList<>();
        for(int x=0;x<12;x++) {
            subjectCodes.add("");
            subjectNames.add("");
            SubjectHours.add("");
            trimName.add("Title");
            elective.add(false);
            preRequest.add("");
            for (int i = 0; i < this.trimName.size(); i++) {
                if(Integer.toString(x).compareTo(this.trimName.get(i))==0){
                    subjectCodes.add(this.subjectCodes.get(i));
                    subjectNames.add(this.subjectNames.get(i));
                    SubjectHours.add(this.SubjectHours.get(i));
                    trimName.add(this.trimName.get(i));
                    elective.add(this.elective.get(i));
                    preRequest.add(this.preRequest.get(i));
                }
            }
        }
        setVals(subjectCodes,subjectNames,SubjectHours,trimName,preRequest,elective);
    }

    public void addAll(String subjectCodes, String subjectNames, String subjectHours, String trim, boolean elective, String preRequest) {
        System.out.println(subjectCodes + "   " + subjectNames + "   " + subjectHours + "   " + trim + "   " + elective + "   " + preRequest);
        this.subjectCodes.add(subjectCodes);
        this.subjectNames.add(subjectNames);
        this.SubjectHours.add(subjectHours);
        this.trimName.add(trim);
        this.elective.add(elective);
        this.preRequest.add(preRequest);
    }

    public boolean addVal(String subjectCodes, String subjectNames, String subjectHours, String trim, boolean elective) {
        boolean valueAdded = (!this.subjectCodes.contains(subjectCodes) || subjectCodes.isEmpty());
        if (valueAdded) {
            this.subjectCodes.add(subjectCodes);
            this.subjectNames.add(subjectNames);
            this.SubjectHours.add(subjectHours);
            this.trimName.add(trim);
            this.elective.add(elective);
            this.preRequest.add("-");
        }
        return valueAdded;
    }


}
