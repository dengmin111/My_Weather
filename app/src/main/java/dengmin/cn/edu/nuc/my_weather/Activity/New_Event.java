package dengmin.cn.edu.nuc.my_weather.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.List;

import javax.security.auth.login.LoginException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import dengmin.cn.edu.nuc.my_weather.R;
import dengmin.cn.edu.nuc.my_weather.db.Event;

public class New_Event extends AppCompatActivity {

    @InjectView(R.id.title_ed)
    EditText titleEd;

    @InjectView(R.id.time_btn_new)
    Button timeBtnNew;
    @InjectView(R.id.time_tv_new)
    TextView timeTvNew;
    @InjectView(R.id.select_time)
    Spinner selectTime;
    @InjectView(R.id.input_conent)
    EditText inputConent;
    @InjectView(R.id.overNew_btn)
    Button overNewBtn;
    @InjectView(R.id.reset_btn)
    Button resetBtn;
    @InjectView(R.id.select_hz)
    Spinner selectHz;
    @InjectView(R.id.data_btn_new)
    Button dataBtnNew;
    @InjectView(R.id.data_tv_new)
    TextView dataTvNew;


    private Intent intent;
    private String _id;
    private static final String TAG = "New_Event";
    //temp = 0 执行新建功能，temp = 1执行查找功能,temp=2对某一条记录进行具体操作
    private String TEMP = "0";

    private int remind_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        ButterKnife.inject(this);

        intent = getIntent();
        TEMP = intent.getStringExtra("TEMP");
        switch (TEMP) {
            case "0":
                break;
            case "1":
                break;
            case "2":
                //为修改和删除的界面添加初始数据
                overNewBtn.setText("修改");
                resetBtn.setText("删除");
                _id = intent.getStringExtra("_id");
                List<Event> eventList = DataSupport.where("id=?", _id).find(Event.class);
                Log.i(TAG, "onCreate: eventlist size:" + eventList.size());
                Log.i(TAG, "onCreate: id"+_id);
                Event event = eventList.get(0);
                titleEd.setText(event.getTitle());
                inputConent.setText(event.getContent());
                timeTvNew.setText(event.getTime());
                dataTvNew.setText(event.getData());
                setSpinnerItemSelectedByValue(selectHz, event.getHz());
                setSpinnerItemSelectedByValue(selectTime, event.getRemind_time());
                Log.i(TAG, "onCreate: " + event.getTitle());

                break;
            default:
        }
    }

    @OnClick({R.id.data_btn_new, R.id.time_btn_new, R.id.overNew_btn, R.id.reset_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.data_btn_new:
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(New_Event.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                dataTvNew.setText(i + "-" + (i1 + 1) + "-" + i2);
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
                ).show();
                break;
            case R.id.time_btn_new:
                Calendar calendar1 = Calendar.getInstance();
                new TimePickerDialog(New_Event.this, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timeTvNew.setText(hourOfDay + ":" + minute);
                        remind_time = hourOfDay*10000+minute+100;
                    }
                }, calendar1.get(Calendar.HOUR), calendar1.get(Calendar.MINUTE), true)
                        .show();
                break;
            case R.id.overNew_btn:
                switch (TEMP) {
                    case "0":
                        add();
                        intent.putExtra("return", "success");
                        setResult(RESULT_OK, intent);
                        break;
                    case "1":
                        //传递查询值
                        intent.putExtra("data", dataTvNew.getText().toString());
                        intent.putExtra("time", timeTvNew.getText().toString());
                        intent.putExtra("title", titleEd.getText().toString());
                        intent.putExtra("content", inputConent.getText().toString());
                        Log.i(TAG, "onViewClicked: data: "+ dataTvNew.getText().toString());
                        Log.i(TAG, "onViewClicked: title : "+titleEd.getText().toString());
                        setResult(RESULT_OK, intent);
                        break;
                    case "2":
                        ContentValues values = new ContentValues();
                        values.put("data", dataTvNew.getText().toString());
                        values.put("time", timeTvNew.getText().toString());
                        values.put("title", titleEd.getText().toString());
                        values.put("content", inputConent.getText().toString());
                        values.put("hz", selectHz.getSelectedItem().toString());
                        values.put("remind_time", selectTime.getSelectedItem().toString());
                        DataSupport.update(Event.class, values, Long.parseLong(_id));
                        setResult(RESULT_OK,intent);
                        break;
                    default:
                }
                finish();
                break;
            case R.id.reset_btn:
                switch (TEMP) {
                    case "2":
                        DataSupport.delete(Event.class, Long.parseLong(_id));
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    default:
                        titleEd.setText(null);
                        inputConent.setText(null);
                        timeTvNew.setText(null);
                        dataTvNew.setText(null);
                        break;
                }
                break;
        }
    }

    public void add() {
        if(titleEd.getText() == null)
        {
            Toast.makeText(this,"请输入标题",Toast.LENGTH_LONG).show();
        }else if(dataTvNew.getText().toString() == null){
            Toast.makeText(this,"请选择日期",Toast.LENGTH_LONG).show();
        }else if(timeTvNew.getText().toString() == null){
            Toast.makeText(this,"请选择时间",Toast.LENGTH_LONG).show();
        }else {
            Event event = new Event();
            event.setData(dataTvNew.getText().toString());
            event.setTime(timeTvNew.getText().toString());
            event.setTitle(titleEd.getText().toString());
            event.setContent(inputConent.getText().toString());
            event.setHz(selectHz.getSelectedItem().toString());
            event.setRemind_time(selectTime.getSelectedItem().toString());
            result_reminad_time();
            event.setRemindtime(remind_time);
            event.save();
        }

    }

    public static void setSpinnerItemSelectedByValue(Spinner spinner, String value) {
        SpinnerAdapter apsAdapter = spinner.getAdapter(); //得到SpinnerAdapter对象
        int k = apsAdapter.getCount();
        for (int i = 0; i < k; i++) {
            Log.i(TAG, "setSpinnerItemSelectedByValue: "+value);
            //if (value==apsAdapter.getItem(i).toString()) {
            if (value.equals(apsAdapter.getItem(i).toString())) {
                spinner.setSelection(i, true);// 默认选中项
                break;
            }
        }
    }
    public void result_reminad_time(){
        String remindtime = selectTime.getSelectedItem().toString();
        switch (remindtime){
            case "半小时":
                remind_time = remind_time-3000;
                break;
            case "一小时":
                remind_time = remind_time-10000;
                break;
            case "两小时":
                remind_time = remind_time-20000;
                break;
        }
    }
}
