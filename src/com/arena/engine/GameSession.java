package com.arena.engine;

import com.arena.factory.LevelFactory;
import com.arena.model.combatant.Player;
import com.arena.model.item.Item;
import com.arena.model.level.LevelConfig;
import com.arena.observer.BattleLogger;
import com.arena.strategy.SpeedBasedTurnOrder;
import com.arena.ui.CLIGameUI;

import java.util.List;

//  orchestrates one full play-through: setup -> battle -> result

public class GameSession {

    private final CLIGameUI ui;

    public GameSession(CLIGameUI ui) {
        this.ui = ui;
    }

    // Entry point - runs until the player chooses to exit
    public void start() {
        ui.showLoadingScreen();

        boolean running = true;

        // Persistent selections for "replay with same settings"
        Player savedPlayer = null;
        List<Item> savedItems = null;
        Integer savedLevelNumber = null;

        while (running) {
            // Either fresh selections or carry over from replay
            Player player;
            List<Item> items;
            LevelConfig level;

            if (savedPlayer == null) {
                // First run or new game - full selection
                player = ui.selectPlayer();
                items = ui.selectItems();
                level = ui.selectLevel();
            } else {
                // Replay: recreate player with original class/items/level
                // Rebuild using factory so stats are fully reset
                player = rebuildPlayer(savedPlayer);
                items = savedItems;
                level = LevelFactory.createLevel(savedLevelNumber);
                System.out.println("  Replaying with same settings...\n");
            }

            player.setInventory(items);

            // Save for potential replay
            savedPlayer = player;
            savedItems = items;
            savedLevelNumber = level.getLevelNumber();

            // Build engine with injected dependencies
            BattleEngine engine = new BattleEngine(
                    player,
                    level,
                    new SpeedBasedTurnOrder(),
                    ui);
            engine.addObserver(new BattleLogger());

            // Run the battle
            BattleResult result = engine.runBattle();

            // Display results
            GameStatistics stats = new GameStatistics(
                    result, engine.getTotalRounds(),
                    player, engine.getActiveEnemies());

            if (result == BattleResult.PLAYER_VICTORY) {
                ui.showVictoryScreen(stats);
            } else {
                ui.showDefeatScreen(stats);
            }

            // Post-game menu
            int choice = ui.showPostGameMenu();
            switch (choice) {
                case 1 -> {
                    // replay - loop continues with savedPlayer set
                }
                case 2 -> {
                    // New game - clear saved selections
                    savedPlayer = null;
                    savedItems = null;
                    savedLevelNumber = null;
                    ui.showLoadingScreen();
                }
                case 3 -> running = false;
            }
        }

        System.out.println("\n  Thanks for playing Combat Arena! Goodbye.\n");
        ui.closeScanner();
    }

    // Rebuilds a fresh player of the same subclass with reset stats
    // Used for "replay with same settings" to avoid carrying over HP damage

    private Player rebuildPlayer(Player original) {
        return switch (original.getClass().getSimpleName()) {
            case "Warrior" -> new com.arena.model.combatant.Warrior(original.getName());
            case "Wizard" -> new com.arena.model.combatant.Wizard(original.getName());
            default -> throw new IllegalStateException(
                    "Unknown player type: " + original.getClass());
        };
    }
}
