package com.example.maimyou.RecycleViewMaterials;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.maimyou.R;

public class ChildViewHolder extends com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder {
    private final TextView mTextView;

    public ChildViewHolder(View itemView) {
        super(itemView);
        mTextView= itemView.findViewById(R.id.ChildText);
        mTextView.setOnClickListener(v -> Toast.makeText(itemView.getContext(), mTextView.getText().toString(),Toast.LENGTH_LONG).show());
    }

    public void bind(Child child){
        mTextView.setText(child.Title);
    }
}
