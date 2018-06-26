package dengmin.cn.edu.nuc.my_weather.service;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;

import dengmin.cn.edu.nuc.my_weather.R;
import dengmin.cn.edu.nuc.my_weather.gson.Weather;
import dengmin.cn.edu.nuc.my_weather.util.GetAreaWithGSON;
import dengmin.cn.edu.nuc.my_weather.util.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class Auto_Update_Service extends Service {
    private Weather weather;
    private static final String TAG = "Auto_Update_Service";
    public Auto_Update_Service() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        Log.i(TAG, "updataservice invoke ");
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int once = 4*60*60*1000;
        long triggerAtTime = SystemClock.elapsedRealtime()+once;
        Intent intent1 = new Intent(this,Auto_Update_Service.class);
        PendingIntent pi = PendingIntent.getService(this,0,intent1,0);
        manager.cancel(pi);
//        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//        Notification.Builder builder = new Notification.Builder(Auto_Update_Service.this);
//        builder.setContentTitle(weather.now.more.info);
//        builder.setContentText("天气已更新");
//        builder.setWhen(System.currentTimeMillis());
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
//        builder.setContentIntent(pi);
//        builder.setAutoCancel(true);
//        Notification notification = builder.build();
//        notificationManager.notify(0,notification);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }
    //更新天气
    private void updateWeather(){
        Log.i(TAG, "updateWeather: 天气已更新");
        SharedPreferences prefs = getSharedPreferences("data",MODE_PRIVATE);
        String weatherId = prefs.getString("weatherId",null);
        if(weatherId!=null){
            String url = "http://guolin.tech/api/weather?cityid="+weatherId+"&key=47618a326a824f3a88b9f10915d4f0fd";
            HttpUtil.sendOkHttpRequest(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    weather = GetAreaWithGSON.handleWeatherPesponse(responseText);
                    if(weather!= null&&"ok".equals(weather.status)){
                        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                        editor.putString("weather",responseText);
                        editor.putString("weather_today",weather.now.more.info);
                        editor.apply();
                    }
                }
            });
        }
    }
}
