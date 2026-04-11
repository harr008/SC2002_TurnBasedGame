package com.arena.model.skill;

import com.arena.engine.BattleContext;
import com.arena.model.action.ActionContext;
import com.arena.model.combatant.Combatant;
import com.arena.model.combatant.Wizard;
import com.arena.observer.BattleEvent;
import com.arena.observer.BattleEventType;
import java.util.List;

// Wizard's Arcane Blast special skill

// Effect:
//   1. Deals BasicAttack damage to ALL living enemies
//   2. For each enemy defeated by this blast, adds +10 to the Wizard's attack (cumulative, lasting until end of level)

//  The attack bonus is stored directly on the Wizard via addArcaneBlastBonus()
//  and included in Wizard.getEffectiveAttack(), so it applies to all subsequent
//  attacks automatically (including the boosted blast mid-chain)

//  Note on Power Stone interaction:
//  - When triggered by Power Stone, this method is called directly
//  - The ATK boost is awarded per kill exactly the same way - the Wizard's
//  - attack updates between each enemy hit in the loop

public class ArcaneBlast implements SpecialSkill {

    private final Wizard wizard;

    public ArcaneBlast(Wizard wizard) {
        this.wizard = wizard;
    }

    @Override
    public String getName() {
        return "Arcane Blast";
    }

    @Override
    public String getDescription() {
        return "Deal Basic Attack damage to ALL enemies. "
                + "Each enemy defeated grants +10 ATK until end of level.";
    }

    @Override
    public void executeSkill(ActionContext context) {
        BattleContext bc = context.getBattleContext();
        List<Combatant> enemies = bc.getLivingEnemies();

        bc.notifyObservers(new BattleEvent(BattleEventType.SKILL_USED,
                wizard.getName() + " unleashes Arcane Blast on all enemies!"));

        for (Combatant enemy : enemies) {
            boolean invuln = enemy.isInvulnerable();
            int damage;
            if (invuln) {
                damage = 0;
            } else {
                damage = Math.max(0, wizard.getEffectiveAttack() - enemy.getEffectiveDefense());
            }
            enemy.takeDamage(damage);

            bc.notifyObservers(new BattleEvent(BattleEventType.ATTACK,
                    "  Arcane Blast hits " + enemy.getName()
                            + " -> " + damage + " dmg ("
                            + wizard.getEffectiveAttack() + "-" + enemy.getEffectiveDefense() + ")"
                            + " | HP: " + (enemy.getHp() + damage) + " -> " + enemy.getHp()));

            if (!enemy.isAlive()) {
                bc.notifyObservers(new BattleEvent(BattleEventType.COMBATANT_DEFEATED,
                        enemy.getName() + " has been ELIMINATED by Arcane Blast!"));
                wizard.addArcaneBlastBonus(10);
                bc.notifyObservers(new BattleEvent(BattleEventType.EFFECT_APPLIED,
                        wizard.getName() + "'s ATK boosted to "
                                + wizard.getEffectiveAttack() + " (+10 per kill)"));
            }
        }
    }

    @Override
    public boolean needsEnemyTarget() {
        return false;
    }
}
