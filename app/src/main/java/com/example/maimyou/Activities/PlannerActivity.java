package com.example.maimyou.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.maimyou.Classes.Trimester;
import com.example.maimyou.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.maimyou.Activities.DashBoardActivity.Intake;
import static com.example.maimyou.Activities.RegisterActivity.SHARED_PREFS;

public class PlannerActivity extends AppCompatActivity {
    //views
    TextView completedRatio, hoursFinished, hoursRemaining, newCompleteHours, minHours, maxHours, MinCGPA, MaxCGPA, NewGPA;
    EditText newCGPAEdit, totalHoursEdit;
    SeekBar SeekHours, SeekCGPA;
    ProgressBar progressBar;
    LineChart lineChart;
    FrameLayout cancel;

    //vars
    Context context = this;
    ArrayList<Trimester> trimesters = new ArrayList<>();
    double newCGPA = 0, hoursDone = 0, CGPA = 0, minCGPA = 0, maxCGPA = 0, newCompletedHours = 0, totalCoreHours = 0;
    ArrayList<Double> GPA = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);

        init();
        ConnectToFirebase();
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    public void init() {
        lineChart = findViewById(R.id.lineChart);
        NewGPA = findViewById(R.id.NewGPA);
        SeekHours = findViewById(R.id.SeekHours);
        SeekCGPA = findViewById(R.id.SeekCGPA);
        totalHoursEdit = findViewById(R.id.totalHoursEdit);
        newCGPAEdit = findViewById(R.id.newCGPAEdit);
        completedRatio = findViewById(R.id.completedRatio);
        progressBar = findViewById(R.id.progressBar);
        hoursFinished = findViewById(R.id.hoursFinished);
        hoursRemaining = findViewById(R.id.hoursRemaining);
        newCompleteHours = findViewById(R.id.newCompleteHours);
        minHours = findViewById(R.id.minHours);
        maxHours = findViewById(R.id.maxHours);
        MinCGPA = findViewById(R.id.minCGPA);
        MaxCGPA = findViewById(R.id.MaxCGPA);
        cancel = findViewById(R.id.cancel);
        cancel.setOnTouchListener((v, event) -> {
            closeKeyBoard(totalHoursEdit);
            closeKeyBoard(newCGPAEdit);
            totalHoursEdit.clearFocus();
            newCGPAEdit.clearFocus();
            return false;
        });
    }

    public void updateCharts() {

    }

    @SuppressLint("SetTextI18n")
    public void print() {
        totalHoursEdit.setText("" + (int) newCompletedHours);
        newCGPAEdit.setText(round(newCGPA, 2) + "");
        MaxCGPA.setText(round(maxCGPA, 2) + "");
        MinCGPA.setText(round(minCGPA, 2) + "");
        minHours.setText("" + (int) hoursDone);
        maxHours.setText("" + (int) totalCoreHours);
        newCompleteHours.setText("New completed hours: " + (int) newCompletedHours);
        NewGPA.setText("New CGPA: " + round(newCGPA, 2));
        hoursRemaining.setText("" + (int) (totalCoreHours - hoursDone));
        hoursFinished.setText("" + (int) hoursDone);
        completedRatio.setText((int) ((hoursDone / totalCoreHours) * 100) + "%");
        progressBar.setVisibility(View.GONE);
        totalHoursEdit.addTextChangedListener(totalHoursEditWatcher);
        SeekHours.setOnSeekBarChangeListener(hoursLis);
        newCGPAEdit.addTextChangedListener(newCGPAEditWatcher);
        SeekCGPA.setOnSeekBarChangeListener(CGPALis);
    }

    public void ConnectToFirebase() {

        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot parentSnapshot) {
                DataSnapshot snapshot = parentSnapshot.child("Member").child(loadData("Id")).child("Profile");
                if (snapshot.child("Degree").exists() && snapshot.child("Intake").exists()) {
                    FirebaseDatabase.getInstance().getReference().child("UNDERGRADUATE PROGRAMMES").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshotLocal) {
                            for (DataSnapshot child : snapshotLocal.getChildren()) {
                                if (checkIntake(child.getKey(), snapshot.child("Degree").getValue(), snapshot.child("Intake").getValue())) {
                                    Intake = child.getKey();
                                    getData(parentSnapshot, child.getKey());
                                    return;
                                }
                            }
                            toast("Your course structure was not found!");
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    toast("Please add your major in your profile!");
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getData(DataSnapshot parentSnapshot, String intake) {
        DataSnapshot courseStructure = parentSnapshot.child("UNDERGRADUATE PROGRAMMES").child(intake).child("Trimesters");
        DataSnapshot profileGrades = parentSnapshot.child("Member").child(loadData("Id")).child("Profile").child("Trimesters");
        if (profileGrades.exists() && courseStructure.exists()) {
            if (profileGrades.exists()) {
                trimesters.clear();
                GPA.clear();
                for (DataSnapshot child : profileGrades.getChildren()) {
                    trimesters.add(getTrim(child));
                    GPA.add(getDouble(trimesters.get(trimesters.size() - 1).getGPA()));
                }
                totalCoreHours = 0;
                boolean elective = false;
                for (DataSnapshot trimester : courseStructure.getChildren()) {
                    for (DataSnapshot child : trimester.getChildren()) {
                        if (child.child("Elective").exists() && child.child("SubjectHours").exists() && child.child("SubjectName").exists() && child.getKey() != null) {
                            String Elective = Objects.requireNonNull(child.child("Elective").getValue()).toString();
                            String Code = child.getKey();
                            String Name = Objects.requireNonNull(child.child("SubjectName").getValue()).toString();
                            String Hours = Objects.requireNonNull(child.child("SubjectHours").getValue()).toString();
                            if (!Code.toLowerCase().contains("mpu") && !Name.toLowerCase().startsWith("mpu") && !Name.toLowerCase().contains("train") && !Name.toLowerCase().contains("management") && !Name.toLowerCase().contains("introduction to research methodology")) {
                                if (!Elective.toLowerCase().contains("true")) {
                                    totalCoreHours += getDouble(Hours);
                                } else if (!elective) {
                                    totalCoreHours += getDouble(Hours);
                                }
                            }
                            if (Elective.toLowerCase().contains("true")) {
                                elective = true;
                            }
                        }
                    }
                    elective = false;
                }
                if (trimesters.size() > 0) {
                    hoursDone = getDouble(trimesters.get(trimesters.size() - 1).getTotalHours());
                    CGPA = getDouble(trimesters.get(trimesters.size() - 1).getCGPA());
                    minCGPA = CGPA;
                    maxCGPA = CGPA;
                    newCGPA = CGPA;
                }
//                toast(minCGPA + "");

                print();
            } else {
                progressBar.setVisibility(View.GONE);
                toast("No subject was found!");
            }
        } else {
            progressBar.setVisibility(View.GONE);
            toast("Please update your profile!");
        }
    }

    public double getDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception ignored) {
            return 0;
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void toast(String ms) {
        this.runOnUiThread(() -> Toast.makeText(context, ms, Toast.LENGTH_LONG).show());
    }

    public void toastShort(String ms) {
        this.runOnUiThread(() -> Toast.makeText(context, ms, Toast.LENGTH_SHORT).show());
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
            if (child.child("subjectCodes").exists() && child.child("subjectNames").exists() && child.child("subjectGades").exists()) {
                String Code = Objects.requireNonNull(child.child("subjectCodes").getValue()).toString();
                String Name = Objects.requireNonNull(child.child("subjectNames").getValue()).toString();
                String Grade = Objects.requireNonNull(child.child("subjectGades").getValue()).toString();
                trimester.addSubject(Code, Name, Grade);
            }
        }
        return trimester;
    }

    private void closeKeyBoard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }

    TextWatcher totalHoursEditWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            SeekHours.setOnSeekBarChangeListener(null);
            SeekCGPA.setOnSeekBarChangeListener(null);
            newCGPAEdit.removeTextChangedListener(newCGPAEditWatcher);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!totalHoursEdit.getText().toString().isEmpty()) {
                if (getDouble(totalHoursEdit.getText().toString()) > (totalCoreHours - hoursDone) || getDouble(totalHoursEdit.getText().toString()) < 0) {
                    if (getDouble(totalHoursEdit.getText().toString()) > (totalCoreHours - hoursDone)) {
                        newCompletedHours = (totalCoreHours - hoursDone);
                        SeekHours.setProgress(100);
//                        totalHoursEdit.setText("" + (int) newCompletedHours);
                        newCompleteHours.setText("New completed hours: " + (int) newCompletedHours);
                        toastShort("Number is out of boundaries");
                    }
                } else {
                    newCompletedHours = getDouble(totalHoursEdit.getText().toString());
                    newCompleteHours.setText("New completed hours: " + (int) newCompletedHours);
                    SeekHours.setProgress((int) ((newCompletedHours / (totalCoreHours - hoursDone)) * 100d));
                }
                minCGPA = (newCompletedHours * 2d + hoursDone * CGPA) / (newCompletedHours + hoursDone);
                maxCGPA = (newCompletedHours * 4d + hoursDone * CGPA) / (newCompletedHours + hoursDone);
                MaxCGPA.setText(round(maxCGPA, 2) + "");
                MinCGPA.setText(round(minCGPA, 2) + "");
                SeekCGPA.setProgress(50);
                newCGPA = CGPA;
                newCGPAEdit.setText("" + round(CGPA, 2));
                NewGPA.setText("New CGPA: " + round(CGPA, 2));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            SeekHours.setOnSeekBarChangeListener(hoursLis);
            newCGPAEdit.addTextChangedListener(newCGPAEditWatcher);
            SeekCGPA.setOnSeekBarChangeListener(CGPALis);
            updateCharts();
        }
    };
    SeekBar.OnSeekBarChangeListener hoursLis = new SeekBar.OnSeekBarChangeListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            newCompletedHours = (((double) progress) / 100d) * ((totalCoreHours - hoursDone));
            newCompleteHours.setText("New completed hours: " + (int) newCompletedHours);
            minCGPA = (newCompletedHours * 2d + hoursDone * CGPA) / (newCompletedHours + hoursDone);
            maxCGPA = (newCompletedHours * 4d + hoursDone * CGPA) / (newCompletedHours + hoursDone);
//            toast(""+round(minCGPA,2));
            MaxCGPA.setText(round(maxCGPA, 2) + "");
            MinCGPA.setText(round(minCGPA, 2) + "");
            newCGPA = CGPA;
            totalHoursEdit.setText("" + (int) newCompletedHours);
            newCGPAEdit.setText("" + round(CGPA, 2));
            NewGPA.setText("New CGPA: " + round(CGPA, 2));
            SeekCGPA.setProgress(50);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            SeekCGPA.setOnSeekBarChangeListener(null);
            totalHoursEdit.removeTextChangedListener(totalHoursEditWatcher);
            newCGPAEdit.removeTextChangedListener(newCGPAEditWatcher);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            totalHoursEdit.addTextChangedListener(totalHoursEditWatcher);
            newCGPAEdit.addTextChangedListener(newCGPAEditWatcher);
            SeekCGPA.setOnSeekBarChangeListener(CGPALis);
            updateCharts();

        }
    };
    TextWatcher newCGPAEditWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            SeekCGPA.setOnSeekBarChangeListener(null);
            SeekHours.setOnSeekBarChangeListener(null);
            totalHoursEdit.removeTextChangedListener(totalHoursEditWatcher);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!newCGPAEdit.getText().toString().isEmpty() && getDouble(newCGPAEdit.getText().toString()) != 0) {
                if (getDouble(newCGPAEdit.getText().toString()) <= round(maxCGPA, 2) && getDouble(newCGPAEdit.getText().toString()) >= round(minCGPA, 2) && getDouble(newCGPAEdit.getText().toString()) != 0) {
                    newCGPA = getDouble(newCGPAEdit.getText().toString());
                    SeekCGPA.setProgress((int) (((newCGPA - minCGPA) / (maxCGPA - minCGPA)) * 100d));
                    NewGPA.setText("New CGPA: " + round(newCGPA, 2));
                } else {
                    if (getDouble(newCGPAEdit.getText().toString()) > maxCGPA) {
                        newCGPA = maxCGPA;
                        SeekCGPA.setProgress(100);
                        NewGPA.setText("New CGPA: " + round(newCGPA, 2));
                        toastShort("CGPA is out of boundaries");
                    } else if (getDouble(newCGPAEdit.getText().toString()) < minCGPA) {
                        newCGPA = minCGPA;
                        SeekCGPA.setProgress(0);
                        NewGPA.setText("New CGPA: " + round(newCGPA, 2));
                        toastShort("CGPA is out of boundaries");
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            totalHoursEdit.addTextChangedListener(totalHoursEditWatcher);
            SeekHours.setOnSeekBarChangeListener(hoursLis);
            SeekCGPA.setOnSeekBarChangeListener(CGPALis);
            updateCharts();
        }
    };

    SeekBar.OnSeekBarChangeListener CGPALis = new SeekBar.OnSeekBarChangeListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            newCGPA = ((((double) progress) / 100d) * (maxCGPA - minCGPA)) + minCGPA;
            NewGPA.setText("New CGPA: " + round(newCGPA, 2));
            newCGPAEdit.setText("" + round(newCGPA, 2));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            SeekHours.setOnSeekBarChangeListener(null);
            totalHoursEdit.removeTextChangedListener(totalHoursEditWatcher);
            newCGPAEdit.removeTextChangedListener(newCGPAEditWatcher);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            newCGPAEdit.addTextChangedListener(newCGPAEditWatcher);
            totalHoursEdit.addTextChangedListener(totalHoursEditWatcher);
            SeekHours.setOnSeekBarChangeListener(hoursLis);
            updateCharts();
        }
    };

    public void reset(View view) {
        SeekHours.setOnSeekBarChangeListener(null);
        SeekCGPA.setOnSeekBarChangeListener(null);
        totalHoursEdit.removeTextChangedListener(totalHoursEditWatcher);
        newCGPAEdit.removeTextChangedListener(newCGPAEditWatcher);
        newCompletedHours = 0;
        minCGPA = CGPA;
        maxCGPA = CGPA;
        newCGPA = CGPA;
        SeekHours.setProgress(0);
        SeekCGPA.setProgress(50);
        print();
        totalHoursEdit.addTextChangedListener(totalHoursEditWatcher);
        SeekHours.setOnSeekBarChangeListener(hoursLis);
        newCGPAEdit.addTextChangedListener(newCGPAEditWatcher);
        SeekCGPA.setOnSeekBarChangeListener(CGPALis);
    }

    @Override
    public void onBackPressed() {
        if (totalHoursEdit.isFocused() || newCGPAEdit.isFocused()) {
            closeKeyBoard(totalHoursEdit);
            closeKeyBoard(newCGPAEdit);
            totalHoursEdit.clearFocus();
            newCGPAEdit.clearFocus();
        } else {
            super.onBackPressed();
        }
    }
}
