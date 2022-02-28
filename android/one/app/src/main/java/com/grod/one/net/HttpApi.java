package com.grod.one.net;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.grod.one.utils.Utils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpApi {

    private static HttpApi api;
    private OkHttpClient client;
    private Handler handler;

    private HttpApi() {
        client = new OkHttpClient();
        handler = new Handler();
    }

    public static HttpApi get() {
        if (api == null) {
            api = new HttpApi();
        }
        return api;
    }

    public void post(String url, String json, HttpListener listener) {
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        send(request, listener);
    }

    public void get(String url, HttpListener listener) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        send(request, listener);
    }

    public void send(Request request, HttpListener listener) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                handler.post(() -> {
                    String url = call.request().url().toString();
                    Utils.logE(url + ":" + e.getMessage());
                    listener.onError("请求异常");
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                handler.post(() -> {
                    try {
                        String url = call.request().url().toString();
                        String data = response.body().string();
                        Utils.logE(url + ":" + data);
                        listener.onResult(data);
                    } catch (IOException e) {
                        onFailure(call, e);
                    }
                });

            }
        });
    }
}
