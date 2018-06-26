package dengmin.cn.edu.nuc.my_weather.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;


import dengmin.cn.edu.nuc.my_weather.service.Auto_Update_Service;
import dengmin.cn.edu.nuc.my_weather.service.Event_remind_Service;
import dengmin.cn.edu.nuc.my_weather.util.Check_Limilt;
import dengmin.cn.edu.nuc.my_weather.util.Query_Area;

public class MainActivity extends AppCompatActivity {

    private Intent intent2;
    public LocationClient locationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private static final String TAG = "test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: invoked");
        String[] permissionList = null;
        permissionList = Check_Limilt.check_limilt(this);

        if (permissionList != null) {
            ActivityCompat.requestPermissions(this, permissionList, 1);
        } else {
            locationClient = new LocationClient(getApplicationContext());
            locationClient.registerLocationListener(myListener);

            LocationClientOption option = new LocationClientOption();
            option.setIsNeedAddress(true);
//可选，是否需要地址信息，默认为不需要，即参数为false
//如果开发者需要获得当前点的地址信息，此处必须为true
            Log.i(TAG, "onStartCommand: 111");
            locationClient.setLocOption(option);
            //mLocationClient为第二步初始化过的LocationClient对象
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
//更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
            Log.i(TAG, "onCreate: 定位开始");
            locationClient.start();
        }

        Intent intent = new Intent(MainActivity.this, Auto_Update_Service.class);
        startService(intent);
        Intent intent1 = new Intent(MainActivity.this, Event_remind_Service.class);
        startService(intent1);
        try{
            SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
            String weatherId = preferences.getString("weatherId",null);
            Log.i(TAG, "onCreate: "+weatherId);
            intent2 = new Intent(this, Look_Event.class);
        }catch (Exception e){
            intent2 = new Intent(this,select_area.class);
            e.printStackTrace();
        }
        Handler handler = new Handler();
        //延时
        Log.i(TAG, "onCreate: 延时开始");
        handler.sendEmptyMessageDelayed(0,3000);
        startActivity(intent2);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "如果没有该权限可能无法使用部分功能", Toast.LENGTH_LONG).show();
                            Toast.makeText(this, "请使用手动定位", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(this, select_area.class);
                            startActivityForResult(intent, 7);
                            finish();
                        }
                    }
                } else {
                    //若缺少权限则手动定位
                    Toast.makeText(this,"请使用手动定位",Toast.LENGTH_LONG).show();
                    Intent intent3 = new Intent(MainActivity.this, select_area.class);
                    startActivity(intent3);
                }
        }
    }
//    private Boolean Haveaddress(String countyName){
//        boolean result = false;
//        try {
//            SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
//            if(preferences.getString("countyName",null).equals(countyName));
//            {
//                Log.i(TAG, "Haveaddress: 1"+preferences.getString("countyName",null));
//                Log.i(TAG, "Haveaddress: 2"+countyName);
//                result = true;
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return result;
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 7 && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "部分功能需要确切的地理位置", Toast.LENGTH_LONG).show();
        }
    }
    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            String provinceName = bdLocation.getProvince();
            String cityName = bdLocation.getCity();    //获取城市
            String districtName = bdLocation.getDistrict();    //获取区县
            Log.i(TAG, "onReceiveLocation:1 province"+provinceName);
            Log.i(TAG, "onReceiveLocation: 2city"+cityName);
            Log.i(TAG, "onReceiveLocation:3 county"+districtName);
            Query_Area.QueryFromWeb("http://guolin.tech/api/china","province",0);

            int provinceId = Query_Area.QueryFromDatabaseforProvince(provinceName);

            Query_Area.QueryFromWeb("http://guolin.tech/api/china"+provinceId,"city",provinceId);
            int cityId = Query_Area.QueryFromDatabaseforCity(cityName,provinceId);
            Log.i(TAG, "onReceiveLocation: city "+cityId);
            Log.i(TAG, "onReceiveLocation: province"+provinceId);
            Query_Area.QueryFromWeb("http://guolin.tech/api/china"+provinceId+cityId,"county",cityId);
            String weatherId = Query_Area.QueryFromDatabaseforCounty(districtName,cityId);
            SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
            editor.putString("weatherId",weatherId);
            Log.i(TAG, "onReceiveLocation: fff"+weatherId);
            editor.putString("countyName",districtName);
            editor.putString("cityName",cityName);
            editor.apply();

        }
    }

}
