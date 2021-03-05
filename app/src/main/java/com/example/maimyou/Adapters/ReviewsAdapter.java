package com.example.maimyou.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.maimyou.Classes.reviews;
import com.example.maimyou.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewsAdapter extends ArrayAdapter<reviews.reviewArray> {

    private final Context mContext;
    private final int mResource;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView Name, time, review;
        CircleImageView profilePicture;
        RatingBar ratingBar;
    }

    /**
     * Default constructor for the PersonListAdapter
     * //     *
     * //     * @param context
     * //     * @param resource
     * //     * @param objects
     * //
     */
    public ReviewsAdapter(Context context, int resource, ArrayList<reviews.reviewArray> items) {
        super(context, resource, items);
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String name = getItem(position).getName();
        String time = getItem(position).getTime();
        String review = getItem(position).getReview();
        String profilePic = getItem(position).getProfilePic();
        float rate = getItem(position).getRate();

        //ViewHolder object
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.Name = convertView.findViewById(R.id.Name);
            holder.time = convertView.findViewById(R.id.time);
            holder.review = convertView.findViewById(R.id.review);
            holder.profilePicture = convertView.findViewById(R.id.profilePicture);
            holder.ratingBar = convertView.findViewById(R.id.ratingBar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.Name.setText(name);
        holder.time.setText(getTime(time));
        holder.review.setText(review);
        if (profilePic.isEmpty()) {
            Picasso.get().load(R.drawable.avatar).into(holder.profilePicture);
        } else {
            Picasso.get().load(profilePic).error(R.drawable.avatar).into(holder.profilePicture);
        }
        holder.ratingBar.setRating(rate);
        return convertView;
    }

    public String getTime(String Unix) {
        long time = System.currentTimeMillis() - getLong(Unix);
        if (time < 60000) {
            return "less than a minute ago";
        }
        long years = 0, months = 0, weeks = 0, days = 0, hours = 0, minutes = 0;
        time /= 1000;
        while (time >= 60) {
            if (time >= 31536000) {
                time -= 31536000;
                years += 1;
            } else if (time >= 2592000) {
                time -= 2592000;
                months += 1;
            } else if (time >= 604800) {
                time -= 604800;
                weeks += 1;
            } else if (time >= 86400) {
                time -= 86400;
                days += 1;
            } else if (time >= 3600) {
                time -= 3600;
                hours += 1;
            } else {
                time -= 60;
                minutes += 1;
            }
        }

        if (years > 0) {
            if (years > 1) {
                return years + " years ago";
            } else {
                return "a year ago";
            }
        } else if (months > 0) {
            if (months > 1) {
                return months + " months ago";
            } else {
                return "a month ago";
            }
        } else if (weeks > 0) {
            if (weeks > 1) {
                return weeks + " weeks ago";
            } else {
                return "a week ago";
            }
        } else if (days > 0) {
            if (days > 1) {
                return days + " days ago";
            } else {
                return "a day ago";
            }
        } else if (hours > 0) {
            if (hours > 1) {
                return hours + " hours ago";
            } else {
                return "an hour ago";
            }
        } else if (minutes > 0) {
            if (minutes > 1) {
                return minutes + " minutes ago";
            } else {
                return "a minute ago";
            }
        }
        return "";
    }

    public long getLong(String str){
        try {
            return Long.parseLong(str);
        }catch (Exception ignored){
            return 0;
        }
    }
}