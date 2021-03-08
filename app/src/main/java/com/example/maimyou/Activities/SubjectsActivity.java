package com.example.maimyou.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.maimyou.Adapters.SubjectReviewsAdapter;
import com.example.maimyou.Classes.SubjectReviews;
import com.example.maimyou.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.maimyou.Activities.RegisterActivity.SHARED_PREFS;

public class SubjectsActivity extends AppCompatActivity {

    //views
    FrameLayout SearchBarContainer;
    EditText inputSearch;
    ListView SubjectsList;
    ProgressBar progressBar;

    //vars
    boolean searchIsOpened = false;
    int width = 0, height = 0;
    Context context = this;
    SubjectsActivity subjectsActivity = this;
    ArrayList<SubjectReviews> subjects = new ArrayList<>();
    ArrayList<SubjectReviews> SearchSubjects = new ArrayList<>();

    public void search_bar(View view) {
        if (searchIsOpened) {
            closeSearch();
        } else {
            openSearch();
        }
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);

        initViews();
        ConnectToFirebase();
    }

    public void getSearch(String str) {
        SearchSubjects.clear();
        for (SubjectReviews subject : subjects) {
            if (subject.getSubjectCode().toString().toLowerCase().contains(str.toLowerCase()) || subject.getSubjectName().toString().toLowerCase().contains(str.toLowerCase())) {
                Spannable code = new SpannableString(subject.getSubjectCode().toString()), name = new SpannableString(subject.getSubjectName().toString());
                SearchSubjects.add(subject);
                int i = subject.getSubjectCode().toString().toLowerCase().indexOf(str.toLowerCase());
                int i2 = subject.getSubjectName().toString().toLowerCase().indexOf(str.toLowerCase());
                BackgroundColorSpan backgroundSpan = new BackgroundColorSpan(Color.YELLOW);
                if (subject.getSubjectCode().toString().toLowerCase().contains(str.toLowerCase()) && i >= 0) {
                    code.setSpan(backgroundSpan, i, i + str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                if (subject.getSubjectName().toString().toLowerCase().contains(str.toLowerCase()) && i2 >= 0) {
                    name.setSpan(backgroundSpan, i2, i2 + str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                SearchSubjects.get(SearchSubjects.indexOf(subject)).setSubjectSearch(code, name);
            }
        }
        printSubjects(SearchSubjects);
        if (SearchSubjects.size() == 0) {
            Toast.makeText(context, "Subject not found!", Toast.LENGTH_SHORT).show();
        }
    }

    public void printSubjects(ArrayList<SubjectReviews> subjects) {
        SubjectReviewsAdapter adapter = new SubjectReviewsAdapter(context, R.layout.subject_review, subjects);
        adapter.setSubjectsActivity(subjectsActivity);
        SubjectsList.setAdapter(adapter);
    }

    public void ConnectToFirebase() {
        subjects.clear();
        SearchSubjects.clear();
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot parentSnapshot) {
                DataSnapshot snapshot = parentSnapshot.child("Subjects");
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        SubjectReviews rev = new SubjectReviews(child.child("SubjectName").getValue(), child.child("Category").getValue(), child.getKey(), getRatings(child.child("rating")), Long.toString(child.child("Grades").getChildrenCount()), child.child("Grades").child(loadData("camsysId")).exists());
                        subjects.add(rev);
                        SearchSubjects.add(rev);
                    }
                    SubjectReviewsAdapter adapter = new SubjectReviewsAdapter(context, R.layout.subject_review, subjects);
                    adapter.setSubjectsActivity(subjectsActivity);
                    SubjectsList.setAdapter(adapter);
                } else {
                    Toast.makeText(context, "Please update Syllabus!", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public String getRatings(DataSnapshot ratings) {
        if (ratings.exists()) {
            double totalRate = 0, counter = 0;
            for (DataSnapshot child : ratings.getChildren()) {
                if (child.child("Rate").exists() && getDouble(Objects.requireNonNull(child.child("Rate").getValue()).toString()) > 0) {
                    totalRate += getDouble(Objects.requireNonNull(child.child("Rate").getValue()).toString());
                    counter++;
                }
            }
            if (totalRate <= 0 || counter <= 0) {
                return "0";
            }
            return Double.toString(round(totalRate / counter, 1));
        }
        return "0";
    }

    public double getDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception ignored) {
            return 0;
        }
    }

    public void itemClicked(String code) {
        Intent intent = new Intent(getBaseContext(), ViewSubjectActivity.class);
        intent.putExtra("SubjectCode", code);
        startActivity(intent);
    }

    public void initViews() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        SearchBarContainer = findViewById(R.id.SearchBarContainer);
        inputSearch = findViewById(R.id.inputSearch);
        SubjectsList = findViewById(R.id.SubjectsList);
        progressBar = findViewById(R.id.progressBar);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getSearch(inputSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                getSearch(inputSearch.getText().toString());
            }
        });
    }

    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }

    public void openSearch() {
        searchIsOpened = true;
        SearchBarContainer.setVisibility(View.VISIBLE);
        showSoftKeyboard(inputSearch);
        slideViewWidth(inputSearch, dpToPx(40), width - dpToPx(60), 500);
        inputSearch.setHint("Enter subject's code or name");
    }

    public void closeSearch() {
        searchIsOpened = false;
        hideKeyBoard(inputSearch);
        slideViewWidth(inputSearch, width - dpToPx(60), dpToPx(40), 500);
        Handler handler = new Handler();
        handler.postDelayed(() -> SearchBarContainer.setVisibility(View.INVISIBLE), 500);
        inputSearch.setHint("");
        inputSearch.setText("");
        inputSearch.setError(null);
        inputSearch.clearFocus();
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void hideKeyBoard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void slideViewWidth(final View view, int currentWidth, int newWidth, long duration) {

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentWidth, newWidth)
                .setDuration(duration);

        /* We use an update listener which listens to each tick
         * and manually updates the height of the view  */

        slideAnimator.addUpdateListener(animation1 -> {
            view.getLayoutParams().width = (Integer) animation1.getAnimatedValue();
            view.requestLayout();
        });

        /*  We use an animationSet to play the animation  */

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
    }

    public int dpToPx(int dip) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        return (int) px;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @Override
    public void onBackPressed() {
        if (searchIsOpened) {
            closeSearch();
        } else {
            finish();
            super.onBackPressed();
        }
    }
//
//    @Override
//    public void finish() {
//        super.finish();
//        overridePendingTransition(R.anim.slide0, R.anim.slide_in_top);
//    }
}