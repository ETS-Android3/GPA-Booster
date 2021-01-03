package com.example.maimyou.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.maimyou.R;
import com.example.maimyou.RecycleViewMaterials.Child;
import com.example.maimyou.RecycleViewMaterials.ChildAdapter;
import com.example.maimyou.RecycleViewMaterials.Parent;

import java.util.ArrayList;

public class CourseStructure extends AppCompatActivity {

    //Views
    RecyclerView recyclerView;

    //Vars

    public void back(View view) {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_structure);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(new ChildAdapter(addCourses(new ArrayList<>())));
    }

    public ArrayList<Parent> addCourses(ArrayList<Parent> parent){
        parent.add(new Parent("Electronics",addTrimesters(new ArrayList<>(),"")));
        parent.add(new Parent("Computer",addTrimesters(new ArrayList<>(),"")));
        parent.add(new Parent("Telecommunications",addTrimesters(new ArrayList<>(),"")));
        parent.add(new Parent("Electrical",addTrimesters(new ArrayList<>(),"")));
        parent.add(new Parent("NanoTechnology",addTrimesters(new ArrayList<>(),"")));
        return parent;
    }

    public ArrayList<Child> addTrimesters(ArrayList<Child> child,String major){
        child.add(new Child("Trimister 1"));
        child.add(new Child("Trimister 2"));
        child.add(new Child("Trimister 3"));
        return child;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}