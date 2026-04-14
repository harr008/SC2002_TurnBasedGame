package com.arena.model.item;

import com.arena.model.action.ActionContext;
import com.arena.model.combatant.Combatant;
import com.arena.model.effect.SmokeBombEffect;
import com.arena.observer.BattleEvent;
import com.arena.observer.BattleEventType;

//  Smoke Bomb item - applies SmokeBombEffect to the user

//  Enemy attacks deal 0 damage in the current round and the next round
//  Duration is round-based (2 rounds), ticked at end of each round
public class SmokeBomb implements Item {

    @Override
    public String getName() {
        return "Smoke Bomb";
    }

    @Override
    public String getDescription() {
        return "Enemy attacks deal 0 damage this round and next.";
    }

    @Override
    public void use(ActionContext context) {
        Combatant actor = context.getActor();
        actor.applyEffect(new SmokeBombEffect());

        context.getBattleContext().notifyObservers(new BattleEvent(
                BattleEventType.EFFECT_APPLIED,
                actor.getName() + " throws a Smoke Bomb! "
                        + "Invulnerable this round and next."));
    }
}
