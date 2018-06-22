package dengmin.cn.edu.nuc.my_weather.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ContentFrameLayout;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import dengmin.cn.edu.nuc.my_weather.R;

public class Setting extends FragmentActivity {

    @InjectView(R.id.go_to_event)
    RadioButton goToEvent;
    @InjectView(R.id.go_to_weather)
    RadioButton goToWeather;
    @InjectView(R.id.go_to_setting)
    RadioButton goToSetting;
    @InjectView(R.id.select_city_byhand)
    Button selectCityByhand;

    private FragmentManager fm;
    private ContentFrameLayout mContentFrameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.inject(this);

        fm = getSupportFragmentManager();

    }

    @OnClick({R.id.go_to_event, R.id.go_to_weather, R.id.go_to_setting,R.id.select_city_byhand})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.go_to_event:
                Intent intent = new Intent(Setting.this, Look_Event.class);
                startActivity(intent);
                break;
            case R.id.go_to_weather:
                Intent intent1 = new Intent(Setting.this, mWeather.class);
                startActivity(intent1);
                break;
            case R.id.go_to_setting:
                Intent intent2 = new Intent(Setting.this, Setting.class);
                startActivity(intent2);
                break;
            case R.id.select_city_byhand:
                Intent intent3 = new Intent(Setting.this,select_area.class);
                startActivityForResult(intent3,4);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setWeatherID(data);
    }
    public  void setWeatherID(Intent data){
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putString("weatherId",data.getStringExtra("weatherId"));
        editor.putString("cityName",data.getStringExtra("cityName"));
        editor.putString("countyName",data.getStringExtra("countyName"));
        editor.apply();
    }

}
