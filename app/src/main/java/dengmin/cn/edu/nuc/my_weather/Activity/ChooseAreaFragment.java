package dengmin.cn.edu.nuc.my_weather.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dengmin.cn.edu.nuc.my_weather.R;
import dengmin.cn.edu.nuc.my_weather.db.City;
import dengmin.cn.edu.nuc.my_weather.db.County;
import dengmin.cn.edu.nuc.my_weather.db.Province;
import dengmin.cn.edu.nuc.my_weather.util.GetAreaWithGSON;
import dengmin.cn.edu.nuc.my_weather.util.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {

    private static final String TAG = "ChooseAreaFragment";

    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;


    @InjectView(R.id.list_view)
    ListView listView;
    @InjectView(R.id.title_text)
    TextView titleText;
    @InjectView(R.id.back)
    ImageButton backBtn;


    private ProgressDialog progressDialog;
    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;

    private View view;
    private String cityName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.choose_area, container, false);
        ButterKnife.inject(this, view);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        Log.i(TAG, "onCreateView: invoke");

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    cityName = cityList.get(position).getCityName();
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String weatherId = countyList.get(position).getWeatherId();
                    String countyName = countyList.get(position).getCountyName();

                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE).edit();
                    editor.putString("weatherId", weatherId);
                    editor.putString("cityName", cityName);
                    editor.putString("countyName", countyName);
                    editor.apply();

                    Intent intent = getActivity().getIntent();
                    if (weatherId != null) {
                        getActivity().setResult(Activity.RESULT_OK);
                    } else {
                        getActivity().setResult(Activity.RESULT_CANCELED);
                    }
                    Log.i(TAG, "onItemClick: " + weatherId + "|" + countyName + "|" + cityName);
                    getActivity().finish();
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
        Log.i(TAG, "onActivityCreated: invoked");
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryProvinces() {
        titleText.setText("中国");
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());

        Log.i(TAG, "provinceId = " + selectedProvince.getId());
        cityList = DataSupport.where("provinceId = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            Log.i(TAG, "queryCities: address" + address);
            queryFromServer(address, "city");
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());

        countyList = DataSupport.where("cityId = ?", String.valueOf(selectedCity.getId())).find(County.class);
        Log.i(TAG, "queryProvinces: listsize: " + provinceList.size());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }


    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                switch (type) {
                    case "province":
                        Log.i(TAG, "onResponse: type : " + type);
                        result = GetAreaWithGSON.handleProvinceResponse(responseText);
                        break;
                    case "city":
                        Log.i(TAG, "onResponse: type : " + type);
                        result = GetAreaWithGSON.handleCityResponse(responseText, selectedProvince.getId());
                        break;
                    case "county":
                        Log.i(TAG, "onResponse: type : " + type);
                        result = GetAreaWithGSON.handleCountyResponse(responseText, selectedCity.getId());
                        break;
                    default:
                        Log.i(TAG, "onResponse: default" + type);
                        break;
                }
                Log.i(TAG, "result:" + String.valueOf(result));
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

//    @OnClick(R.id.back_btn)
//    public void onViewClicked() {
//        Log.i(TAG, "onViewClicked: invoked");
//        if (currentLevel == LEVEL_COUNTY) {
//            queryCities();
//        } else if (currentLevel == LEVEL_CITY) {
//            queryProvinces();
//        } else if (currentLevel == LEVEL_PROVINCE) {
//            Log.i(TAG, "onClick: " + currentLevel);
//            Toast.makeText(getActivity(), "失败", Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(getActivity(), mWeather.class);
//            startActivity(intent);
//        }
//    }
}
