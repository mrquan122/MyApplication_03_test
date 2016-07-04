package com.example.administrator.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import com.example.administrator.app.R;
import com.example.administrator.app.fragment.weatherFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/14.
 */
public class WeatherActivity extends AppCompatActivity{
    private  List<Object>views;
    private  ViewPager viewPager;
    private FragmentPagerAdapter fpa;
    @Override
    protected void onCreate (Bundle saveInstanceState)  {
        super.onCreate(saveInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        viewPager = (ViewPager) findViewById(R.id.vp);
        views = new ArrayList<Object>();

        fpa = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return (Fragment) views.get(position);
            }

            @Override
            public int getCount() {
                return views.size();
            }
        };

        String cityCode= getIntent().getStringExtra("city_code");

       // Log.i("首次启动",cityCode);
        Fragment fragment= weatherFragment.newInstance(cityCode);
        views.add(fragment);
        viewPager.setAdapter(fpa);


    }
/*
    @Override
    protected void onStart() {
        super.onStart();

        String cityCode= getIntent().getStringExtra("city_code");
        Fragment fragment= weatherFragment.newInstance(cityCode);
        views.add(fragment);
        viewPager.setAdapter(fpa);
        fpa.notifyDataSetChanged();
    }  */

     @Override
     protected void onNewIntent(Intent intent) {
      super.onNewIntent(intent);
      setIntent(intent);
      String cityCode= getIntent().getStringExtra("city_code");

         Fragment fragment= weatherFragment.newInstance(cityCode);

         views.add(fragment);
         //  viewPager.setAdapter(fpa);
         fpa.notifyDataSetChanged();

         Log.i("再次启动",cityCode);

     }


 /*   @Override
    protected void onRestart() {
        super.onRestart();
        String cityCode= getIntent().getStringExtra("city_code");

        Fragment fragment= weatherFragment.newInstance(cityCode);

        views.add(fragment);
        //  viewPager.setAdapter(fpa);
        fpa.notifyDataSetChanged();

      //  Log.i("再启动",cityCode);
    }  */


}







