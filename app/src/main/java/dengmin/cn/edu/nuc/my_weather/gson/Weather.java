package dengmin.cn.edu.nuc.my_weather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

//总的实例类
public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
