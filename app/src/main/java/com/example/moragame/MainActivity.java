package com.example.moragame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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

    private int gameMillisecond;
    private int targetMillisecond;
    private boolean gameCountDownFinish;
    private TextView countText;
    private TextView hartText;

    private Handler gameTimer;
    private boolean gameOver;
    private boolean gaming;

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        initGame();
    }

    public void initGame(){
        player = new Player();
        computer = new Computer(this);
        gameSate = GameSate.INIT_GAME;
        gameOver = false;
    }

    public void startGame(){
        if (gameOver){
            initGame();
        }

        gameMillisecond = 0;
        targetMillisecond = 1000;
        gameCountDownFinish = false;

        onAction(GameSate.COMPUTER_ROUND);
    }

    public void onAction(GameSate state){
        gameSate = state;
        switch (state){
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
                    player.setLife(player.getLife()-1);
                    Log.d(TAG,String.valueOf(player.getLife()));
                    if (player.getLife() == 0){
                        gameOver=true;
                        gaming=false;
                        return;
                    }
                }

                Log.d(TAG,winState.toString());
                onAction(GameSate.START_GAME);
                break;
        }
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
        countText = findViewById(R.id.count_text);
        hartText = findViewById(R.id.hart_text);

        scissorsIbn.setOnClickListener(this);
        rockIbn.setOnClickListener(this);
        paperIbn.setOnClickListener(this);
        startBtn.setOnClickListener(this);
        quiteBtn.setOnClickListener(this);
        computerImg.setOnClickListener(this);
        countText.setOnClickListener(this);
        hartText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.scissors_ibn:
                Log.d(TAG,getResources().getString(R.string.scissors));
                if (gameSate == GameSate.PLAYER_ROUND){
                    player.setMora(Mora.SCISSOR);
                    onAction(GameSate.CHECK_WIN_STATE);
                }
                Log.d(TAG,WinState.getWinState(player.getMora(),computer.getMora()).toString());
                break;
            case R.id.rock_ibn:
                Log.d(TAG,getResources().getString(R.string.rock));
                if (gameSate == GameSate.PLAYER_ROUND){
                    player.setMora(Mora.SCISSOR);
                    onAction(GameSate.CHECK_WIN_STATE);
                }
                break;
            case R.id.paper_ibn:
                Log.d(TAG,getResources().getString(R.string.paper));
                if (gameSate == GameSate.PLAYER_ROUND){
                    player.setMora(Mora.SCISSOR);
                    onAction(GameSate.CHECK_WIN_STATE);
                }
                break;
            case R.id.start_btn:
                if (!gaming){
                    gaming = true;
                    onAction(GameSate.START_GAME);
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