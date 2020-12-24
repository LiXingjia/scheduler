package com.in.timelinenested.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.githang.statusbar.StatusBarCompat;
import com.in.timelinenested.Adapter.TabFragmentPagerAdapter;
import com.in.timelinenested.R;

public class FragmentActivity extends AppCompatActivity {

    private ViewPager myViewPager;
    private TabFragmentPagerAdapter adapter;
    private TabLayout tabLayout;

    private TabLayout.Tab one;
    private TabLayout.Tab two;
    private TabLayout.Tab three;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    //    getSupportActionBar().hide(); //隐藏掉整个action bar
        setContentView(R.layout.activity_fragment);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.main));

        InitView();
    }

    /**
     * 初始化控件
     */
    private void InitView() {
        //用适配器将viewPager和Fragment绑定在一起
        myViewPager = (ViewPager) findViewById(R.id.myViewPager);
        adapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(adapter);

        //将TabLayout和viewPager绑定在一起
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(myViewPager);

        //指定Tab的位置
        one = tabLayout.getTabAt(0);
        two = tabLayout.getTabAt(1);
        three = tabLayout.getTabAt(2);
    }
}
