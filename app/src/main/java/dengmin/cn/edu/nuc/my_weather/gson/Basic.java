package dengmin.cn.edu.nuc.my_weather.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {
    //这个标识是让JSON字段与java字段间建立映射
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {

        @SerializedName("loc")
        public String updateTime;

    }
}
