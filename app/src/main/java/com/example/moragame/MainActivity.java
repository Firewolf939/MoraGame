package com.example.moragame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moragame.Game.Computer;
import com.example.moragame.Game.Mora;
import com.example.moragame.Game.Player;
import com.example.moragame.Game.WinState;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,onActionListener,
Runnable{

    private ImageButton scissorsIbn;
    private ImageButton rockIbn;
    private ImageButton paperIbn;
    private Button startBtn;
    private Button quiteBtn;

    private ImageView computerImg;
    private Player player;
    private Computer computer;
    private GameSate gameSate;

    private int beginMillisecond = 1000;
    private int levelStep = 5;
    private int gameMillisecond;
    private int targetMillisecond;
    private boolean gameCountDownFinish;

    private TextView countText;
    private TextView hartText;
    private TextView winCountText;
    private TextView bigCounterText;
    private TextView hitCountText;
    private TextView roundText;
    private TextView hitComboText;

    private Handler gameTimer;
    private boolean gaming;
    private int round;
    private int combo;
    private int hitCombo;
    private int countSecond;
    private int topHitCombo;

    private SoundPool soundPool;
    private int[] soundResId;
    private final int SOUND_CORRECT = 0;
    private final int SOUND_WRONG = 1;

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        load();
        findViews();
        initGame();
        initSound();
    }

    public void save(){
        SharedPreferences sharedPreferences = getSharedPreferences("Game",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("combo",topHitCombo);
        editor.commit();
    }

    public void load(){
        SharedPreferences sharedPreferences = getSharedPreferences("Game",
                Context.MODE_PRIVATE);
        hitCombo = topHitCombo = sharedPreferences.getInt("combo",0);
    }

    private void delete(){
        SharedPreferences sharedPreferences = getSharedPreferences("Game",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public void initGame(){
        player = new Player();
        computer = new Computer(this);
        gameSate = GameSate.INIT_GAME;
        countSecond=3;
        round=0;
        combo=0;
        roundText.setText("ROUND 1");
        countText.setText("1:000");
        String winCount=String.format("%02d",player.getWinCount());

        hartText.setText(player.getHart());
        winCountText.setText(String.valueOf(player.getWinCount()));
    }

    public void initSound(){
        soundPool = new SoundPool.Builder().setMaxStreams(10).build();
        soundResId = new int[]{soundPool.load(this,R.raw.correct_ogg,1),
        soundPool.load(this,R.raw.wrong,1)};
    }

    public void playSound(int id){
        soundPool.play(soundResId[id],1,1,1,0,1);
    }

    public void startGame(){

        targetMillisecond = beginMillisecond - (round/levelStep)*50;
        if(targetMillisecond<=200){
            targetMillisecond=200;
        }
        round++;

        this.setTitle(getResources().getString(R.string.app_name)+"=>"+targetMillisecond+
                "ms");

        roundText.setText("ROUND "+ ++round);
        gameMillisecond = 0;
        //targetMillisecond = 1000;
        gameCountDownFinish = false;
        onAction(GameSate.COMPUTER_ROUND);
    }

    public void onAction(GameSate state){
        gameSate = state;
        switch (state){
            case INIT_GAME:
                initGame();
                bigCounterText.setText(String.valueOf(countSecond));
                findViewById(R.id.grid_layout).setVisibility(View.INVISIBLE);
                bigCounterText.setVisibility(View.VISIBLE);
                gameTimer = new Handler(Looper.getMainLooper());
                gameTimer.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        countSecond--;
                        if (countSecond == 0){
                            gameTimer.removeCallbacks(this);
                            bigCounterText.setVisibility(View.INVISIBLE);
                            findViewById(R.id.grid_layout).setVisibility(View.VISIBLE);
                            onAction(GameSate.START_GAME);
                            return;
                        }
                        bigCounterText.setText(String.valueOf(countSecond));
                        gameTimer.postDelayed(this,0);
                    }
                });
                break;
            case START_GAME:
                startGame();
                break;
            case COMPUTER_ROUND:
                computer.AI();
                break;
            case PLAYER_ROUND:
                starGameCountDown();
                break;
            case CHECK_WIN_STATE:
                WinState winState = WinState.getWinState(
                        player.getMora(),computer.getMora());

                if (winState == WinState.COMPUTER_WIN){
                    playSound(SOUND_WRONG);
                    combo=0;
                    player.setLife(player.getLife()-1);
                    hartText.setText(player.getHart());
                    Log.d(TAG,String.valueOf(player.getLife()));
                }else if (winState == WinState.PLAYER_WIN){
                    playSound(SOUND_CORRECT);
                    player.setWinCount(player.getWinCount() + 1);
                    winCountText.setText(String.format("%02d",player.getWinCount()));
                    combo++;
                    hitCountText.setVisibility(View.VISIBLE);
                    hitCountText.setText(combo + getResources().getString(R.string.hit));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(800);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hitCountText.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    }).start();

                    if (combo>hitCombo){
                        hitCombo=combo;
                        hitComboText.setText(getResources().getString(R.string.hit_combo)+"\n"+hitCombo);
                    }
                }else if (winState == WinState.EVEN){
                    combo = 0;
                }

                hitCountText.setText(combo+getResources().getString(R.string.hit));

                if (player.getLife() == 0){
                    gaming=false;
                    onAction(GameSate.GAME_OVER);
                    return;
                }
                Log.d(TAG,winState.toString());
                onAction(GameSate.START_GAME);
                break;
            case GAME_OVER:

                if (hitCombo>topHitCombo){
                    topHitCombo = hitCombo;
                    save();
                }

                StringBuilder sb = new StringBuilder();
                sb.append("SCORE:"+player.getWinCount()).append("\n\n")
                        .append("HIT COMBO:"+hitCombo);
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle(R.string.result)
                        .setMessage(sb.toString())
                        .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();
                break;
        }
    }

    private void showExitDialog(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.message)
                .setMessage(R.string.exit)
                .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.finish();
                        System.exit(0);
                    }
                })
                .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            showExitDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void starGameCountDown(){
        computerImg.setImageResource(Mora.getMoraResId(computer.getMora()));

        if (gameTimer != null){
            gameTimer.removeCallbacks(this);
        }

        gameTimer = new Handler(Looper.getMainLooper());
        gameTimer.post(this);
    }

    public void findViews(){
        scissorsIbn = findViewById(R.id.scissors_ibn);
        rockIbn = findViewById(R.id.rock_ibn);
        paperIbn = findViewById(R.id.paper_ibn);
        startBtn = findViewById(R.id.start_btn);
        quiteBtn = findViewById(R.id.quit_btn);
        computerImg = findViewById(R.id.computer_img);
        countText = findViewById(R.id.counter_text);
        hartText = findViewById(R.id.hart_text);
        winCountText = findViewById(R.id.win_count_text);
        bigCounterText = findViewById(R.id.big_counter_text);
        hitCountText = findViewById(R.id.hit_count_text);
        roundText = findViewById(R.id.round_text);
        hitComboText = findViewById(R.id.hit_combo_text);

        scissorsIbn.setOnClickListener(this);
        rockIbn.setOnClickListener(this);
        paperIbn.setOnClickListener(this);
        startBtn.setOnClickListener(this);
        quiteBtn.setOnClickListener(this);
        computerImg.setOnClickListener(this);
        countText.setOnClickListener(this);
        hartText.setOnClickListener(this);
        winCountText.setOnClickListener(this);

        bigCounterText.setVisibility(View.INVISIBLE);
        hitCountText.setVisibility(View.INVISIBLE);

        hitComboText.setText(getResources().getString(R.string.hit_combo)+"\n"+hitCombo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.scissors_ibn:
                Log.d(TAG,getResources().getString(R.string.scissors));
                if (gameSate == GameSate.PLAYER_ROUND){
                    player.setMora(Mora.SCISSOR);
                    gameCountDownFinish = true;
                    onAction(GameSate.CHECK_WIN_STATE);
                }
                Log.d(TAG,WinState.getWinState(player.getMora(),computer.getMora()).toString());
                break;
            case R.id.rock_ibn:
                Log.d(TAG,getResources().getString(R.string.rock));
                if (gameSate == GameSate.PLAYER_ROUND){
                    player.setMora(Mora.ROCK);
                    gameCountDownFinish = true;
                    onAction(GameSate.CHECK_WIN_STATE);
                }
                break;
            case R.id.paper_ibn:
                Log.d(TAG,getResources().getString(R.string.paper));
                if (gameSate == GameSate.PLAYER_ROUND){
                    player.setMora(Mora.PAPER);
                    gameCountDownFinish = true;
                    onAction(GameSate.CHECK_WIN_STATE);
                }
                break;
            case R.id.start_btn:
                if (!gaming){
                    gaming = true;
                    onAction(GameSate.INIT_GAME);
                    Log.d(TAG,getResources().getString(R.string.start));
                }
                break;
            case R.id.quit_btn:
                Log.d(TAG,getResources().getString(R.string.quit));
                break;
        }
    }

    @Override
    public void run() {
        if (gameCountDownFinish){
            return;
        }
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        gameMillisecond += 10;
        if (gameMillisecond >= targetMillisecond){
            gameMillisecond = targetMillisecond;
            gameCountDownFinish = true;
            player.setMora(Mora.NONE);
            onAction(GameSate.CHECK_WIN_STATE);
        }

        int sec = (targetMillisecond - gameMillisecond)/1000;
        int ms = (targetMillisecond - gameMillisecond)%1000;
        String time = String.format("%d:%03d",sec,ms);
        countText.setText(time);

        gameTimer.postDelayed(this,0);
    }
}