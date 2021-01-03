package com.example.maimyou.RecycleViewMaterials;

import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maimyou.R;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class ChildViewHolder extends com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder {
    private TextView mTextView;

    public ChildViewHolder(View itemView) {
        super(itemView);
        mTextView= itemView.findViewById(R.id.ChildText);
    }

    public void bind(Child child){
        mTextView.setText(child.Title);
    }
}
