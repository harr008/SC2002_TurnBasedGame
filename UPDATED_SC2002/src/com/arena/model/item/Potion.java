package com.arena.model.item;

import com.arena.model.action.ActionContext;
import com.arena.model.combatant.Combatant;
import com.arena.observer.BattleEvent;
import com.arena.observer.BattleEventType;

// Potion item - heals the user for 100 HP, capped at max HP

//  New HP = min(currentHP + 100, maxHP)

public class Potion implements Item {

    private static final int HEAL_AMOUNT = 100;

    @Override
    public String getName() {
        return "Potion";
    }

    @Override
    public String getDescription() {
        return "Restore 100 HP (capped at max HP).";
    }

    @Override
    public void use(ActionContext context) {
        Combatant actor = context.getActor();
        int before = actor.getHp();
        actor.heal(HEAL_AMOUNT);
        int after = actor.getHp();

        context.getBattleContext().notifyObservers(new BattleEvent(
                BattleEventType.ITEM_USED,
                actor.getName() + " drinks Potion: HP " + before + " -> " + after
                        + " (+" + (after - before) + ")"));
    }
}
