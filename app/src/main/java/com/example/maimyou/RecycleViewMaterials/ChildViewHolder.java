package com.example.maimyou.RecycleViewMaterials;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maimyou.R;

public class ChildViewHolder extends com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder {
    private final TextView mTextView;
    Child child;

    public ChildViewHolder(View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.ChildText);
        mTextView.setOnClickListener(v -> viewCourse(mTextView.getText().toString()));
    }

    public void viewCourse(String str){
        if(child!=null){
            child.viewCourse(str);
        }
    }

    public void bind(Child child) {
        mTextView.setText(child.Title);
        this.child = child;
    }
}
