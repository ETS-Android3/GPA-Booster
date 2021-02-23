package com.example.maimyou.CarouselLayout;

public class Tip {
    private String name,des;
    private int imageSource;

    public Tip(int imageSource, String name,String des) {
        this.name = name;
        this.imageSource = imageSource;
        this.des=des;
    }

    public String getDes() {
        return des;
    }

    public String getName() {
        return name;
    }

    public int getImageSource() {
        return imageSource;
    }
}