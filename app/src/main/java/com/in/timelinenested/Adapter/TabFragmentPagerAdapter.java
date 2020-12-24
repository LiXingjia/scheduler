package com.in.timelinenested.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.in.timelinenested.fragment.ContactBlackFragment;
import com.in.timelinenested.fragment.ContactVirtualeFragment;
import com.in.timelinenested.fragment.ContactMainFragment;

public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] mTitles = new String[]{"好友","虚拟好友","小黑屋"};

    public TabFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 1){
            return new ContactVirtualeFragment();
        } else if (position == 2) {
            return new ContactBlackFragment();
        }
        return new ContactMainFragment();
    }

    @Override
    public int getCount() {
        return mTitles.length;//有几个页面
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}




