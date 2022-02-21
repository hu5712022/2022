package com.grod.one.bean;

import org.greenrobot.eventbus.EventBus;

public class MsgEvent {
    public static final int ONE_MSG = 0;

    public int code;
    public Object data;


    public MsgEvent(int code){
        this.code = code;
    }

    public MsgEvent setData(Object object){
        object = data;
        return this;
    }

    public void post(){
        EventBus.getDefault().post(this);
    }
}
