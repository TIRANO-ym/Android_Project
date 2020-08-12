package com.tirano.myapplication;

import android.content.Context;
import android.media.MediaPlayer;

/* 배경음악 담당 */
public class MusicSound {

    MediaPlayer player;
    Context context;
    int music_volume;

    public MusicSound(Context context){
        this.context = context;
        music_volume = 1;
    }

    public void setMusic(int id){
        player = MediaPlayer.create(context, id);
        player.setVolume(music_volume, music_volume);
        player.setLooping(true);
    }

    public void start(){
        player.start();
    }
    public void stop(){
        player.stop();
    }

    public void on(){
        music_volume = 1;
        player.setVolume(music_volume, music_volume);
    }
    public void off(){
        music_volume = 0;
        player.setVolume(music_volume, music_volume);
    }
    public boolean getON(){
        if(music_volume == 1) return true;
        else return false;
    }
}
