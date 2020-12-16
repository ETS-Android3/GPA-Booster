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

import com.example.maimyou.R;
import com.example.maimyou.Classes.Trimester;

import java.util.ArrayList;

public class AdapterTrimester extends ArrayAdapter<Trimester> {

    private Context mContext;
    private int mResource;
    int width = 0;


    public AdapterTrimester setWidth(int width) {
        this.width = width;
        return this;
    }

    ListView subjectsList;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView trimester, gpa, hours;
        ListView subjectsList;
    }

    /**
     * Default constructor for the PersonListAdapter
     * //     *
     * //     * @param context
     * //     * @param resource
     * //     * @param objects
     * //
     */
    public AdapterTrimester(Context context, int resource, ArrayList<Trimester> items) {
        super(context, resource, items);
        mContext = context;
        mResource = resource;
    }

    SubjectAdapter adapter;
    ArrayList<Trimester.subjects> subjects;
    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String trimester = getItem(position).getSemesterName();
        String gpa = getItem(position).getGPA();
        String hours = getItem(position).getHours();
        subjects = getItem(position).getSubjects();

        //ViewHolder object
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.trimester = convertView.findViewById(R.id.trimester);
            holder.gpa = convertView.findViewById(R.id.gpa);
            holder.hours = convertView.findViewById(R.id.hours);
            holder.subjectsList = convertView.findViewById(R.id.subjectsList);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.trimester.setText("Trimester " + trimester);
        holder.gpa.setText(gpa);
        holder.hours.setText(hours);
        adapter = new SubjectAdapter(mContext, R.layout.subject, subjects);
        holder.subjectsList.setAdapter(adapter);
        setListViewHeightBasedOnChildren(holder.subjectsList);
        return convertView;
    }


    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}