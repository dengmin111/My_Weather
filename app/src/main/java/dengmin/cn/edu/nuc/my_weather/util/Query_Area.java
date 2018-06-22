package dengmin.cn.edu.nuc.my_weather.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import dengmin.cn.edu.nuc.my_weather.db.City;
import dengmin.cn.edu.nuc.my_weather.db.County;
import dengmin.cn.edu.nuc.my_weather.db.Province;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static org.litepal.LitePalApplication.getContext;

public class Query_Area {
    private static final String TAG = "Query_AreaFromWeb";

    private static boolean result = false;
    public static Boolean QueryFromWeb(String address, final String type, final int id){
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                switch (type){
                    case "province":
                        Log.i(TAG, "onResponse: type : "+type);
                        result = GetAreaWithGSON.handleProvinceResponse(responseText);
                        break;
                    case "city":
                        Log.i(TAG, "onResponse: type : "+type);
                        result = GetAreaWithGSON.handleCityResponse(responseText, id);
                        break;
                    case "county":
                        Log.i(TAG, "onResponse: type : "+type);
                        result = GetAreaWithGSON.handleCountyResponse(responseText, id);
                        break;
                    default:
                        Log.i(TAG, "onResponse: default"+type);
                        break;
                }
            }
        });
        return result;
    }
    public static Province QueryFromDatabaseforProvince(String provinceName){
        Province result = null;
        List<Province> provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            for (Province province : provinceList) {
                if (province.getProvinceName() == provinceName) {
                    result = province;
                    break;
                }
            }
        }
        return result;
    }
    public static City QueryFromDatabaseforCity(String cityName,int provinceid){
        City result = null;
        List<City> cityList = DataSupport.where("provinceid = ?", String.valueOf(provinceid)).find(City.class);
        if (cityList.size() > 0) {

            for (City city : cityList) {
                if (city.getCityName() ==  cityName){
                    result = city;
                    break;
                }
            }
        }
        return result;
    }
    public static County QueryFromDatabaseforCounty(String countyName,int cityid){
        County result = null;
        List<County> countyList = DataSupport.where("cityId = ?", String.valueOf(cityid)).find(County.class);
        if (countyList.size() > 0) {
            for (County county : countyList) {
                if(county.getCountyName() == countyName){
                    result = county;
                    break;
                }
            }
        }
        return result;
    }

}
