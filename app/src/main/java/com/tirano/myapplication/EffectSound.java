package com.tirano.myapplication;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/* 효과음 담당 */
public class EffectSound {

    Context context;
    private int clickSound;
    private int rightAnswerSound;
    private int clearSound;
    private SoundPool soundPool;
    private boolean ON;

    public EffectSound(Context context) {
        this.context = context;
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0 );
        on();
    }

    public void playRightAnswerSound() {
        soundPool.play(rightAnswerSound,1,1,1,0,1);
    }
    public void playClearSound(){
        soundPool.play(clearSound,1,1,1,0,1);
    }
    public void playClickSound(){
        soundPool.play(clickSound,1,1,1,0,1);
    }

    public void on(){
        clickSound = soundPool.load(context, R.raw.effect_click, 1);
        rightAnswerSound =  soundPool.load(context, R.raw.effect_right, 1);
        clearSound = soundPool.load(context, R.raw.effect_clear, 1);
        ON = true;
    }
    public void off(){
        clickSound = 0;
        rightAnswerSound = 0;
        clearSound = 0;
        ON = false;
    }

    public boolean getON(){ return ON; }
}
