package com.example.maimyou.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.maimyou.Adapters.ReviewsAdapter;
import com.example.maimyou.Classes.MyJavaScriptInterface;
import com.example.maimyou.Classes.UriUtils;
import com.example.maimyou.Classes.reviews;
import com.example.maimyou.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import im.delight.android.webview.AdvancedWebView;

import static com.example.maimyou.Activities.RegisterActivity.SHARED_PREFS;
import static com.example.maimyou.Fragments.FragmentCamsys.webView;

public class ViewSubjectActivity extends AppCompatActivity implements AdvancedWebView.Listener{

    //views
    TextView subName, subCode, subCat, stars, stars2, reviews, reviews2, users, GPA, GRADE;
    ProgressBar Prog1, Prog2, Prog3, Prog4, Prog5;
    CircleImageView profilePicture;
    AdvancedWebView webView;
    ListView reviewsList;
    RatingBar ratingBar;

    //vars
    String SubjectCode;
    DataSnapshot parent;
    reviews reviewsHolder = new reviews();
    Context context = this;

    public void back(View view) {
        onBackPressed();
    }

    public void addReview(View view) {
        Intent intent = new Intent(getBaseContext(), PushReviewActivity.class);
        intent.putExtra("SubjectCode", SubjectCode);
        if (parent != null) {
            if (parent.child("Subjects").child(SubjectCode).child("rating").child(loadData("Id")).exists()) {
                if (parent.child("Subjects").child(SubjectCode).child("rating").child(loadData("Id")).child("Rate").exists()) {
                    intent.putExtra("Rate", Objects.requireNonNull(parent.child("Subjects").child(SubjectCode).child("rating").child(loadData("Id")).child("Rate").getValue()).toString());
                }
                if (parent.child("Subjects").child(SubjectCode).child("rating").child(loadData("Id")).child("review").exists()) {
                    intent.putExtra("Review", Objects.requireNonNull(parent.child("Subjects").child(SubjectCode).child("rating").child(loadData("Id")).child("review").getValue()).toString());
                }
            }
        }
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_subject);

        Init();
        ConnectToFirebase();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        ConnectToFirebase();
        if (webView != null) {

            webView.onResume();
        }
    }

    public class review {
        String id, Time, Rate, Review;

        public String getRate() {
            return Rate;
        }

        public String getReview() {
            return Review;
        }

        public String getId() {
            return id;
        }

        public String getTime() {
            return Time;
        }

        public review(String id, Object time, Object rate, Object review) {
            this.id = id;
            if (time != null) {
                Time = time.toString();
            } else {
                Time = "0";
            }

            if (rate != null) {
                Rate = rate.toString();
            } else {
                Rate = "0";
            }

            if (review != null) {
                Review = review.toString();
            } else {
                Review = "";
            }
        }
    }

    public void ConnectToFirebase() {
        SubjectCode = getIntent().getStringExtra("SubjectCode");
        TextView TitleCode = findViewById(R.id.TitleCode);
        if (SubjectCode != null) {
            TitleCode.setText(SubjectCode);
            subCode.setText(SubjectCode);
            FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot parentSnapshot) {
                    parent = parentSnapshot;
                    if (parentSnapshot.child("Member").child(loadData("Id")).child("Profile").child("PersonalImage").exists()) {
                        Picasso.get().load(Objects.requireNonNull(parentSnapshot.child("Member").child(loadData("Id")).child("Profile").child("PersonalImage").getValue()).toString()).error(R.drawable.avatar).into(profilePicture);
                    }
                    DataSnapshot snapshot = parentSnapshot.child("Subjects").child(SubjectCode);
                    if (snapshot.exists()) {
                        if (snapshot.child("SubjectName").exists()) {
                            subName.setText(Objects.requireNonNull(snapshot.child("SubjectName").getValue()).toString());
                        }
                        if (snapshot.child("Category").exists()) {
                            subCat.setText(Objects.requireNonNull(snapshot.child("Category").getValue()).toString());
                        }
                        if (snapshot.child("SubjectLink").exists()) {
                            webView.loadUrl(Objects.requireNonNull(snapshot.child("SubjectLink").getValue()).toString());
                        }
                        reviewsHolder.clear();
                        ArrayList<review> revHolders = new ArrayList<>();
                        if (snapshot.child("rating").exists()) {
                            for (DataSnapshot child : snapshot.child("rating").getChildren()) {
                                if (child.getKey() != null) {
                                    revHolders.add(new review(child.getKey(), child.child("Time").getValue(), child.child("Rate").getValue(), child.child("review").getValue()));
                                }
                            }
                        }

                        Collections.sort(revHolders, (o1, o2) -> (int) ((getLong(o2.getTime()) - getLong(o1.getTime()))));

                        for (review revHolder : revHolders) {
                            reviewsHolder.addReview(revHolder.getId(), revHolder.getRate(), revHolder.getTime(), revHolder.getReview(), parentSnapshot.child("Member").child(revHolder.getId()).child("Profile").child("PersonalImage").getValue(), getName(parentSnapshot, revHolder.getId()));
                        }
                        double gpa = 0;
                        long counter = 0;
                        if (snapshot.child("Grades").exists()) {
                            for (DataSnapshot child : snapshot.child("Grades").getChildren()) {
                                if (getGPA(child.getValue()) > 0) {
                                    gpa += getGPA(child.getValue());
                                    counter++;
                                }
                            }
                        }
                        String gpaString = Double.toString(round(gpa / ((double) counter)));
                        GPA.setText(gpaString);
                        GRADE.setText(getGrade(gpa / ((double) counter)));
                        ratingBar.setRating(reviewsHolder.getTotalRateFloat());
                        ReviewsAdapter adapter = new ReviewsAdapter(context, R.layout.review, reviewsHolder.getRevArr());
                        reviewsList.setAdapter(adapter);
                        setListViewHeightBasedOnChildren(reviewsList);
                        stars.setText(reviewsHolder.getTotalRate());
                        stars2.setText(reviewsHolder.getTotalRate());
                        reviews.setText(compressNum(reviewsHolder.getRevNum()) + " reviews");
                        reviews2.setText(Long.toString(reviewsHolder.getRevNum()));
                        if (snapshot.child("Grades").exists()) {
                            users.setText(compressNum(snapshot.child("Grades").getChildrenCount()));
                        }
                        Prog1.setProgress((int) reviewsHolder.getProg1());
                        Prog2.setProgress((int) reviewsHolder.getProg2());
                        Prog3.setProgress((int) reviewsHolder.getProg3());
                        Prog4.setProgress((int) reviewsHolder.getProg4());
                        Prog5.setProgress((int) reviewsHolder.getProg5());

                    } else {
                        Toast.makeText(getApplicationContext(), "Subject was not found!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Subject was not found!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public String getGrade(double gpa) {
        int marks = (int) (((gpa - 2d) / 2d) * 30d + 50d);
        if (marks >= 80) {
            return "A";
        } else if (marks >= 75) {
            return "A-";
        } else if (marks >= 70) {
            return "B+";
        } else if (marks >= 65) {
            return "B";
        } else if (marks >= 60) {
            return "B-";
        } else if (marks >= 55) {
            return "C+";
        } else if (marks >= 50) {
            return "C";
        } else {
            return "-";
        }
    }

    public Long getLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public double round(double a) {
        return Math.round(a * 10.0) / 10.0;
    }

    public double getGPA(Object object) {
        if (object != null) {
            String grade = object.toString();
            if (!grade.trim().toLowerCase().contains("con") && grade.trim().length() <= 2) {
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
                marks += 2.5;

                if (marks >= 80) {
                    gpa = 4;
                } else if (80 > marks && marks >= 50) {
                    gpa = ((marks - 50) / 30) * 2 + 2;
                } else {
                    gpa = 0;
                }
                return gpa;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);

            if (listItem != null) {
                // This next line is needed before you call measure or else you won't get measured height at all. The listitem needs to be drawn first to know the height.
                listItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();

            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public String getName(DataSnapshot snapshot, String id) {
        if (snapshot.child("Member").child(id).child("Profile").child("Name").exists()) {
            return Objects.requireNonNull(snapshot.child("Member").child(id).child("Profile").child("Name").getValue()).toString();
        } else {
            try {
                return Objects.requireNonNull(snapshot.child("Member").child(id).child("camsysId").getValue()).toString();
            } catch (Exception ignored) {
                return "";
            }
        }
    }

    public String compressNum(long num) {
        String str = Long.toString(num);
        ArrayList<String> names = new ArrayList<>();
        names.add("");
        names.add("K");
        names.add("M");
        names.add("B");
        int index = 0;
        while (str.length() > 3) {
            str = str.substring(0, str.length() - 3);
            index++;
        }
        if (index >= names.size()) {
            index = names.size() - 1;
        }
        return str + names.get(index);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void Init() {
        subName = findViewById(R.id.subName);
        subCode = findViewById(R.id.subCode);
        subCat = findViewById(R.id.subCat);
        stars = findViewById(R.id.stars);
        stars2 = findViewById(R.id.stars2);
        reviews = findViewById(R.id.reviews);
        reviews2 = findViewById(R.id.reviews2);
        users = findViewById(R.id.users);
        GPA = findViewById(R.id.GPA);
        GRADE = findViewById(R.id.GRADE);
        Prog1 = findViewById(R.id.Prog1);
        Prog2 = findViewById(R.id.Prog2);
        Prog3 = findViewById(R.id.Prog3);
        Prog4 = findViewById(R.id.Prog4);
        Prog5 = findViewById(R.id.Prog5);
        profilePicture = findViewById(R.id.profilePicture);
        reviewsList = findViewById(R.id.reviewsList);
        ratingBar = findViewById(R.id.ratingBar);
        webView = findViewById(R.id.webView);
        webView.setListener(this, this);
        webView.setMixedContentAllowed(true);
        webView.setThirdPartyCookiesEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings webSettings = webView.getSettings();
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setBuiltInZoomControls(true);
//        webSettings.setDomStorageEnabled(true);
    }


    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (webView != null) {
            webView.onActivityResult(requestCode, resultCode, data);
        }
    }




    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        if (webView != null) {

            webView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {

            webView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
    }

    @Override
    public void onPageFinished(String url) {
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
        Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onExternalPageRequest(String url) {
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }


    //    @Override
//    public void onBackPressed() {
//        if (searchIsOpened) {
//            closeSearch();
//        } else {
//            finish();
//            super.onBackPressed();
//        }
//    }
}
