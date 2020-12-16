package com.example.maimyou.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.maimyou.Fragments.Frag1;
import com.example.maimyou.Fragments.Frag2;
import com.example.maimyou.Fragments.Frag3;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new Frag1();
                break;
            case 1:
                fragment = new Frag2();
                break;
            case 2:
                fragment = new Frag3();
                break;
        }
        return fragment;
    }
    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "GPA to GRADES";
            case 1:
                return "GRADES to GPA";
            case 2:
                return "Marks to GPA";
        }
        return null;
    }

}
