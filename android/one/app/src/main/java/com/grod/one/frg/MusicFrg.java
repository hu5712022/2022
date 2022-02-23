package com.grod.one.frg;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.grod.one.R;
import com.grod.one.frg.music.Music;
import com.grod.one.utils.PermissionUtils;

import java.io.File;
import java.util.List;

import butterknife.BindView;

public class MusicFrg extends BaseFrg{
    @BindView(R.id.rv)
    RecyclerView rv;
    BaseQuickAdapter<Music, BaseViewHolder> adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = View.inflate(act, R.layout.frg_music,null);
        init("音乐");
         adapter = new BaseQuickAdapter<Music, BaseViewHolder>(R.layout.item_music) {
            @Override
            protected void convert(@NonNull BaseViewHolder helper, Music item) {
                helper.setText(R.id.tv_name,item.name);
            }
        };
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(act));
        if(!isInit){
            Intent intent =new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(new File("/")));
            act.sendBroadcast(intent);
            isInit = true;
            loadData();
        }
        setTitleRight("刷新").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });
        return rootView;
    }

    public void loadData() {
        PermissionUtils.request(act, PermissionUtils.permissFile(), new PermissionUtils.OnPermissListener() {
            @Override
            public void onOk() {
                List<Music> locaList = Music.getLocaList(act);
                adapter.setNewData(locaList);
            }
        });
    }
}
