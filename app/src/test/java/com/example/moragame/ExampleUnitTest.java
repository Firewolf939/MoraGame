package com.example.moragame;

import android.view.MotionEvent;

import com.example.moragame.Game.Mora;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void gameState_test(){
        for (Mora mora : Mora.values()){
            System.out.println(mora);
        }
        for (Mora mora : Mora.values()){
            System.out.println(mora.ordinal());
        }
        Mora mora = Mora.PAPER;

        System.out.println(mora.equals(Mora.PAPER));
    }


}