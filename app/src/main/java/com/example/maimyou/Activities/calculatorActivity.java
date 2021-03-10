package com.example.maimyou.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.maimyou.Adapters.GradesListAdapter;
import com.example.maimyou.Classes.Marks;
import com.example.maimyou.R;
import com.example.maimyou.Adapters.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class calculatorActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    boolean radiob = false, cgpab = false, shownum = false, isShown = false, isShown2 = false;
    RadioGroup rg;
    RadioButton rb;
    CheckBox checkBox;
    TextView minGPA, aveGPA, maxGPA;
    int numOfSub, subject = 0, fragment = 0, Position = 0;
    double cgpa, markstogpa, gpatomarks, goal, r = 5;
    String grade;
    String[][] grades = new String[6][2];
    boolean var1 = false, var2 = false, frag1Loaded = false;
    EditText F1numhours1, F1numhours2, F1numhours3, F1numhours4, F1numhours5, F1numhours6;
    EditText numhours1, numhours2, numhours3, numhours4, numhours5, numhours6, GPAEDIT;
    Dialog settingsDialog, settingsDialog2;
    SeekBar seekBar;
    String plan = "";


    public void radiob(View view) {
        int radiobutton = rg.getCheckedRadioButtonId();
        rb = findViewById(radiobutton);
        numOfSub = Integer.parseInt((String) rb.getText());
        visibilityControlCenter(numOfSub);
        radiob = true;
    }

    public void firstop(View view) {
        EditText myText = (EditText) this.findViewById(R.id.writeGPA);
        myText.setText("3.67");
        cgpa = 3.67;
        cgpab = true;
    }

    public void secondupop(View view) {
        EditText myText = (EditText) this.findViewById(R.id.writeGPA);
        myText.setText("3.33");
        cgpa = 3.33;
        cgpab = true;
    }

    public void secondlowerop(View view) {
        EditText myText = (EditText) this.findViewById(R.id.writeGPA);
        myText.setText("2.67");
        cgpa = 2.67;
        cgpab = true;
    }

    public void thirdop(View view) {
        EditText myText = (EditText) this.findViewById(R.id.writeGPA);
        myText.setText("2.00");
        cgpa = 2.00;
        cgpab = true;
    }

    public void closeShowNum(View view) {
        settingsDialog.dismiss();
        settingsDialog2.dismiss();
    }

    public void checkClicked(View view) {
        if (checkBox.isChecked()) {
            if (!isShown) {
                isShown = true;
                settingsDialog.show();
            }
            shownum = true;
        } else {
            shownum = false;
        }
        if (!radiob && TextUtils.isEmpty(GPAEDIT.getText())) {
        } else if (!radiob) {
        } else if (TextUtils.isEmpty(GPAEDIT.getText())) {
        } else {
            checker();
        }
    }

    public void EnterPressed2(View view) {
        if (!radiob && TextUtils.isEmpty(GPAEDIT.getText())) {
            GPAEDIT.setError("GPA is required!");
            Toast.makeText(calculatorActivity.this, "Write GPA and select number of subjects", Toast.LENGTH_SHORT).show();
        } else if (!radiob) {
            Toast.makeText(calculatorActivity.this, "Select number of subjects", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(GPAEDIT.getText())) {
            GPAEDIT.setError("GPA is required!");
            Toast.makeText(calculatorActivity.this, "Write GPA", Toast.LENGTH_SHORT).show();
        } else {
            cgpa = Float.valueOf(GPAEDIT.getText().toString());
            checker();
        }
        GPAEDIT.setError(null);
        GPAEDIT.clearFocus();
        F1numhours1.clearFocus();
        F1numhours2.clearFocus();
        F1numhours3.clearFocus();
        F1numhours4.clearFocus();
        F1numhours5.clearFocus();
        F1numhours6.clearFocus();
        closeKeyBoard(GPAEDIT);
        closeKeyBoard(F1numhours1);
        closeKeyBoard(F1numhours2);
        closeKeyBoard(F1numhours3);
        closeKeyBoard(F1numhours4);
        closeKeyBoard(F1numhours5);
        closeKeyBoard(F1numhours6);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        for (int y = 0; y < grades.length; y++) {
            grades[y][0] = "";
            grades[y][1] = "";
        }
        plan = getIntent().getStringExtra("plan");
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Position = position;
                setFrag();
            }

            @Override
            public void onPageSelected(int position) {
                Position = position;
                setFrag();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (Position == 0) {
                    Frag1Starter();
                }
                if (Position == 1) {
                    Frag2Starter();
                }
                if (Position == 2) {
                    Frag3Starter();
                }
                fragment = Position;
            }
        });
    }

    public void setFrag() {
        if (!frag1Loaded) {
            Frag1Starter();
            frag1Loaded = true;
        }
    }

    public void setPermission(int l) {
        if (l == 0) {
            var1 = false;
            var2 = false;

        } else if (l == 1) {
            var1 = true;
        } else if (l == 2) {
            var2 = true;
        }

    }

    public int getPermission() {
        if (!var2 && var1) {
            return 1;
        } else if (!var1 && var2) {
            return 2;
        } else return 3;
    }

    public void Frag1Starter() {
        settingsDialog2 = new Dialog(this);
        settingsDialog2.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        settingsDialog2.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        settingsDialog2.setCanceledOnTouchOutside(true);
        settingsDialog2.setContentView(getLayoutInflater().inflate(R.layout.image_layout2
                , null));
        visibilityControlCenter(numOfSub);
        rg = (RadioGroup) findViewById(R.id.radioGroup);
        seekBar = (SeekBar) findViewById(R.id.seekBar2);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                r = progress + 1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (!isShown2) {
                    isShown2 = true;
                    settingsDialog2.show();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!radiob && TextUtils.isEmpty(GPAEDIT.getText())) {
                } else if (!radiob) {
                } else if (TextUtils.isEmpty(GPAEDIT.getText())) {
                } else {
                    checker();
                }
            }
        });
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        GPAEDIT = (EditText) findViewById(R.id.writeGPA);
        F1numhours1 = (EditText) findViewById(R.id.F1numhours1);
        F1numhours2 = (EditText) findViewById(R.id.F1numhours2);
        F1numhours3 = (EditText) findViewById(R.id.F1numhours3);
        F1numhours4 = (EditText) findViewById(R.id.F1numhours4);
        F1numhours5 = (EditText) findViewById(R.id.F1numhours5);
        F1numhours6 = (EditText) findViewById(R.id.F1numhours6);
        settingsDialog = new Dialog(this);
        settingsDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        settingsDialog.setCanceledOnTouchOutside(true);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.image_layout
                , null));
        ArrayList<Marks> ToPrint = new ArrayList<>();
        Marks Print;
        String[] grads2 = {"A", "A-", "A-", "A-", "A-", "A-", "B+", "B+", "B+", "B+", "B+", "B", "B", "B", "B", "B", "B-", "B-", "B-", "B-", "B-", "C+", "C+", "C+", "C+", "C+", "C", "C", "C", "C", "C"};

//        Print = new Marks("Write GPA and select number of subjects, then press Enter (hours are optional, default hours : 3 hours).", "", "", "", "", "", "");
//        ToPrint.add(Print);

        Print = new Marks("", "Grade", "", "Marks", "", "Points", "", 1);
        ToPrint.add(Print);

        int marks = 80;
        for (int x = 0; x < grads2.length; x++) {
            Print = new Marks("", grads2[x], "", Integer.toString(marks - x) + "%", "", Double.toString(roundNum(marksToGPAmeth(marks - x))), "", 0);
            ToPrint.add(Print);
        }
        Print = new Marks("", "", "", "", "", "", "", 0);
        ToPrint.add(Print);
        ListView listView = findViewById(R.id.plans);

        GradesListAdapter arrayAdapter = new GradesListAdapter(this, R.layout.grades_layout, ToPrint);
        listView.setAdapter(arrayAdapter);

        final EditText tx = findViewById(R.id.writeGPA);
        tx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cgpab = false;
                cgpa = 0;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cgpab = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                cgpab = true;
            }
        });
        setPlane(plan);
    }

    public void setPlane(String plan) {
        if (plan != null && !plan.isEmpty()) {
            String[] arr = plan.split(",", -1);
            if (arr.length > 1) {
                GPAEDIT.setText(arr[0]);
                radiob(getRadioButton(arr.length - 1));
                for (int i = 1; i < arr.length; i++) {
                    setEditText(i, arr[i]);
                }
                EnterPressed2(null);
            }
        }
    }

    public void setEditText(int i, String hours) {
        if (i == 1) {
            F1numhours1.setText(hours);
        } else if (i == 2) {
            F1numhours2.setText(hours);
        } else if (i == 3) {
            F1numhours3.setText(hours);
        } else if (i == 4) {
            F1numhours4.setText(hours);
        } else if (i == 5) {
            F1numhours5.setText(hours);
        } else if (i == 6) {
            F1numhours6.setText(hours);
        }
    }

    public RadioButton getRadioButton(int subNum) {
        RadioButton rd;
        if (subNum == 1) {
            rd = findViewById(R.id.radioButton);
        } else if (subNum == 2) {
            rd = findViewById(R.id.radioButton2);
        } else if (subNum == 3) {
            rd = findViewById(R.id.radioButton3);
        } else if (subNum == 4) {
            rd = findViewById(R.id.radioButton4);
        } else if (subNum == 5) {
            rd = findViewById(R.id.radioButton5);
        } else {
            rd = findViewById(R.id.radioButton6);
        }
        rd.setChecked(true);
        return rd;
    }

    public void Frag2Starter() {
        minGPA = findViewById(R.id.minGPA);
        aveGPA = findViewById(R.id.aveGPA);
        maxGPA = findViewById(R.id.maxGPA);
        numhours1 = findViewById(R.id.numhours1);
        numhours2 = findViewById(R.id.numhours2);
        numhours3 = findViewById(R.id.numhours3);
        numhours4 = findViewById(R.id.numhours4);
        numhours5 = findViewById(R.id.numhours5);
        numhours6 = findViewById(R.id.numhours6);
        ArrayList<String> list = new ArrayList<>();
        list.add("-");
        list.add("A+");
        list.add("A");
        list.add("A-");
        list.add("B+");
        list.add("B");
        list.add("B-");
        list.add("C+");
        list.add("C");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_grade2, list);
        adapter.setDropDownViewResource(R.layout.spinner_grade_drop_down2);
        setSpinner(findViewById(R.id.spin1), 1, adapter);
        setSpinner(findViewById(R.id.spin2), 2, adapter);
        setSpinner(findViewById(R.id.spin3), 3, adapter);
        setSpinner(findViewById(R.id.spin4), 4, adapter);
        setSpinner(findViewById(R.id.spin5), 5, adapter);
        setSpinner(findViewById(R.id.spin6), 6, adapter);


        numhours1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateThegrade2(1);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        numhours2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateThegrade2(2);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        numhours3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateThegrade2(3);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        numhours4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateThegrade2(4);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        numhours5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateThegrade2(5);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        numhours6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateThegrade2(6);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void setSpinner(AppCompatSpinner spin, int sub, ArrayAdapter<String> adapter) {
        spin.setAdapter(adapter);
        spin.setSelection(0);
        spin.setOnItemSelectedListener(getListener(sub));
    }

    public AdapterView.OnItemSelectedListener getListener(int sub) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                subject = sub;
                grade = text;
                checker();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    public void Frag3Starter() {
        final TextView setGrad = findViewById(R.id.setGradeee);
        final EditText setGPA = findViewById(R.id.setGPAAA);
        final EditText setMarks = findViewById(R.id.setMarksss);

        TextWatcher text1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setPermission(1);
                if (getPermission() == 1) {
                    if (!TextUtils.isEmpty(setMarks.getText())) {
                        markstogpa = ((double) Float.valueOf(setMarks.getText().toString()));
                        if (markstogpa >= 80 && markstogpa <= 100) {
                            setGPA.setText("4");
                        } else if (markstogpa >= 50 && markstogpa <= 80) {
                            setGPA.setText(Double.toString(roundNum(((markstogpa - 50) / 30) * 2 + 2)));
                        } else if (markstogpa < 50 && markstogpa >= 0) {
                            setGPA.setText("0");
                        } else {
                            setGPA.setText("00");
                        }

                        setGrad.setText(marks(markstogpa));
//                        setGrad.setTextColor(Color.RED);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setPermission(0);
            }
        };
        TextWatcher text2 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setPermission(2);
                if (getPermission() == 2) {
                    if (!TextUtils.isEmpty(setGPA.getText())) {
                        gpatomarks = ((double) Float.valueOf(setGPA.getText().toString()));
                        if (gpatomarks >= 0 && gpatomarks <= 4) {
                            setMarks.setText(Double.toString(roundNum(((gpatomarks - 2) / 2) * 30 + 50)));
                        } else {
                            setMarks.setText("00");
                            gpatomarks = 500;
                        }
                        setGrad.setText(marks(((gpatomarks - 2) / 2) * 30 + 50));
//                        setGrad.setTextColor(Color.RED);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setPermission(0);
            }
        };
        setMarks.addTextChangedListener(text1);
        setGPA.addTextChangedListener(text2);
    }

    public void visibilityControlCenter(int numOfSubjects) {
        if (numOfSubjects == 1) {
            F1numhours1.setVisibility(View.VISIBLE);
            F1numhours1.setHint("hours (optional default = 3)");
            F1numhours2.setVisibility(View.GONE);
            F1numhours3.setVisibility(View.GONE);
            F1numhours4.setVisibility(View.GONE);
            F1numhours5.setVisibility(View.GONE);
            F1numhours6.setVisibility(View.GONE);
        } else if (numOfSubjects == 2) {
            F1numhours1.setVisibility(View.VISIBLE);
            F1numhours2.setVisibility(View.VISIBLE);
            F1numhours1.setHint("hours (optional)");
            F1numhours2.setHint("hours (optional)");
            F1numhours3.setVisibility(View.GONE);
            F1numhours4.setVisibility(View.GONE);
            F1numhours5.setVisibility(View.GONE);
            F1numhours6.setVisibility(View.GONE);
        } else if (numOfSubjects == 3) {
            F1numhours1.setVisibility(View.VISIBLE);
            F1numhours2.setVisibility(View.VISIBLE);
            F1numhours3.setVisibility(View.VISIBLE);
            F1numhours1.setHint("3");
            F1numhours2.setHint("3");
            F1numhours3.setHint("3");
            F1numhours4.setVisibility(View.GONE);
            F1numhours5.setVisibility(View.GONE);
            F1numhours6.setVisibility(View.GONE);
        } else if (numOfSubjects == 4) {
            F1numhours1.setVisibility(View.VISIBLE);
            F1numhours2.setVisibility(View.VISIBLE);
            F1numhours3.setVisibility(View.VISIBLE);
            F1numhours4.setVisibility(View.VISIBLE);
            F1numhours1.setHint("3");
            F1numhours2.setHint("3");
            F1numhours3.setHint("3");
            F1numhours4.setHint("3");
            F1numhours5.setVisibility(View.GONE);
            F1numhours6.setVisibility(View.GONE);
        } else if (numOfSubjects == 5) {
            F1numhours1.setVisibility(View.VISIBLE);
            F1numhours2.setVisibility(View.VISIBLE);
            F1numhours3.setVisibility(View.VISIBLE);
            F1numhours4.setVisibility(View.VISIBLE);
            F1numhours5.setVisibility(View.VISIBLE);
            F1numhours1.setHint("3");
            F1numhours2.setHint("3");
            F1numhours3.setHint("3");
            F1numhours4.setHint("3");
            F1numhours5.setHint("3");
            F1numhours6.setVisibility(View.GONE);
        } else if (numOfSubjects == 6) {
            F1numhours1.setVisibility(View.VISIBLE);
            F1numhours2.setVisibility(View.VISIBLE);
            F1numhours3.setVisibility(View.VISIBLE);
            F1numhours4.setVisibility(View.VISIBLE);
            F1numhours5.setVisibility(View.VISIBLE);
            F1numhours6.setVisibility(View.VISIBLE);
            F1numhours1.setHint("3");
            F1numhours2.setHint("3");
            F1numhours3.setHint("3");
            F1numhours4.setHint("3");
            F1numhours5.setHint("3");
            F1numhours6.setHint("3");
        }
    }

    public void Grade1(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup);
        popup.show();
        subject = 1;
    }

    public void Grade2(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup);
        popup.show();
        subject = 2;
    }

    public void Grade3(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup);
        popup.show();
        subject = 3;
    }

    public void Grade4(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup);
        popup.show();
        subject = 4;
    }

    public void Grade5(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup);
        popup.show();
        subject = 5;
    }

    public void Grade6(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup);
        popup.show();
        subject = 6;
    }

    public void checker() {
        if (radiob && cgpab && fragment == 0) {
            printExpectedGrads();
        }
        if (subject != 0 && !grade.isEmpty() && fragment == 1) {
            updateThegrade();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                grade = "A";
                checker();
                return true;
            case R.id.item2:
                grade = "A-";
                checker();
                return true;
            case R.id.item3:
                grade = "B+";
                checker();
                return true;
            case R.id.item4:
                grade = "B";
                checker();
                return true;
            case R.id.item5:
                grade = "B-";
                checker();
                return true;
            case R.id.item6:
                grade = "C+";
                checker();
                return true;
            case R.id.item7:
                grade = "C";
                checker();
                return true;
            case R.id.item8:
                grade = "-";
                checker();
                return true;
            default:
                return false;
        }
    }

    public double grade(String grade) {
        double marks = 0, gpa;

        if (grade.isEmpty()) {
            return 0;
        }

        if (isFound("A", grade)) {
            marks = 80;
        } else if (isFound("B", grade)) {
            marks = 65;
        } else if (isFound("C", grade)) {
            marks = 50;
        }

        if (isFound("+", grade)) {
            marks += 5;
        } else if (isFound("-", grade)) {
            marks -= 5;
        }

        if (marks >= 80 && marks <= 100) {
            return 4;
        } else if (marks <= 80 && marks >= 50) {
            gpa = (((marks - 50) / 30) * 2) + 2;
            return gpa;
        } else if (marks <= 50 && marks >= 0) {
            return 0;
        }
        return 999999;

    }

    public double grademin(String grade) {
        double marks = 0, gpa;

        if (grade.isEmpty()) {
            return 0;
        }

        if (isFound("A", grade)) {
            marks = 80;
        } else if (isFound("B", grade)) {
            marks = 65;
        } else if (isFound("C", grade)) {
            marks = 50;
        }

        if (isFound("+", grade)) {
            marks += 5;
        } else if (isFound("-", grade)) {
            marks -= 5;
        }

        if (marks >= 80 && marks <= 100) {
            return 4;
        } else if (marks <= 80 && marks >= 50) {
            gpa = (((marks - 50) / 30) * 2) + 2;
            return gpa;
        } else if (marks <= 50 && marks >= 0) {
            return 0;
        }
        return 999999;
    }

    public double grademax(String grade) {
        double marks = 0, gpa;

        if (grade.isEmpty()) {
            return 0;
        }

        if (isFound("A-", grade)) {
            marks = 75 + r;
        } else if (isFound("A", grade)) {
            marks = 80;
        } else if (isFound("B+", grade)) {
            marks = 70 + r;
        } else if (isFound("B-", grade)) {
            marks = 60 + r;
        } else if (isFound("B", grade)) {
            marks = 65 + r;
        } else if (isFound("C+", grade)) {
            marks = 55 + r;
        } else if (isFound("C", grade)) {
            marks = 50 + r;
        }

        if (marks >= 80 && marks <= 100) {
            return 4;
        } else if (marks <= 80 && marks >= 50) {
            gpa = (((marks - 50) / 30) * 2) + 2;
            return gpa;
        } else if (marks <= 50 && marks >= 0) {
            return 0;
        }
        return 999999;

    }

    public String marks(double marks) {
        if (marks <= 100 && marks >= 90) {
            return "A+";
        } else if (marks < 90 && marks >= 80) {
            return "A";
        } else if (marks < 80 && marks >= 75) {
            return "A-";
        } else if (marks < 75 && marks >= 70) {
            return "B+";
        } else if (marks < 70 && marks >= 65) {
            return "B";
        } else if (marks < 65 && marks >= 60) {
            return "B-";
        } else if (marks < 60 && marks >= 55) {
            return "C+";
        } else if (marks < 55 && marks >= 50) {
            return "C";
        } else if (marks < 50 && marks >= 45) {
            return "C-";
        } else if (marks < 45 && marks >= 0) {
            return "F";
        } else {
            return "??";
        }
    }

    public double gradeMin(String grade) {
        double marks = 0, gpa;

        if (isFound("A", grade)) {
            marks = 80;
        } else if (isFound("B", grade)) {
            marks = 65;
        } else if (isFound("C", grade)) {
            marks = 50;
        }

        if (isFound("+", grade)) {
            marks += 5;
        } else if (isFound("-", grade)) {
            marks -= 5;
        }

        if (marks >= 80 && marks <= 100) {
            return 4;
        } else if (marks <= 80 && marks >= 50) {
            gpa = (((marks - 50) / 30) * 2) + 2;
            return gpa;
        } else if (marks <= 50 && marks >= 0) {
            return 0;
        }
        return 999999;

    }

    public double gradeMax(String grade) {
        double marks = 0, gpa;

        if (isFound("A", grade)) {
            marks = 80;
        } else if (isFound("B", grade)) {
            marks = 65;
        } else if (isFound("C", grade)) {
            marks = 50;
        }

        if (isFound("+", grade)) {
            marks += 5;
        } else if (isFound("-", grade)) {
            marks -= 5;
        }

        marks += 4.5;

        if (marks >= 80 && marks <= 100) {
            return 4;
        } else if (marks <= 80 && marks >= 50) {
            gpa = (((marks - 50) / 30) * 2) + 2;
            return gpa;
        } else if (marks <= 50 && marks >= 0) {
            return 0;
        }
        return 999999;

    }

    public double Marks(String grade) {
        double marks = 0;

        if (isFound("A", grade)) {
            marks = 80;
        } else if (isFound("B", grade)) {
            marks = 65;
        } else if (isFound("C", grade)) {
            marks = 50;
        }

        if (isFound("+", grade)) {
            marks += 5;
        } else if (isFound("-", grade)) {
            marks -= 5;
        }

        if (marks >= 0 && marks <= 100) {
            return marks;
        }
        return 999999;

    }

    public void printExpectedGrads() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.cgpammu", Context.MODE_PRIVATE);
        goal = (double) sharedPreferences.getFloat("goal", 0);
        ArrayList<Marks> takenSubjects = new ArrayList<>();

        Marks Title = new Marks("Expected grades", "", "", "", "", "", "", 1);
        takenSubjects.add(Title);

        if (cgpa <= 4 && cgpa >= 2) {
            takenSubjects.addAll(array(numOfSub));
        } else if (cgpa >= 0 && cgpa < 2) {
            Title = new Marks("This is too low", "", "", "", "", "", "", 0);
            takenSubjects.add(Title);
        } else {
            Title = new Marks("Wrong GPA", "", "", "", "", "", "", 0);
            takenSubjects.add(Title);
        }
        Title = new Marks("", "", "", "", "", "", "", 0);
        takenSubjects.add(Title);
        ListView listView = (ListView) findViewById(R.id.plans);

        GradesListAdapter arrayAdapter = new GradesListAdapter(this, R.layout.grades_layout, takenSubjects);
        listView.setAdapter(arrayAdapter);
    }

    @SuppressLint("SetTextI18n")
    public void updateThegrade() {
        int counter = 0;
        double totalmarksmin = 0, totalmarksmax = 0;
        if (subject == 1) {
//            TextView subject1 = (TextView) findViewById(R.id.subject1);
//            subject1.setText(grade);
            grades[0][0] = grade;
            if (TextUtils.isEmpty(numhours1.getText())) {
                grades[0][1] = "3";
            } else {
                grades[0][1] = numhours1.getText().toString();
            }
        } else if (subject == 2) {
//            TextView subject2 = (TextView) findViewById(R.id.subject2);
//            subject2.setText(grade);
            grades[1][0] = grade;
            if (TextUtils.isEmpty(numhours2.getText())) {
                grades[1][1] = "3";
            } else {
                grades[1][1] = numhours2.getText().toString();
            }
        } else if (subject == 3) {
//            TextView subject3 = (TextView) findViewById(R.id.subject3);
//            subject3.setText(grade);
            grades[2][0] = grade;
            if (TextUtils.isEmpty(numhours3.getText())) {
                grades[2][1] = "3";
            } else {
                grades[2][1] = numhours3.getText().toString();
            }
        } else if (subject == 4) {
//            TextView subject4 = (TextView) findViewById(R.id.subject4);
//            subject4.setText(grade);
            grades[3][0] = grade;
            if (TextUtils.isEmpty(numhours4.getText())) {
                grades[3][1] = "3";
            } else {
                grades[3][1] = numhours4.getText().toString();
            }
        } else if (subject == 5) {
//            TextView subject5 = (TextView) findViewById(R.id.subject5);
//            subject5.setText(grade);
            grades[4][0] = grade;
            if (TextUtils.isEmpty(numhours5.getText())) {
                grades[4][1] = "3";
            } else {
                grades[4][1] = numhours5.getText().toString();
            }
        } else if (subject == 6) {
//            TextView subject6 = (TextView) findViewById(R.id.subject6);
//            subject6.setText(grade);
            grades[5][0] = grade;
            if (TextUtils.isEmpty(numhours6.getText())) {
                grades[5][1] = "3";
            } else {
                grades[5][1] = numhours6.getText().toString();
            }
        }

        for (String[] strings : grades) {
            if (!strings[0].isEmpty() && strings[0].compareTo("-") != 0) {
                counter += Integer.parseInt(strings[1]);
                totalmarksmin += gradeMin(strings[0]) * Double.parseDouble(strings[1]);
                totalmarksmax += gradeMax(strings[0]) * Double.parseDouble(strings[1]);
            }
        }
        minGPA.setText(Double.toString(roundNum(totalmarksmin / counter)));
        aveGPA.setText(Double.toString(roundNum((totalmarksmin + totalmarksmax) / (2 * counter))));
        maxGPA.setText(Double.toString(roundNum(totalmarksmax / counter)));
    }

    public void updateThegrade2(int choice) {
        int counter = 0;
        double totalmarksmin = 0, totalmarksmax = 0;
        if (choice == 1) {
            if (TextUtils.isEmpty(numhours1.getText())) {
                grades[0][1] = "3";
            } else {
                grades[0][1] = numhours1.getText().toString();
            }
        } else if (choice == 2) {
            if (TextUtils.isEmpty(numhours2.getText())) {
                grades[1][1] = "3";
            } else {
                grades[1][1] = numhours2.getText().toString();
            }
        } else if (choice == 3) {
            if (TextUtils.isEmpty(numhours3.getText())) {
                grades[2][1] = "3";
            } else {
                grades[2][1] = numhours3.getText().toString();
            }
        } else if (choice == 4) {
            if (TextUtils.isEmpty(numhours4.getText())) {
                grades[3][1] = "3";
            } else {
                grades[3][1] = numhours4.getText().toString();
            }
        } else if (choice == 5) {
            if (TextUtils.isEmpty(numhours5.getText())) {
                grades[4][1] = "3";
            } else {
                grades[4][1] = numhours5.getText().toString();
            }
        } else if (choice == 6) {
            if (TextUtils.isEmpty(numhours6.getText())) {
                grades[5][1] = "3";
            } else {
                grades[5][1] = numhours6.getText().toString();
            }
        }
        for (int y = 0; y < grades.length; y++) {
            if (!grades[y][0].isEmpty() && grades[y][0].compareTo("-") != 0) {
                counter += Integer.parseInt(grades[y][1]);
                totalmarksmin += gradeMin(grades[y][0]) * Double.parseDouble(grades[y][1]);
                totalmarksmax += gradeMax(grades[y][0]) * Double.parseDouble(grades[y][1]);
            }
        }
        minGPA.setText(Double.toString(roundNum(totalmarksmin / counter)));
//        minGPA.setTextColor(Color.RED);
        maxGPA.setText(Double.toString(roundNum(totalmarksmax / counter)));
//        maxGPA.setTextColor(Color.RED);
    }

    public ArrayList<Marks> array(int num_of_sub) {
        String[] grads = {"A", "A-", "B+", "B", "B-", "C+", "C", ""};
        ArrayList<String> grades = new ArrayList<>();
        int hours1, hours2, hours3, hours4, hours5, hours6;

        if (TextUtils.isEmpty(F1numhours1.getText())) {
            hours1 = 3;
        } else {
            hours1 = Integer.valueOf(F1numhours1.getText().toString());
        }
        if (TextUtils.isEmpty(F1numhours2.getText())) {
            hours2 = 3;
        } else {
            hours2 = Integer.valueOf(F1numhours2.getText().toString());
        }
        if (TextUtils.isEmpty(F1numhours3.getText())) {
            hours3 = 3;
        } else {
            hours3 = Integer.valueOf(F1numhours3.getText().toString());
        }
        if (TextUtils.isEmpty(F1numhours4.getText())) {
            hours4 = 3;
        } else {
            hours4 = Integer.valueOf(F1numhours4.getText().toString());
        }
        if (TextUtils.isEmpty(F1numhours5.getText())) {
            hours5 = 3;
        } else {
            hours5 = Integer.valueOf(F1numhours5.getText().toString());
        }
        if (TextUtils.isEmpty(F1numhours6.getText())) {
            hours6 = 3;
        } else {
            hours6 = Integer.valueOf(F1numhours6.getText().toString());
        }

        if (num_of_sub < 6) {
            hours6 = 0;
        }
        if (num_of_sub < 5) {
            hours5 = 0;
        }
        if (num_of_sub < 4) {
            hours4 = 0;
        }
        if (num_of_sub < 3) {
            hours3 = 0;
        }
        if (num_of_sub < 2) {
            hours2 = 0;
        }
        double grmin, grmax;
        int s;
        int l1, l2, l3, l4, l5, l6;

        for (l1 = 0; l1 < 7; l1++) {
            if (num_of_sub < 6) {
                l1 = 7;
            }
            for (l2 = 0; l2 < 7; l2++) {
                if (num_of_sub < 5) {
                    l2 = 7;
                }
                for (l3 = 0; l3 < 7; l3++) {
                    if (num_of_sub < 4) {
                        l3 = 7;
                    }
                    for (l4 = 0; l4 < 7; l4++) {
                        if (num_of_sub < 3) {
                            l4 = 7;
                        }
                        for (l5 = 0; l5 < 7; l5++) {
                            if (num_of_sub < 2) {
                                l5 = 7;
                            }
                            for (l6 = 0; l6 < 7; l6++) {

                                grmin = (hours6 * grademin(grads[l1]) + hours5 * grademin(grads[l2]) + hours4 * grademin(grads[l3]) + hours3 * grademin(grads[l4]) + hours2 * grademin(grads[l5]) + hours1 * grademin(grads[l6])) / (hours1 + hours2 + hours3 + hours4 + hours5 + hours6);
                                grmax = (hours6 * grademax(grads[l1]) + hours5 * grademax(grads[l2]) + hours4 * grademax(grads[l3]) + hours3 * grademax(grads[l4]) + hours2 * grademax(grads[l5]) + hours1 * grademax(grads[l6])) / (hours1 + hours2 + hours3 + hours4 + hours5 + hours6);

                                if (possible(grmin, grmax)) {
                                    String[] showGrads = {hours6 + grads[l1], hours5 + grads[l2], hours4 + grads[l3], hours3 + grads[l4], hours2 + grads[l5], hours1 + grads[l6]};
                                    showGrads = Descending2(showGrads);
                                    grades.add(showGrads[0] + "," + showGrads[1] + "," + showGrads[2] + "," + showGrads[3] + "," + showGrads[4] + "," + showGrads[5] + "," + "," + "," + "," + "," + ",");
                                }
                            }
                        }
                    }
                }
            }
        }

        ArrayList<Marks> ToPrint = new ArrayList<>();
        Marks Print;

        Set<String> set = new HashSet<>(grades);
        grades.clear();
        grades.addAll(set);


        String[][] TempGrades1 = ArrangeGrades(grades);
        for (s = 0; s < grades.size(); s++) {
            Print = new Marks("", noNum(TempGrades1[s][0]), noNum(TempGrades1[s][1]), noNum(TempGrades1[s][2]), noNum(TempGrades1[s][3]), noNum(TempGrades1[s][4]), noNum(TempGrades1[s][5]), 0);
            ToPrint.add(Print);
        }
        grades.clear();

        String[] grads2 = {"A", "A-", "A-", "A-", "A-", "A-", "B+", "B+", "B+", "B+", "B+", "B", "B", "B", "B", "B", "B-", "B-", "B-", "B-", "B-", "C+", "C+", "C+", "C+", "C+", "C", "C", "C", "C", "C"};

        Print = new Marks("", "Grade", "", "Marks", "", "Points", "", 1);
        ToPrint.add(Print);

        int marks = 80;
        for (int x = 0; x < grads2.length; x++) {
            Print = new Marks("", grads2[x], "", Integer.toString(marks - x) + "%", "", Double.toString(roundNum(marksToGPAmeth(marks - x))), "", 0);
            ToPrint.add(Print);
        }

        return ToPrint;
    }

    double marksToGPAmeth(int marks) {
        return (((((double) marks) - 50) / 30) * 2 + 2);
    }

    public String[][] ArrangeGrades(ArrayList<String> oldGrades) {
        String[][] TempGrades = new String[oldGrades.size()][20];
        String NewTempGrades = "";
        ArrayList<String> Newlast = new ArrayList<>();
        String[] grads = {"A", "A-", "B+", "B", "B-", "C+", "C"};

        for (int s = 0; s < oldGrades.size(); s++) {
            TempGrades[s] = oldGrades.get(s).split(",", -1);
        }


        TempGrades = Descending(TempGrades, 0);
        int counter = 0;
        for (int y2 = 0; y2 < grads.length; y2++) {
            for (int r = numOfSub; r > 0; r--) {
                for (int y = 0; y < TempGrades.length; y++) {
                    for (int x = 0; x < 6; x++) {
                        if (noNumNoCon(grads[y2]).compareTo(noNumNoCon(TempGrades[y][x])) == 0) {
                            counter++;
                        }
                    }
                    if (counter == r) {
                        for (int x = 0; x < numOfSub; x++) {
                            NewTempGrades += TempGrades[y][x] + ",";
                            TempGrades[y][x] = "";
                        }
                        NewTempGrades += "," + "," + "," + "," + ",";
                        Newlast.add(NewTempGrades);
                        NewTempGrades = "";
                    }
                    counter = 0;
                }
            }
        }

        for (int s = 0; s < oldGrades.size(); s++) {
            TempGrades[s] = Newlast.get(s).split(",", -1);
        }

        return TempGrades;
    }

    public String[][] Descending(String[][] order, int n) {
        String temp;
        for (int i = 0; i < order.length; i++) {
            for (int j = i + 1; j < order.length; j++) {
                if (grade(order[i][n]) < grade(order[j][n])) {
                    for (int l = 0; l < order[i].length; l++) {
                        temp = order[i][l];
                        order[i][l] = order[j][l];
                        order[j][l] = temp;
                    }
                }
            }
        }
        return order;
    }

    public String[] Descending2(String[] order) {
        String temp;
        for (int i = 0; i < order.length; i++) {
            for (int j = i + 1; j < order.length; j++) {
                if (grade(order[i]) < grade(order[j])) {
                    temp = order[i];
                    order[i] = order[j];
                    order[j] = temp;
                }
            }
        }
        return order;
    }

    public boolean possible(double grmin, double grmmax) {
        if (cgpa == 4) {
            if (grmin == cgpa) {
                return true;
            } else {
                return false;
            }
        }
        if (grmin <= cgpa && cgpa < grmmax) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isFound(String p, String hph) {
        return hph.contains(p);
    }

    public String noNum(String grad) {
        if (!shownum) {
            String newGrade = "";
            if (isFound("A", grad)) {
                newGrade = "A";
            } else if (isFound("B", grad)) {
                newGrade = "B";
            } else if (isFound("C", grad)) {
                newGrade = "C";
            }

            if (isFound("+", grad)) {
                newGrade += "+";
            } else if (isFound("-", grad)) {
                newGrade += "-";
            }
            return newGrade;
        }
        return grad;
    }

    public String noNumNoCon(String grad) {
        String newGrade = "";
        if (isFound("A", grad)) {
            newGrade = "A";
        } else if (isFound("B", grad)) {
            newGrade = "B";
        } else if (isFound("C", grad)) {
            newGrade = "C";
        }

        if (isFound("+", grad)) {
            newGrade += "+";
        } else if (isFound("-", grad)) {
            newGrade += "-";
        }
        return newGrade;
    }

    public String fixGrade(String grad) {
        String newGrade = "";
        if (isFound("A", grad)) {
            newGrade = "A";
        } else if (isFound("B", grad)) {
            newGrade = "B";
        } else if (isFound("C", grad)) {
            newGrade = "C";
        }

        if (isFound("+", grad)) {
            newGrade += "+";
        } else if (isFound("-", grad)) {
            newGrade += "-";
        }
        return newGrade;
    }

    public double roundNum(double num) {
        return (Math.round(num * 100.0) / 100.0);
    }

//    public int numricalOf(String numrical) {
//        String[] temp = numrical.split("", -1);
//        for (String s : temp) {
//            if (isnumrical(s)) {
//                return Integer.parseInt(s);
//            }
//        }
//        return 0;
//    }
//
//    public boolean isnumrical(String str) {
//        boolean numeric = true;
//        try {
//            Double num = Double.parseDouble(str);
//        } catch (NumberFormatException e) {
//            numeric = false;
//        }
//        return numeric;
//    }

    private void closeKeyBoard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
