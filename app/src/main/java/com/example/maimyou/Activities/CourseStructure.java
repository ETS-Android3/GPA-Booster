package com.example.maimyou.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.maimyou.R;
import com.example.maimyou.RecycleViewMaterials.Child;
import com.example.maimyou.RecycleViewMaterials.ChildAdapter;
import com.example.maimyou.RecycleViewMaterials.Parent;

import java.util.ArrayList;

public class CourseStructure extends AppCompatActivity {

    //Views
    RecyclerView RecEE, RecCE, RecTE, RecEL, RecNA;
    RelativeLayout RelEE, RelCE, RelTE, RelEL, RelNA;
    ImageView ArrEE, ArrCE, ArrTE, ArrEL, ArrNA;

    //Vars
    Boolean ExpandEE = false, ExpandCE = false, ExpandTE = false, ExpandEL = false, ExpandNA = false;


    public void EE(View view) {
        ExpandEE = ExpandView(ArrEE,RelEE,ExpandEE);
    }

    public void CE(View view) {
        ExpandCE = ExpandView(ArrCE,RelCE,ExpandCE);
    }

    public void TE(View view) {
        ExpandTE = ExpandView(ArrTE,RelTE,ExpandTE);
    }

    public void EL(View view) {
        ExpandEL = ExpandView(ArrEL,RelEL,ExpandEL);
    }

    public void NA(View view) {
        ExpandNA = ExpandView(ArrNA,RelNA,ExpandNA);
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
        InflateRec(RecEE);

        ArrCE = findViewById(R.id.ArrCE);
        RelCE = findViewById(R.id.RelCE);
        RecCE = findViewById(R.id.RecCE);
        InflateRec(RecCE);

        ArrTE = findViewById(R.id.ArrTE);
        RelTE = findViewById(R.id.RelTE);
        RecTE = findViewById(R.id.RecTE);
        InflateRec(RecTE);

        ArrEL = findViewById(R.id.ArrEL);
        RelEL = findViewById(R.id.RelEL);
        RecEL = findViewById(R.id.RecEL);
        InflateRec(RecEL);

        ArrNA = findViewById(R.id.ArrNA);
        RelNA = findViewById(R.id.RelNA);
        RecNA = findViewById(R.id.RecNA);
        InflateRec(RecNA);
    }

    public void InflateRec(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ChildAdapter(addTrimesters(new ArrayList<>())));
    }

    public ArrayList<Parent> addTrimesters(ArrayList<Parent> parent) {
        parent.add(new Parent("Trimester 1", addCourses(new ArrayList<>(), "")));
        parent.add(new Parent("Trimester 2", addCourses(new ArrayList<>(), "")));
        parent.add(new Parent("Trimester 3", addCourses(new ArrayList<>(), "")));
        return parent;
    }

    public ArrayList<Child> addCourses(ArrayList<Child> child, String major) {
        child.add(new Child("course 1"));
        child.add(new Child("course 2"));
        child.add(new Child("course 3"));
        child.add(new Child("course 4"));
        child.add(new Child("course 5"));
        return child;
    }

    public boolean ExpandView(ImageView imageView,RelativeLayout relativeLayout,boolean expanded){
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

    public static void expand(final View view, int startHeight, int duration) {
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