package com.example.maimyou.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.maimyou.Activities.CourseStructure;
import com.example.maimyou.Classes.DisplayCourse;
import com.example.maimyou.R;

import java.util.ArrayList;

public class AdapterDisplayCourse extends ArrayAdapter<DisplayCourse> {
    private final Context mContext;
    private final int mResource;
    CourseStructure courseStructure;

    public void setCourseStructure(CourseStructure courseStructure) {
        this.courseStructure = courseStructure;
    }

    private static class ViewHolder {
        TextView Grade, Code, Subject, Hours, preRequest, trimesterTitle, TotalHours;
        RelativeLayout title, expand, Bottom;
        LinearLayout preRequisite;
    }

    public AdapterDisplayCourse(Context context, int resource, ArrayList<DisplayCourse> print) {
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
        if (mode == 0) {
            holder.title = convertView.findViewById(R.id.title);
            holder.title.setVisibility(View.INVISIBLE);
        } else if (mode == 1) {
            String trimesterTitle = getItem(position).getTrimesterTitle();
            holder.title = convertView.findViewById(R.id.title);
            holder.title.setVisibility(View.VISIBLE);
            holder.trimesterTitle = convertView.findViewById(R.id.trimesterTitle);
            holder.trimesterTitle.setText(trimesterTitle);
        } else if (mode == 2) {
            String Elective = getItem(position).getElective(), Grade = getItem(position).getGrade(), Code = getItem(position).getCode(), Subject = getItem(position).getSubject(), Hours = getItem(position).getHours(), preRequest = getItem(position).getPreRequest();
            holder.expand = convertView.findViewById(R.id.expand);
            holder.expand.setVisibility(View.VISIBLE);
            holder.preRequisite = convertView.findViewById(R.id.preRequisite);
            holder.expand.setOnClickListener(v -> {
                if (holder.preRequest.getHeight() > 0) {
                    contract(holder.preRequisite, 300);
                } else {
                    expand(holder.preRequisite, 300);
                }
            });
            holder.Grade = convertView.findViewById(R.id.Grade);
            holder.Grade.setText(Grade);
            holder.Code = convertView.findViewById(R.id.Code);
            if (Elective.toLowerCase().contains("true")) {
                Code += "\nElective";
            }
            holder.Code.setText(Code);
            holder.Subject = convertView.findViewById(R.id.Subject);
            holder.Subject.setText(Subject);
            holder.Hours = convertView.findViewById(R.id.Hours);
            holder.Hours.setText(Hours);
            holder.preRequest = convertView.findViewById(R.id.preRequest);
            holder.preRequest.setText(Html.fromHtml(preRequest), TextView.BufferType.SPANNABLE);
        } else if (mode == 3) {
            String TotalHours = getItem(position).getTotalHours();
            holder.Bottom = convertView.findViewById(R.id.Bottom);
            holder.Bottom.setVisibility(View.VISIBLE);
            holder.TotalHours = convertView.findViewById(R.id.TotalHours);
            holder.TotalHours.setText(TotalHours);
        }
        return convertView;
    }

    public void expand(final View view, int duration) {
        view.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();

        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);

        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(), targetHeight);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(duration);
        anim.addUpdateListener(animation -> {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = (int) (targetHeight * animation.getAnimatedFraction());
            view.setLayoutParams(layoutParams);
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // At the end of animation, set the height to wrap content
                // This fix is for long views that are not shown on screen
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
        });
        anim.start();
    }

    public void contract(final View view, long duration) {
        int currentHeight = view.getHeight();
        int newHeight = 0;
        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentHeight, newHeight)
                .setDuration(duration);

        slideAnimator.addUpdateListener(animation1 -> {
            view.getLayoutParams().height = (Integer) animation1.getAnimatedValue();
            view.requestLayout();
        });

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
    }
}