package com.arena.strategy;

import com.arena.engine.BattleContext;
import com.arena.model.action.Action;
import com.arena.model.combatant.Enemy;

// Selects the action the enemy will perform on its turn

public interface EnemyActionStrategy {

    // @param enemy The enemy whose turn it is
    // @param context Read-only view of the current battle state
    // @return The Action to execute (target is assigned by BattleEngine)

    Action selectAction(Enemy enemy, BattleContext context);
}
