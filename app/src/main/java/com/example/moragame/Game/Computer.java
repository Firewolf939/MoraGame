package com.example.moragame.Game;

import com.example.moragame.GameSate;
import com.example.moragame.onActionListener;

import java.util.Random;

public class Computer extends Player{
    private onActionListener listener;

    public Computer(onActionListener listener){
        this.listener = listener;
    }

    public void AI(){

        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setMora(getRandomMora());

        listener.onAction(GameSate.PLAYER_ROUND);
    }

    public static Mora getRandomMora(){
        int index = new Random().nextInt(Mora.PAPER.ordinal()+1);
        if (index == 0){
            return Mora.SCISSOR;
        }else if (index == 1){
            return Mora.ROCK;
        }else{
            return Mora.PAPER;
        }

    }
}
