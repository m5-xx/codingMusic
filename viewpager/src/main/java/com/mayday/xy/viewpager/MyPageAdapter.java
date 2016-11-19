package com.mayday.xy.viewpager;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by xy-pc on 2016/11/13.
 */

public class MyPageAdapter extends PagerAdapter {
    private ArrayList<View> viewList;

    public MyPageAdapter(ArrayList<View> viewList) {
        this.viewList=viewList;
    }

    //要展示的view个数
    @Override
    public int getCount() {
        return viewList.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    //实例化view，并添加到容器
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = viewList.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
//        super.destroyItem(container, position, object);
    }
}
