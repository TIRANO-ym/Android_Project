package com.tirano.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btn_single, btn_multi, btn_option;

    Dialog dialog;
    static MusicSound musicSound;
    static EffectSound effectSound;

    /* 뒤로가기 버튼을 2초안에 2번 누르면 종료 */
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        btn_single = (Button)findViewById(R.id.home_single_mode);
        btn_multi = (Button)findViewById(R.id.home_multi_mode);
        btn_option = (Button)findViewById(R.id.home_option);
        effectSound = new EffectSound(MainActivity.this);
        musicSound = new MusicSound(MainActivity.this);

        musicSound.setMusic(R.raw.music_home);
        musicSound.start();     // 배경음악

        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.option);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        /* 싱글모드 액티비티로 이동 */
        btn_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicSound.stop();
                effectSound.playClickSound();
                Intent intent = new Intent(MainActivity.this, SingleActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        btn_multi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                effectSound.playClickSound();
                Toast.makeText(MainActivity.this, "멀티모드는 아직 개발 전입니다", Toast.LENGTH_SHORT).show();
            }
        });

        /* 옵션 dialog */
        btn_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                effectSound.playClickSound();

                final Button option_level = (Button)dialog.findViewById(R.id.option_level);     // 옵션_난이도 변경
                final Button option_regame = (Button)dialog.findViewById(R.id.option_regame);   // 옵션_새 게임
                final Button option_home = (Button)dialog.findViewById(R.id.option_home);       // 옵션_홈으로
                final Button option_X = (Button)dialog.findViewById(R.id.option_x);             // 닫기 버튼
                final Switch option_music = (Switch)dialog.findViewById(R.id.switch_music);     // 배경음 on/off
                final Switch option_effect = (Switch)dialog.findViewById(R.id.switch_effect);   // 효과음 on/off

                option_level.setTextColor(Color.parseColor("#A4A4A4"));
                option_regame.setTextColor(Color.parseColor("#A4A4A4"));
                option_home.setTextColor(Color.parseColor("#A4A4A4"));
                option_level.setClickable(false);
                option_regame.setClickable(false);
                option_home.setClickable(false);

                /* 현재 배경음, 효과음 상태 반영 */
                if(musicSound.getON()) option_music.setChecked(true);
                else option_music.setChecked(false);
                if(effectSound.getON()) option_effect.setChecked(true);
                else option_effect.setChecked(false);

                option_music.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        effectSound.playClickSound();
                        if(isChecked) musicSound.on();
                        else musicSound.off();
                    }
                });
                option_effect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            effectSound.on();
                            effectSound.playClickSound();
                        }
                        else
                            effectSound.off();
                    }
                });

                option_X.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        effectSound.playClickSound();
                        dialog.dismiss();
                        option_level.setClickable(true);
                        option_regame.setClickable(true);
                        option_home.setClickable(true);
                    }
                });

                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        option_X.callOnClick();    // 화면 바깥을 눌러서 취소했을 경우 X버튼 누른것과 같은 효과
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        musicSound.setMusic(R.raw.music_home);
        musicSound.start();
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime){
            musicSound.stop();
            super.onBackPressed();
        } else{
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "뒤로가기를 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }
}

