package com.example.maimyou.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.maimyou.Classes.Marks;
import com.example.maimyou.R;

import java.util.ArrayList;


public class GradesListAdapter extends ArrayAdapter<Marks> {

    private final Context mContext;
    private final int mResource;

    private static class ViewHolder {
        TextView TitleHolder;
        TextView GradesHolder0;
        TextView GradesHolder1;
        TextView GradesHolder2;
        TextView GradesHolder3;
        TextView GradesHolder4;
        TextView GradesHolder5;
    }

    public GradesListAdapter(Context context, int resource, ArrayList<Marks> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the persons information
        String Title = getItem(position).getTitle();
        String grade0 = getItem(position).getFirst();
        String grade1 = getItem(position).getSecond();
        String grade2 = getItem(position).getThird();
        String grade3 = getItem(position).getForth();
        String grade4 = getItem(position).getFifth();
        String grade5 = getItem(position).getSixth();
        int color = getItem(position).getColor();

        //ViewHolder object
        ViewHolder holder;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        holder = new ViewHolder();
        holder.TitleHolder = convertView.findViewById(R.id.ForTitle);
        holder.GradesHolder0 = convertView.findViewById(R.id.Gradestext1);
        holder.GradesHolder1 = convertView.findViewById(R.id.Gradestext2);
        holder.GradesHolder2 = convertView.findViewById(R.id.Gradestext3);
        holder.GradesHolder3 = convertView.findViewById(R.id.Gradestext4);
        holder.GradesHolder4 = convertView.findViewById(R.id.Gradestext5);
        holder.GradesHolder5 = convertView.findViewById(R.id.Gradestext6);
        convertView.setTag(holder);

        if (color == 0) {
            holder.GradesHolder0.setTextColor(getColour(grade0));
            holder.GradesHolder1.setTextColor(getColour(grade1));
            holder.GradesHolder2.setTextColor(getColour(grade2));
            holder.GradesHolder3.setTextColor(getColour(grade3));
            holder.GradesHolder4.setTextColor(getColour(grade4));
            holder.GradesHolder5.setTextColor(getColour(grade5));

            convertView.setBackgroundColor(Color.alpha(0));
            holder.TitleHolder.setVisibility(View.GONE);
            holder.GradesHolder0.setTextSize(16);
            holder.GradesHolder1.setTextSize(16);
            holder.GradesHolder2.setTextSize(16);
            holder.GradesHolder3.setTextSize(16);
            holder.GradesHolder4.setTextSize(16);
            holder.GradesHolder5.setTextSize(16);

        } else if (color == 1) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
            holder.TitleHolder.setTextColor(mContext.getResources().getColor(R.color.backGroundColour));
            holder.TitleHolder.setTextSize(20);
            holder.GradesHolder0.setTextSize(20);
            holder.GradesHolder0.setTextColor(mContext.getResources().getColor(R.color.backGroundColour));
            holder.GradesHolder1.setTextSize(20);
            holder.GradesHolder1.setTextColor(mContext.getResources().getColor(R.color.backGroundColour));
            holder.GradesHolder2.setTextSize(20);
            holder.GradesHolder2.setTextColor(mContext.getResources().getColor(R.color.backGroundColour));
            holder.GradesHolder3.setTextSize(20);
            holder.GradesHolder3.setTextColor(mContext.getResources().getColor(R.color.backGroundColour));
            holder.GradesHolder4.setTextSize(20);
            holder.GradesHolder4.setTextColor(mContext.getResources().getColor(R.color.backGroundColour));
            holder.GradesHolder5.setTextSize(20);
            holder.GradesHolder5.setTextColor(mContext.getResources().getColor(R.color.backGroundColour));
        }

        holder.TitleHolder.setText(Title);
        holder.GradesHolder0.setText(grade0);
        holder.GradesHolder1.setText(grade1);
        holder.GradesHolder2.setText(grade2);
        holder.GradesHolder3.setText(grade3);
        holder.GradesHolder4.setText(grade4);
        holder.GradesHolder5.setText(grade5);

        checkInputVisibility(holder.TitleHolder, Title);
        checkInputVisibility(holder.GradesHolder0, grade0);
        checkInputVisibility(holder.GradesHolder1, grade1);
        checkInputVisibility(holder.GradesHolder2, grade2);
        checkInputVisibility(holder.GradesHolder3, grade3);
        checkInputVisibility(holder.GradesHolder4, grade4);
        checkInputVisibility(holder.GradesHolder5, grade5);

        return convertView;
    }

    public int getColour(String grade) {
        int color;
        if (isFound("A-", grade)) {
            color = (Color.rgb(0, 200, 0));
        } else if (isFound("A", grade)) {
            color = (Color.rgb(0, 100, 0));
        } else if (isFound("B+", grade)) {
            color = (Color.rgb(100, 100, 0));
        } else if (isFound("B-", grade)) {
            color = (Color.rgb(200, 200, 0));
        } else if (isFound("B", grade)) {
            color = (Color.rgb(150, 150, 0));
        } else if (isFound("C+", grade)) {
            color = (Color.rgb(255, 0, 0));
        } else if (isFound("C", grade)) {
            color = (Color.rgb(155, 0, 0));
        } else {
            color = (Color.rgb(0, 0, 0));
        }
        return color;
    }

    public void checkInputVisibility(TextView textView, String str) {
        if (str.trim().isEmpty()) {
            textView.setVisibility(View.GONE);
        }
    }

    public boolean isFound(String p, String hph) {
        return hph.contains(p);
    }
}
