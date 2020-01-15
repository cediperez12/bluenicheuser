package com.coecs.bluenicheuser;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] fragments;

    public ViewPagerAdapter(FragmentManager fm){
        super(fm);
        fragments = new Fragment[]{
                new InstructionFragment1(), new InstructionFragment2(), new InstructionFragment3()
        };
    }

    @Override
    public Fragment getItem(int i) {
        return fragments[i];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
