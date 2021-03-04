package com.example.maimyou.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.maimyou.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewSubjectActivity extends AppCompatActivity {

    //views
    TextView subName, subCode, subCat, stars, stars2, reviews, reviews2, users, GPA, GRADE;
    ProgressBar Prog1, Prog2, Prog3, Prog4, Prog5;
    CircleImageView profilePicture;
    ListView reviewsList;
    RatingBar ratingBar;
    public void back(View view){
        onBackPressed();
    }
    public void addReview(View view) {

    }

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_subject);

        Init();
        ConnectToFirebase();
    }
    public void ConnectToFirebase(){
        String SubjectCode = getIntent().getStringExtra("SubjectCode");
        TextView TitleCode = findViewById(R.id.TitleCode);
        if (SubjectCode != null) {
            TitleCode.setText(SubjectCode);
            FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot parentSnapshot) {
                    DataSnapshot snapshot = parentSnapshot.child("Subjects").child(SubjectCode);
                    if (snapshot.exists()) {

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
    }

    @Override
    public void onBackPressed() {
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
