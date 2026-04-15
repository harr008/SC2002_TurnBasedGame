package com.arena.model.action;

import com.arena.model.combatant.Combatant;
import com.arena.model.effect.DefendEffect;
import com.arena.observer.BattleEvent;
import com.arena.observer.BattleEventType;

//  Defend action - applies (or resets) a DefendEffect on the actor

//  Grants +10 DEF for the current round and the next
//  Using Defend when already active resets the duration to 2 (no stacking)

public class DefendAction implements Action {

    @Override
    public String getName() {
        return "Defend";
    }

    @Override
    public void execute(ActionContext context) {
        Combatant actor = context.getActor();
        actor.applyEffect(new DefendEffect());

        context.getBattleContext().notifyObservers(new BattleEvent(
                BattleEventType.DEFEND,
                actor.getName() + " takes a defensive stance! (+10 DEF for this round and next)"));
    }

    @Override
    public boolean needsEnemyTarget() {
        return false;
    }

    @Override
    public boolean needsSelf() {
        return true;
    }
}
