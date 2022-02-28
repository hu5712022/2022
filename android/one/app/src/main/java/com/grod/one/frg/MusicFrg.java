package com.grod.one.frg;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.grod.one.R;
import com.grod.one.frg.music.Music;
import com.grod.one.frg.music.MusicApi;
import com.grod.one.listener.ObjListener;
import com.grod.one.net.HttpApi;
import com.grod.one.net.HttpListener;
import com.grod.one.utils.PermissionUtils;
import com.grod.one.utils.SpUtils;
import com.grod.one.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MusicFrg extends BaseFrg{
    @BindView(R.id.rv)
    RecyclerView rv;
    BaseQuickAdapter<Music, BaseViewHolder> adapter;
    int currentPos = -1;

    @Override
    protected void initView() {
        rootView = View.inflate(act, R.layout.frg_music,null);
        init("音乐");
        adapter = new BaseQuickAdapter<Music, BaseViewHolder>(R.layout.item_music) {
            @Override
            protected void convert(@NonNull BaseViewHolder helper, Music item) {
                helper.setText(R.id.tv_name,item.name);
                if(helper.getAdapterPosition() == currentPos){
                    helper.setTextColor(R.id.tv_name, Utils.color(R.color.purple_200));
                }else {
                    helper.setTextColor(R.id.tv_name, Utils.color(R.color.color_303030));
                }
            }
        };
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter a, View view, int position) {
                Music item = adapter.getItem(position);

                MusicApi.get().play(item.path, new ObjListener() {
                    @Override
                    public void onResult(Object data) {
                        int code = (int) data;
                        if(code == 0){
                            //初始化播放成功
                            int pos = currentPos;
                            currentPos  = position;
                            if(pos!=-1) {
                                adapter.notifyItemChanged(pos);
                            }
                            adapter.notifyItemChanged(position);
                        }else if(code == 1){
                            //播放结束 或 播放失败
                        }
                    }
                });
            }
        });
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(act));
        setTitleRight("刷新").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showFromDialog();
            }
        });
        loadLocaData();
    }

    private void showFromDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        SpUtils.getInt("music_src");
        builder.setSingleChoiceItems(new String[]{"本地", "网易云"}, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    Intent intent =new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(new File("/")));
                    act.sendBroadcast(intent);
                    loadLocaData();
                }else if(which == 1){
                    loadWyy();
                }
                SpUtils.putInt("music_src",which);
            }
        });
        builder.show();
    }

    public void loadWyy(){
        String url = "https://interface.music.163.com/weapi/v6/playlist/detail";
        String data = "params=Nfnx77pJ9AQB1%2BwCMLYSv2sJJYc3Mg2fW%2FqmXwdYqF%2FI1kUuNZ%2FAF0MlA5qU%2BfL1NDP7BjU42wk4wUS%2FMPvD0VbCf04rLipDsauni%2Bgbz9E%3D&encSecKey=4460c23ae6a10ff5df080ebe2b0fc4a0babd0b6af10296586ad4b815b47e638948e71ea2b6f61ba2db27af6239ce6a974f51c524c30df78ecd81eb88cfd272641a0a62e9d3477672e8d5553719fd121e6276a7b0ef9280b510eae43fd6586c1c81f7312a71bfb0c84e7be15189bf4ea7e287e97fe09826f1c1b5927837adc127";
        RequestBody body = RequestBody.create(data, MediaType.get("application/x-www-form-urlencoded"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        HttpApi.get().send(request, new HttpListener() {
            @Override
            public void onResult(String data) {
                try {
                    JSONObject json =new JSONObject(data);
                } catch (JSONException e) {
                    onError(data);
                }
            }
        });
    }



    public void loadLocaData() {
        PermissionUtils.request(act, PermissionUtils.permissFile(), new PermissionUtils.OnPermissListener() {
            @Override
            public void onOk() {
                List<Music> locaList = Music.getLocaList(act);
                adapter.setNewData(locaList);
            }
        });
    }
}
