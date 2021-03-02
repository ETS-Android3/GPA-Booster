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
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import com.example.maimyou.Classes.DisplayCourseForEdit;
import com.example.maimyou.Fragments.FragmentEdit;
import com.example.maimyou.R;

import java.util.ArrayList;

public class AdapterDisplayCourseForEdit extends ArrayAdapter<DisplayCourseForEdit> {
    private final Context mContext;
    private final int mResource;
    FragmentEdit fragmentEdit;

    public void setFragmentEdit(FragmentEdit fragmentEdit) {
        this.fragmentEdit = fragmentEdit;
    }

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
    public View getView(final int i, View convertView, ViewGroup parent) {
        int mode = getItem(i).getMode();

        final ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        holder = new ViewHolder();
        if (mode == 1) {
            String trimesterTitle = getItem(i).getTrimesterTitle();
            holder.title = convertView.findViewById(R.id.title);
            holder.title.setVisibility(View.VISIBLE);
            holder.trimesterTitle = convertView.findViewById(R.id.trimesterTitle);
            holder.trimesterTitle.setText(trimesterTitle);
        } else if (mode == 2) {
            String Grade = getItem(i).getGrade(), Code = getItem(i).getCode(), Subject = getItem(i).getSubject(), Hours = getItem(i).getHours(),Elective=getItem(i).getElective();
            holder.subjectContainer = convertView.findViewById(R.id.subjectContainer);
            holder.subjectContainer.setVisibility(View.VISIBLE);
            holder.Grade = convertView.findViewById(R.id.Grade);
            ArrayList<String> list = new ArrayList<>();
            if (Code.toLowerCase().contains("mpu")||isNumeric(Code)) {
                list.add("-");
                list.add("PS");
                list.add("FL");
            } else {
                list.add("-");
                list.add("A+");
                list.add("A");
                list.add("A-");
                list.add("B+");
                list.add("B");
                list.add("B-");
                list.add("C+");
                list.add("C");
                list.add("CON");
                list.add("FL");
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, R.layout.spinner_grade, list);
            adapter.setDropDownViewResource(R.layout.spinner_grade_drop_down);
            holder.Grade.setAdapter(adapter);
            int index = list.size() - 1;
            if (list.contains(Grade)) {
                index = list.indexOf(Grade);
            }
            holder.Grade.setSelection(index);
            holder.Grade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String text = parent.getItemAtPosition(position).toString();
                    fragmentEdit.setGrade(getItem(i).getCode(),text,getItem(i).getHours(),getItem(i).getSem(),getItem(i).getSubject());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            holder.Code = convertView.findViewById(R.id.Code);
            if(isNumeric(Code)){
                Code="";
            }
            if(Elective.contains("true")){
                Code+="\nElective";
            }
            holder.Code.setText(Code);
            holder.Subject = convertView.findViewById(R.id.Subject);
            holder.Subject.setText(Subject);
        }
        return convertView;
    }
    public boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}