package com.jules.takemehomecountrytable.Fragments.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {

    private int numoftab;

    public PageAdapter(FragmentManager fm, int numofTabs) {
        super(fm);
        this.numoftab = numofTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new tabMapDep();
            case 1:
                return new tabMapExam();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numoftab;
    }


    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
