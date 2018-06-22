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
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.List;

import dengmin.cn.edu.nuc.my_weather.R;
import dengmin.cn.edu.nuc.my_weather.db.Event;

public class Event_remind_Service extends Service {
    private List<Event> eventList;
    public Event_remind_Service() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public int onStartCommand(Intent intent, int flags, int startId) {
        Query_event_remindtime();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int once = 24*60*60*1000;
        long triggerAtTime = SystemClock.elapsedRealtime()+once;
        Intent intent1 = new Intent(this,Auto_Update_Service.class);
        PendingIntent pi = PendingIntent.getService(Event_remind_Service.this,0,intent,0);
        manager.cancel(pi);
        //设置闹钟的执行模式，间隔时间和行为
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void Query_event_remindtime(){
        Calendar calendar = Calendar.getInstance();
        String data = (String.valueOf(calendar.get(Calendar.YEAR))+String.valueOf(calendar.get(Calendar.MONTH)+1)+String.valueOf(calendar.get(Calendar.DAY_OF_YEAR)));
        eventList = DataSupport.where("data=?", data).order("remindtime asc").find(Event.class);
        for(Event event:eventList){
            int i=1;
            int remindtime = event.getRemindtime();
            long selectTime = start_remind(remindtime);
            if (selectTime == 0){
                continue;
            }
            Intent intent1 = new Intent(this,Auto_Update_Service.class);
            PendingIntent pi = PendingIntent.getService(this,0,intent1,0);
            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            alarmManager.cancel(pi);
            long triggerAtTime = SystemClock.elapsedRealtime()+selectTime;
            NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(Event_remind_Service.this);
            builder.setContentTitle(event.getTitle().toString());
            builder.setContentText(event.getTime().toString()+event.getContent().toString());
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            builder.setAutoCancel(true);

            Notification notification = builder.build();
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            notification.sound = uri;//默认铃声作为提示音
            notification.ledARGB = Color.BLUE;// 控制 LED 灯的颜色，一般有红绿蓝三种颜色可选
            notification.ledOnMS = 1000;// 指定 LED 灯亮起的时长，以毫秒为单位
            notification.ledOffMS = 1000;// 指定 LED 灯暗去的时长，也是以毫秒为单位
            notification.flags = Notification.FLAG_SHOW_LIGHTS;// 指定通知的一些行为，

            manager.notify(i,notification);
            alarmManager.set(AlarmManager.RTC_WAKEUP,selectTime,pi);

        }
    }
    private long start_remind(int remindtime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        long selectTime;
        int hour = remindtime/10000;
        int minute = (remindtime-hour*10000)/100;
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        selectTime = calendar.getTimeInMillis();
        if(selectTime>selectTime){
            selectTime = 0;
        }
        return selectTime;
    }
}
