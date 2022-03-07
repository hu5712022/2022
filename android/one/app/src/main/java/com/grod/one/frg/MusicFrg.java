package com.grod.one.frg;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.cyl.musicapi.BaseApiImpl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grod.one.R;
import com.grod.one.frg.music.Music;
import com.grod.one.frg.music.MusicApi;
import com.grod.one.listener.ObjListener;
import com.grod.one.net.HttpApi;
import com.grod.one.net.HttpListener;
import com.grod.one.utils.AES;
import com.grod.one.utils.PermissionUtils;
import com.grod.one.utils.SpUtils;
import com.grod.one.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MusicFrg extends BaseFrg {
    @BindView(R.id.rv)
    RecyclerView rv;
    BaseQuickAdapter<Music, BaseViewHolder> adapter;
    int currentPos = -1;

    @Override
    protected void initView() {
        rootView = View.inflate(act, R.layout.frg_music, null);
        init("音乐");
        adapter = new BaseQuickAdapter<Music, BaseViewHolder>(R.layout.item_music) {
            @Override
            protected void convert(@NonNull BaseViewHolder helper, Music item) {
                helper.setText(R.id.tv_name, item.name);
                helper.setText(R.id.tv_singer, item.singer);
                if (helper.getAdapterPosition() == currentPos) {
                    helper.itemView.setBackgroundResource(R.color.color_33BB86FC);
                } else {
                    helper.itemView.setBackgroundResource(0);
                }
            }
        };
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter a, View view, int position) {
                Music item = adapter.getItem(position);

                if (item.wyy) {
                    playWyy(item, position);
                } else {
                    play(item.path, position);
                }
            }
        });
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(act));
        setTitleRight("歌单").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFromDialog();
            }
        });
        int music_src = SpUtils.getInt("music_src");

        loadData(music_src);
    }


    private void playWyy(Music item, int position) {
        String path = SpUtils.getString("music_path" + item.id_wyy);
        if (!TextUtils.isEmpty(path) && new File(path).exists()) {
            item.path = path;
            play(item.path, position);
            return;
        }
        HttpApi.wyyUrl(item.id_wyy, new HttpListener() {
            @Override
            public void onResult(String data) {
                JsonObject json = (JsonObject) new JsonParser().parse(data);
                String url = json.get("musicurl").getAsString();
                item.path = url;
                play(item.path, position);
                String[] split = url.split("\\.");
                String ty = "." + split[split.length - 1];
                String path = Utils.cachePath() + "/" + item.name+ty;
                HttpApi.get().down(url, path, new HttpListener() {
                    @Override
                    public void onResult(String data) {
                        SpUtils.putString("music_path" + item.id_wyy, path);
                    }
                });
            }
        });
    }

    private void play(String path, int position) {
        MusicApi.get().play(path, new ObjListener() {
            @Override
            public void onResult(Object data) {
                int code = (int) data;
                if (code == 0) {
                    //初始化播放成功
                    int pos = currentPos;
                    currentPos = position;
                    if (pos != -1) {
                        adapter.notifyItemChanged(pos);
                    }
                    adapter.notifyItemChanged(position);
                } else if (code == 1) {
                    //播放结束 或 播放失败
                }
            }
        });
    }

    private void showFromDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        int music_src = SpUtils.getInt("music_src");
        builder.setSingleChoiceItems(new String[]{"本地", "网易云新歌榜","网易云飙升榜","网易云说唱榜","qq"}, music_src, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadData(which);
            }
        });
        builder.show();
    }

    private void loadData(int pos) {
        if (pos == 0) {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(new File("/")));
            act.sendBroadcast(intent);
            loadLocaData();
        } else if (pos == 1) {
            loadWyy(3779629);
        }else if(pos == 2){
            loadWyy(19723756);
        }else if(pos == 3){
            loadWyy(991319590);
        }else if(pos == 4){
            BaseApiImpl.INSTANCE.getQQTopList();
        }
        SpUtils.putInt("music_src", pos);
    }

    public void loadWyy(long id) {
        // 新歌榜 3779629
        // 飙升榜 19723756
        // 说唱榜 991319590
        HttpApi.wyyList(id,new HttpListener(){
            @Override
            public void onResult(String data) {
                JsonObject json = (JsonObject) new JsonParser().parse(data);
                JsonArray tracks = json.getAsJsonArray("results");
                List<Music> list = new ArrayList<>();
                for (int i = 0; i < tracks.size(); i++) {
                    JsonObject item = tracks.get(i).getAsJsonObject();
                    Music music = new Music();
                    music.name = item.get("name").getAsString();
                    music.id_wyy = item.get("id").getAsLong();
                    JsonArray artist = item.getAsJsonArray("artist");
                    StringBuilder singer = new StringBuilder();
                    for(int x=0;x<artist.size();x++) {
                        if(x!=artist.size()-1){
                            singer.append(artist.get(x).getAsJsonObject().get("name").getAsString()).append("、");
                        }else {
                            singer.append(artist.get(x).getAsJsonObject().get("name").getAsString());
                        }
                    }
                    music.singer = singer.toString();
                    music.wyy = true;
                    list.add(music);
                }
                adapter.setNewData(list);
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
