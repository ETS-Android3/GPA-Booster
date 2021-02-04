package com.example.maimyou.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maimyou.Activities.DashBoardActivity;
import com.example.maimyou.Adapters.AdapterDisplayCourse;
import com.example.maimyou.Adapters.AdapterDisplayCourseForEdit;
import com.example.maimyou.Classes.DisplayCourse;
import com.example.maimyou.Classes.DisplayCourseForEdit;
import com.example.maimyou.Classes.Helper;
import com.example.maimyou.Classes.Trimester;
import com.example.maimyou.R;
import com.example.maimyou.RecycleViewMaterials.Child;
import com.example.maimyou.RecycleViewMaterials.ChildAdapter;
import com.example.maimyou.RecycleViewMaterials.Parent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.maimyou.Activities.DashBoardActivity.Intake;
import static com.example.maimyou.Activities.DashBoardActivity.actionListener;

public class FragmentEdit extends Fragment {
    //Views
    ListView editGradeListView;

    //vars
    int FirstTrim;
    Context context;
    String id, Name = "", StudentId = "";
    ArrayList<Trimester> trimesters;
    DashBoardActivity dashBoardActivity;
    public boolean finishedLoading = false;
    boolean UserDataPrinted = false;

    public FragmentEdit(String id, Context context, DashBoardActivity dashBoardActivity) {
        this.id = id;
        this.context = context;
        this.dashBoardActivity = dashBoardActivity;
        downLoadData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() != null) {
            UserDataPrinted = false;
            editGradeListView = getView().findViewById(R.id.editGradeListView);
            actionListener.setOnActionPerformed(() -> viewCourse(Intake));
            RadioGroup radioGroup = getView().findViewById(R.id.radio);
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                RadioButton radioButton = getView().findViewById(checkedId);
                InflateRec(getView().findViewById(R.id.RecIntake), radioButton.getText().toString().toLowerCase());
            });
        }
    }


    public void viewCourse(String str) {
        FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").child(str).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Trimesters").exists()) {
                    ArrayList<DisplayCourseForEdit> editCourse = new ArrayList<>();
                    int firstTrim = getTrim(str);
                    FirstTrim = firstTrim;
                    for (DataSnapshot trimester : snapshot.child("Trimesters").getChildren()) {
                        editCourse.add(new DisplayCourseForEdit(getTitle(firstTrim), 0));
                        firstTrim++;
                        for (DataSnapshot subject : trimester.getChildren()) {
                            if (subject.child("Elective").exists() && subject.child("PreRequisite").exists() && subject.child("SubjectHours").exists() && subject.child("SubjectName").exists()) {
                                editCourse.add(new DisplayCourseForEdit("A", subject.getKey(), Objects.requireNonNull(subject.child("SubjectName").getValue()).toString(), Objects.requireNonNull(subject.child("SubjectHours").getValue()).toString()));
                            }
                        }
                    }
                    editCourse.add(new DisplayCourseForEdit());
                    editCourse.add(new DisplayCourseForEdit());
                    AdapterDisplayCourseForEdit adapter = new AdapterDisplayCourseForEdit(context, R.layout.edit_course, editCourse);
//                    adapter.setCourseStructure(courseStructure);
                    editGradeListView.setAdapter(adapter);
                    Helper.getListViewSize(editGradeListView);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String getTitle(int trimInt) {
        int year = ((trimInt - FirstTrim) / 3);
        year++;
        while (trimInt > 3) {
            trimInt -= 3;
        }
        return "Trimester " + trimInt + " - Year " + year;
    }

    public void InflateRec(RecyclerView recyclerView, String Major) {
        FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<Child> ChildTrim1 = new ArrayList<>();
                ArrayList<Child> ChildTrim2 = new ArrayList<>();
                ArrayList<Child> ChildTrim3 = new ArrayList<>();
                ArrayList<Parent> parent = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getKey() != null) {
                        if (child.getKey().toLowerCase().contains(Major)) {
                            int trim = getTrim(child.getKey().toLowerCase());
                            if (trim == 1) {
                                ChildTrim1.add(new Child(child.getKey()));
                            } else if (trim == 2) {
                                ChildTrim2.add(new Child(child.getKey()));
                            } else if (trim == 3) {
                                ChildTrim3.add(new Child(child.getKey()));
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
                    recyclerView.setAdapter(new ChildAdapter(parent, R.layout.recycle_parent_edit, R.layout.recycle_child_edit));
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
        return 1;
    }

    public void downLoadData() {
        if (!dashBoardActivity.isConnected()) {
            Toast.makeText(context, "No internet connection!", Toast.LENGTH_LONG).show();
            return;
        }

        FirebaseDatabase.getInstance().getReference().child("Member").child(id).child("Info").child("ModifiedInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.child("Name").exists()) {
//                    Name = Objects.requireNonNull(snapshot.child("Name").getValue()).toString();
//                }
//
//                if (snapshot.child("Id").exists()) {
//                    StudentId = Objects.requireNonNull(snapshot.child("Id").getValue()).toString();
//                }
//
////                    if (snapshot.child("Degree").getValue() != null) {
////                        Degree.setText(snapshot.child("Degree").getValue().toString());
////                    }
//                if (snapshot.child("Trimesters").exists()) {
//                    trimesters = new ArrayList<>();
//                    Iterable<DataSnapshot> children = snapshot.child("Trimesters").getChildren();
//                    for (DataSnapshot child : children) {
//                        if (child.getValue() != null) {
//                            trimesters.add(getTrim(child));
//                        }
//                    }
//
//                    printUserData();
                finishedLoading = true;
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

//    public void printUserData() {
//        if (!UserDataPrinted && getView() != null) {
//            UserDataPrinted = true;
//            userName.setText(Name);
//            userName2.setText(Name);
//            ID.setText(StudentId);
//            ID2.setText(StudentId);
//            if (trimesters.size() > 0) {
//
//                CGPA.setText(trimesters.get(trimesters.size() - 1).getCGPA());
//                TotalHours.setText(trimesters.get(trimesters.size() - 1).getTotalHours());
////                AdapterTrimester adapter = new AdapterTrimester(context, R.layout.trimester, trimesters);
////                gradlistView.setAdapter(adapter);
////                setListViewHeightBasedOnChildren(gradlistView
//            }
//            progressBar.setVisibility(View.GONE);
//        }
//    }
}