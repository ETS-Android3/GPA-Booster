package com.example.maimyou.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.maimyou.R;

import java.util.ArrayList;

public class simpleAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private int mResource;

    private static class ViewHolder {
        TextView fileName;
    }

    public simpleAdapter(Context context, int resource, ArrayList<String> items) {
        super(context, resource, items);
        mContext = context;
        mResource = resource;
    }
    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String fileName = getItem(position);


        //ViewHolder object
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.fileName =  convertView.findViewById(R.id.string);


            convertView.setTag(holder);
        } else {
            holder =(ViewHolder) convertView.getTag();
        }

        holder.fileName.setText(fileName);

        return convertView;
    }

}