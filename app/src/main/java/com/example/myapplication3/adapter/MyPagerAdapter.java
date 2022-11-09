package com.example.myapplication3.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class MyPagerAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> mFragments;

    public MyPagerAdapter(@NonNull FragmentActivity activity, ArrayList<Fragment> mFragments) {
        super(activity);
        this.mFragments = mFragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return mFragments.size();
    }
}