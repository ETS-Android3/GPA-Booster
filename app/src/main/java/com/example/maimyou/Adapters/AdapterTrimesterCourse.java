package com.example.maimyou.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.maimyou.Activities.UploadCourseActivity;
import com.example.maimyou.R;
import com.example.maimyou.Classes.Trimester;
import com.example.maimyou.Classes.TrimesterCourse;

import java.util.ArrayList;

import static com.example.maimyou.Activities.UploadCourseActivity.touchClass;

public class AdapterTrimesterCourse extends ArrayAdapter<String> {
    private Context mContext;
    private int mResource;
    TrimesterCourse trimesterCourseToPrint = new TrimesterCourse();
    ArrayList<Boolean> heights = new ArrayList<>();
    ArrayList<View> con = new ArrayList<>();
    UploadCourseActivity uploadCourseActivity;
    ArrayList<Trimester> trimesters;


    public AdapterTrimesterCourse setTrimesters(ArrayList<Trimester> trimesters) {
        this.trimesters = trimesters;
        return this;
    }

    public AdapterTrimesterCourse setUploadCourseActivity(UploadCourseActivity uploadCourseActivity) {
        this.uploadCourseActivity = uploadCourseActivity;
        return this;
    }

    public AdapterTrimesterCourse setTrimesterCourseToPrint(TrimesterCourse trimesterCourseToPrint) {
        this.trimesterCourseToPrint = trimesterCourseToPrint;
        heights.clear();
        for (int i = 0; i < trimesterCourseToPrint.getPreRequest().size(); i++) {
            heights.add(false);
        }
        return this;
    }

    private static class ViewHolder {
        TextView Grade, Code, Subject, Hours, preRequest, trimesterTitle;
        LinearLayout preRequestee;
        RelativeLayout expand;
        FrameLayout thisaswell;
    }

    public AdapterTrimesterCourse(Context context, int resource, ArrayList<String> print) {
        super(context, resource, print);
        mContext = context;
        mResource = resource;
    }


    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (position < trimesterCourseToPrint.getSubjectHours().size()&&!touchClass.setIstouched) {
            String trimester = trimesterCourseToPrint.getTrimName().get(position);
            String subjectCode = trimesterCourseToPrint.getSubjectCodes().get(position);
            String SubectHours = trimesterCourseToPrint.getSubjectHours().get(position);
            String subjectName = trimesterCourseToPrint.getSubjectNames().get(position);
            String preRequest = trimesterCourseToPrint.getPreRequest().get(position);
            boolean elective = trimesterCourseToPrint.getElective().get(position);
            //ViewHolder object
            final ViewHolder holder;


            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.Grade = convertView.findViewById(R.id.Grade);
            holder.Code = convertView.findViewById(R.id.Code);
            holder.Subject = convertView.findViewById(R.id.Subject);
            holder.Hours = convertView.findViewById(R.id.Hours);
            holder.thisaswell = convertView.findViewById(R.id.thisaswell);

            holder.preRequest = convertView.findViewById(R.id.preRequest);
            holder.trimesterTitle = convertView.findViewById(R.id.trimesterTitle);
            holder.preRequestee = convertView.findViewById(R.id.preRequestee);
            holder.expand = convertView.findViewById(R.id.expand);


            if (trimester.compareTo("Title") == 0) {
                holder.trimesterTitle.setVisibility(View.VISIBLE);
                holder.thisaswell.setVisibility(View.VISIBLE);
                try {
                    holder.trimesterTitle.setText(getTrim(trimesterCourseToPrint.getTrimName().get(position + 1)));
                }catch(Exception ignored){

                }
                holder.preRequestee.setVisibility(View.GONE);
            } else {
                heights.set(position, false);
                holder.expand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!heights.get(position)) {
                            expand(holder.preRequestee);
                            heights.set(position, true);
                        } else {
                            slideView(holder.preRequestee, 300);
                            heights.set(position, false);
                        }
                    }
                });

                holder.trimesterTitle.setVisibility(View.GONE);
                holder.thisaswell.setVisibility(View.GONE);
                if (elective) {
                    holder.Code.setText(subjectCode + "\n(elective)");
                } else {
                    holder.Code.setText(subjectCode);
                }
                holder.Subject.setText(subjectName);
                holder.Hours.setText(SubectHours);
                holder.preRequest.setText(preRequest);
                String grade = "",lastGrade="";
                if(trimesters!=null) {
                    for (int i = 0; i < trimesters.size(); i++) {
                        grade = trimesters.get(i).getGradeFromCode(subjectCode);
                        if (!grade.isEmpty()) {
                            lastGrade = grade;
                        }
                    }
                }
                holder.Grade.setText(lastGrade);
            }
        }
        return convertView;
    }

    public static void expand(final View view) {
        view.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();

        // Set initial height to 0 and show the view
        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);

        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(), targetHeight);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(300);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = (int) (targetHeight * animation.getAnimatedFraction());
                view.setLayoutParams(layoutParams);
            }
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

    public void slideView(final View view, long duration) {
        int currentWidth = view.getHeight(), newWidth = 0;

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentWidth, newWidth)
                .setDuration(duration);

        /* We use an update listener which listens to each tick
         * and manually updates the height of the view  */

        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation1) {
                Integer value = (Integer) animation1.getAnimatedValue();
                view.getLayoutParams().height = value.intValue();
                view.requestLayout();
            }
        });

        /*  We use an animationSet to play the animation  */

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
//        animationSet.addListener(new AnimatorListenerAdapter() {
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                if (newHeight == 0) {
//                    view.setVisibility(View.GONE);
//                }
//            }
//        });
    }

    public int dpToPx(int dip) {
        Resources r = uploadCourseActivity.getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        return (int) px;
    }


    public String getTrim(String trim) {
        if (isNumeric(trim)) {
            int trimInt = Integer.parseInt(trim);
            trimInt++;

            while (trimInt > 3) {
                trimInt -= 3;
            }
            return "Trimester " + trimInt;
        }
        return "Trimester ?";
    }

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}