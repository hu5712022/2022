package com.grod.one.frg;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grod.one.R;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;

public class BaseFrg extends Fragment {
    protected View rootView;

    protected Activity act;
    protected boolean isInit;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = getActivity();
        isInit = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isInit = false;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public void init(String title){
        ButterKnife.bind(this,rootView);
        setTitle(title);
    }
    public void setTitle(String title) {
        TextView tv = rootView.findViewById(R.id.tv_title);
        tv.setText(title);
    }
    public TextView setTitleRight(String title) {
        TextView tv = rootView.findViewById(R.id.tv_title_right);
        tv.setText(title);
        return tv;
    }
}
