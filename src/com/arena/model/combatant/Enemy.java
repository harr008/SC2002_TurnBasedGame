package com.arena.model.combatant;

import com.arena.strategy.BasicAttackStrategy;
import com.arena.strategy.EnemyActionStrategy;

public abstract class Enemy extends Combatant {

    private EnemyActionStrategy actionStrategy;

    protected Enemy(String name, int hp, int attack, int defense, int speed) {
        super(name, hp, attack, defense, speed);
        this.actionStrategy = new BasicAttackStrategy(); // default
    }

    public EnemyActionStrategy getActionStrategy() {
        return actionStrategy;
    }

    // Allows injecting a different strategy at runtime (e.g. for future AI types)
    public void setActionStrategy(EnemyActionStrategy strategy) {
        this.actionStrategy = strategy;
    }
}
