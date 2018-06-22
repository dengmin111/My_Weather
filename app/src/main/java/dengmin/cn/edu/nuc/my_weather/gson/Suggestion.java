package dengmin.cn.edu.nuc.my_weather.gson;

import com.google.gson.annotations.SerializedName;

//建议
public class Suggestion {
    public class Comfor{
        @SerializedName("txt")
        public String info;
    }

    public class CarWash{
        @SerializedName("txt")
        public String info;
    }
    public class Sport{
        @SerializedName("txt")
        public String info ;
    }

    @SerializedName("comf")
    public Comfor comfor;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;
}
