package com.example.maimyou.RecycleViewMaterials;

import android.view.View;
import android.widget.TextView;
import com.example.maimyou.R;

public class ChildViewHolder extends com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder {
    private final TextView mTextView;
    Child child;

    public ChildViewHolder(View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.ChildText);
        mTextView.setOnClickListener(v ->
        {

            viewCourse(mTextView.getText().toString(),mTextView);
//            mTextView.setBackgroundColor(Color.parseColor("#c1c1c0"));
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                itemView.setBackgroundColor(itemView.getContext().getColor(R.color.colorRipple));
//            }
        });
    }

    public void viewCourse(String str,View view){
        if(child!=null){
            child.viewCourse(str,view);
        }
    }

    public void bind(Child child) {
        mTextView.setText(child.Title);
        this.child = child;
    }
}
