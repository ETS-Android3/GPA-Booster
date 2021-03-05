package com.example.maimyou.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maimyou.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.maimyou.Activities.RegisterActivity.SHARED_PREFS;

public class PushReviewActivity extends AppCompatActivity {
    RatingBar ratingBar;
    EditText review;
    String SubjectCode;
    Context context = this;

    public void submit(View view) {
        if (ratingBar.getRating() > 0) {
            FirebaseDatabase.getInstance().getReference().child("Subjects").child(SubjectCode).child("rating").child(loadData("Id")).child("Rate").setValue(ratingBar.getRating());
            FirebaseDatabase.getInstance().getReference().child("Subjects").child(SubjectCode).child("rating").child(loadData("Id")).child("Time").setValue(System.currentTimeMillis());
            FirebaseDatabase.getInstance().getReference().child("Subjects").child(SubjectCode).child("rating").child(loadData("Id")).child("review").setValue(review.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(context, "Review has been added successfully", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        } else {
            Toast.makeText(this, "Please rate the subject", Toast.LENGTH_LONG).show();
        }
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_review);

        ratingBar = findViewById(R.id.ratingBar);
        review = findViewById(R.id.review);
        SubjectCode = getIntent().getStringExtra("SubjectCode");
        String Rate = getIntent().getStringExtra("Rate");
        String Review = getIntent().getStringExtra("Review");
        if(Rate!=null){
            ratingBar.setRating(getFloat(Rate));
        }
        if(Review!=null){
            review.setText(Review);
        }
        if (SubjectCode != null) {
            TextView TitleCode = findViewById(R.id.TitleCode);
            TitleCode.setText(SubjectCode);
        } else {
            finish();
        }
    }

    public float getFloat(String str){
        try{
            return Float.parseFloat(str);
        }catch (Exception ignored){
            return 0;
        }
    }

    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}