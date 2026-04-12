package com.arena.model.item;

import com.arena.model.action.ActionContext;
import com.arena.model.combatant.Combatant;
import com.arena.model.combatant.Player;
import com.arena.observer.BattleEvent;
import com.arena.observer.BattleEventType;


// Power Stone item - triggers the player's special skill once for free

// Critical rule: Does NOT start or modify the special skill cooldown timer
// The effect is identical to using the skill directly (including Arcane Blast ATK bonus per kill)
// But recordSkillUsed() is never called

// If the skill requires a target, the target must already be in context.targets
// CLIGameUI resolves target selection before constructing ItemAction(powerStone)

public class PowerStone implements Item {

    @Override
    public String getName() {
        return "Power Stone";
    }

    @Override
    public String getDescription() {
        return "Trigger your special skill once without starting or changing cooldown.";
    }

    @Override
    public void use(ActionContext context) {
        Combatant actor = context.getActor();

        if (!(actor instanceof Player player))
            return;

        context.getBattleContext().notifyObservers(new BattleEvent(
                BattleEventType.ITEM_USED,
                actor.getName() + "'s Power Stone activates "
                        + player.getSpecialSkill().getName() + " (cooldown unaffected)!"));

        // Execute skill directly - bypasses SpecialSkillAction so cooldown is untouched
        player.getSpecialSkill().executeSkill(context);
    }
}
