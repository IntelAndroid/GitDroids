package com.feicuiedu.gitdroid.splash;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.feicuiedu.gitdroid.splash.pager.Pager0;
import com.feicuiedu.gitdroid.splash.pager.Pager1;
import com.feicuiedu.gitdroid.splash.pager.Pager2;

/**
 * Created by Administrator on 2016/6/28 0028.
 */
public class SplashPagerAdapter extends PagerAdapter {
    private final View[] views;

    public SplashPagerAdapter(Context context) {
        views = new View[]{
                new Pager0(context),
                new Pager1(context),
                new Pager2(context)
        };
    }

    @Override public int getCount() {
        return views.length;
    }

    @Override public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override public Object instantiateItem(ViewGroup container, int position) {
        View view = views[position];
        container.addView(view);
        return view;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
        View view = views[position];
        container.removeView(view);
    }

    public View getView(int position){
        return views[position];
    }
}