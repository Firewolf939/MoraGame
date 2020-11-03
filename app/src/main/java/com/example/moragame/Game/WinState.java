package com.example.moragame.Game;

public enum WinState {
    PLAYER_WIN,
    COMPUTER_WIN,
    EVEN,
    IDLE;

    public static WinState getWinState(Mora player, Mora computer){
        if (player == computer){
            return WinState.EVEN;
        }
        if (player == Mora.SCISSOR && computer == Mora.PAPER){
            return WinState.PLAYER_WIN;
        }
        if (computer == Mora.SCISSOR && player == Mora.PAPER){
            return WinState.COMPUTER_WIN;
        }
        if (player.ordinal() > computer.ordinal()){
            return WinState.PLAYER_WIN;
        }
        return WinState.COMPUTER_WIN;
    }
}
