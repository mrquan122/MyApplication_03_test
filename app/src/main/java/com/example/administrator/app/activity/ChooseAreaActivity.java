package com.example.administrator.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.app.R;
import com.example.administrator.app.db.CoolWeatherDB;
import com.example.administrator.app.model.City;
import com.example.administrator.app.model.County;
import com.example.administrator.app.model.Province;
import com.example.administrator.app.util.HttpCallbacListener;
import com.example.administrator.app.util.HttpUtil;
import com.example.administrator.app.util.Utility;

import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Administrator on 2016/4/13.
 */
public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE =0;
    public static final int LEVEL_CITY =1;
  //  public static final int LEVEL_COUNTY =0;
    private ProgressDialog progressDialog;
    private TextView titlText;
    private ListView listView;
    private ArrayAdapter <String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List <String> dataList = new ArrayList<String>();

    /**
     * 省列表
     */
    private List<Province> provinceList;

    /**
     * 市列表
     */
    private List<City> cityList;

    /**
     * 县列表
     */
    private List<County> countyList;

    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;

    private boolean isFromWeatherActivity;
    @Override

    protected void onCreate (Bundle saveInstanceState){
        super.onCreate(saveInstanceState);

       isFromWeatherActivity =getIntent().getBooleanExtra("from_weather_activity",false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("city_selected",false)&&!isFromWeatherActivity) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.list_view);
        titlText = (TextView)findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
                if (currentLevel==LEVEL_PROVINCE){
                    selectedProvince =provinceList.get(index);
                    queryCities();
                }else if (currentLevel==LEVEL_CITY){
                    String cityCode =cityList.get(index).getCityCode();
                    Intent intent =new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("city_code",cityCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();//加载省级数据
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询；
     */
    private void queryProvinces (){
       provinceList =coolWeatherDB.loadProvinces();
        if(provinceList.size()>0){
             dataList.clear();
            for(Province province :provinceList){

                dataList.add(province.getProvinceName());
            }

            adapter.notifyDataSetChanged();
           // titlText.setText(selectedCity.getCityName());
            currentLevel =LEVEL_PROVINCE;
        }else{
            queryFromServer(null,"province");
            Log.i("province","provinceList is null,is dowmload from server");
        }
    }
    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询；
     */

    private void queryCities (){
       cityList =coolWeatherDB.loadCities(selectedProvince.getProvinceCode());
        if(cityList.size()>0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
            }
            Log.i("cityname",dataList.get(0).toString());
            adapter.notifyDataSetChanged();
          //  titlText.setText(selectedCity.getCityName());
            currentLevel =LEVEL_CITY;
        }else{
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }



    /**
     * 根据传入的代号和类型从服务器上查询省市县数据
     */

    private void queryFromServer(final String code,final String type){
        String address;
        if(!TextUtils.isEmpty(code)){
            address ="http://flash.weather.com.cn/wmaps/xml/" + code + ".xml";
        }else {
                address ="http://flash.weather.com.cn/wmaps/xml/china.xml";
            }
          //  showProgressDialog();
            Log.i("province",type);
            HttpUtil.sendHttpRequest(address, new HttpCallbacListener() {
                @Override
                public void onFinish(InputStream response) throws IOException, XmlPullParserException, ParserConfigurationException, SAXException {
                    boolean result=false;
                    if("province".equals(type)){
                        result= Utility.handleProvincesResponse(response,coolWeatherDB);
                    }else if ("city".equals(type)){
                        result = Utility.handleCityResponse(response,coolWeatherDB);
                    }
                    if (result){
                        //通过runOnUiThread()方法回到主线程处理逻辑
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                          //      closeProgressDialog();
                                if("province".equals(type)){
                                    queryProvinces();
                                }else if ("city".equals(type)){
                                    queryCities();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onError(Exception e) {
                    //通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        }


    /**
     * 显示进度对话框
     */

     private void  showProgressDialog(){
         if(progressDialog==null){
             progressDialog =new ProgressDialog(this);
             progressDialog.setMessage("正在加载..");
             progressDialog.setCanceledOnTouchOutside(false);
         }
         progressDialog.show();
     }

    /**
     * 关闭进度对话框
     */

    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
    /**
     * 捕获Back键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出
     */
    @Override
    public void onBackPressed (){
      if (currentLevel==LEVEL_CITY){
            queryProvinces();
        }else {
            if(isFromWeatherActivity){
                Intent intent = new Intent(this,WeatherActivity.class);
                startActivity(intent);
            }
          finish();
        }
    }
}



