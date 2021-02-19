package com.example.maimyou.Fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.example.maimyou.Activities.DashBoardActivity;
import com.example.maimyou.Adapters.AdapterTrimester;
import com.example.maimyou.R;
import com.example.maimyou.Classes.Trimester;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.maimyou.Activities.DashBoardActivity.fragmentIndex;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentProfile extends Fragment {
    //vars
    String id, Name = "", StudentId = "";
    ArrayList<Trimester> trimesters;
    int y = -1;
    Context context;
    DashBoardActivity dashBoardActivity;
    boolean buttonOut = false, containerOut = false, UserDataPrinted = false, collapsed = false;

    //views
    ListView gradeListView;
    TextView TotalHours, CGPA, userName, userName2, ID, ID2;
    ImageView profilePictureAdmin, profilePictureAdmin2;
    ProgressBar progressBar;
    FloatingActionButton edit;
    NestedScrollView nestedScrollView;
    FrameLayout cardViewContainer;
    LinearLayout userInfo;
    CardView cardView;
    AppBarLayout appBar;

    public FragmentProfile(String id, Context context, DashBoardActivity dashBoardActivity) {
        this.id = id;
        this.context = context;
        this.dashBoardActivity = dashBoardActivity;
        downLoadData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        Fresco.initialize(context);
        fragmentIndex = 2;
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() != null) {
            UserDataPrinted = false;

            cardViewContainer = getView().findViewById(R.id.cardViewContainer);
            nestedScrollView = getView().findViewById(R.id.nestedScrollView);
            edit = getView().findViewById(R.id.edit);
            appBar = getView().findViewById(R.id.appBar);
            progressBar = getView().findViewById(R.id.progressBar);
            userName = getView().findViewById(R.id.userName);
            userName2 = getView().findViewById(R.id.userName2);
            CGPA = getView().findViewById(R.id.CGPA);
            TotalHours = getView().findViewById(R.id.TotalHours);
//            Name = getView().findViewById(R.id.Name);
            ID = getView().findViewById(R.id.ID);
            ID2 = getView().findViewById(R.id.ID2);
//            Degree = getView().findViewById(R.id.Degree);
            gradeListView = getView().findViewById(R.id.gradeListView);
            profilePictureAdmin = getView().findViewById(R.id.profilePictureAdmin);
            profilePictureAdmin2 = getView().findViewById(R.id.profilePictureAdmin2);
            userInfo = getView().findViewById(R.id.userInfo);
            cardView = getView().findViewById(R.id.cardView);
            fadeOutNoDelay(userInfo);
            fadeOutNoDelay(profilePictureAdmin2);

            collapsed = false;
            if (y > 0) {
                appBar.setExpanded(false, false);
                animationOutUP(cardViewContainer, 0);
                fadeIn(userInfo, 0);
                fadeIn(profilePictureAdmin2, 0);
                containerOut = true;
            }
            collapsed = true;
            buttonOut = false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                nestedScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (view1, i, i1, i2, i3) -> {

                    y = i1;

                    if (i1 > i3) {

                        if (!buttonOut) {
                            animationOutDOWN(edit);
                            buttonOut = true;
                        }
                    } else {

                        if (buttonOut) {
                            animationInUP(edit);
                            buttonOut = false;
                        }
                    }
                });
            }

            appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
                if (collapsed) {
                    if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                        //  Collapsed
                        if (!containerOut) {
                            animationOutUP(cardViewContainer, 500);
                            fadeIn(userInfo, 200);
                            fadeIn(profilePictureAdmin2, 200);
                            containerOut = true;
                        }
                    } else {
                        // Expanded
                        if (containerOut) {
                            fadeOut(userInfo);
                            fadeOut(profilePictureAdmin2);
                            returnAndFadeIn(cardViewContainer);
                            fadeIn(cardView, 500);
                            containerOut = false;
                        }
                    }
                }
            });


            if (!Name.isEmpty() && !StudentId.isEmpty()) {
                try {
                    printUserData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void downLoadData() {
        if (!dashBoardActivity.isConnected()) {
            Toast.makeText(context, "No internet connection!", Toast.LENGTH_LONG).show();
            return;
        }

        FirebaseDatabase.getInstance().getReference().child("Member").child(id).child("Profile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Name").getValue() != null) {
                    Name = Objects.requireNonNull(snapshot.child("Name").getValue()).toString();
                }

                if (snapshot.child("Id").getValue() != null) {
                    StudentId = Objects.requireNonNull(snapshot.child("Id").getValue()).toString();
                }

//                    if (snapshot.child("Degree").getValue() != null) {
//                        Degree.setText(snapshot.child("Degree").getValue().toString());
//                    }
                if (snapshot.child("Trimesters").getValue() != null) {
                    trimesters = new ArrayList<>();
                    Iterable<DataSnapshot> children = snapshot.child("Trimesters").getChildren();
                    for (DataSnapshot child : children) {
                        if (child.getValue() != null) {
                            trimesters.add(getTrim(child));
                        }
                    }

                    printUserData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void printUserData() {
        if (!UserDataPrinted && getView() != null) {
            UserDataPrinted = true;
            userName.setText(Name);
            userName2.setText(Name);
            ID.setText(StudentId);
            ID2.setText(StudentId);
            if (trimesters.size() > 0) {

                CGPA.setText(trimesters.get(trimesters.size() - 1).getCGPA());
                TotalHours.setText(trimesters.get(trimesters.size() - 1).getTotalHours());
                AdapterTrimester adapter = new AdapterTrimester(context, R.layout.trimester, trimesters);
                gradeListView.setAdapter(adapter);
                setListViewHeightBasedOnChildren(gradeListView);

            }
            progressBar.setVisibility(View.GONE);
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

    public Trimester getTrim(DataSnapshot dataSnapshot) {
        String semesterName = "", GPA = "", CGPA = "", academicStatus = "", hours = "", totalHours = "", totalPoint = "";
        if (dataSnapshot.child("semesterName").exists()) {
            semesterName = dataSnapshot.child("semesterName").getValue().toString();
        }
        if (dataSnapshot.child("gpa").exists()) {
            GPA = dataSnapshot.child("gpa").getValue().toString();
        }
        if (dataSnapshot.child("cgpa").exists()) {
            CGPA = dataSnapshot.child("cgpa").getValue().toString();
        }
        if (dataSnapshot.child("academicStatus").exists()) {
            academicStatus = dataSnapshot.child("academicStatus").getValue().toString();
        }
        if (dataSnapshot.child("hours").exists()) {
            hours = dataSnapshot.child("hours").getValue().toString();
        }
        if (dataSnapshot.child("totalHours").exists()) {
            totalHours = dataSnapshot.child("totalHours").getValue().toString();
        }
        if (dataSnapshot.child("totalPoint").exists()) {
            totalPoint = dataSnapshot.child("totalPoint").getValue().toString();
        }
        Trimester trimester = new Trimester(semesterName, GPA, CGPA, academicStatus, hours, totalHours, totalPoint);
        Iterable<DataSnapshot> subjectCodes = dataSnapshot.child("subjects").getChildren();
        for (DataSnapshot child : subjectCodes) {
            if (child.child("subjectCodes").getValue() != null && child.child("subjectNames").getValue() != null && child.child("subjectGades").getValue() != null) {
                trimester.addSubject(child.child("subjectCodes").getValue().toString(), child.child("subjectNames").getValue().toString(), child.child("subjectGades").getValue().toString());
            }
        }
        return trimester;
    }

    public void fadeIn(View view, int duration) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(duration);
        fadeIn.setFillAfter(true);
        view.startAnimation(fadeIn);
    }

    public void fadeOut(View view) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new DecelerateInterpolator()); //and this
        fadeOut.setDuration(200);
        fadeOut.setFillAfter(true);
        view.startAnimation(fadeOut);
    }

    public void fadeOutNoDelay(View view) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new DecelerateInterpolator()); //and this
        fadeOut.setDuration(0);
        fadeOut.setFillAfter(true);
        view.startAnimation(fadeOut);
    }

    public void returnAndFadeIn(View view) {
        Animation inFromBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromBottom.setDuration(0);
        inFromBottom.setInterpolator(new AccelerateInterpolator());
        inFromBottom.setFillAfter(true);
        view.startAnimation(inFromBottom);
    }

    public void animationInUP(View view) {
        Animation inFromBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromBottom.setDuration(500);
        inFromBottom.setInterpolator(new AccelerateInterpolator());
        inFromBottom.setFillAfter(true);
        view.startAnimation(inFromBottom);
    }

    private void animationOutDOWN(View view) {
        Animation outtoBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f);
        outtoBottom.setDuration(500);
        outtoBottom.setInterpolator(new AccelerateInterpolator());
        outtoBottom.setFillAfter(true);
        view.startAnimation(outtoBottom);
    }

    private void animationOutUP(View view, int duration) {
        Animation outtoBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f);
        outtoBottom.setDuration(duration);
        outtoBottom.setInterpolator(new AccelerateInterpolator());
        outtoBottom.setFillAfter(true);
        view.startAnimation(outtoBottom);
    }
}