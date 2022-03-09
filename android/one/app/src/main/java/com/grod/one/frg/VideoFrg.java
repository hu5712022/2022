package com.grod.one.frg;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.grod.one.OneApp;
import com.grod.one.R;
import com.grod.one.frg.video.Video;

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
                jzvdStd.setUp("http://jzvd.nathen.cn/c6e3dc12a1154626b3476d9bf3bd7266/6b56c5f0dc31428083757a45764763b0-5287d2089db37e62345123a1be272f8b.mp4"
                        , "饺子闭眼睛");
                Glide.with(jzvdStd.posterImageView).load("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640");

            }
        };
        rv.setLayoutManager(new LinearLayoutManager(act));
        rv.setAdapter(adapter);
        adapter.addData(new Video());

    }
}
