package com.example.myapplication3;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication3.adapter.MyPagerAdapter;
import com.example.myapplication3.entity.TabEntity;
import com.example.myapplication3.fragments.HomeFragment;
import com.example.myapplication3.fragments.MyFragment;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity {

    private String[] mTitles = {"首页", "我的"};
    private int[] mIconUnselectIds = {
            R.drawable.ic__home, R.drawable.ic_account};
    private int[] mIconSelectIds = {
            R.drawable.ic__home_press, R.drawable.ic_account_press};
    private ViewPager2 viewPager;
    private CommonTabLayout commonTabLayout;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    @Override
    protected int initLayout() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        viewPager = findViewById(R.id.viewpager);
        commonTabLayout = findViewById(R.id.commonTabLayout);
    }

    @Override
    protected void initData() {
        mFragments.add(HomeFragment.newInstance());
        mFragments.add(MyFragment.newInstance());
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        commonTabLayout.setTabData(mTabEntities);
        commonTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        viewPager.setAdapter(new MyPagerAdapter(this, mFragments));
        viewPager.setOffscreenPageLimit(mFragments.size());
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {  //页面滑动事件监听
            @Override
            public void onPageSelected(int position) {
                commonTabLayout.setCurrentTab(position);
            }
        });
    }
}