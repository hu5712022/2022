package com.grod.one.frg;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grod.one.R;
import com.grod.one.frg.video.Video;
import com.grod.one.net.HttpApi;
import com.grod.one.net.HttpListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.jzvd.JzvdStd;

public class VideoFrg extends BaseFrg {
    @BindView(R.id.rv)
    RecyclerView rv;
    BaseQuickAdapter<Video, BaseViewHolder> adapter;

    @Override
    protected void initView() {
        rootView = View.inflate(act, R.layout.frg_list, null);
        init("视频");
        adapter = new BaseQuickAdapter<Video, BaseViewHolder>(R.layout.item_video) {
            @Override
            protected void convert(@NonNull BaseViewHolder helper, Video item) {
                JzvdStd jzvdStd = helper.getView(R.id.video);
                if(item.path == null) {
                    helper.getView(R.id.view_click).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(TextUtils.equals(item.srcType,"yyt")) {
                                HttpApi.get().get("https://data.yinyuetai.com/video/getVideoInfo?id=" + item.srcId, new HttpListener() {
                                    @Override
                                    public void onResult(String data) {
                                        JsonObject json = (JsonObject) new JsonParser().parse(data);
                                        String videoUrl = json.get("videoUrl").getAsString();
                                        item.path = videoUrl;
                                        adapter.notifyItemChanged(helper.getAdapterPosition());
                                    }
                                });
                            }else if(TextUtils.equals(item.srcType,"dy")){
                                // 抖音接口
                                // https://www.douyin.com/web/api/v2/aweme/iteminfo/?item_ids=7006666248016629000
                            }
                        }
                    });
                    helper.setGone(R.id.view_click,true);
                }else {
                    helper.setGone(R.id.view_click,false);
                }
                jzvdStd.setUp(item.path, item.title);
                Glide.with(act)
                        .load(item.img)
                        .into(jzvdStd.posterImageView);
            }
        };
        rv.setLayoutManager(new LinearLayoutManager(act));
        rv.setAdapter(adapter);
        adapter.addData(new Video());
        setTitleRight("来源").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFromDialog();
            }
        });

        getYytList();
    }

    private void showFromDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(act);

        builder.setItems(new String[]{"音悦台", "", "", ""}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    private void getDyList(){
        // 通过网页数据 抓id
        // https://www.douyin.com/discover
        // 抓这个  ： https://www.douyin.com/video/7055555524519054628
    }

    private void getYytList() {
        // videoType  12345
        String url = "https://data.yinyuetai.com/video/getTypeVideoList?videoType=1";
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
                    listVideo.add(video);
                }
                adapter.setNewData(listVideo);
            }
        });
    }
}
