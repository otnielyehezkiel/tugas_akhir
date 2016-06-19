package com.example.otnielyeheskiel.bumprecord;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {
    ViewPager pager;
    TabLayout tabLayout;
    private PagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        // Fragment manager to add fragment in viewpager we will pass object of Fragment manager to adpater class.
        FragmentManager manager=getSupportFragmentManager();

        //object of PagerAdapter passing fragment manager object as a parameter of constructor of PagerAdapter class.
        adapter = new PagerAdapter(manager);

        //set Adapter to view pager
        pager.setAdapter(adapter);

        //set tablayout with viewpager
        tabLayout.setupWithViewPager(pager);

        // adding functionality to tab and viewpager to manage each other when a page is changed or when a tab is selected
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout){
            int currentPosition = 0;
            @Override
            public void onPageSelected(int newPosition) {
                FragmentLifecycle fragmentToHide = (FragmentLifecycle)adapter.getItem(currentPosition);
                fragmentToHide.onPauseFragment();

                FragmentLifecycle fragmentToShow = (FragmentLifecycle)adapter.getItem(newPosition);
                fragmentToShow.onResumeFragment();

                currentPosition = newPosition;
            }
        });

    }

}
