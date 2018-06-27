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
    public static void QueryFromWeb(String address, final String type, final int id){
        Log.i(TAG, "QueryFromWeb: 1 address"+address);
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                switch (type){
                    case "province":
                        Log.i(TAG, "onResponse: type 1: "+type);
                        result = GetAreaWithGSON.handleProvinceResponse(responseText);
                        Log.i(TAG, "onResponse: result 1"+result);
                        break;
                    case "city":
                        Log.i(TAG, "onResponse: type 2: "+type);
                        result = GetAreaWithGSON.handleCityResponse(responseText, id);
                        Log.i(TAG, "onResponse: result 2"+result);
                        break;
                    case "county":
                        Log.i(TAG, "onResponse: type 3: "+type);
                        result = GetAreaWithGSON.handleCountyResponse(responseText, id);
                        Log.i(TAG, "onResponse: result 3"+result);
                        break;
                    default:
                        Log.i(TAG, "onResponse: default"+type);
                        break;
                }
            }
        });
    }
    public static int QueryFromDatabaseforProvince(String provinceName){
        int result = 0;
        List<Province> provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            for (Province province : provinceList) {
                //百度地图返回的数据时山西省而和风天气天气返回的数据是山西，这不就是个坑吗，下方两项同样如此
                if (provinceName.contains(province.getProvinceName().toString())) {
                    Log.i(TAG, "QueryFromDatabaseforProvince: true"+provinceName);
                    result = province.getId();
                    break;
                }
            }
        }
        return result;
    }
    public static int QueryFromDatabaseforCity(String cityName,int provinceid){
        int result = 0;
        List<City> cityList = DataSupport.where("provinceid = ?", String.valueOf(provinceid)).find(City.class);
        if (cityList.size() > 0) {

            for (City city : cityList) {
                if (cityName.contains(city.getCityName().toString())){
                    result = city.getId();
                    Log.i(TAG, "QueryFromDatabaseforCity: true"+city.getCityName());
                    break;
                }
            }
        }
        return result;
    }
    public static String QueryFromDatabaseforCounty(String countyName,int cityid){
        String result = null;
        List<County> countyList = DataSupport.where("cityId = ?", String.valueOf(cityid)).find(County.class);
        if (countyList.size() > 0) {
            for (County county : countyList) {
                if(countyName.contains(county.getCountyName().toString())){
                    result = county.getWeatherId();
                    Log.i(TAG, "QueryFromDatabaseforCounty: true"+county.getCountyName());
                    break;
                }
            }
        }
        return result;
    }

}
