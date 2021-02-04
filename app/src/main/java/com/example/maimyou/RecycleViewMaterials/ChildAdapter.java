package com.example.maimyou.RecycleViewMaterials;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.maimyou.R;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class ChildAdapter extends ExpandableRecyclerViewAdapter<ParentViewHolder,ChildViewHolder> {
    int resChild,resParent;
    public ChildAdapter(List<? extends ExpandableGroup> groups,int resParent,int resChild) {
        super(groups);
        this.resChild=resChild;
        this.resParent=resParent;
    }

    @Override
    public ParentViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(resParent,parent,false);
        return new ParentViewHolder(v);
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(resChild,parent,false);
        return new ChildViewHolder(v);
    }

    @Override
    public void onBindChildViewHolder(ChildViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final Child child = (Child) group.getItems().get(childIndex);
        holder.bind(child);
    }

    @Override
    public void onBindGroupViewHolder(ParentViewHolder holder, int flatPosition, ExpandableGroup group) {
        final Parent parent = (Parent) group;
        holder.bind(parent);
    }
}
