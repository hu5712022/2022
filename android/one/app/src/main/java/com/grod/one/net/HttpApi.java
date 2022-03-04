package com.grod.one.net;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.grod.one.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }
            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        };
        SSLContext  sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());
        } catch ( Exception e) {
        }
        client = new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(),xtm).build();

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

    public void down(String url, String path, HttpListener listener) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
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
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                InputStream is = response.body().byteStream();
                try {
                    File file = new File(path);
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] bytes = new byte[2048];
                    int len = 0;
                    while (-1 != (len = is.read(bytes))) {
                        fos.write(bytes, 0, len);
                    }
                    fos.flush();
                    fos.close();
                    is.close();
                    Utils.logE(url + ":下载成功");
                    handler.post(() -> {
                        listener.onResult(path);
                    });

                } catch (IOException e) {
                    onFailure(call, e);
                }


            }
        });
    }


}
