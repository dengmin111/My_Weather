package dengmin.cn.edu.nuc.my_weather.util;

import android.app.DownloadManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import dengmin.cn.edu.nuc.my_weather.db.Province;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {
    private static final String TAG = "HttpUtil";
    public static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
    public static void sendPost(String address,String json){
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON,json);
        Request request = new Request.Builder()
                .post(body)
                .url("address")
                .build();
        try {
            Response response=client.newCall(request).execute();
            //判断请求是否成功
            if(response.isSuccessful()){
                //打印服务端返回结果
                Log.i(TAG,response.body().string());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}