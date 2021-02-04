package com.example.maimyou.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;

import com.example.maimyou.Classes.DisplayCourseForEdit;
import com.example.maimyou.R;

import java.util.ArrayList;

public class AdapterDisplayCourseForEdit extends ArrayAdapter<DisplayCourseForEdit> {
    private final Context mContext;
    private final int mResource;
//    CourseStructure courseStructure;

//    public void setCourseStructure(CourseStructure courseStructure) {
//        this.courseStructure = courseStructure;
//    }

    private static class ViewHolder {
        TextView Code, Subject, trimesterTitle;
        LinearLayout title, subjectContainer;
        AppCompatSpinner Grade;
    }

    public AdapterDisplayCourseForEdit(Context context, int resource, ArrayList<DisplayCourseForEdit> print) {
        super(context, resource, print);
        mContext = context;
        mResource = resource;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int mode = getItem(position).getMode();

        final ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        holder = new ViewHolder();
        if (mode == 1) {
            String trimesterTitle = getItem(position).getTrimesterTitle();
            holder.title = convertView.findViewById(R.id.title);
            holder.title.setVisibility(View.VISIBLE);
            holder.trimesterTitle = convertView.findViewById(R.id.trimesterTitle);
            holder.trimesterTitle.setText(trimesterTitle);
        } else if (mode == 2) {
            String Grade = getItem(position).getGrade(), Code = getItem(position).getCode(), Subject = getItem(position).getSubject(), Hours = getItem(position).getHours();
            holder.subjectContainer = convertView.findViewById(R.id.subjectContainer);
            holder.subjectContainer.setVisibility(View.VISIBLE);
            holder.Grade = convertView.findViewById(R.id.Grade);
            String [] list;
            if (Code.toLowerCase().contains("mpu")) {
                list = new String[]{"-","PS", "FL"};
            } else {
                list = new String[]{"-", "A+", "A", "A-", "B+", "B", "B-", "C+", "C", "FL"};
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,R.layout.spinner_grade, list);
            adapter.setDropDownViewResource(R.layout.spinner_grade_drop_down);
            holder.Grade.setAdapter(adapter);
//            holder.Grade.setSelection(0);
            holder.Grade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String text = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            holder.Code = convertView.findViewById(R.id.Code);
            holder.Code.setText(Code);
            holder.Subject = convertView.findViewById(R.id.Subject);
            holder.Subject.setText(Subject);
        }
        return convertView;
    }
}