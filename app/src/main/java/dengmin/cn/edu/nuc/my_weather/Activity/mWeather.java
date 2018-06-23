package dengmin.cn.edu.nuc.my_weather.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import dengmin.cn.edu.nuc.my_weather.R;
import dengmin.cn.edu.nuc.my_weather.gson.Forecast;
import dengmin.cn.edu.nuc.my_weather.gson.Weather;
import dengmin.cn.edu.nuc.my_weather.util.GetAreaWithGSON;
import dengmin.cn.edu.nuc.my_weather.util.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class mWeather extends AppCompatActivity {


    @InjectView(R.id.city_title)
    TextView cityTitle;
    @InjectView(R.id.update_time_title)
    TextView updateTimeTitle;
    @InjectView(R.id.degree_tv)
    TextView degreeTv;
    @InjectView(R.id.weather_info_tv)
    TextView weatherInfoTv;
    @InjectView(R.id.aqi_tv)
    TextView aqiTv;
    @InjectView(R.id.pm25_tv)
    TextView pm25Tv;
    @InjectView(R.id.forecast_layout)
    LinearLayout forecastLayout;
    @InjectView(R.id.comfort_tv)
    TextView comfortTv;
    @InjectView(R.id.car_wash_tv)
    TextView carWashTv;
    @InjectView(R.id.sport_tv)
    TextView sportTv;
    @InjectView(R.id.weather_layout)
    ScrollView weatherLayout;
    @InjectView(R.id.go_to_event)
    RadioButton goToEvent;
    @InjectView(R.id.go_to_weather)
    RadioButton goToWeather;
    @InjectView(R.id.go_to_setting)
    RadioButton goToSetting;
    private static final String TAG = "mWeather";
    @InjectView(R.id.select_city_btn)
    ImageButton selectCityBtn;
    @InjectView(R.id.swipe_ref)
    SwipeRefreshLayout swipeRef;
    private String weatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        ButterKnife.inject(this);

        Log.i(TAG, "onCreate: run");
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        weatherId = pref.getString("weatherId", null);
        String weatherString = pref.getString("weather", null);
        if (weatherId == null) {
            selectArea();
        }
        Log.i(TAG, "selectArea over");
        if (weatherString != null) {
            Weather weather = GetAreaWithGSON.handleWeatherPesponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            Log.i(TAG, "onCreate: weatherId0 = " + weatherId);
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }

        swipeRef.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRef.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });
    }

    //请求天气信息
    public void requestWeather(final String weatherId) {
        Log.i(TAG, "onCreate: weatherId1" + weatherId);
        String url = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=47618a326a824f3a88b9f10915d4f0fd";
        Log.i("url", "requestWeather: " + url);
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mWeather.this, "获取天气信息失败，请检查网络连接", Toast.LENGTH_LONG).show();
                        swipeRef.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.i(TAG, "onResponse: " + responseText);
                final Weather weather = GetAreaWithGSON.handleWeatherPesponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }
                    }
                });
            }
        });
    }

    //展示weather类中的数据
    public void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;

        String updataTime = weather.basic.update.updateTime.split("")[1];

        String degree = weather.now.temperature + "℃";

        String weatherInfo = weather.now.more.info;

        cityTitle.setText(cityName);
        updateTimeTitle.setText(updataTime);
        degreeTv.setText(degree);
        weatherInfoTv.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView datetv = view.findViewById(R.id.data_tv);
            TextView infotv = view.findViewById(R.id.info_tv);
            TextView maxTv = view.findViewById(R.id.max_tv);
            TextView minTv = view.findViewById(R.id.min_tv);
            datetv.setText(forecast.date);
            infotv.setText(forecast.more.info);
            maxTv.setText(forecast.temperature.max);
            minTv.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            aqiTv.setText(weather.aqi.city.aqi);
            pm25Tv.setText(weather.aqi.city.pm25);
        }
        String comfor = "舒适度： " + weather.suggestion.comfor.info;
        String carwarsh = "洗车指数: " + weather.suggestion.carWash.info;
        String sport = "运动指数: " + weather.suggestion.sport.info;
        comfortTv.setText(comfor);
        carWashTv.setText(carwarsh);
        sportTv.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.go_to_event, R.id.go_to_weather, R.id.go_to_setting, R.id.select_city_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.go_to_event:
                Intent intent = new Intent(mWeather.this, Look_Event.class);
                startActivity(intent);
                break;
            case R.id.go_to_weather:
                Intent intent1 = new Intent(mWeather.this, mWeather.class);
                startActivity(intent1);
                break;
            case R.id.go_to_setting:
                Intent intent2 = new Intent(mWeather.this, Setting.class);
                startActivity(intent2);
                break;
            case R.id.select_city_btn:
                selectArea();
        }
    }

    public void selectArea() {
        Intent intent3 = new Intent(mWeather.this, select_area.class);
        startActivityForResult(intent3, 5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 5:
                if (resultCode == RESULT_OK) {
                    setWeatherID(data);
                    onRestart();
                } else {
                    Toast.makeText(mWeather.this, "失败", Toast.LENGTH_LONG).show();
                }
        }
    }

    public void setWeatherID(Intent data) {
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putString("weatherId", data.getStringExtra("weatherId"));
        editor.putString("cityName", data.getStringExtra("cityName"));
        editor.putString("countyName", data.getStringExtra("countyName"));
        editor.apply();
    }
}
