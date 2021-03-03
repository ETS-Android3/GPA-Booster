package com.example.maimyou.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.maimyou.Classes.Gradedsubjects;
import com.example.maimyou.Classes.Trimester;
import com.example.maimyou.R;

import java.util.ArrayList;

public class AdapterSortSubjects extends ArrayAdapter<Gradedsubjects> {

    private Context mContext;
    private int mResource;


    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView code, name, grade;
        View end;
    }

    /**
     * Default constructor for the PersonListAdapter
     * //     *
     * //     * @param context
     * //     * @param resource
     * //     * @param objects
     * //
     */
    public AdapterSortSubjects(Context context, int resource, ArrayList<Gradedsubjects> items) {
        super(context, resource, items);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String Code = getItem(position).getCode();
        String Name = getItem(position).getName();
        String Grade = getItem(position).getGrade();
        boolean end = getItem(position).isEnd();

        //ViewHolder object
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.code = convertView.findViewById(R.id.Code);
            holder.name = convertView.findViewById(R.id.CourseTitle);
            holder.grade = convertView.findViewById(R.id.Grade);
            holder.end = convertView.findViewById(R.id.end);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.code.setText(Code);
        holder.name.setText(Name);
        holder.grade.setText(Grade);
        if(end){
            holder.end.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

}