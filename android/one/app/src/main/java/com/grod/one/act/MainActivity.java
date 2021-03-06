package com.grod.one.act;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.grod.one.R;
import com.grod.one.act.BaseAct;
import com.grod.one.frg.BaseFrg;
import com.grod.one.frg.ImgFrg;
import com.grod.one.frg.MusicFrg;
import com.grod.one.frg.VideoFrg;
import com.grod.one.frg.ViewFrg;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jzvd.Jzvd;

public class MainActivity extends BaseAct {

    @BindView(R.id.vp)
    ViewPager vp;
    List<BaseFrg> listFrg = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        listFrg.add(new ViewFrg());
        listFrg.add(new MusicFrg());
        listFrg.add(new VideoFrg());
        listFrg.add(new ImgFrg());
        PagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return listFrg.get(position);
            }

            @Override
            public int getCount() {
                return listFrg.size();
            }
        };
        vp.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }
}