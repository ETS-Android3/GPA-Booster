package com.example.maimyou.RecycleViewMaterials;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.example.maimyou.R;

import static com.example.maimyou.Activities.DashBoardActivity.Intake;
import static com.example.maimyou.Activities.DashBoardActivity.IntakeView;
import static com.example.maimyou.Activities.DashBoardActivity.actionListener;

public class ChildViewHolder extends com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder {
    private final TextView mTextView;
    Child child;

    public ChildViewHolder(View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.ChildText);
        mTextView.setOnClickListener(v ->{
            Intake = mTextView.getText().toString();
            if(IntakeView!=null){
                IntakeView.setBackgroundColor(Color.TRANSPARENT);
            }
            IntakeView = mTextView;
            actionListener.performAction();
            mTextView.setBackgroundColor(Color.parseColor("#c1c1c0"));
        });
    }

    public void bind(Child child) {
        mTextView.setText(child.Title);
        if(child.Title.compareTo(Intake)==0){
            mTextView.setBackgroundColor(Color.parseColor("#c1c1c0"));
        }else{
            mTextView.setBackgroundColor(Color.TRANSPARENT);
        }
        this.child = child;
    }
}
