package com.grod.one.frg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grod.one.R;

public class ImgFrg extends BaseFrg{

    @Override
    protected void initView() {
         rootView = View.inflate(act,R.layout.frg_img,null);
         init("图片");


    }
}
