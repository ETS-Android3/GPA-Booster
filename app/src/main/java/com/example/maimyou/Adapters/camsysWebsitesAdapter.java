package com.example.maimyou.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.example.maimyou.Classes.camsysPage;
import com.example.maimyou.R;
import java.util.ArrayList;

public class camsysWebsitesAdapter extends ArrayAdapter<camsysPage> {

    private Context mContext;
    private int mResource;


    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView webSiteName;
    }

    /**
     * Default constructor for the PersonListAdapter
     * //     *
     * //     * @param context
     * //     * @param resource
     * //     * @param objects
     * //
     */
    public camsysWebsitesAdapter(Context context, int resource, ArrayList<camsysPage> items) {
        super(context, resource, items);
        mContext = context;
        mResource = resource;
    }


    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String Name = getItem(position).getPageName();
        String WebS = getItem(position).getPageAdd();

        camsysPage websites= new camsysPage(Name, WebS);
        //ViewHolder object
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.webSiteName =  convertView.findViewById(R.id.webSiteName);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.webSiteName.setText(websites.getPageName());

        return convertView;
    }
}