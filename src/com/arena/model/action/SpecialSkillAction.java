package com.arena.model.action;

import com.arena.model.combatant.Player;
import com.arena.observer.BattleEvent;
import com.arena.observer.BattleEventType;

//  SpecialSkillAction delegates to the player's SpecialSkill and records
//  the cooldown. It is the standard route for skill use (not via Power Stone)

//  Power Stone bypasses this action and calls skill.executeSkill() directly,
//  leaving the cooldown timer untouched

public class SpecialSkillAction implements Action {

    @Override
    public String getName() {
        return "Special Skill";
    }

    @Override
    public void execute(ActionContext context) {
        if (!(context.getActor() instanceof Player player))
            return;

        int currentRound = context.getBattleContext().getCurrentRound();

        context.getBattleContext().notifyObservers(new BattleEvent(
                BattleEventType.SKILL_USED,
                player.getName() + " uses " + player.getSpecialSkill().getName() + "!"));

        player.getSpecialSkill().executeSkill(context);

        // Record cooldown only for the real skill use (not Power Stone)
        player.recordSkillUsed(currentRound);
    }

    @Override
    public boolean needsEnemyTarget() {
        // Resolved at UI layer based on skill.needsEnemyTarget()
        return false;
    }

    @Override
    public boolean needsSelf() {
        return false;
    }
}
