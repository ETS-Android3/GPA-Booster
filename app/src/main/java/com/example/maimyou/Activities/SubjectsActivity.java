package com.example.maimyou.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.example.maimyou.Adapters.SubjectReviewsAdapter;
import com.example.maimyou.Classes.SubjectReviews;
import com.example.maimyou.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import static com.example.maimyou.Activities.RegisterActivity.SHARED_PREFS;

public class SubjectsActivity extends AppCompatActivity {

    //views
    FrameLayout SearchBarContainer;
    EditText inputSearch;
    ListView SubjectsList;

    //vars
    boolean searchIsOpened = false;
    int width = 0, height = 0;
    Context context = this;
    ArrayList<SubjectReviews> subjects = new ArrayList<>();

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
        showSubjects();
    }

    public void showSubjects() {
        subjects.clear();
        FirebaseDatabase.getInstance().getReference().child("Subjects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        subjects.add(new SubjectReviews(child.child("SubjectName").getValue(), child.getKey(), getRatings(), Long.toString(child.child("Grades").getChildrenCount()), child.child("Grades").child(loadData("Id")).exists()));
                    }
                    SubjectReviewsAdapter adapter = new SubjectReviewsAdapter(context, R.layout.subject_review, subjects);
                    SubjectsList.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public String getRatings() {
        return "4.2";
    }

    public void initViews() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        SearchBarContainer = findViewById(R.id.SearchBarContainer);
        inputSearch = findViewById(R.id.inputSearch);
        SubjectsList = findViewById(R.id.SubjectsList);
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

    @Override
    public void onBackPressed() {
        if (searchIsOpened) {
            closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide0, R.anim.slide_in_top);
    }
}