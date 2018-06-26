package dengmin.cn.edu.nuc.my_weather.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.ContentFrameLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.JsonArray;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import dengmin.cn.edu.nuc.my_weather.R;
import dengmin.cn.edu.nuc.my_weather.db.Event;
import dengmin.cn.edu.nuc.my_weather.gson.EventToGson;
import dengmin.cn.edu.nuc.my_weather.util.GetAreaWithGSON;
import dengmin.cn.edu.nuc.my_weather.util.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Setting extends FragmentActivity {

    @InjectView(R.id.go_to_event)
    ImageButton goToEvent;
    @InjectView(R.id.go_to_weather)
    ImageButton goToWeather;
    @InjectView(R.id.go_to_setting)
    ImageButton goToSetting;
    @InjectView(R.id.select_city_byhand)
    Button selectCityByhand;
    @InjectView(R.id.put_event)
    Button putEvent;
    @InjectView(R.id.get_event)
    Button getEvent;

    private FragmentManager fm;
    private ContentFrameLayout mContentFrameLayout;

    private static final String TAG = "Setting";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.inject(this);

        fm = getSupportFragmentManager();

    }

    @OnClick({R.id.go_to_event, R.id.go_to_weather, R.id.go_to_setting, R.id.select_city_byhand,R.id.get_event,R.id.put_event})
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
                Intent intent3 = new Intent(Setting.this, select_area.class);
                startActivityForResult(intent3, 4);

            case R.id.put_event:
                Log.i(TAG, "put_event");
                List<Event> eventList = DataSupport.findAll(Event.class);
                String GSON_Event = GetAreaWithGSON.getEventGsonString(eventList);
                Log.i(TAG, "onViewClicked: "+GSON_Event);
                HttpUtil.sendPost(" ",GSON_Event);
                break;
            case R.id.get_event:
                HttpUtil.sendOkHttpRequest("", new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String responseText = response.body().string();
                        GetAreaWithGSON.handleEventResponse(responseText);
                    }
                });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_OK) {
            Toast.makeText(this, "手动定位成功", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "请重新定位", Toast.LENGTH_LONG).show();
        }
    }


}
