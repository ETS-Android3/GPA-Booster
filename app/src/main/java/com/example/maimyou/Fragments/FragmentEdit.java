package com.example.maimyou.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maimyou.Activities.DashBoardActivity;
import com.example.maimyou.Adapters.AdapterDisplayCourseForEdit;
import com.example.maimyou.CarouselLayout.Tip;
import com.example.maimyou.Classes.DisplayCourseForEdit;
import com.example.maimyou.Classes.Helper;
import com.example.maimyou.Classes.Trimester;
import com.example.maimyou.Classes.subjects;
import com.example.maimyou.Dialogs.BottomSheetDialog;
import com.example.maimyou.Dialogs.TipContainerDialog;
import com.example.maimyou.R;
import com.example.maimyou.RecycleViewMaterials.Child;
import com.example.maimyou.RecycleViewMaterials.ChildAdapter;
import com.example.maimyou.RecycleViewMaterials.Parent;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.maimyou.Activities.DashBoardActivity.InfoAvail;
import static com.example.maimyou.Activities.DashBoardActivity.Intake;
import static com.example.maimyou.Activities.DashBoardActivity.actionListener;
import static com.example.maimyou.Activities.DashBoardActivity.fragmentIndex;

public class FragmentEdit extends Fragment {
    //Views
    ListView editGradeListView;
    TextView userNameT, NameT, IDT, CamsysIdT, CamsysPassT, CGPAT, totalHoursT, CGPAA, TotalHoursTitle;
    public ProgressBar progressBar;
    LinearLayout Title;
    //    TextView NameTextView, CamsysIdTextView, CamsysPassTextView, CGPATextView, totalHoursTextView;
    RadioGroup radioGroup;
    BottomSheetDialog bottomSheet;
    AppBarLayout appBar;
    public CircleImageView profilePictureAdmin;
    TipContainerDialog cdd;
//    PopupWindow mypopupWindow;


    //vars
    int FirstTrim;
    double range = 50;
    String imageUri = "";
    Context context;
    String id, Name = "", CamsysId = "", CamsysPass = "", CGPAText = "", totalHours = "", intake = "", MaxCGPA = "", MinCGPA = "", CurCGPA = "";
    ArrayList<Trimester> trimesters = new ArrayList<>();
    DashBoardActivity dashBoardActivity;
    FragmentEdit fragmentEdit = this;
    public boolean finishedLoading, containerOut = false;
    boolean UserDataPrinted = false;
    ArrayList<String> Codes = new ArrayList<>();
    ArrayList<String> Grades = new ArrayList<>();
    ArrayList<String> Hours = new ArrayList<>();
    ArrayList<String> Names = new ArrayList<>();
    ArrayList<Integer> Trims = new ArrayList<>();
    public ArrayList<subjects> subjects = new ArrayList<>();

    public FragmentEdit(String id, Context context, DashBoardActivity dashBoardActivity) {
        this.id = id;
        this.context = context;
        this.dashBoardActivity = dashBoardActivity;
        finishedLoading = false;
        downLoadData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentIndex = 1;
        return inflater.inflate(R.layout.fragment_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() != null) {
            UserDataPrinted = false;
//            NameTextView = getView().findViewById(R.id.Name);
//            CamsysIdTextView = getView().findViewById(R.id.CamsysId);
//            CamsysPassTextView = getView().findViewById(R.id.CamsysPass);
//            CGPATextView = getView().findViewById(R.id.CGPA);
//            totalHoursTextView = getView().findViewById(R.id.totalHours);

            Codes = new ArrayList<>();
            Grades = new ArrayList<>();
            Hours = new ArrayList<>();
            Names = new ArrayList<>();
            Trims = new ArrayList<>();
            CircleImageView arrow = getView().findViewById(R.id.arrow);
            fadeOutNoDelay(arrow);
            getView().findViewById(R.id.lightBulb).setOnClickListener(v -> OpenTip());
            cdd = new TipContainerDialog(dashBoardActivity);
            ArrayList<Tip> tips = new ArrayList<>();
            tips.add(new Tip(R.drawable.man1, "", "You can set your profile from here if you are bared or for other reasons"));
            tips.add(new Tip(R.drawable.man2, "", "Set your id and name, the picture and Camsys password are optional"));
            tips.add(new Tip(R.drawable.man3, "", "GPA and Hours will be calculated automatically after adding subjects"));
            tips.add(new Tip(R.drawable.man4, "", "After adding subjects your average calculated CGPA will show here with new core hours"));
            tips.add(new Tip(R.drawable.man5, "", "This is the maximum & minimum Calculated CGPA and you can change your CGPA in that range"));
            tips.add(new Tip(R.drawable.man6, "", "After you are done press save"));
            tips.add(new Tip(R.drawable.man7, "", "Thank you for downloading the app"));
            cdd.setTips(tips);
            cdd.setString("aut");
            FirebaseDatabase.getInstance().getReference().child("Member").child(dashBoardActivity.loadData("Id")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.child("DoNotShowUploadTip").exists()) {
                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(() -> {
                            OpenTip();
                            fadeIn(arrow, 200);
                            handler.postDelayed(() -> fadeOut(arrow, 400), 2000);

                        }, 1000);

                    } else {
                        cdd.setCheckBox(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            cdd.setCanceledOnTouchOutside(true);
            profilePictureAdmin = getView().findViewById(R.id.profilePictureAdmin);
            Title = getView().findViewById(R.id.Title);
            CGPAA = getView().findViewById(R.id.CGPAA);
            TotalHoursTitle = getView().findViewById(R.id.TotalHoursTitle);
            progressBar = getView().findViewById(R.id.progressBar);
            userNameT = getView().findViewById(R.id.userName);
            NameT = getView().findViewById(R.id.Name);
            IDT = getView().findViewById(R.id.ID);
            CamsysIdT = getView().findViewById(R.id.CamsysId);
            CamsysPassT = getView().findViewById(R.id.CamsysPass);
            CGPAT = getView().findViewById(R.id.CGPA);
            totalHoursT = getView().findViewById(R.id.totalHours);
            appBar = getView().findViewById(R.id.appBar);
            //                mypopupWindow.showAsDropDown(v, -153, 0);
            getView().findViewById(R.id.menuButton).setOnClickListener(this::openPopUpWindow);

            fadeOutNoDelay(Title);

            getView().findViewById(R.id.nameB).setOnClickListener(v -> createBottomSheet(NameT.getText().toString().trim(), "Enter your name", "ModifiedInfo/Name", ""));
            getView().findViewById(R.id.editCamsysId).setOnClickListener(v -> createBottomSheet(IDT.getText().toString().trim(), "Enter your Id", "ModifiedInfo/Id", "camsysId"));
            getView().findViewById(R.id.editCamsysPass).setOnClickListener(v -> createBottomSheet(CamsysPassT.getText().toString().trim(), "Enter your Camsys password", "camsysPassword", ""));
            getView().findViewById(R.id.editCGPA).setOnClickListener(v -> createBottomSheet(CGPAT.getText().toString().trim(), "Enter your CGPA", "", ""));
            getView().findViewById(R.id.editCGPAB).setOnClickListener(v -> createBottomSheet(CGPAT.getText().toString().trim(), "Enter your CGPA", "", ""));
            appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
                if ((Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange()) == 0) {
                    //  Collapsed
                    if (!containerOut) {
                        fadeIn(Title, 200);
                        containerOut = true;
                    }
                } else {
                    // Expanded
                    if (containerOut) {
                        fadeOut(Title, 200);
                        containerOut = false;
                    }
                }
            });

            if (finishedLoading) {
                progressBar.setVisibility(View.GONE);
            }

            ImageButton backB = getView().findViewById(R.id.backB);
            if (!InfoAvail) {
                backB.setVisibility(View.GONE);
            }
            FirebaseDatabase.getInstance().getReference().child("Member").child(id).child("Profile").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        backB.setVisibility(View.GONE);
                    } else {
                        backB.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
//            try {
//                setPopUpWindow();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            editGradeListView = getView().findViewById(R.id.editGradeListView);
            actionListener.setOnActionPerformed(() -> {
                intake = Intake;
                clear();
                FirebaseDatabase.getInstance().getReference().child("Member").child(id).child("ModifiedInfo").child("Intake").setValue(getIntake(Intake));
            });
            radioGroup = getView().findViewById(R.id.radio);
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                RadioButton radioButton = getView().findViewById(checkedId);
                InflateRec(getView().findViewById(R.id.RecIntake), radioButton.getText().toString().toLowerCase());
                clear();
                setFirebaseDegree(radioButton.getText().toString().toLowerCase());
            });
            if (finishedLoading) {
                printData();
            }
        }
    }

    public void OpenTip() {
        cdd.show();
    }


    public void openPopUpWindow(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.getMenuInflater().inflate(R.menu.edit_option_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getTitle().toString().toLowerCase().contains("delete")) {
                FirebaseDatabase.getInstance().getReference().child("Member").child(id).child("Profile").removeValue();
                FirebaseDatabase.getInstance().getReference().child("Member").child(id).child("ModifiedInfo").removeValue();
                dashBoardActivity.delete();
            } else if (item.getTitle().toString().toLowerCase().contains("reset")) {
                FirebaseDatabase.getInstance().getReference().child("Member").child(id).child("CamsysInfo").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        FirebaseDatabase.getInstance().getReference().child("Member").child(id).child("Profile").setValue(snapshot.getValue()).addOnCompleteListener(task -> {
                            Toast.makeText(context, "Your profile has been reset successfully", Toast.LENGTH_LONG).show();
                            dashBoardActivity.openProfile();
                            dashBoardActivity.SetSubjectsReview(dashBoardActivity.loadData("camsysId"));
                        }).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show());
                        FirebaseDatabase.getInstance().getReference().child("Member").child(id).child("ModifiedInfo").setValue(snapshot.getValue());
                        FirebaseDatabase.getInstance().getReference().child("Member").child(id).child("ModifiedInfo").child("UpdatedFrom").setValue("Modified");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            return true;

        });
        popup.show();


//            mypopupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
    }


    public void setRadioGroup(String major) {
        if (major.toLowerCase().contains("ce")) {
            radioGroup.check(R.id.radioComputer);
        } else if (major.toLowerCase().contains("ee")) {
            radioGroup.check(R.id.radioElectronics);
        } else if (major.toLowerCase().contains("le")) {
            radioGroup.check(R.id.radioElectrical);
        } else if (major.toLowerCase().contains("te")) {
            radioGroup.check(R.id.radioTelecom);
        } else if (major.toLowerCase().contains("nano")) {
            radioGroup.check(R.id.radioNano);
        }
    }

    public void setFirebaseDegree(String major) {
        String degree = "";
        if (major.toLowerCase().contains("ce")) {
            degree = "Bachelor of Engineering (Honours) Electronics majoring in Computer";
        } else if (major.toLowerCase().contains("ee")) {
            degree = "Bachelor of Engineering (Honours) Electronics majoring in Electronics";
        } else if (major.toLowerCase().contains("le")) {
            degree = "Bachelor of Engineering (Honours) Electronics majoring in Electrical";
        } else if (major.toLowerCase().contains("te")) {
            degree = "Bachelor of Engineering (Honours) Electronics majoring in Telecommunications";
        } else if (major.toLowerCase().contains("nano")) {
            degree = "Bachelor of Engineering (Honours) Electronics majoring in Nanotechnology";
        }
        if (!degree.isEmpty()) {
            FirebaseDatabase.getInstance().getReference().child("Member").child(id).child("ModifiedInfo").child("Degree").setValue(degree);
        }
    }

    public void clear() {
        Codes = new ArrayList<>();
        Grades = new ArrayList<>();
        Hours = new ArrayList<>();
        Names = new ArrayList<>();
        Trims = new ArrayList<>();
    }

    public void viewCourse(String str) {
        if (Codes.size() == 0) {
            FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot parentSnapshot) {
                    DataSnapshot snapshot = parentSnapshot.child("UNDERGRADUATE PROGRAMMES").child(str);
                    if (snapshot.child("Trimesters").exists()) {
                        ArrayList<DisplayCourseForEdit> editCourse = new ArrayList<>();
                        int firstTrim = getTrim(str);
                        FirstTrim = firstTrim;
                        int trimInt = 0;
                        Codes = new ArrayList<>();
                        Grades = new ArrayList<>();
                        Hours = new ArrayList<>();
                        Trims = new ArrayList<>();
                        Names = new ArrayList<>();
                        for (DataSnapshot trimester : snapshot.child("Trimesters").getChildren()) {
                            editCourse.add(new DisplayCourseForEdit(getTitle(firstTrim), 0));
                            firstTrim++;
                            for (DataSnapshot subject : trimester.getChildren()) {
                                if (subject.child("Elective").exists() && subject.child("PreRequisite").exists() && subject.child("SubjectHours").exists() && subject.child("SubjectName").exists()) {
                                    if (trimesters.size() > 0) {
                                        int index = 0;
                                        String Grade = "";
                                        for (Trimester trim : trimesters) {
                                            String temp = trim.getGradeFromCode(subject.getKey());
                                            if (!temp.isEmpty()) {
                                                Grade = temp;
                                                index = trimesters.indexOf(trim);
                                            }
                                        }
                                        if (Grade.trim().isEmpty()) {
                                            Grade = "-";
                                        }
                                        if (!Codes.contains(subject.getKey()) && index <= trimInt) {
                                            editCourse.add(new DisplayCourseForEdit(Grade, subject.getKey(), Objects.requireNonNull(subject.child("SubjectName").getValue()).toString(), Objects.requireNonNull(subject.child("SubjectHours").getValue()).toString(), Objects.requireNonNull(subject.child("Elective").getValue()).toString(), trimInt + ""));
                                            Codes.add(subject.getKey());
                                            Names.add(Objects.requireNonNull(subject.child("SubjectName").getValue()).toString());
                                            Grades.add(Grade);
                                            Trims.add(trimInt);
                                            Hours.add(Objects.requireNonNull(subject.child("SubjectHours").getValue()).toString());
                                        }
                                    } else {
                                        editCourse.add(new DisplayCourseForEdit("-", subject.getKey(), Objects.requireNonNull(subject.child("SubjectName").getValue()).toString(), Objects.requireNonNull(subject.child("SubjectHours").getValue()).toString(), Objects.requireNonNull(subject.child("Elective").getValue()).toString(), trimInt + ""));
                                        Codes.add(subject.getKey());
                                        Names.add(Objects.requireNonNull(subject.child("SubjectName").getValue()).toString());
                                        Trims.add(trimInt);
                                        Grades.add("-");
                                        Hours.add(Objects.requireNonNull(subject.child("SubjectHours").getValue()).toString());
                                    }
                                }
                            }

                            if (trimInt < trimesters.size() && trimesters.size() > 0) {
                                for (Trimester.subjects subject : trimesters.get(trimInt).getSubjects()) {
                                    int index = 0;
                                    for (Trimester trim : trimesters) {
                                        String temp = trim.getGradeFromCode(subject.getSubjectCodes());
                                        if (!temp.isEmpty()) {
                                            index = trimesters.indexOf(trim);
                                        }
                                    }
                                    if (!Codes.contains(subject.getSubjectCodes()) && index <= trimInt) {
                                        if (parentSnapshot.child("Subjects").child(subject.getSubjectCodes()).exists()) {
                                            editCourse.add(new DisplayCourseForEdit(subject.getSubjectGades(), subject.getSubjectCodes(), subject.getSubjectNames(), Objects.requireNonNull(parentSnapshot.child("Subjects").child(subject.getSubjectCodes()).child("SubjectHours").getValue()).toString(), "false", trimInt + ""));
                                            Hours.add(Objects.requireNonNull(parentSnapshot.child("Subjects").child(subject.getSubjectCodes()).child("SubjectHours").getValue()).toString());
                                        } else {
                                            editCourse.add(new DisplayCourseForEdit(subject.getSubjectGades(), subject.getSubjectCodes(), subject.getSubjectNames(), "3", "false", trimInt + ""));
                                            Hours.add("3");
                                        }
                                        Codes.add(subject.getSubjectCodes());
                                        Trims.add(trimInt);
                                        Names.add(subject.getSubjectNames());
                                        Grades.add(subject.getSubjectGades());
                                    }
                                }
                            }
                            trimInt++;
                        }
                        editCourse.add(new DisplayCourseForEdit());
                        editCourse.add(new DisplayCourseForEdit());
                        AdapterDisplayCourseForEdit adapter = new AdapterDisplayCourseForEdit(context, R.layout.edit_course, editCourse);
                        adapter.setFragmentEdit(fragmentEdit);
                        editGradeListView.setAdapter(adapter);
                        Helper.getListViewSize(editGradeListView);
                        saveTrim();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void setGrade(String code, String grade, String hours, String sem, String name) {
        if (Grades.get(Codes.indexOf(code)).compareTo(grade) != 0) {
            if (!Codes.contains(code)) {
                Codes.add(code);
                Names.add(name);
                Trims.add(getInt(sem));
                Grades.add(grade);
                Hours.add(hours);
            } else {
                Grades.set(Codes.indexOf(code), grade);
                Codes.set(Codes.indexOf(code), code);
                Names.set(Codes.indexOf(code), name);
                Trims.set(Codes.indexOf(code), getInt(sem));
                Hours.set(Codes.indexOf(code), hours);
            }
            saveTrim();
            FirebaseDatabase.getInstance().getReference().child("Member").child(id).child("ModifiedInfo").child("Trimesters").removeValue();
            FirebaseDatabase.getInstance().getReference().child("Member").child(id).child("ModifiedInfo").child("Trimesters").setValue(this.trimesters);
        }
    }

    public void createBottomSheet(String content, String title, String val, String val2) {
        bottomSheet = new BottomSheetDialog();
        bottomSheet.setFragmentEdit(fragmentEdit);
        bottomSheet.setId(id);
        bottomSheet.setValString(content);
        bottomSheet.setTitle(title);
        bottomSheet.setDataName(val);
        bottomSheet.setDataName2(val2);
        bottomSheet.setMaxMin(MaxCGPA, MinCGPA, CurCGPA);
        if (getFragmentManager() != null) {
            bottomSheet.show(getFragmentManager(), "exampleBottomSheet");
        }

    }

    public void setRange(int range) {
        this.range = range;
//        Toast.makeText(getContext(),range+"",Toast.LENGTH_LONG).show();
        saveTrim();
        FirebaseDatabase.getInstance().getReference().child("Member").child(id).child("ModifiedInfo").child("Trimesters").removeValue();
        FirebaseDatabase.getInstance().getReference().child("Member").child(id).child("ModifiedInfo").child("Trimesters").setValue(this.trimesters);
    }

    public void saveTrim() {
        if (Codes.size() > 0) {
            Trimester[] trimesters = new Trimester[12];
            for (int i = 0; i < 12; i++) {
                trimesters[i] = new Trimester();
                trimesters[i].setRange(range);
            }
            double totalPoints = 0, MaxPoints = 0, MinPoints = 0, totalHoursCore = 0;
            subjects.clear();
            for (String Code : Codes) {
                if (Grades.get(Codes.indexOf(Code)).trim().compareTo("-") != 0) {
                    trimesters[Trims.get(Codes.indexOf(Code))].addSubjectComputeGPA(Code, Names.get(Codes.indexOf(Code)), Grades.get(Codes.indexOf(Code)), Hours.get(Codes.indexOf(Code)));
                    subjects.add(new subjects(Code, Names.get(Codes.indexOf(Code)), Grades.get(Codes.indexOf(Code))));
                }
            }
//            Toast.makeText(getContext(),range+"",Toast.LENGTH_LONG).show();
            for (int i = 0; i < 12; i++) {
                if (trimesters[i].getSubjectSize() > 0) {
                    trimesters[i].SetTrimesterTitle(getTrimTitle(i));
                    MaxPoints += trimesters[i].compMaxPoint();
                    totalPoints += trimesters[i].compTotalPoint();
                    MinPoints += trimesters[i].compMinPoint();
//                    totalHours += trimesters[i].compTotalHours();
                    totalHoursCore += trimesters[i].compTotalHoursCore();
                    trimesters[i].setCGPA(Double.toString(round(totalPoints / totalHoursCore)), ((int) totalHoursCore) + "", (round(totalPoints)) + "");
                }
            }
            CurCGPA = Double.toString(round(totalPoints / totalHoursCore));
            MaxCGPA = Double.toString(round(MaxPoints / totalHoursCore));
            MinCGPA = Double.toString(round(MinPoints / totalHoursCore));

            this.trimesters.clear();
            for (int i = 0; i < 12; i++) {
                if (trimesters[i].getSubjectSize() > 0) {
                    this.trimesters.add(trimesters[i]);
                }
            }
        }
    }

    public double round(double a) {
        return Math.round(a * 100.0) / 100.0;
    }

    public String getTrimTitle(int i) {
        int trim = getTrim(intake);
        int year = getYear(intake);
        trim += i;
        while (trim > 3) {
            trim -= 3;
            year++;
        }
        return trim + " - " + year + "/" + (year + 1);
    }

    public String getIntake(String intake) {
        int trim = getTrim(intake);
        int year = getYear(intake);
        return trim + " - " + year + "/" + (year + 1);
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

    public int getYear(String Course) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(Course);
        while (m.find()) {
            if (m.group().length() == 2 && isNumeric(m.group())) {
                return getIntYear("20" + m.group());
            } else if (m.group().length() == 4 && isNumeric(m.group())) {
                return getIntYear(m.group());
            }
        }
        return 2016;
    }

    public void downLoadData() {
        if (!dashBoardActivity.isConnected()) {
            Toast.makeText(context, "No internet connection!", Toast.LENGTH_LONG).show();
        }

        FirebaseDatabase.getInstance().getReference().child("Member").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("ModifiedInfo").child("Id").exists()) {
                    CamsysId = Objects.requireNonNull(snapshot.child("ModifiedInfo").child("Id").getValue()).toString();
                }
                if (snapshot.child("camsysPassword").exists()) {
                    CamsysPass = Objects.requireNonNull(snapshot.child("camsysPassword").getValue()).toString();
                }

                if (snapshot.child("ModifiedInfo").exists()) {
                    getDataFromSnap(snapshot.child("ModifiedInfo"));
                } else {
                    finishedLoading = true;
                    if (getView() != null) {
                        printData();
                    }
                }


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

//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void getDataFromSnap(DataSnapshot snapshot) {
        if (snapshot.child("PersonalImage").exists()) {
            imageUri = Objects.requireNonNull(snapshot.child("PersonalImage").getValue()).toString();
        }

        if (snapshot.child("Degree").exists() && snapshot.child("Intake").exists()) {
            FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshotLocal) {
                    for (DataSnapshot child : snapshotLocal.getChildren()) {
                        if (checkIntake(child.getKey(), snapshot.child("Degree").getValue(), snapshot.child("Intake").getValue())) {
                            intake = child.getKey();
                            Intake = child.getKey();
                            initData(snapshot);
                            if (getView() != null) {
                                printData();
                            }
                            finishedLoading = true;
                            return;
                        }
                    }
                    toast("Your course structure was not found!");
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    finishedLoading = true;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            initData(snapshot);
            if (getView() != null) {
                printData();
            }
            finishedLoading = true;
        }
    }

    public void toast(String ms) {
        dashBoardActivity.runOnUiThread(() -> Toast.makeText(context, ms, Toast.LENGTH_LONG).show());
    }

    public void initData(DataSnapshot snapshot) {

        if (snapshot.child("Name").exists()) {
            Name = Objects.requireNonNull(snapshot.child("Name").getValue()).toString();
        }
//        if(Codes.size()>0){
//            saveTrim();
//        }else {
        if (snapshot.child("Trimesters").exists()) {
            trimesters.clear();
            for (DataSnapshot child : snapshot.child("Trimesters").getChildren()) {
                trimesters.add(getTrim(child));
            }
        }
//        }
        if (trimesters.size() > 0) {
            CGPAText = trimesters.get(trimesters.size() - 1).getCGPA();
            totalHours = trimesters.get(trimesters.size() - 1).getTotalHours();
        }
    }

    public Trimester getTrim(DataSnapshot dataSnapshot) {
        String semesterName = "", GPA = "", CGPA = "", academicStatus = "", hours = "", totalHours = "", totalPoint = "";
        if (dataSnapshot.child("semesterName").exists()) {
            semesterName = Objects.requireNonNull(dataSnapshot.child("semesterName").getValue()).toString();
        }
        if (dataSnapshot.child("gpa").exists()) {
            GPA = Objects.requireNonNull(dataSnapshot.child("gpa").getValue()).toString();
        }
        if (dataSnapshot.child("cgpa").exists()) {
            CGPA = Objects.requireNonNull(dataSnapshot.child("cgpa").getValue()).toString();
        }
        if (dataSnapshot.child("academicStatus").exists()) {
            academicStatus = Objects.requireNonNull(dataSnapshot.child("academicStatus").getValue()).toString();
        }
        if (dataSnapshot.child("hours").exists()) {
            hours = Objects.requireNonNull(dataSnapshot.child("hours").getValue()).toString();
        }
        if (dataSnapshot.child("totalHours").exists()) {
            totalHours = Objects.requireNonNull(dataSnapshot.child("totalHours").getValue()).toString();
        }
        if (dataSnapshot.child("totalPoint").exists()) {
            totalPoint = Objects.requireNonNull(dataSnapshot.child("totalPoint").getValue()).toString();
        }
        Trimester trimester = new Trimester(semesterName, GPA, CGPA, academicStatus, hours, totalHours, totalPoint);
        Iterable<DataSnapshot> subjectCodes = dataSnapshot.child("subjects").getChildren();
        for (DataSnapshot child : subjectCodes) {
            if (child.child("subjectCodes").getValue() != null && child.child("subjectNames").getValue() != null && child.child("subjectGades").getValue() != null) {
                trimester.addSubject(Objects.requireNonNull(child.child("subjectCodes").getValue()).toString(), Objects.requireNonNull(child.child("subjectNames").getValue()).toString(), Objects.requireNonNull(child.child("subjectGades").getValue()).toString());
            }
        }
        return trimester;
    }

    public void printData() {
        userNameT.setText(Name);
        NameT.setText(Name);
        IDT.setText(CamsysId);
        CamsysIdT.setText(CamsysId);
        CamsysPassT.setText(CamsysPass);
        CGPAT.setText(CGPAText);
        CGPAA.setText(CGPAText);
        TotalHoursTitle.setText(totalHours);
        totalHoursT.setText(totalHours);
        setRadioGroup(intake);
        viewCourse(intake);
        if (!imageUri.isEmpty()) {
            Picasso.get().load(imageUri).error(R.drawable.avatar).into(profilePictureAdmin);
        }
        progressBar.setVisibility(View.GONE);
        UserDataPrinted = true;
    }

    public boolean checkIntake(String courseStructure, Object Degree, Object Intake) {
        if (Degree != null && Intake != null) {
            int trim = getInt(Intake.toString().trim().substring(0, 1));
            int courseTrim = getTrim(courseStructure);
            String year = between(Intake.toString().trim(), "-", "/").trim();
            String degree = getMajor(Degree.toString());
            if (!degree.isEmpty() && !year.isEmpty() && trim > 0) {
                return courseStructure.contains(year) && courseStructure.contains(degree) && courseTrim == trim;
            }
        }
        return false;
    }

    public int getInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception ignored) {
            return 0;
        }
    }

    public int getIntYear(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception ignored) {
            return 2016;
        }
    }

    public String between(String value, String a, String b) {
        // Return a substring between the two strings.
        int posA = value.indexOf(a);
        if (posA == -1) {
            return "";
        }
        int posB = value.lastIndexOf(b);
        if (posB == -1) {
            return "";
        }
        int adjustedPosA = posA + a.length();
        if (adjustedPosA >= posB) {
            return "";
        }
        return value.substring(adjustedPosA, posB);
    }

    public String getMajor(String str) {
        if (str.toLowerCase().contains("electronics majoring in computer")) {
            return "ce";
        } else if (str.toLowerCase().contains("electronics majoring in electronics")) {
            return "ee";
        } else if (str.toLowerCase().contains("electronics majoring in telecommunications")) {
            return "te";
        } else if (str.toLowerCase().contains("electronics majoring in electrical")) {
            return "le";
        } else if (str.toLowerCase().contains("electronics majoring in nanotechnology")) {
            return "nano";
        } else {
            return "";
        }
    }

    public boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public void fadeOutNoDelay(View view) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new DecelerateInterpolator()); //and this
        fadeOut.setDuration(0);
        fadeOut.setFillAfter(true);
        view.startAnimation(fadeOut);
    }

    public void fadeIn(View view, int duration) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(duration);
        fadeIn.setFillAfter(true);
        view.startAnimation(fadeIn);
    }

    public void fadeOut(View view, int duration) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new DecelerateInterpolator()); //and this
        fadeOut.setDuration(duration);
        fadeOut.setFillAfter(true);
        view.startAnimation(fadeOut);
    }

    public void cancel() {
        bottomSheet.dismiss();
    }
}