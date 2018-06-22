package dengmin.cn.edu.nuc.my_weather.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;

import dengmin.cn.edu.nuc.my_weather.db.City;
import dengmin.cn.edu.nuc.my_weather.db.County;
import dengmin.cn.edu.nuc.my_weather.db.Province;
import dengmin.cn.edu.nuc.my_weather.util.Query_Area;

public class Location extends Service {
    public LocationClient locationClient;
    private static final String TAG = "Location";
    public Location() {
    }

    @Override
    public IBinder onBind(Intent intent) {
       return  null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new MyLocationListener());

        locationClient.start();
        Log.i(TAG, "onStartCommand: 定位服务已启动");
        return super.onStartCommand(intent, flags, startId);
    }


    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            String provinceName = bdLocation.getProvince();
            String cityName = bdLocation.getCity();    //获取城市
            String districtName = bdLocation.getDistrict();    //获取区县
            Log.i(TAG, "onReceiveLocation: province"+provinceName);
            Log.i(TAG, "onReceiveLocation: city"+cityName);
            Log.i(TAG, "onReceiveLocation: county"+districtName);
            Query_Area.QueryFromWeb("http://guolin.tech/api/china",provinceName,0);
            Province province = Query_Area.QueryFromDatabaseforProvince(provinceName);
            Query_Area.QueryFromWeb("http://guolin.tech/api/china"+province.getId(),"city",Integer.valueOf(province.getId()));
            City city = Query_Area.QueryFromDatabaseforCity(cityName,province.getId());
            Query_Area.QueryFromWeb("http://guolin.tech/api/china"+province.getId()+city.getId(),districtName,city.getId());
            County county = Query_Area.QueryFromDatabaseforCounty(districtName,city.getId());
            SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
            editor.putString("weatherId",county.getWeatherId());
            editor.putString("countyName",county.getCountyName());
            editor.putString("cityName",city.getCityName());
            editor.apply();
        }
    }
}
