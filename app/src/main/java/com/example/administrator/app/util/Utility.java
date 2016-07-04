package com.example.administrator.app.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.administrator.app.db.CoolWeatherDB;
import com.example.administrator.app.model.City;
import com.example.administrator.app.model.Province;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Administrator on 2016/3/15.
 */
public class Utility {
    public static boolean handleProvincesResponse(InputStream response, CoolWeatherDB coolWeatherDB)
            throws XmlPullParserException, IOException {
        List<Province> provinceList = null;
        Province province = null;
        XmlPullParser parser = null;

        parser = XmlPullParserFactory.newInstance().newPullParser();

        parser.setInput(response, "UTF-8");
        int eventType = 0;
        eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    provinceList = new ArrayList<>();
                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("city")) {
                        province = new Province();
                        province.setProvinceName(parser.getAttributeValue(null, "quName"));
                        province.setProvinceCode(parser.getAttributeValue(null, "pyName"));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("city")) {
                        coolWeatherDB.saveProvince(province);
                        //      provinceList.add(province);

                    }
                    break;
            }

            eventType = parser.next();

        }
      return true;
    }


    public static boolean handleCityResponse(InputStream response, CoolWeatherDB coolWeatherDB)
            throws ParserConfigurationException, IOException, SAXException {
        List<City> cityList = null;
        City city = null;
         DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
         Document doc = builder.parse(response);
         Element root = doc.getDocumentElement();
         NodeList nodes=root.getElementsByTagName("city");
        for(int i= 0 ;i<nodes.getLength();i++){
            city = new City();
            city.setProvinceId(root.getNodeName());
            city.setCityName(nodes.item(i).getAttributes().getNamedItem("cityname").getNodeValue());
            city.setCityCode(nodes.item(i).getAttributes().getNamedItem("url").getNodeValue());
         //   Log.i("province",city.getCityName());

            coolWeatherDB.saveCity(city);
        }


      return true;
    }
    public static boolean handleWeatherResponse(Context context, InputStream response, Handler handler)
            throws XmlPullParserException, IOException, JSONException {
        BufferedReader br = new BufferedReader(new InputStreamReader(response, "utf-8"));
        String line = null;
        StringBuilder builder=new StringBuilder();
        while ((line = br.readLine()) != null) {
            builder.append(line);
        }

        JSONObject jsonObject = new JSONObject(builder.toString()).getJSONObject("weatherinfo");
        String cityName = null;
        String cityCode = null;
        String temp1 = null;
        String weatherDesp = null;
        String publishTime = null;

        cityName = jsonObject.getString("city");
        temp1 = jsonObject.getString("temp1");
        cityCode = jsonObject.getString("cityid");
        weatherDesp = jsonObject.getString("weather1");
        publishTime = jsonObject.getString("date_y");

        saveWeatherInfo(context, cityName, temp1,weatherDesp ,cityCode, publishTime);
        Message msg = new Message();
        msg.what=1;
        Bundle bundle = new Bundle();
        bundle.putString("cityName",cityName);
        msg.setData(bundle);
        handler.sendMessage(msg);
      //  Log.i("weatherinfo",cityName+temp1+"--");

        return true;
    }


    /**
     *将服务器返回的的所有天气信息存储到 SharedPreference文件中。
     *
     *
     public static void saveWeatherInfo(Context context, String cityName,  String temp1,
                                        String weatherDesp,String cityCode,String publishTime){

         SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
               editor.putBoolean("city_selected",true);
               editor.putString("city",cityName);
               editor.putString("temp1",temp1);
               editor.putString("weather_desp",weatherDesp);
                editor.putString("weather_code",cityCode);
               editor.putString("publish_time",publishTime);

               editor.commit();

     }  */
    public static void saveWeatherInfo(Context context, String cityName,  String temp1,
                                       String weatherDesp,String cityCode,String publishTime) throws IOException {
        FileOutputStream fos =context.openFileOutput(cityName,Context.MODE_PRIVATE);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        osw.write(cityName);
         /*   osw.write(temp1);
        osw.write(weatherDesp);
        osw.write(cityCode);
        osw.write(publishTime); */
        osw.close();

    }

}

