package com.example.administrator.app.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.app.R;
import com.example.administrator.app.activity.ChooseAreaActivity;
import com.example.administrator.app.util.HttpCallbacListener;
import com.example.administrator.app.util.HttpUtil;
import com.example.administrator.app.util.Utility;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2016/6/24 0024.
 */
public class weatherFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
    private LinearLayout weatherInfoLayout;

    /**
     * 用于显示城市名
     */
    private TextView cityNameText;
    /**
     * 用于显示发布时间
     */
    private TextView publishText;
    /**
     * 用于显示天气描述
     */
    private TextView weatherDespText;
    /**
     * 用于显示气温1
     */
    private TextView temp1Text;
    /**
     * 用于显示气温2
     */
    private TextView temp2Text;
    /**
     * 用于显示当前日期
     */
    private TextView currentDateText;
    /**
     * 用于显示切换城市按钮
     */
    private Button swithCity;
    /**
     * 更新天气按钮
     */
    private Button refreshWeather;

    public static weatherFragment newInstance(String cityCode) {
        weatherFragment fragment = new weatherFragment();
        Bundle args = new Bundle();
        args.putString("cityCode",cityCode);
        fragment.setArguments(args);   //   Log.i("Fragment",cityCode);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mBundle = getArguments();          // Log.i("bundle",getArguments().getString("cityCode"));
        String code=mBundle.getString("cityCode");
        queryWeatherInfo(code);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);
        weatherInfoLayout = (LinearLayout) view.findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) view.findViewById(R.id.city_name);
        publishText = (TextView) view.findViewById(R.id.publish_text);
        weatherDespText = (TextView) view.findViewById(R.id.weather_desp);
        temp1Text = (TextView) view.findViewById(R.id.temp1);
        currentDateText = (TextView) view.findViewById(R.id.current_date);
        swithCity = (Button) view.findViewById(R.id.swith_city);
        refreshWeather = (Button) view.findViewById(R.id.refresh_weather);
        swithCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
        return view;
    }

    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                Bundle bundle=msg.getData();
                String cityName = (String) bundle.get("cityName");
                try {
                    showWeather(cityName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.swith_city:
                Intent intent = new Intent(getActivity(), ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                break;
            case R.id.refresh_weather:
                publishText.setText("同步中..");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String weatherCode = prefs.getString("weather_code", "");
                if (!TextUtils.isEmpty(weatherCode)) {
                    queryWeatherInfo(weatherCode);
                }
                break;

            default:
                break;

        }
    }

    private void queryWeatherInfo(String code){
     //   Log.i("citycode",code);
        String address="http://weather.51wnl.com/weatherinfo/GetMoreWeather?cityCode="+code+"&weatherType=0";

         HttpUtil.sendHttpRequest(address, new HttpCallbacListener() {
            @Override
            public void onFinish(InputStream response) throws IOException, XmlPullParserException, JSONException {
                //处理服务器返回的天气信息
                Utility.handleWeatherResponse(getActivity(), response,handler);
            }

            @Override
            public void onError(Exception e) {

            }
        });


    }
/*
    public void showWeather(){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

      //  Log.i("prefs", prefs.getString("city", ""));
        cityNameText.setText(prefs.getString("city", "")
        );
        temp1Text.setText(prefs.getString("temp1", "")
        );

        weatherDespText.setText(prefs.getString("weather_desp", "")
        );
        publishText.setText("今天" + "发布");
        currentDateText.setText(prefs.getString("publish_time", "")
        );
        weatherInfoLayout.setVisibility(View.VISIBLE);
    } */
    public void showWeather(String cityName) throws IOException {
        FileInputStream fis = getActivity().openFileInput(cityName);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br= new BufferedReader(isr);
        String line =null;
        StringBuilder sb = new StringBuilder();
        while(!((line = br.readLine()) == null)){
            sb.append(line);

        }
        cityNameText.setText(sb.toString());


    }
}
