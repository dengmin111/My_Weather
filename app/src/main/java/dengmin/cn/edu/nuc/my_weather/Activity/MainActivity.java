package dengmin.cn.edu.nuc.my_weather.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dengmin.cn.edu.nuc.my_weather.R;
import dengmin.cn.edu.nuc.my_weather.service.Auto_Update_Service;
import dengmin.cn.edu.nuc.my_weather.service.Event_remind_Service;
import dengmin.cn.edu.nuc.my_weather.service.Location;

public class MainActivity extends AppCompatActivity {

    private Intent intent2;
    private static final String TAG = "test";
    @InjectView(R.id.welcome_iv)
    ImageView welcomeIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);


//        List<String> permissionList = new ArrayList<>();
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
//                PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
//        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
//                PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.READ_PHONE_STATE);
//        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
//                PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        }
//        if (!permissionList.isEmpty()) {
//            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
//            ActivityCompat.requestPermissions(this, permissions, 1);
//        } else {
//            Intent intent3 = new Intent(MainActivity.this, Location.class);
//            startService(intent3);
//        }
//        Intent intent = new Intent(MainActivity.this, Auto_Update_Service.class);
//        startService(intent);
//        Intent intent1 = new Intent(MainActivity.this, Event_remind_Service.class);
//        startService(intent1);
        if(HaveWeather()){
             intent2 = new Intent(MainActivity.this, Look_Event.class);

        }else {
            intent2 = new Intent(MainActivity.this,select_area.class);
        }
        startActivity(intent2);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "如果没有该权限可能无法使用部分功能", Toast.LENGTH_LONG).show();
                            Toast.makeText(this, "请使用手动定位", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(this, select_area.class);
                            startActivityForResult(intent, 7);
                            finish();
                        }
                    }
                } else {
                    Intent intent3 = new Intent(MainActivity.this, Location.class);
                    startService(intent3);
                }
        }
    }
    private boolean HaveWeather(){
        boolean result = false;
        SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
        if(preferences.getString("weatherId",null)!=null);
        {
            result = true;
        }
        return result;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 7 && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "部分功能需要确切的地理位置", Toast.LENGTH_LONG).show();
        }
    }
}
