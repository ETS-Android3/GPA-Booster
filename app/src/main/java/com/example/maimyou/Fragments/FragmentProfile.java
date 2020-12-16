package com.example.maimyou.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.maimyou.Adapters.AdapterTrimester;
import com.example.maimyou.R;
import com.example.maimyou.Classes.Trimester;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentProfile extends Fragment{
    String id = "";
    Context context;


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

        }
    }

//        if (getView() != null) {
//
//            userName = getView().findViewById(R.id.userName);
//            CGPA = getView().findViewById(R.id.CGPA);
//            TotalHours = getView().findViewById(R.id.TotalHours);
//            Name = getView().findViewById(R.id.Name);
//            ID = getView().findViewById(R.id.ID);
//            Degree = getView().findViewById(R.id.Degree);
//            gradlistView = getView().findViewById(R.id.gradlistView);
//
//            FirebaseDatabase.getInstance().getReference().child("Member").child(id).child("CamsysInfo").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (snapshot.child("Name").getValue() != null) {
//                        NameS = snapshot.child("Name").getValue().toString();
//                        userName.setText(NameS);
//                        Name.setText(NameS);
//                    }
//
//                    if (snapshot.child("Id").getValue() != null) {
//                        Id = snapshot.child("Id").getValue().toString();
//                        ID.setText(Id);
//                    }
//
//                    if (snapshot.child("Degree").getValue() != null) {
//                        DegreeS = snapshot.child("Degree").getValue().toString();
//                        Degree.setText(DegreeS);
//                    }
//                    if (snapshot.child("Trimesters").getValue() != null) {
//                        trimesters = new ArrayList<>();
//                        Iterable<DataSnapshot> children = snapshot.child("Trimesters").getChildren();
//                        for (DataSnapshot child : children) {
//                            if (child.getValue() != null) {
//                                trimesters.add(getTrim(child));
//                            }
//                        }
//                        if (trimesters.size() > 0) {
//
//                            CGPA.setText(trimesters.get(trimesters.size() - 1).getCGPA());
//                            TotalHours.setText(trimesters.get(trimesters.size() - 1).getTotalHours());
//                            AdapterTrimester adapter = new AdapterTrimester(context, R.layout.trimester, trimesters);
//                            gradlistView.setAdapter(adapter);
//
//                            setListViewHeightBasedOnChildren(gradlistView);
//
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//        }

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
            if (child.child("subjectCodes").getValue() != null&&child.child("subjectNames").getValue() != null&&child.child("subjectGades").getValue() != null) {
                trimester.addSubject(child.child("subjectCodes").getValue().toString(),child.child("subjectNames").getValue().toString(),child.child("subjectGades").getValue().toString());
            }
        }
        return trimester;
    }
}