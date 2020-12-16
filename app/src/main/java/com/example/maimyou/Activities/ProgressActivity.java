package com.example.maimyou.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.example.maimyou.R;
import com.example.maimyou.Classes.Trimester;
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
import static com.example.maimyou.Activities.RegisterActivity.SHARED_PREFS;

public class ProgressActivity extends AppCompatActivity {
    LineChart lineChart;
    ArrayList<Trimester> trimesters;
    String NameS = "", Id = "", DegreeS = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        lineChart = findViewById(R.id.lineChart);


        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("CamsysInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Name").getValue() != null) {
                    NameS = snapshot.child("Name").getValue().toString();
                }

                if (snapshot.child("Id").getValue() != null) {
                    Id = snapshot.child("Id").getValue().toString();
                }

                if (snapshot.child("Degree").getValue() != null) {
                    DegreeS = snapshot.child("Degree").getValue().toString();
                }
                if (snapshot.child("Trimesters").getValue() != null) {
                    trimesters = new ArrayList<>();
                    Iterable<DataSnapshot> children = snapshot.child("Trimesters").getChildren();
                    for (DataSnapshot child : children) {
                        if (child.getValue() != null) {
                            trimesters.add(getTrim(child));
                        }
                    }
                    printChart();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    public void printChart(){
        ArrayList<String> aXes=new ArrayList<>();
        ArrayList<Entry> yXes = new ArrayList<>();
        double x = 0;
        int numDataPoint = trimesters.size();

        for(int i=0;i<numDataPoint;i++){
            yXes.add(new Entry(getFloat(trimesters.get(i).getGPA()),i));
            System.out.println(getFloat(trimesters.get(i).getGPA()));
            aXes.add(i,trimesters.get(i).getSemesterName().substring(0,1));
            System.out.println("Trimester "+trimesters.get(i).getSemesterName());
        }

        String[] xaxes = new String[aXes.size()];
        for(int i=0; i<aXes.size();i++){
            xaxes[i] = aXes.get(i);
        }

        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        LineDataSet lineDataSet1 = new LineDataSet(yXes,"GPA");
        lineDataSet1.setDrawCircles(false);
        lineDataSet1.setColor(Color.BLUE);

        lineDataSets.add(lineDataSet1);
        lineChart.setData(new LineData(xaxes,lineDataSets));
        lineChart.setVisibleXRangeMaximum(65f);
    }
    public float getFloat(String s){
        try {
            return (float) Double.parseDouble(s);
        }catch (Exception ignored){
            return 0;
        }
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
            if (child.child("subjectCodes").getValue() != null&&child.child("subjectNames").getValue() != null&&child.child("subjectGades").getValue() != null) {
                trimester.addSubject(child.child("subjectCodes").getValue().toString(),child.child("subjectNames").getValue().toString(),child.child("subjectGades").getValue().toString());
            }
        }
        return trimester;
    }

    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide0,R.anim.slide_in_top);
    }
}
