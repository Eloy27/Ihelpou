package com.example.ihelpou.ui.main;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.ihelpou.R;
import com.example.ihelpou.models.User;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static int[] TAB_TITLES;
    private final Context mContext;
    private User user;
    private int count;

    public SectionsPagerAdapter(Context context, FragmentManager fm, User user, int count) {
        super(fm);
        this.mContext = context;
        this.user = user;
        this.count = count;
        if (count == 2){
            this.TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
        }
        else if (count == 3){
            this.TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
        }
    }

    @Override
    public Fragment getItem(int position) {
        return PlaceholderFragment.newInstance(position + 1, user);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return count;
    }
}