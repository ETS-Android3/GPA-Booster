package com.example.maimyou.Classes;

public class Marks {
    private String Title;
    private String First;
    private String second;
    private String third;
    private String forth;
    private String fifth;
    private String sixth;


    public Marks(String Title, String First, String second, String third, String forth, String fifth, String sixth) {
        this.Title = Title;
        this.First = First;
        this.second = second;
        this.third = third;
        this.forth = forth;
        this.fifth = fifth;
        this.sixth = sixth;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getFirst() {
        return First;
    }

    public void setFirst(String first) {
        First = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public String getThird() {
        return third;
    }

    public void setThird(String third) {
        this.third = third;
    }

    public String getForth() {
        return forth;
    }

    public void setForth(String forth) {
        this.forth = forth;
    }

    public String getFifth() {
        return fifth;
    }

    public void setFifth(String fifth) {
        this.fifth = fifth;
    }

    public String getSixth() {
        return sixth;
    }

    public void setSixth(String sixth) {
        this.sixth = sixth;
    }
}
