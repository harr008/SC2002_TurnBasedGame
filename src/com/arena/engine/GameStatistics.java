package com.arena.engine;

import com.arena.model.combatant.Enemy;
import com.arena.model.combatant.Player;
import com.arena.model.item.Item;

import java.util.List;

//  Immutable snapshot of battle statistics captured at game end
//  Created by GameSession after BattleEngine.runBattle() returns

public class GameStatistics {

    private final BattleResult result;
    private final int totalRounds;
    private final Player player;
    private final List<Enemy> enemies;

    public GameStatistics(BattleResult result, int totalRounds,
            Player player, List<Enemy> enemies) {
        this.result = result;
        this.totalRounds = totalRounds;
        this.player = player;
        this.enemies = List.copyOf(enemies);
    }

    public BattleResult getResult() {
        return result;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public int getRemainingHp() {
        return player.getHp();
    }

    public long getEnemiesRemaining() {
        int aliveCount = 0;
        for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).isAlive()) {
                aliveCount++;
            }
        }
        return aliveCount;
    }

    // Summary of items still in inventory at game end
    public String itemSummary() {
        List<Item> inv = player.getInventory();
        if (inv.isEmpty()) {
            return "None";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inv.size(); i++) {
            Item item = inv.get(i);
            sb.append(item.getName()).append(" ");
        }

        return sb.toString().trim();
    }
}
