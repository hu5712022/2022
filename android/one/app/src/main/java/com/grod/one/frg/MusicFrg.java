package com.grod.one.frg;

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

import butterknife.BindView;

public class MusicFrg extends BaseFrg{
    @BindView(R.id.rv)
    RecyclerView rv;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = View.inflate(act, R.layout.frg_music,null);
        init("音乐");
        BaseQuickAdapter<Music, BaseViewHolder> adapter = new BaseQuickAdapter<Music, BaseViewHolder>(R.layout.item_music) {
            @Override
            protected void convert(@NonNull BaseViewHolder helper, Music item) {
                
            }
        };
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(act));
        return rootView;
    }
}
