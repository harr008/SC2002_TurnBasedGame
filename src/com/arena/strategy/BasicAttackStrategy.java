package com.arena.strategy;

import com.arena.engine.BattleContext;
import com.arena.model.action.Action;
import com.arena.model.action.BasicAttackAction;
import com.arena.model.combatant.Enemy;

//  Selects BasicAttack

public class BasicAttackStrategy implements EnemyActionStrategy {

    @Override
    public Action selectAction(Enemy enemy, BattleContext context) {
        return new BasicAttackAction();
    }
}
