package dengmin.cn.edu.nuc.my_weather.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import dengmin.cn.edu.nuc.my_weather.R;
import dengmin.cn.edu.nuc.my_weather.db.Event;

public class Look_Event extends AppCompatActivity {

    @InjectView(R.id.go_to_event)
    RadioButton goToEvent;
    @InjectView(R.id.go_to_weather)
    RadioButton goToWeather;
    @InjectView(R.id.go_to_setting)
    RadioButton goToSetting;
    @InjectView(R.id.data_tv_event)
    TextView dataTvEvent;
    @InjectView(R.id.weather_tv_event)
    TextView weatherTvEvent;
    @InjectView(R.id.new_event)
    Button newEvent;
    @InjectView(R.id.select_event)
    Button selectEvent;
    @InjectView(R.id.look_event_lv)
    ListView lookEventLv;


    private TextView dataTvLook;

    private TextView timeTvLook;

    private TextView titleTvLook;

    private TextView contentTvLook;

    private TextView idTvLook;


    private List<Event> eventList;
    private String data;
    private SQLiteDatabase db;
    private static final String TAG = "Look_Event";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_event);
        ButterKnife.inject(this);

        dataTvLook = findViewById(R.id.data_tv_look);
        timeTvLook = findViewById(R.id.time_tv_look);
        titleTvLook = findViewById(R.id.title_tv_look);
        contentTvLook = findViewById(R.id.content_tv_look);
        idTvLook = findViewById(R.id.id_tv_look);
        LitePal.getDatabase();

        Calendar calendar = Calendar.getInstance();
        data = String.valueOf(calendar.get(Calendar.YEAR)) + "-" + String.valueOf(calendar.get(Calendar.MONTH) + 1)
                + "-" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        Log.i(TAG, "onCreate: " + data);
        lookEventLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View v = lookEventLv.getChildAt(position);
                TextView id1 = v.findViewById(R.id.id_tv_look);
                String id2 = id1.getText().toString();
                Intent intent = new Intent(Look_Event.this, New_Event.class);
                intent.putExtra("TEMP", "2");
                intent.putExtra("_id", id2);
                startActivityForResult(intent,2);
            }
        });
        QueryToday();
    }

    @OnClick({R.id.go_to_event, R.id.go_to_weather, R.id.go_to_setting, R.id.new_event, R.id.select_event})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.new_event:
                Intent intent3 = new Intent(Look_Event.this, New_Event.class);
                intent3.putExtra("TEMP", "0");
                startActivityForResult(intent3, 0);
                break;
            case R.id.select_event:
                Intent intent4 = new Intent(Look_Event.this, New_Event.class);
                intent4.putExtra("TEMP", "1");
                startActivityForResult(intent4, 1);
                break;
            case R.id.go_to_event:
                Intent intent = new Intent(Look_Event.this, Look_Event.class);
                startActivity(intent);
                break;
            case R.id.go_to_weather:
                Intent intent1 = new Intent(Look_Event.this, mWeather.class);
                startActivity(intent1);
                break;
            case R.id.go_to_setting:
                Intent intent2 = new Intent(Look_Event.this, Setting.class);
                startActivity(intent2);
                break;
        }
    }
    public void QueryToday() {
        eventList = DataSupport.where("data=?", data).find(Event.class);
        displayList(eventList);
    }
    public void QueryItem(String title,String content,String time,String data){
        Log.i(TAG, "QueryItem: "+title+" "+content+" "+time+" "+data);
        Cursor cursor = DataSupport.findBySQL("select * from event where title like "+title+" and content like "+content+" and data like "+data+" and time like "+time+";");
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(Look_Event.this, R.layout.event_item, cursor,
                new String[]{"_id", "title", "content", "data","time"},
                new int[]{R.id.id_tv_look,R.id.title_tv_look,R.id.content_tv_look,R.id.data_tv_look,R.id.time_tv_look});
        lookEventLv.setAdapter(adapter);
    }
    public void displayList(List<Event> eventList){
        lookEventLv.clearAnimation();
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        Log.i(TAG, "displayList: size"+eventList.size());
        int i=0;
        for (Event event : eventList) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("_id", event.getId());
            map.put("data", event.getData());
            map.put("time", event.getTime());
            map.put("title", event.getTitle());
            map.put("content", event.getContent());
            listItem.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, listItem, R.layout.event_item,
                new String[]{"_id","data", "time", "title", "content"},
                new int[]{R.id.id_tv_look,R.id.data_tv_look, R.id.time_tv_look, R.id.title_tv_look, R.id.content_tv_look});
        lookEventLv.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: resultCode"+requestCode);
        switch (requestCode){
            case 0:
                if(resultCode == RESULT_OK){
                    QueryToday();
                }
                break;
            case 1:
                if(resultCode == RESULT_OK){
                    String data1 = data.getStringExtra("data");
                    String time = data.getStringExtra("time");
                    String title = data.getStringExtra("title");
                    String content = data.getStringExtra("content");
                    Log.i(TAG, "onActivityResult: "+data1);
                    Log.i(TAG, "onActivityResult: "+time);
                    Log.i(TAG, "onActivityResult: "+title);
                    Log.i(TAG, "onActivityResult: "+content);
                    QueryItem(title,content,time,data1);

                }
                break;
            case 2:
                if(resultCode == RESULT_OK){
                    QueryToday();
                    Log.i(TAG, "onActivityResult: case2 invoke");
                }
                break;
            default:
                Log.i(TAG, "onActivityResult: requestCode = "+requestCode);
        }
    }

}
