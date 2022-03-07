package com.grod.one.frg;

import android.view.View;

import com.grod.one.R;

public class VideoFrg extends BaseFrg{

    @Override
    protected void initView() {
         rootView = View.inflate(act,R.layout.frg_list,null);
         init("视频");


    }
}
