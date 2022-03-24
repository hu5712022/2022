package com.grod.one.frg;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cyl.musicapi.BaseApiImpl;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grod.one.R;
import com.grod.one.frg.video.Video;
import com.grod.one.net.HttpApi;
import com.grod.one.net.HttpListener;
import com.grod.one.utils.SpUtils;
import com.grod.one.utils.Utils;
import com.grod.one.view.web.WebViewX5;

import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import okhttp3.Request;

public class VideoFrg extends BaseFrg {
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.web)
    WebView web;
    BaseQuickAdapter<Video, BaseViewHolder> adapter;

    @Override
    protected void initView() {
        rootView = View.inflate(act, R.layout.frg_list, null);
        init("视频");
        adapter = new BaseQuickAdapter<Video, BaseViewHolder>(R.layout.item_video) {
            @Override
            protected void convert(@NonNull BaseViewHolder helper, Video item) {
                JzvdStd jzvdStd = helper.getView(R.id.video);
                int pos = helper.getAdapterPosition();
                helper.getView(R.id.tv_url).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.equals(item.srcType, "yyt")) {
                            WebViewX5 web = new WebViewX5(act);
                            web.loadUrl("https://www.yinyuetai.com/play?id=" + item.srcId);
                            web.setWebViewClient(new WebViewClient() {
                                boolean first = true;

                                @Override
                                public WebResourceResponse shouldInterceptRequest(WebView view,
                                                                                  WebResourceRequest request) {
                                    String url = request.getUrl().toString();
                                    Utils.logE(url);
                                    if (url.endsWith("mp4") && first) {
                                        first = false;
                                        x.task().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                web.destroy();
                                                String videoUrl = url;
                                                item.path = videoUrl;
                                                adapter.notifyItemChanged(pos);
                                                Utils.toast("获取url：" + videoUrl);
                                                String path = new File(Utils.getVideoDir(),
                                                        item.title + ".mp4").getAbsolutePath();
                                                HttpApi.get().downX(videoUrl, path,
                                                        new HttpListener() {
                                                            @Override
                                                            public void onError(String data) {
                                                                web.destroy();
                                                                Utils.toast("下载失败：" + item.title + data);
                                                            }

                                                            @Override
                                                            public void onResult(String data) {
                                                                web.destroy();
                                                                //下完
                                                                Utils.toast("下载成功：" + item.title);
                                                                item.path = path;

                                                                adapter.notifyItemChanged(pos);
                                                            }
                                                        });
                                            }
                                        }, 1000);
                                    }
                                    return super.shouldInterceptRequest(view,
                                            request);
                                }
                            });
//                                String url = "https://data.yinyuetai" +
//                                        ".com/video/getVideoInfo?id=" + item.srcId;
//                                HttpApi.get().get(url,
//                                        new HttpListener() {
//                                            @Override
//                                            public void onResult(String data) {
//                                                JsonObject json =
//                                                        (JsonObject) new JsonParser().parse(data);
//                                                String videoUrl =
//                                                        json.get("videoUrl").getAsString();
//                                                item.path = videoUrl;
//                                                jzvdStd.setUp(item.path, item.title);
//                                                jzvdStd.startButton.performClick();
//                                                String path = new File(Utils.getVideoDir(),
//                                                item.title + ".mp4").getAbsolutePath();
//                                                HttpApi.get().downX(videoUrl,path,new
//                                                HttpListener());
//                                            }
//                                        });

                        } else if (TextUtils.equals(item.srcType, "dy")) {
                            // 抖音接口
                            // https://www.douyin.com/web/api/v2/aweme/iteminfo/?item_ids=7006666248016629000
                            HttpApi.get().get("https://www.douyin.com/web/api/v2/aweme/iteminfo" +
                                    "/?item_ids=" + item.srcId, new HttpListener() {
                                @Override
                                public void onResult(String data) {
                                    JsonObject json = (JsonObject) new JsonParser().parse(data);
                                    String videoUrl = json.getAsJsonArray("item_list").get(0)
                                            .getAsJsonObject().getAsJsonObject("video")
                                            .getAsJsonObject("play_addr")
                                            .getAsJsonArray("url_list")
                                            .get(0).getAsString();
                                    item.path = videoUrl;
                                    adapter.notifyItemChanged(pos);
                                    Utils.toast("获取url：" + videoUrl);
                                    String path = new File(Utils.getVideoDir(),
                                            item.title + ".mp4").getAbsolutePath();
                                    HttpApi.get().downX(videoUrl, path,
                                            new HttpListener() {
                                                @Override
                                                public void onError(String data) {
                                                    web.destroy();
                                                    Utils.toast("下载失败：" + item.title + data);
                                                }

                                                @Override
                                                public void onResult(String data) {
                                                    web.destroy();
                                                    //下完
                                                    Utils.toast("下载成功：" + item.title);
                                                    item.path = path;
                                                    adapter.notifyItemChanged(pos);
                                                }
                                            });
                                }
                            });
                        }
                    }
                });
                if (!TextUtils.isEmpty(item.path) && !item.path.startsWith("http")) {
                    helper.setText(R.id.tv_url, "已下载");
                } else {
                    helper.setText(R.id.tv_url, "获取url");
                }
                jzvdStd.setUp(item.path, item.title);
                Glide.with(act)
                        .load(item.img)
                        .into(jzvdStd.posterImageView);
            }
        };
        rv.setLayoutManager(new LinearLayoutManager(act));
        rv.setAdapter(adapter);
        rv.getItemAnimator().setChangeDuration(0);
        adapter.addData(new Video());
        setTitleRight("来源").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFromDialog();
            }
        });


        loadData(SpUtils.getInt("video_src"));
    }

    private void loadData(int pos) {
        if (pos < 5) {
            getYytList(pos + 1);
        } else {
            getDyList();
        }
    }

    private void showFromDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        String[] strs = {"音悦台内地", "音悦台香港", "音悦台台湾", "音悦台日韩", "音悦台榜", "抖音"};
        builder.setItems(strs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                SpUtils.putInt("video_src", which);
                loadData(which);
            }
        });
        builder.show();
    }

    final class Dy {
        @JavascriptInterface
        public void load(String data) {
            String[] split = data.split("\"><a href=\"//www.douyin.com/video/");
            List<Video> listVideo = new ArrayList<>();
            for (int i = 1; i < split.length; i++) {
                Video video = new Video();
                String item = split[i];
                String id = item.split("\"")[0];
                String img =
                        "https://" + item.split("<img src=\"//")[1].split("\"")[0].replaceAll(
                                "&amp;", "&");
                String name = item.split("alt=\"")[1].split("\"")[0];
                video.srcType = "dy";
                video.srcId = id;
                video.title = name;
                video.img = img;
                String path =
                        new File(Utils.getVideoDir(), video.title + ".mp4").getAbsolutePath();
                if (new File(path).exists()) {
                    video.path = path;
                }
                listVideo.add(video);
            }
            x.task().post(new Runnable() {
                @Override
                public void run() {
                    adapter.setNewData(listVideo);
                }
            });

        }
    }

    @SuppressLint("JavascriptInterface")
    private void getDyList() {
        WebView web = new WebView(act);
        web.getSettings().setSupportZoom(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setJavaScriptEnabled(true);
        web.requestFocus();
        web.getSettings().setUseWideViewPort(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setSupportZoom(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.addJavascriptInterface(new Dy(), "android");
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                x.task().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.loadUrl("javascript:window.android.load(document" +
                                ".getElementsByTagName('html')[0].innerHTML);");
                    }
                }, 1000);

            }
        });
        web.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.74 Safari/537.36 Edg/99" +
                ".0.1150.46");
        web.loadUrl("https://www.douyin.com/discover");


    }

    private void getYytList(int type) {
        // videoType  12345
        String url = "https://data.yinyuetai.com/video/getTypeVideoList?videoType=" + type;
        HttpApi.get().get(url, new HttpListener() {
            @Override
            public void onResult(String data) {
                JsonObject json = (JsonObject) new JsonParser().parse(data);
                JsonArray list = json.getAsJsonArray("list");
                List<Video> listVideo = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    JsonObject item = list.get(i).getAsJsonObject();
                    Video video = new Video();
                    video.srcType = "yyt";
                    video.title = item.get("videoName").getAsString();
                    video.img = item.get("videoImg").getAsString();
                    video.srcId = item.get("id").getAsString();
                    video.artist = item.get("artistName").getAsString();

                    String path =
                            new File(Utils.getVideoDir(), video.title + ".mp4").getAbsolutePath();
                    if (new File(path).exists()) {
                        video.path = path;
                    }

                    listVideo.add(video);
                }
                adapter.setNewData(listVideo);
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }
}
