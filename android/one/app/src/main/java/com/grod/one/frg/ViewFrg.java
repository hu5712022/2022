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
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.grod.one.R;
import com.grod.one.view.FloatView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewFrg extends BaseFrg{
    @BindView(R.id.rv)
    RecyclerView rv;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = View.inflate(act, R.layout.frg_view,null);
        init("弹弹效果");

        BaseQuickAdapter<String, BaseViewHolder> adapter = new BaseQuickAdapter<String,BaseViewHolder>(R.layout.item_frg_view) {
            @Override
            protected void convert(BaseViewHolder holder, String o) {
                holder.addOnClickListener(R.id.bt_start,R.id.bt_stop);
            }
        };
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                FloatView fv = (FloatView) adapter.getViewByPosition(position,R.id.fv);
                if(view.getId() == R.id.bt_start){
                    fv.startAnim();
                }else if(view.getId() == R.id.bt_stop){
                    fv.stopAnim();
                }
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(act));
        rv.setAdapter(adapter);
        adapter.bindToRecyclerView(rv);
        adapter.addData("");
        adapter.addData("");
        adapter.addData("");
        adapter.addData("");
        return rootView;
    }
}
