package dengmin.cn.edu.nuc.my_weather.util;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import dengmin.cn.edu.nuc.my_weather.db.Event;
import dengmin.cn.edu.nuc.my_weather.gson.EventToGson;
import dengmin.cn.edu.nuc.my_weather.gson.Weather;
import dengmin.cn.edu.nuc.my_weather.db.City;
import dengmin.cn.edu.nuc.my_weather.db.County;
import dengmin.cn.edu.nuc.my_weather.db.Province;


public class GetAreaWithGSON {

    private static final String TAG = "GetAreaWithGSON";;
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                Log.i(TAG, "handleProvinceResponse: "+allProvinces.length());
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //解析和处理服务器返回的市级数据

    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    Log.i(TAG, "handleCityResponse: city name"+cityObject.getString("name"));
                    Log.i(TAG, "handleCityResponse: city id"+cityObject.getString("id"));
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //解析和处理服务器返回的县级数据

    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    //解析天气数据并返回weather类
    public static Weather handleWeatherPesponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getEventGsonString(List<Event> eventList){
        try{
            JsonArray array = new JsonArray();
            for (Event event : eventList){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("title",event.getTitle());
                jsonObject.put("content",event.getContent());
                jsonObject.put("data",event.getData());
                jsonObject.put("time",event.getTime());
                jsonObject.put("hz",event.getHz());
                array.add(String.valueOf(jsonObject));
            }
            Log.i(TAG, "getEventGsonString: "+array.getAsString());
            return array.getAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static boolean handleEventResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allEvents = new JSONArray(response);
                for (int i = 0; i < allEvents.length(); i++) {
                    JSONObject eventyObject = allEvents.getJSONObject(i);
                    Event event = new Event();
                    event.setTitle(eventyObject.getString("title"));
                    event.setContent(eventyObject.getString("content"));
                    event.setData(eventyObject.getString("data"));
                    event.setHz(eventyObject.getString("hz"));
                    event.setTime(eventyObject.getString("time"));
                    event.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
