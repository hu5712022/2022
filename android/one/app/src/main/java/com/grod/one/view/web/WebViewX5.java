package com.grod.one.view.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WebViewX5 extends WebView {
    public WebViewX5(@NonNull Context context) {
        this(context,null);
    }

    public WebViewX5(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WebViewX5(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public WebViewX5(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    private void init() {
        getSettings().setJavaScriptEnabled(true);
        addJavascriptInterface(new JsEvent(),"android");
    }
}
