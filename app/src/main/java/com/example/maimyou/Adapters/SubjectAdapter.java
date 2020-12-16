package com.example.maimyou.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.maimyou.R;
import com.example.maimyou.Classes.Trimester;

import java.util.ArrayList;

public class SubjectAdapter extends ArrayAdapter<Trimester.subjects> {

    private Context mContext;
    private int mResource;


    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView Code,CourseTitle,Grade;
    }

    /**
     * Default constructor for the PersonListAdapter
     * //     *
     * //     * @param context
     * //     * @param resource
     * //     * @param objects
     * //
     */
    public SubjectAdapter(Context context, int resource, ArrayList<Trimester.subjects> items) {
        super(context, resource, items);
        mContext = context;
        mResource = resource;
    }
    Trimester.subjects subjects;
    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String Code = getItem(position).getSubjectCodes();
        String CourseTitle = getItem(position).getSubjectNames();
        String Grade = getItem(position).getSubjectGades();

        subjects= new Trimester.subjects(Code,CourseTitle,Grade);
        //ViewHolder object
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.Code =  convertView.findViewById(R.id.Code);
            holder.CourseTitle =  convertView.findViewById(R.id.CourseTitle);
            holder.Grade =  convertView.findViewById(R.id.Grade);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.Code.setText(Code);
        holder.CourseTitle.setText(CourseTitle);
        holder.Grade.setText(Grade);

        return convertView;
    }
}