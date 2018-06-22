package dengmin.cn.edu.nuc.my_weather.gson;

import com.google.gson.annotations.SerializedName;
//温度天气
public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
