package com.grod.one.net;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grod.one.frg.music.Music;
import com.grod.one.utils.SpUtils;
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
import java.util.ArrayList;
import java.util.List;

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


    public static void wyyUrl(long id,HttpListener listener){
        // 备用api https://musiclake.leanapp.cn/song/url?id=1924462547
        // https://api.no0a.cn/ 文档
        HttpApi.get().get("https://api.no0a.cn/api/cloudmusic/url/" + id, listener);

        // 网易云 linux api
//        JsonObject json = new JsonObject();
//        json.addProperty("method", "POST");
//        json.addProperty("url", "http://music.163.com/api/song/enhance/player/url");
//        JsonObject params = new JsonObject();
//        JsonArray musicIdArray = new JsonArray();
//        musicIdArray.add(item.id_wyy);
//        params.addProperty("br", 320000);
//        params.add("ids", musicIdArray);
//        json.add("params", params);
//        String encrypted = AES.encrypt(json.toString());
//        Request.Builder requestBuilder = new Request.Builder();
//        requestBuilder.url(HttpUrl.parse("http://music.163.com/api/linux/forward"));
//        requestBuilder.url(HttpUrl.parse("https://interface3.music.163.com/eapi/song/enhance/player/url"));
//        requestBuilder.addHeader("Referrer", "http://music.163.com/");
//        requestBuilder.addHeader("X-REAL-IP", "47.93.50." + (1 + new Random().nextInt(255)));
//        FormBody body = new FormBody.Builder().add("eparams", encrypted).build();
//        requestBuilder.post(body);
//        Request request = requestBuilder.build();
//        HttpApi.get().send(request, new HttpListener() {
//            @Override
//            public void onResult(String data) {
//                super.onResult(data);
//            }
//        });
    }



    public static void wyyList(long id, HttpListener listener) {
        // 备用api https://musiclake.leanapp.cn/song/url?id=1924462547
        // https://api.no0a.cn/ 文档
        String url = "https://api.no0a.cn/api/cloudmusic/playlist/"+id;
        HttpApi.get().get(url,listener);
        //网易云 weapi
//        String url = "https://interface.music.163.com/weapi/v6/playlist/detail";
//        String data = "params=Nfnx77pJ9AQB1%2BwCMLYSv2sJJYc3Mg2fW%2FqmXwdYqF%2FI1kUuNZ%2FAF0MlA5qU%2BfL1NDP7BjU42wk4wUS%2FMPvD0VbCf04rLipDsauni%2Bgbz9E%3D&encSecKey=4460c23ae6a10ff5df080ebe2b0fc4a0babd0b6af10296586ad4b815b47e638948e71ea2b6f61ba2db27af6239ce6a974f51c524c30df78ecd81eb88cfd272641a0a62e9d3477672e8d5553719fd121e6276a7b0ef9280b510eae43fd6586c1c81f7312a71bfb0c84e7be15189bf4ea7e287e97fe09826f1c1b5927837adc127";
//        RequestBody body = RequestBody.create(data, MediaType.get("application/x-www-form-urlencoded"));
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .build();
//        HttpApi.get().send(request, new HttpListener() {
//            @Override
//            public void onResult(String data) {
//                JsonObject json = (JsonObject) new JsonParser().parse(data);
//                JsonArray tracks = json.getAsJsonObject("playlist").getAsJsonArray("tracks");
//                List<Music> list = new ArrayList<>();
//                for (int i = 0; i < tracks.size(); i++) {
//                    JsonObject item = tracks.get(i).getAsJsonObject();
//                    Music music = new Music();
//                    music.name = item.get("name").getAsString();
//                    music.id_wyy = item.get("id").getAsLong();
//                    music.wyy = true;
//                    list.add(music);
//                }
//                adapter.setNewData(list);
//            }
//        });
    }
}
