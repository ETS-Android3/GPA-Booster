package com.example.maimyou.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
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
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
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
    ProgressBar progressBar, progressHours;
    EditText newCGPAEdit, totalHoursEdit;
    SeekBar SeekHours, SeekCGPA;
    LineChart lineChart;
    FrameLayout cancel;

    //vars
    double newCGPA = 0, hoursDone = 0, CGPA = 0, minCGPA = 0, maxCGPA = 0, newCompletedHours = 0, totalCoreHours = 0, GPAToPlan = 0;
    ArrayList<Trimester> trimesters = new ArrayList<>();
    ArrayList<String> Hours = new ArrayList<>();
    String hoursTitle = "Hours added to plan: ";
    ArrayList<String> Trim = new ArrayList<>();
    ArrayList<Double> GPA = new ArrayList<>();
    ArrayList<Double> hours = new ArrayList<>();
    Context context = this;
    int progress = 0;

    public void back(View view) {
        onBackPressed();
    }

    public void Menu(View view) {
        PopupMenu popup = new PopupMenu(getApplicationContext(), view);
        popup.getMenuInflater().inflate(R.menu.planner_option_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getTitle().toString().toLowerCase().contains("reset")) {
                reset(view);
            }
            return true;
        });
        popup.show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);
        init();
        ConnectToFirebase();
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    public void init() {
        progressHours = findViewById(R.id.progressHours);
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
        startProgress(progress);
    }

    @SuppressLint("SetTextI18n")
    public void updateCharts() {
        progress = (int) (((hoursDone + newCompletedHours) / totalCoreHours) * 100);
        completedRatio.setText(progress + "%");
        hoursFinished.setText("" + (int) (hoursDone + newCompletedHours));
        hoursRemaining.setText("" + (int) (totalCoreHours - (hoursDone + newCompletedHours)));
        ArrayList<String> xAXES = new ArrayList<>();
        ArrayList<Entry> yAXES = new ArrayList<>();
        double firstSem = (Trim.size() > 0) ? getDouble(Trim.get(0).trim().substring(0, 1)) : 1;
        double overFlow = 0;
        ArrayList<Double> comingGPAPerHour = new ArrayList<>();
//        double lastSem = (Trim.size() > 0) ? getDouble(Trim.get(Trim.size() - 1).trim().substring(0, 1)) : 1;
        if (newCompletedHours > 0) {
            double startGPA = (GPA.size() > 0) ? (GPA.get(GPA.size() - 1)) : 0;
            double comingCGPA = (newCGPA * (hoursDone + newCompletedHours) - hoursDone * CGPA) / newCompletedHours;
            double M = ((comingCGPA - startGPA) / (newCompletedHours / 2));

            for (int i = (int) newCompletedHours; i > 0; i--) {
                double gpa = i * M + startGPA;
                System.out.println(gpa);
                if (gpa > 4) {
                    overFlow += gpa - 4;
                    gpa = 4;
                } else if (gpa < 2) {
                    overFlow -= gpa;
                    gpa = 2;
                }
                if (overFlow > 0) {
                    if (overFlow > (4 - gpa)) {
                        overFlow -= (4 - gpa);
                        gpa = 4;
                    } else {
                        gpa += overFlow;
                        overFlow = 0;
                    }
                } else if (overFlow < 0) {
                    if (overFlow < (2 - gpa)) {
                        overFlow += (gpa - 2);
                        gpa = 2;
                    } else {
                        gpa += overFlow;
                        overFlow = 0;
                    }
                }
                comingGPAPerHour.add(gpa);
            }
        }

        int counter = comingGPAPerHour.size() - 1;
        if (GPA.size() > 0 && GPA.size() == Trim.size()) {
            for (int i = 0; i < 12; i++) {
                if (firstSem > 3) {
                    firstSem = 1;
                }
                if (i < GPA.size() && getDouble(Hours.get(i)) > 0) {
                    float gpa = getGPA(GPA.get(i));
                    yAXES.add(new Entry(i, gpa));
                } else if (i >= GPA.size() && newCompletedHours > 0 && counter < newCompletedHours) {
                    double gpaHolder = 0, size = 0;
                    if (firstSem == 3) {
                        for (int l = 0; l < 6 && counter >= 0; l++) {
                            gpaHolder += comingGPAPerHour.get(counter);
                            counter--;
                            size++;
                        }
                    } else {
                        for (int l = 0; l < 15 && counter >= 0; l++) {
                            gpaHolder += comingGPAPerHour.get(counter);
                            counter--;
                            size++;
                        }
                    }
//                    if (overFlow != 0) {
//                        gpaHolder += overFlow;
//                        overFlow = 0;
//                    }
                    float value = (float) round(gpaHolder / size, 2);
                    value = (value > 4) ? 4 : value;
                    value = (value < 2&&value!=0) ? 2 : value;
                    if (value > 0) {
                        GPAToPlan=value;
                        yAXES.add(new Entry(i, value));
                    }
                }
                xAXES.add(i, Integer.toString((int) firstSem));
                firstSem++;
            }
        }

        final LineDataSet set = new LineDataSet(yAXES, "GPA");
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3F);
        set.setValueTextSize(11f);
        set.setColor(getResources().getColor(R.color.colorAccent));
        set.setDrawCircleHole(true);
        set.setDrawCircles(false);
        set.setHighlightEnabled(false);
        set.setDrawFilled(false);
        final LineData group = new LineData(set);
        group.setDrawValues(true);
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAXES));
        lineChart.setData(group);
        lineChart.invalidate();
        lineChart.animateX(500);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setLabelCount(24);
        lineChart.setVisibleXRange(0, 11f);
        lineChart.getAxis(YAxis.AxisDependency.LEFT).setAxisMinimum(0f);
        lineChart.getAxis(YAxis.AxisDependency.LEFT).setAxisMaximum(4.0f);
        Description description = new Description();
        description.setText("Chart of GPA progress");
        lineChart.setDescription(description);
    }

    @SuppressLint("SetTextI18n")
    public void print() {
        totalHoursEdit.setText("" + (int) newCompletedHours);
        newCGPAEdit.setText(round(newCGPA, 2) + "");
        MaxCGPA.setText(round(maxCGPA, 2) + "");
        MinCGPA.setText(round(minCGPA, 2) + "");
        minHours.setText("" + (int) hoursDone);
        maxHours.setText("" + (int) totalCoreHours);
        newCompleteHours.setText(hoursTitle + (int) newCompletedHours);
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
//                            toast("Your course structure was not found!");
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
//                    toast("Please add your major in your profile!");
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
        if (courseStructure.exists()) {
            trimesters.clear();
            GPA.clear();
            Hours.clear();
            Trim.clear();
            hours.clear();
            if (profileGrades.exists()) {
                for (DataSnapshot child : profileGrades.getChildren()) {
                    trimesters.add(getTrim(child));
                    GPA.add(getDouble(trimesters.get(trimesters.size() - 1).getGPA()));
                    Trim.add(trimesters.get(trimesters.size() - 1).getSemesterName());
                    Hours.add(trimesters.get(trimesters.size() - 1).getHours());
                }
            }
            if (trimesters.size() > 0) {
                hoursDone = getDouble(trimesters.get(trimesters.size() - 1).getTotalHours());
                CGPA = getDouble(trimesters.get(trimesters.size() - 1).getCGPA());
                minCGPA = CGPA;
                maxCGPA = CGPA;
                newCGPA = CGPA;
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
                                if (hoursDone < totalCoreHours) {
                                    if (getDouble(Hours) > 0) hours.add(getDouble(Hours));
                                }
                                totalCoreHours += getDouble(Hours);
                            } else if (!elective) {
                                if (hoursDone < totalCoreHours) {
                                    if (getDouble(Hours) > 0) hours.add(getDouble(Hours));
                                }
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

            print();
            updateCharts();
        } else {
            progressBar.setVisibility(View.GONE);
//            toast("Your course structure doesn't exist!");
        }
    }

    public double getDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception ignored) {
            return 0;
        }
    }

    public float getGPA(Double gpa) {
        try {
            return Float.parseFloat(Double.toString(gpa));
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
        this.runOnUiThread(() -> Toast.makeText(context, ms, Toast.LENGTH_SHORT).show());
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
                        newCompleteHours.setText(hoursTitle + (int) newCompletedHours);
                        toastShort("Number is out of boundaries");
                    }
                } else {
                    newCompletedHours = getDouble(totalHoursEdit.getText().toString());
                    newCompleteHours.setText(hoursTitle + (int) newCompletedHours);
                    SeekHours.setProgress((int) ((newCompletedHours / (totalCoreHours - hoursDone)) * 100d));
                }
                minCGPA = (newCompletedHours * 2d + hoursDone * CGPA) / (newCompletedHours + hoursDone);
                maxCGPA = (newCompletedHours * 4d + hoursDone * CGPA) / (newCompletedHours + hoursDone);
                MaxCGPA.setText(round(maxCGPA, 2) + "");
                MinCGPA.setText(round(minCGPA, 2) + "");
                SeekCGPA.setProgress(50);
                if (CGPA != 0) {
                    newCGPA = CGPA;
                } else {
                    newCGPA = (maxCGPA + minCGPA) / 2;
                }
                newCGPAEdit.setText("" + round(newCGPA, 2));
                NewGPA.setText("New CGPA: " + round(newCGPA, 2));
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
            newCompleteHours.setText(hoursTitle + (int) newCompletedHours);
            minCGPA = (newCompletedHours * 2d + hoursDone * CGPA) / (newCompletedHours + hoursDone);
            maxCGPA = (newCompletedHours * 4d + hoursDone * CGPA) / (newCompletedHours + hoursDone);
//            toast(""+round(minCGPA,2));
            MaxCGPA.setText(round(maxCGPA, 2) + "");
            MinCGPA.setText(round(minCGPA, 2) + "");
            if (CGPA != 0) {
                newCGPA = CGPA;
            } else {
                newCGPA = (maxCGPA + minCGPA) / 2;
            }
            totalHoursEdit.setText("" + (int) newCompletedHours);
            newCGPAEdit.setText("" + round(newCGPA, 2));
            NewGPA.setText("New CGPA: " + round(newCGPA, 2));
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
        updateCharts();
    }

    public void calculate(View view) {
        if (newCompletedHours > 18) {
            toast("calculator can take up to 18 hours only!");
            return;
        }
        if (newCompletedHours < 0) {
            toast("Invalid input");
            return;
        }
        double totHours = 0;
        StringBuilder passData = new StringBuilder();
        passData.append(GPAToPlan).append(",");
        for (Double num : hours) {
            totHours += num;
            passData.append(num).append(",");
            if (totHours >= newCompletedHours) {
                break;
            }
        }
        toast(passData.toString());

    }

    private void startProgress(int progressLocal) {
        if (progressLocal <= 100 && progressLocal >= 0) {
            progressHours.setProgress(progressLocal);
        } else if (progressLocal > 100) {
            progressHours.setProgress(100);
        } else {
            progressHours.setProgress(0);
        }
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (progressLocal < progress) {
//                System.out.println(progressLocal);
                startProgress(progressLocal + 1);
            } else if (progressLocal > progress) {
                startProgress(progressLocal - 1);
            } else {
                startProgress(progressLocal);
            }
        }, 10);
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
