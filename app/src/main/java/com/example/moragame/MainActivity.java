package com.example.moragame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.moragame.Game.Computer;
import com.example.moragame.Game.Mora;
import com.example.moragame.Game.Player;
import com.example.moragame.Game.WinState;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton scissorsIbn;
    private ImageButton rockIbn;
    private ImageButton paperIbn;
    private Button startBtn;
    private Button quiteBtn;

    private ImageView computerImg;
    private Player player;
    private Computer computer;

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
        computer = new Computer();

        computer.AI();
        computerImg.setImageResource(Mora.getMoraResId(computer.getMora()));
    }

    public void findViews(){
        scissorsIbn = findViewById(R.id.scissors_ibn);
        rockIbn = findViewById(R.id.rock_ibn);
        paperIbn = findViewById(R.id.paper_ibn);
        startBtn = findViewById(R.id.start_btn);
        quiteBtn = findViewById(R.id.quit_btn);

        scissorsIbn.setOnClickListener(this);
        rockIbn.setOnClickListener(this);
        paperIbn.setOnClickListener(this);
        startBtn.setOnClickListener(this);
        quiteBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.scissors_ibn:
                Log.d(TAG,getResources().getString(R.string.scissors));
                player.setMora(Mora.SCISSOR);
                System.out.println(WinState.getWinState(player.getMora(),computer.getMora()));
                Log.d(TAG,WinState.getWinState(player.getMora(),computer.getMora()).toString());
                break;
            case R.id.rock_ibn:
                Log.d(TAG,getResources().getString(R.string.rock));
                break;
            case R.id.paper_ibn:
                Log.d(TAG,getResources().getString(R.string.paper));
                break;
            case R.id.start_btn:
                initGame();
                Log.d(TAG,getResources().getString(R.string.start));
                break;
            case R.id.quit_btn:
                Log.d(TAG,getResources().getString(R.string.quit));
                break;
        }
    }
}