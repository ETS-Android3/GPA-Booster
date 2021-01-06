package com.example.maimyou.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.maimyou.Adapters.AdapterDisplayCourse;
import com.example.maimyou.Adapters.AdapterTrimesterCourse;
import com.example.maimyou.Classes.DisplayCourse;
import com.example.maimyou.R;
import com.example.maimyou.RecycleViewMaterials.Child;
import com.example.maimyou.RecycleViewMaterials.ChildAdapter;
import com.example.maimyou.RecycleViewMaterials.Parent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CourseStructure extends AppCompatActivity {

    //Views
    RecyclerView RecEE, RecCE, RecTE, RecEL, RecNA;
    RelativeLayout RelEE, RelCE, RelTE, RelEL, RelNA;
    ImageView ArrEE, ArrCE, ArrTE, ArrEL, ArrNA;
    ListView CourseStructureList;

    //Vars
    Context context = this;
    CourseStructure courseStructure = this;
    Boolean ExpandEE = false, ExpandCE = false, ExpandTE = false, ExpandEL = false, ExpandNA = false;

    public void Menu(View view){
        PopupMenu popup = new PopupMenu(CourseStructure.this, view);
        popup.getMenuInflater().inflate(R.menu.course_structure_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            Toast.makeText(CourseStructure.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
            return true;
        });
        popup.show();
    }

    public void EE(View view) {
        ExpandEE = ExpandView(ArrEE, RelEE, ExpandEE);
    }

    public void CE(View view) {
        ExpandCE = ExpandView(ArrCE, RelCE, ExpandCE);
    }

    public void TE(View view) {
        ExpandTE = ExpandView(ArrTE, RelTE, ExpandTE);
    }

    public void EL(View view) {
        ExpandEL = ExpandView(ArrEL, RelEL, ExpandEL);
    }

    public void NA(View view) {
        ExpandNA = ExpandView(ArrNA, RelNA, ExpandNA);
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_structure);

        ArrEE = findViewById(R.id.ArrEE);
        RelEE = findViewById(R.id.RelEE);
        RecEE = findViewById(R.id.RecEE);
        InflateRec(RecEE, "ee");

        ArrCE = findViewById(R.id.ArrCE);
        RelCE = findViewById(R.id.RelCE);
        RecCE = findViewById(R.id.RecCE);
        InflateRec(RecCE, "ce");

        ArrTE = findViewById(R.id.ArrTE);
        RelTE = findViewById(R.id.RelTE);
        RecTE = findViewById(R.id.RecTE);
        InflateRec(RecTE, "te");

        ArrEL = findViewById(R.id.ArrEL);
        RelEL = findViewById(R.id.RelEL);
        RecEL = findViewById(R.id.RecEL);
        InflateRec(RecEL, "le");

        ArrNA = findViewById(R.id.ArrNA);
        RelNA = findViewById(R.id.RelNA);
        RecNA = findViewById(R.id.RecNA);
        InflateRec(RecNA, "na");

        CourseStructureList = findViewById(R.id.CourseStructureList);
    }

    public void InflateRec(RecyclerView recyclerView, String Major) {
        ArrayList<Child> ChildTrim1 = new ArrayList<>();
        ArrayList<Child> ChildTrim2 = new ArrayList<>();
        ArrayList<Child> ChildTrim3 = new ArrayList<>();
        ArrayList<Parent> parent = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getKey() != null) {
                        if (child.getKey().toLowerCase().contains(Major)) {
                            int trim = getTrim(child.getKey().toLowerCase());
                            if (trim == 1) {
                                ChildTrim1.add(new Child(child.getKey(), courseStructure));
                            } else if (trim == 2) {
                                ChildTrim2.add(new Child(child.getKey(), courseStructure));
                            } else if (trim == 3) {
                                ChildTrim3.add(new Child(child.getKey(), courseStructure));
                            }
                        }
                    }
                }
                if (ChildTrim1.size() > 0) {
                    parent.add(new Parent("Trimester 1 (june)", ChildTrim1));
                }
                if (ChildTrim2.size() > 0) {
                    parent.add(new Parent("Trimester 2 (october-november)", ChildTrim2));
                }
                if (ChildTrim3.size() > 0) {
                    parent.add(new Parent("Trimester 3 (february-march)", ChildTrim3));
                }
                if (parent.size() > 0) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new ChildAdapter(parent));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public int getTrim(String Course) {
        if (Course.contains("jun")) {
            return 1;
        } else if (Course.contains("oct") || Course.contains("nov")) {
            return 2;
        } else if (Course.contains("feb") || Course.contains("mar")) {
            return 3;
        }
        return 0;
    }

    public void viewCourse(String str) {
        FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(str).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Trimesters").exists()) {
                    ArrayList<DisplayCourse> displayCourses = new ArrayList<>();
                    for (DataSnapshot trimester : snapshot.child("Trimesters").getChildren()) {
                        displayCourses.add(new DisplayCourse(getTitle(trimester.getKey()), 0));
                        for (DataSnapshot subject : trimester.getChildren()) {
                            if (subject.child("Elective").exists() && subject.child("PreRequisite").exists() && subject.child("SubjectHours").exists() && subject.child("SubjectName").exists()) {
                                displayCourses.add(new DisplayCourse("A",subject.getKey(),subject.child("SubjectName").getValue().toString(),subject.child("SubjectHours").getValue().toString(),subject.child("PreRequisite").getValue().toString()));
                            }
                        }
                        if (trimester.child("TotalHours").exists()) {
                            displayCourses.add(new DisplayCourse(trimester.child("TotalHours").getValue().toString()));
                        }
                    }
                    displayCourses.add(new DisplayCourse());
                    displayCourses.add(new DisplayCourse());
                    AdapterDisplayCourse adapter = new AdapterDisplayCourse(context, R.layout.display_course, displayCourses);
                    adapter.setCourseStructure(courseStructure);
                    CourseStructureList.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String getTitle(String trim) {
        if (isNumeric(trim)) {
            int trimInt = Integer.parseInt(trim);
            trimInt++;

            while (trimInt > 3) {
                trimInt -= 3;
            }
            return "Trimester " + trimInt;
        }
        return "Trimester ?";
    }

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public boolean ExpandView(ImageView imageView, RelativeLayout relativeLayout, boolean expanded) {
        if (!expanded) {
            expand(relativeLayout, dpToPx(50), 300);
            rotate(imageView, true);
            return true;
        } else {
            slideView(relativeLayout, relativeLayout.getHeight(), dpToPx(50), 300);
            rotate(imageView, false);
            return false;
        }
    }

    public void rotate(View view, boolean up) {
        RotateAnimation rotate;
        if (up) {
            rotate = new RotateAnimation(360, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        } else {
            rotate = new RotateAnimation(180, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        }
        rotate.setDuration(300);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setFillAfter(true);
        view.startAnimation(rotate);
    }

    public void expand(final View view, int startHeight, int duration) {
        view.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();

        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);

        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(), targetHeight);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(duration);
        anim.addUpdateListener(animation -> {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = (int) ((targetHeight - startHeight) * animation.getAnimatedFraction()) + startHeight;
            view.setLayoutParams(layoutParams);
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // At the end of animation, set the height to wrap content
                // This fix is for long views that are not shown on screen
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
        });
        anim.start();
    }

    public void slideView(final View view, int currentHeight, int newHeight, long duration) {

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentHeight, newHeight)
                .setDuration(duration);

        slideAnimator.addUpdateListener(animation1 -> {
            view.getLayoutParams().height = (Integer) animation1.getAnimatedValue();
            view.requestLayout();
        });

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
        animationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {


            }
        });
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
        super.onBackPressed();
    }
}