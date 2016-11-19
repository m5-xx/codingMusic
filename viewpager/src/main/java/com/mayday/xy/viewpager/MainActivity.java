package com.mayday.xy.viewpager;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private ArrayList<View> viewList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager= (ViewPager) findViewById(R.id.viewPagerId);
        //初始化VIEW
        initView();
        viewPager.setAdapter(new MyPageAdapter(viewList));
    }

    private void initView() {

        View view=View.inflate(this,R.layout.vp1,null);
        View view1=View.inflate(this,R.layout.vp,null);
        View view2=View.inflate(this,R.layout.vp2,null);
        View view3=View.inflate(this,R.layout.vp4,null);
        viewList.add(view);
        viewList.add(view1);
        viewList.add(view3);
        viewList.add(view2);

    }
}
