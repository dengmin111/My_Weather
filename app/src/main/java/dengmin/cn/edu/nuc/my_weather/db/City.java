package dengmin.cn.edu.nuc.my_weather.db;

import org.litepal.crud.DataSupport;

public class City extends DataSupport{
    private int _id;
    private String cityName;
    private int cityCode;
    private int provindeId;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvindeId() {
        return provindeId;
    }

    public void setProvindeId(int provindeId) {
        this.provindeId = provindeId;
    }
}
