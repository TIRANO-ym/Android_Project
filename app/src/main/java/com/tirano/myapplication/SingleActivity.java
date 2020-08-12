package com.tirano.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import java.util.HashMap;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

import static com.tirano.myapplication.MainActivity.effectSound;
import static com.tirano.myapplication.MainActivity.musicSound;

public class SingleActivity extends AppCompatActivity {

    Button img1, img2, img3, img4, img5, img6, img7, img8, img9, img10, img11, img12, img13, img14, img15, img16, img17, img18, img19, img20;
    Button btn_start, btn_pause, btn_option, btn_levelOK;
    Chronometer chrono;
    SeekBar seekBar;
    int select1, select2;       // 첫번째 선택카드, 두번째 선택카드
    int count = 0;
    int[] cards;
    int[] srcs;
    HashMap<Integer, Integer> map;

    Handler timer;
    Dialog dialog;

    int LEVEL;                  // 난이도. (사용되는 카드 개수)
    private int _EASY, _NORMAL, _HARD;
    boolean PAUSED;
    long time;                  // 진행시간

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _EASY = 6; _NORMAL = 12; _HARD = 20;
        select1 = 0; select2 = 0;
        PAUSED = false;

        musicSound.setMusic(R.raw.music_playing);
        chrono = (Chronometer) findViewById(R.id.chrono);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        img1 = (Button)findViewById(R.id.img1); img1.setOnClickListener(btnClick);
        img2 = (Button)findViewById(R.id.img2); img2.setOnClickListener(btnClick);
        img3 = (Button)findViewById(R.id.img3); img3.setOnClickListener(btnClick);
        img4 = (Button)findViewById(R.id.img4); img4.setOnClickListener(btnClick);
        img5 = (Button)findViewById(R.id.img5); img5.setOnClickListener(btnClick);
        img6 = (Button)findViewById(R.id.img6); img6.setOnClickListener(btnClick);
        img7 = (Button)findViewById(R.id.img7); img7.setOnClickListener(btnClick);
        img8 = (Button)findViewById(R.id.img8); img8.setOnClickListener(btnClick);
        img9 = (Button)findViewById(R.id.img9); img9.setOnClickListener(btnClick);
        img10 = (Button)findViewById(R.id.img10); img10.setOnClickListener(btnClick);
        img11 = (Button)findViewById(R.id.img11); img11.setOnClickListener(btnClick);
        img12 = (Button)findViewById(R.id.img12); img12.setOnClickListener(btnClick);
        img13 = (Button)findViewById(R.id.img13); img13.setOnClickListener(btnClick);
        img14 = (Button)findViewById(R.id.img14); img14.setOnClickListener(btnClick);
        img15 = (Button)findViewById(R.id.img15); img15.setOnClickListener(btnClick);
        img16 = (Button)findViewById(R.id.img16); img16.setOnClickListener(btnClick);
        img17 = (Button)findViewById(R.id.img17); img17.setOnClickListener(btnClick);
        img18 = (Button)findViewById(R.id.img18); img18.setOnClickListener(btnClick);
        img19 = (Button)findViewById(R.id.img19); img19.setOnClickListener(btnClick);
        img20 = (Button)findViewById(R.id.img20); img20.setOnClickListener(btnClick);

        timer = new Handler();
        cards = new int[]{img1.getId(), img2.getId(), img3.getId(), img4.getId(), img5.getId(), img6.getId(), img7.getId(), img8.getId(), img9.getId(), img10.getId(), img11.getId(), img12.getId(), img13.getId(), img14.getId(), img15.getId(), img16.getId(), img17.getId(), img18.getId(), img19.getId(), img20.getId()};
        srcs = new int[]{R.drawable.pic1, R.drawable.pic2, R.drawable.pic3, R.drawable.pic4, R.drawable.pic5, R.drawable.pic6, R.drawable.pic7, R.drawable.pic8, R.drawable.pic9, R.drawable.pic10};

        /* 게임 시작 */
        btn_start = (Button)findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                effectSound.playClickSound();
                resetGameState();   // 게임판 정리
                setImage();         // 이미지 랜덤 배치

                /* 전체 이미지를 2초간 보여준 후 */
                showAllImages();
                timer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideAllImages();    // 모든 이미지 숨기기
                        buttonsUnLock();    // 버튼잠금 해제

                        btn_pause.setClickable(true);
                        chrono.setBase(SystemClock.elapsedRealtime());
                        chrono.start();     // 크로노미터 시작
                    }
                }, 2000);

                btn_start.setVisibility(View.GONE);
                btn_pause.setVisibility(View.VISIBLE);  // 일시정지 버튼 생성
            }
        });

        /* 게임 일시정지 */
        btn_pause = (Button)findViewById(R.id.btn_pause);
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PAUSED) resume();
                else pause();
            }
        });

        /* 옵션 */
        btn_option = (Button)findViewById(R.id.option);
        btn_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                effectSound.playClickSound();
                showOptionDialog(); // 옵션 dialog 띄우기
            }
        });

        showSelectLevelDialog();    // 사용자에게 난이도 선택 팝업창을 보여줌
        buttonsLock();              // 시작 버튼을 누를 때까진 모든 버튼(카드) 클릭 잠금
        musicSound.start();
    }

    /* 버튼(카드) 클릭 리스너 */
    View.OnClickListener btnClick = (new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Button card = (Button)v;

            cardFlip(card);                 // 카드 뒤집기 애니메이션
            card.setClickable(false);

            if(select1 == 0){
                select1 = v.getId();
            }
            else if (select2 == 0){             // 2번째 선택이면
                select2 = v.getId();
                int src1 = map.get(select1);    // 두 이미지 id 가져옴
                int src2 = map.get(select2);

                /* 두 이미지가 다르면 */
                if(src1 != src2){
                    buttonsLock();              // 이미지를 1초간 보여줄 동안은 모든 버튼(카드) 클릭 잠금

                    timer.postDelayed(new Runnable() {      // 이미지를 1초간 보여준 후에
                        @Override
                        public void run() {
                            ((Button)findViewById(select1)).setBackgroundResource(R.drawable.pic_basic);   // 이미지 초기화
                            ((Button)findViewById(select2)).setBackgroundResource(R.drawable.pic_basic);

                            select1 = 0; select2 = 0;   // 선택 초기화
                            buttonsUnLock();            // 버튼(카드) 클릭 잠금해제
                        }
                    }, 1000);
                }
                /* 두 이미지가 같으면 */
                else{
                    effectSound.playRightAnswerSound();              // 정답 효과음 출력
                    card.setClickable(false);
                    ((Button)findViewById(select1)).setClickable(false);
                    map.remove(select1); map.remove(select2);       // map에서 삭제
                    seekBar.setProgress(seekBar.getProgress() + 2, true); // SeekBar 한칸 증가
                    select1 = 0; select2 = 0;
                    count++;

                    if(count == (LEVEL/2)){                         // 모든 그림 쌍을 맞추면
                        effectSound.playClearSound();               // clear 효과음
                        chrono.stop();                              // 타이머 멈추고
                        chrono.setTextColor(Color.BLUE);
                        btn_pause.setVisibility(View.INVISIBLE);
                        timer.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                btn_start.setText("RESTART?");
                                btn_start.setVisibility(View.VISIBLE);      // 재시작 버튼 활성화
                            }
                        }, 1500);
                    }
                }
            }
        }
    });

    /* 카드 뒤집기 애니메이션 */
    public void cardFlip(final Button card){
        final int src = map.get(card.getId());   // 카드의 이미지 소스

        final ObjectAnimator oa1 = ObjectAnimator.ofFloat(card, "scaleX", 1f, 0f);
        final ObjectAnimator oa2 = ObjectAnimator.ofFloat(card, "scaleX", 0f, 1f);
        oa1.setInterpolator(new DecelerateInterpolator());
        oa2.setInterpolator(new AccelerateDecelerateInterpolator());

        oa1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                card.setBackgroundResource(src); // 뒤집기가 끝나면 이미지 교체
                oa2.start();
            }
        });
        oa1.setDuration(125);                   // 애니메이션이 125밀리초 안에 이루어짐
        oa2.setDuration(125);
        oa1.start();
    }

    /* 난이도 설정 */
    public void setLevel(){

        /* 난이도에 맞추어 버튼(카드)개수 조절 */
        Button card;
        for(int i = 0; i < LEVEL; i++){
            card = (Button)findViewById(cards[i]);
            card.setBackgroundResource(R.drawable.pic_basic);
            card.setVisibility(View.VISIBLE);
        }
        for(int i = LEVEL; i < cards.length; i++){
            card = (Button)findViewById(cards[i]);
            card.setVisibility(View.GONE);
        }
    }

    /* 난이도 선택 Dialog */
    public void showSelectLevelDialog(){
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.select_level);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));    // 배경 투명

        final Button easy = (Button)dialog.findViewById(R.id.easy);
        final Button normal = (Button)dialog.findViewById(R.id.normal);
        final Button hard = (Button)dialog.findViewById(R.id.hard);

        View.OnClickListener levelClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                effectSound.playClickSound();
                switch (v.getId()){
                    case R.id.easy : LEVEL = _EASY; break;      // 쉬움
                    case R.id.normal : LEVEL = _NORMAL; break;  // 보통
                    case R.id.hard : LEVEL = _HARD; break;      // 어려움
                }
                setLevel();
            }
        };

        easy.setOnClickListener(levelClick);
        normal.setOnClickListener(levelClick);
        hard.setOnClickListener(levelClick);

        dialog.setCancelable(false);
        dialog.show();

        btn_levelOK = (Button)dialog.findViewById(R.id.ok);     // 확인버튼
        btn_levelOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { effectSound.playClickSound(); dialog.dismiss();
            }
        });
    }

    /* 옵션 Dialog */
    public void showOptionDialog(){
        if(isPlaying())
            // 게임이 진행 중이었다면 잠시 일시정지
            // (싱글모드 기준. 멀티대결모드에서는 일시정지 버튼이 INVISIBLE 이므로 일시정지 불가)
            pause();

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.option);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));    // 배경투명

        final Button option_level = (Button)dialog.findViewById(R.id.option_level);     // 옵션_난이도 변경
        final Button option_regame = (Button)dialog.findViewById(R.id.option_regame);   // 옵션_새 게임
        final Button option_home = (Button)dialog.findViewById(R.id.option_home);       // 옵션_홈으로
        final Button option_X = (Button)dialog.findViewById(R.id.option_x);             // 닫기 버튼
        final Switch option_music = (Switch)dialog.findViewById(R.id.switch_music);     // 배경음 on/off
        final Switch option_effect = (Switch)dialog.findViewById(R.id.switch_effect);   // 효과음 on/off

        if(musicSound.getON()) option_music.setChecked(true);
        else option_music.setChecked(false);

        if(effectSound.getON()) option_effect.setChecked(true);
        else option_effect.setChecked(false);

        // 배경음(music), 효과음(effect) 조절
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

        // 옵션 - [난이도 변경] 클릭
        option_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                effectSound.playClickSound();
                // *** 게임이 진행중일 경우 현재 게임이 종료된다는 알림을 띄운다
                if(isPlaying()){
                    final Dialog notice = new Dialog(SingleActivity.this);
                    notice.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    notice.setContentView(R.layout.option_notice);

                    final Button notice_ok = (Button)notice.findViewById(R.id.notice_ok);
                    final Button notice_cancel = (Button)notice.findViewById(R.id.notice_cancel);

                    // 확인버튼. 진행중인 게임을 중단하고 난이도 변경창 띄움.
                    notice_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            effectSound.playClickSound();
                            notice.dismiss();
                            dialog.dismiss();

                            resetGameState();
                            hideAllImages();
                            showSelectLevelDialog();
                        }
                    });
                    // 취소버튼
                    notice_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            notice.dismiss();
                        }
                    });
                    notice.show();
                }
                // *** 게임중이 아니면 바로 난이도 변경창 띄움
                else{
                    dialog.dismiss();
                    showSelectLevelDialog();
                }
            }
        });

        // 옵션 - [새 게임] 클릭
        option_regame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                effectSound.playClickSound();
                dialog.dismiss();
                resetGameState();
                btn_start.callOnClick();
            }
        });

        // 옵션 - [홈으로] 클릭
        option_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                effectSound.playClickSound();
                // *** 게임이 진행중일 경우 현재 게임이 종료된다는 알림을 띄운다
                if(isPlaying()){
                    final Dialog notice = new Dialog(SingleActivity.this);
                    notice.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    notice.setContentView(R.layout.option_notice);

                    final Button notice_ok = (Button)notice.findViewById(R.id.notice_ok);
                    final Button notice_cancel = (Button)notice.findViewById(R.id.notice_cancel);

                    // 확인버튼. 진행중인 게임을 중단하고 홈으로 이동.
                    notice_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            effectSound.playClickSound();
                            notice.dismiss(); dialog.dismiss();
                            musicSound.stop(); finish();
                        }
                    });
                    // 취소버튼
                    notice_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            notice.dismiss();
                        }
                    });
                    notice.show();
                }
                // *** 게임중이 아니면 바로 홈으로 이동
                else{
                    musicSound.stop(); finish();
                }
            }
        });

        // 옵션 - [X] (닫기버튼)
        option_X.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                effectSound.playClickSound();
                dialog.dismiss();
                if(PAUSED)
                    resume();
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

    /* 이미지 랜덤 배치 */
    public void setImage(){
        Random random = new Random();
        map = new HashMap<>();
        int src_index;
        int card_index;

        /* 총 (사용되는 카드 개수 / 2)장의 사진 랜덤 배치 */
        for(int i = 0; i < (LEVEL/2); i++){

            /* 아직 배치가 안된 이미지 인덱스(src_index)를 가져옴 */
            src_index = random.nextInt(10);
            while(map.containsValue(srcs[src_index]) == true){
                src_index = random.nextInt(10);
                Log.d("sym", "src while......");
            }

            /* 이미지(src)을 두 쌍의 카드에 배치 */
            for(int j = 0; j < 2; j++){
                /* 아직 이미지 배치가 안된 카드 인덱스를 가져옴 */
                card_index = random.nextInt(LEVEL);
                while(map.containsKey(cards[card_index]) == true){
                    card_index = random.nextInt(LEVEL);
                    Log.d("sym", "img while......");
                }
                map.put(cards[card_index], srcs[src_index]);
            }
        }
        count = 0;
    }

    /* 모든 버튼(카드) 클릭 잠금 */
    public void buttonsLock(){
        for(int i = 0; i < cards.length; i++){
            ((Button)findViewById(cards[i])).setClickable(false);
        }
    }
    /* 모든 버튼(카드) 클릭 잠금해제 */
    public void buttonsUnLock(){
        for(int i = 0; i < LEVEL; i++){
            // 이미 맞춘 정답은 map에서 삭제했으므로, map에 없는 id는 UnLock 대상에서 제외
            if(map.containsKey(cards[i]))
                ((Button)findViewById(cards[i])).setClickable(true);
        }
    }
    /* 모든 카드의 사진 초기화(뒷면으로) */
    public void hideAllImages(){
        Button card;
        for(int i = 0; i < LEVEL; i++){
            card = findViewById(cards[i]);
            card.setBackgroundResource(R.drawable.pic_basic);
        }
    }
    /* 모든 카드의 이미지를 보여줌 */
    public void showAllImages(){
        Button card;
        for(int i = 0; i < LEVEL; i++){
            card = findViewById(cards[i]);
            card.setBackgroundResource(map.get(cards[i]));
        }
    }
    /* 게임 상태 초기화 */
    public void resetGameState(){
        buttonsLock();
        PAUSED = false;
        count = 0;
        map = new HashMap<>();

        chrono.setBase(SystemClock.elapsedRealtime());
        chrono.setTextColor(Color.BLACK);
        seekBar.setMax(LEVEL);
        seekBar.setProgress(0);

        btn_start.setText("START");
        btn_start.setVisibility(View.VISIBLE);
        btn_pause.setBackgroundResource(R.drawable.pic_pause);
        btn_pause.setVisibility(View.INVISIBLE);
    }

    /* 게임 일시정지 */
    public void pause(){
        buttonsLock();
        time = chrono.getBase() - SystemClock.elapsedRealtime();    // 멈춘 시간 기억
        PAUSED = true;
        btn_pause.setBackgroundResource(R.drawable.pic_unpause);

        chrono.stop();
        chrono.setTextColor(Color.RED);
    }
    /* 일시정지 후 재시작 */
    public void resume(){
        buttonsUnLock();    // 버튼 잠금 해제
        PAUSED = false;
        btn_pause.setBackgroundResource(R.drawable.pic_pause);

        chrono.setBase(SystemClock.elapsedRealtime() + time);       // 멈춘 시간부터 재시작
        chrono.start();
        chrono.setTextColor(Color.BLACK);
    }

    /* 현재 게임 진행중인지 */
    public boolean isPlaying(){
        /* pause 버튼은 게임중일 때만 사용 가능하므로
           이 버튼이 VISIBLE 이라면 게임중인 것(싱글모드 기준) */
        if(btn_pause.getVisibility() == View.VISIBLE)
            return true;
        else
            return false;
    }

    @Override
    public void onBackPressed() {
        musicSound.stop();
        finish();
    }
}

