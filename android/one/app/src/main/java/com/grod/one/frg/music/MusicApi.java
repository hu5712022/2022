package com.grod.one.frg.music;

import android.media.AudioRouting;
import android.media.MediaPlayer;
import android.text.TextUtils;

import com.grod.one.listener.ObjListener;

import java.io.IOException;

public class MusicApi {

    private String currentUrl;
    private MediaPlayer player;
    public static MusicApi api;

    public static MusicApi get() {
        if (api == null) {
            api = new MusicApi();
        }
        return api;
    }

    public void play(String url, ObjListener listener) {
        if (!TextUtils.equals(url, currentUrl)) {
            //播放另一首，清空上一个 player
            if (player != null) {
                player.pause();
                player.release();
                player = null;
            }
        }

        if (player == null) {
            //初始化播放
            player = new MediaPlayer();
            try {
                currentUrl = url;
                player.setDataSource(url);
                player.prepareAsync();
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        // 播放完成
                        listener.onResult(1);
                        player.release();
                        player = null;
                    }
                });
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        //播放成功
                        listener.onResult(0);
                        player.start();
                    }
                });
            } catch (IOException e) {
                // 播放失败==播放完成
                listener.onResult(1);
            }
        } else {
            if (player.isPlaying()) {
                player.pause();
            } else {
                player.start();
            }
        }
    }
}
