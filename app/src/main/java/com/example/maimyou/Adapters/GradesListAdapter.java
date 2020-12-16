package com.example.maimyou.Adapters;

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

    private Context mContext;
    private int mResource;
    int[] colors = new int[500];

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView TitleHolder;
        TextView[] GradesHolder=new TextView[6];
    }

    /**
     * Default constructor for the PersonListAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public GradesListAdapter(Context context, int resource, ArrayList<Marks> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the persons information
        String Title = getItem(position).getTitle();
        String[] grade=new String[6];
        grade[0] = getItem(position).getFirst();
        grade[1] = getItem(position).getSecond();
        grade[2] = getItem(position).getThird();
        grade[3] = getItem(position).getForth();
        grade[4] = getItem(position).getFifth();
        grade[5] = getItem(position).getSixth();

        colors[position]=0;
        //ViewHolder object
        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.TitleHolder = (TextView) convertView.findViewById(R.id.ForTitle);
            holder.GradesHolder[0] = (TextView) convertView.findViewById(R.id.Gradestext1);
            holder.GradesHolder[1] = (TextView) convertView.findViewById(R.id.Gradestext2);
            holder.GradesHolder[2] = (TextView) convertView.findViewById(R.id.Gradestext3);
            holder.GradesHolder[3] = (TextView) convertView.findViewById(R.id.Gradestext4);
            holder.GradesHolder[4] = (TextView) convertView.findViewById(R.id.Gradestext5);
            holder.GradesHolder[5] = (TextView) convertView.findViewById(R.id.Gradestext6);


            convertView.setTag(holder);
        }
        else{ holder = (ViewHolder) convertView.getTag(); }

        if(isFound("Expected grades",Title)||isFound("Marks",grade[2])){
            colors[position]=1;
        }else
        if(isFound("hours",Title)||isFound("Enter",Title)){
            colors[position]=2;
        }
        for(int i=0;i<6;i++) {
            if (isFound("A-", grade[i])) {
                holder.GradesHolder[i].setTextColor(Color.rgb(0, 200, 0));
            }else if (isFound("A", grade[i])) {
                holder.GradesHolder[i].setTextColor(Color.rgb(0, 100, 0));
            }else if(isFound("B+",grade[i])){
                holder.GradesHolder[i].setTextColor(Color.rgb(100, 100, 0));
            }else if(isFound("B-",grade[i])){
                holder.GradesHolder[i].setTextColor(Color.rgb(200, 200, 0));
            }else if(isFound("B",grade[i])){
                holder.GradesHolder[i].setTextColor(Color.rgb(150, 150, 0));
            }else if(isFound("C+",grade[i])){
                holder.GradesHolder[i].setTextColor(Color.rgb(255, 0, 0));
            }else if(isFound("C",grade[i])){
                holder.GradesHolder[i].setTextColor(Color.rgb(155, 0, 0));
            }else{
                holder.GradesHolder[i].setTextColor(Color.rgb(0, 0, 0));
            }
        }

        if(colors[position]==0){
            convertView.setBackgroundColor(Color.alpha(0));
            holder.TitleHolder.setTextSize(16);
            holder.GradesHolder[0].setTextSize(16);
            holder.GradesHolder[1].setTextSize(16);
            holder.GradesHolder[2].setTextSize(16);
            holder.GradesHolder[3].setTextSize(16);
            holder.GradesHolder[4].setTextSize(16);
            holder.GradesHolder[5].setTextSize(16);

        } else if (colors[position]==1) {
            convertView.setBackgroundColor(Color.BLUE);
            holder.TitleHolder.setTextSize(20);
            holder.GradesHolder[0].setTextSize(20);
            holder.GradesHolder[1].setTextSize(20);
            holder.GradesHolder[2].setTextSize(20);
            holder.GradesHolder[3].setTextSize(20);
            holder.GradesHolder[4].setTextSize(20);
            holder.GradesHolder[5].setTextSize(20);
        } else if (colors[position]==2) {
            convertView.setBackgroundColor(Color.rgb(33,150,243));
            holder.TitleHolder.setTextColor(Color.parseColor("#000000"));
            holder.TitleHolder.setTextSize(14);
        }

        holder.TitleHolder.setText(Title);
        holder.GradesHolder[0].setText(grade[0]);
        holder.GradesHolder[1].setText(grade[1]);
        holder.GradesHolder[2].setText(grade[2]);
        holder.GradesHolder[3].setText(grade[3]);
        holder.GradesHolder[4].setText(grade[4]);
        holder.GradesHolder[5].setText(grade[5]);



        return convertView;
    }
    public boolean isFound(String p,String hph){
        boolean Found = hph.indexOf(p) !=-1? true: false;
        return Found;
    }

}
