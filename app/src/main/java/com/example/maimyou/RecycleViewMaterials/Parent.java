package com.example.maimyou.RecycleViewMaterials;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class Parent extends ExpandableGroup<Child> {
    public Parent(String title, List<Child> items) {
        super(title, items);
    }
}
