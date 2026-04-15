package com.arena;

import com.arena.engine.GameSession;
import com.arena.ui.CLIGameUI;


public class Main {

    public static void main(String[] args) {
        CLIGameUI ui = new CLIGameUI();
        GameSession session = new GameSession(ui);
        session.start();
    }
}
