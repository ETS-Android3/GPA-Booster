package com.example.maimyou.Classes;

import java.util.ArrayList;

public class reviews {
    ArrayList<String> Id = new ArrayList<>(), Rate = new ArrayList<>(), Time = new ArrayList<>(), Review = new ArrayList<>();
    ArrayList<reviewArray> revArr = new ArrayList<>();
    long star1 = 0, star2 = 0, star3 = 0, star4 = 0, star5 = 0, rateCounter = 0;
    float totalRate = 0;

    public ArrayList<reviewArray> getRevArr() {
        return revArr;
    }

    public class reviewArray {
        String Id, Rate, Time, Review, ProfilePic, Name;

        public String getName() {
            return Name;
        }

        public String getProfilePic() {
            return ProfilePic;
        }

        public String getId() {
            return Id;
        }

        public float getRate() {
            return getFloat(Rate);
        }

        public String getTime() {
            return Time;
        }

        public String getReview() {
            return Review;
        }

        public reviewArray(String id, String rate, String time, String review, String profilePic, String name) {
            Id = id;
            Rate = rate;
            Time = time;
            Review = review;
            ProfilePic = profilePic;
            Name = name;
        }
    }


    public reviews() {
    }

    public double getProg1() {
        if ((star1 + star2 + star3 + star4 + star5) > 0) {
            return ((double) star1) / ((double) (star1 + star2 + star3 + star4 + star5)) * 100;
        } else {
            return 0;
        }
    }

    public double getProg2() {
        if ((star1 + star2 + star3 + star4 + star5) > 0) {
            return ((double) star2) / ((double) (star1 + star2 + star3 + star4 + star5)) * 100;
        } else {
            return 0;
        }
    }

    public double getProg3() {
        if ((star1 + star2 + star3 + star4 + star5) > 0) {
            return ((double) star3) / ((double) (star1 + star2 + star3 + star4 + star5)) * 100;
        } else {
            return 0;
        }
    }

    public double getProg4() {
        if ((star1 + star2 + star3 + star4 + star5) > 0) {
            return ((double) star4) / ((double) (star1 + star2 + star3 + star4 + star5)) * 100;
        } else {
            return 0;
        }
    }

    public double getProg5() {
        if ((star1 + star2 + star3 + star4 + star5) > 0) {
            return ((double) star5) / ((double) (star1 + star2 + star3 + star4 + star5)) * 100;
        } else {
            return 0;
        }
    }

    public String getTotalRate() {
        return Double.toString(round(totalRate / rateCounter));
    }

    public float getTotalRateFloat() {
        return (float) round(totalRate / rateCounter);
    }


    public Long getRevNum() {
        return rateCounter;
    }

    public void clear() {
        Id.clear();
        Rate.clear();
        Time.clear();
        Review.clear();
        revArr.clear();
        star1 = 0;
        star2 = 0;
        star3 = 0;
        star4 = 0;
        star5 = 0;
        totalRate = 0;
        rateCounter = 0;
    }

    public void addReview(String id, String rate, String time, String review, Object profilePic, Object Name) {
        Id.add(id);

        if (getFloat(rate) > 0) {
            Rate.add(rate);
            totalRate += getFloat(rate);
            rateCounter++;
        } else {
            Rate.add("");
        }

        if (((int) getFloat(rate)) == 1) {
            star1++;
        } else if (((int) getFloat(rate)) == 2) {
            star2++;
        } else if (((int) getFloat(rate)) == 3) {
            star3++;
        } else if (((int) getFloat(rate)) == 4) {
            star4++;
        } else if (((int) getFloat(rate)) == 5) {
            star5++;
        }

        Time.add(time);
        Review.add(review);

        String pic;
        if (profilePic != null) {
            pic = profilePic.toString();
        } else {
            pic = "";
        }
        String name;
        if (Name != null) {
            name = Name.toString();
        } else {
            name = "";
        }

        revArr.add(new reviewArray(Id.get(Id.size() - 1), Rate.get(Rate.size() - 1), Time.get(Time.size() - 1), Review.get(Review.size() - 1), pic, name));
    }

    public float getFloat(String str) {
        try {
            return Float.parseFloat(str);
        } catch (Exception ignored) {
            return 0;
        }
    }

    public double round(double a) {
        return Math.round(a * 10.0) / 10.0;
    }

}
