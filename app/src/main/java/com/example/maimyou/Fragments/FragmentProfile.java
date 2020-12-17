package com.example.maimyou.Fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import com.example.maimyou.Adapters.AdapterTrimester;
import com.example.maimyou.R;
import com.example.maimyou.Classes.Trimester;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentProfile extends Fragment {
    String id = "";
    Context context;
    boolean buttonOut = false,containerOut=false;

    ListView gradlistView;
    TextView TotalHours, CGPA, userName, ID;
    ArrayList<Trimester> trimesters;
    ProgressBar progressBar;
    FloatingActionButton edit;
    NestedScrollView nestedScrollView;
    FrameLayout cardViewContainer;

    public FragmentProfile() {
    }


    public FragmentProfile setContext(Context context) {
        this.context = context;
        return this;
    }

    public FragmentProfile setId(String id) {
        this.id = id;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Fresco.initialize(context);
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() != null) {


            Animation animFadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_inn);
            Animation animFadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
            cardViewContainer = getView().findViewById(R.id.cardViewContainer);
            nestedScrollView = getView().findViewById(R.id.nestedScrollView);
            edit = getView().findViewById(R.id.edit);
            progressBar = getView().findViewById(R.id.progressBar);
            userName = getView().findViewById(R.id.userName);
            CGPA = getView().findViewById(R.id.CGPA);
            TotalHours = getView().findViewById(R.id.TotalHours);
//            Name = getView().findViewById(R.id.Name);
            ID = getView().findViewById(R.id.ID);
//            Degree = getView().findViewById(R.id.Degree);
            gradlistView = getView().findViewById(R.id.gradlistView);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                nestedScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (view1, i, i1, i2, i3) -> {
                    if (i1 > i3) {
                        if(!containerOut) {
                            animationOutUP(cardViewContainer);
                            containerOut=true;
                        }
                        if (!buttonOut) {
                            animationOutDOWN(edit);
                            buttonOut = true;
                        }
                    } else {
                        if (i1 == 0) {
                            returnAndFadeIn(cardViewContainer);
                            containerOut=false;
                        }
                        if (buttonOut) {
                            animationInUP(edit);
                            buttonOut = false;
                        }
                    }
//                    System.out.println(i1);
                });
            }

            FirebaseDatabase.getInstance().getReference().child("Member").child(id).child("CamsysInfo").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("Name").getValue() != null) {
                        userName.setText(snapshot.child("Name").getValue().toString());
//                        Name.setText(snapshot.child("Name").getValue().toString());
                    }

                    if (snapshot.child("Id").getValue() != null) {
                        ID.setText(snapshot.child("Id").getValue().toString());
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
                        if (trimesters.size() > 0) {

                            CGPA.setText(trimesters.get(trimesters.size() - 1).getCGPA());
                            TotalHours.setText(trimesters.get(trimesters.size() - 1).getTotalHours());
                            AdapterTrimester adapter = new AdapterTrimester(context, R.layout.trimester, trimesters);
                            gradlistView.setAdapter(adapter);

                            setListViewHeightBasedOnChildren(gradlistView);

                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
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
        if (dataSnapshot.child("semesterName").getValue() != null) {
            semesterName = dataSnapshot.child("semesterName").getValue().toString();
        }
        if (dataSnapshot.child("gpa").getValue() != null) {
            GPA = dataSnapshot.child("gpa").getValue().toString();
        }
        if (dataSnapshot.child("cgpa").getValue() != null) {
            CGPA = dataSnapshot.child("cgpa").getValue().toString();
        }
        if (dataSnapshot.child("academicStatus").getValue() != null) {
            academicStatus = dataSnapshot.child("academicStatus").getValue().toString();
        }
        if (dataSnapshot.child("hours").getValue() != null) {
            hours = dataSnapshot.child("hours").getValue().toString();
        }
        if (dataSnapshot.child("totalHours").getValue() != null) {
            totalHours = dataSnapshot.child("totalHours").getValue().toString();
        }
        if (dataSnapshot.child("totalPoint").getValue() != null) {
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

    private void animationOutUP(View view) {
        Animation outtoBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f);
        outtoBottom.setDuration(500);
        outtoBottom.setInterpolator(new AccelerateInterpolator());
        outtoBottom.setFillAfter(true);
        view.startAnimation(outtoBottom);
    }
}