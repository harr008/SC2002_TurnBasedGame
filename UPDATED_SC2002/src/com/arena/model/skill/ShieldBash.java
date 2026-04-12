package com.arena.model.skill;

import com.arena.engine.BattleContext;
import com.arena.model.action.ActionContext;
import com.arena.model.combatant.Combatant;
import com.arena.model.effect.StunEffect;
import com.arena.observer.BattleEvent;
import com.arena.observer.BattleEventType;

//  Warrior's Shield Bash special skill

//  Effect:
//   1. Deals BasicAttack damage to the selected target
//   2. Applies StunEffect - the target skips its remaining turns this round
//      and its turn in the next round

//  Stun duration passed to StunEffect is determined by BattleContext:
//   - If the target has NOT yet taken its turn this round -> turnsRemaining = 2
//     (skips this round's slot + next round's slot)
//   - If the target HAS already acted this round -> turnsRemaining = 1
//     (only skips next round's slot)

public class ShieldBash implements SpecialSkill {

    @Override
    public String getName() {
        return "Shield Bash";
    }

    @Override
    public String getDescription() {
        return "Deal Basic Attack damage to one enemy. Stuns target for its "
                + "remaining turns this round and its next round turn.";
    }

    @Override
    public void executeSkill(ActionContext context) {
        Combatant actor = context.getActor();
        Combatant target = context.getPrimaryTarget();
        BattleContext bc = context.getBattleContext();

        // Damage
        boolean invuln = target.isInvulnerable();

        int damage;
        if (invuln) {
            damage = 0;
        } else {
            damage = Math.max(0, actor.getEffectiveAttack() - target.getEffectiveDefense());
        }
        target.takeDamage(damage);

        bc.notifyObservers(new BattleEvent(BattleEventType.ATTACK,
                actor.getName() + " Shield Bashes " + target.getName()
                        + " -> " + damage + " dmg ("
                        + actor.getEffectiveAttack() + "-" + target.getEffectiveDefense() + ")"
                        + " | HP: " + (target.getHp() + damage) + " -> " + target.getHp()));

        if (!target.isAlive()) {
            bc.notifyObservers(new BattleEvent(BattleEventType.COMBATANT_DEFEATED,
                    target.getName() + " has been ELIMINATED!"));
            return; // no need to stun a dead enemy
        }

        int turnsRemaining;
        if (bc.hasActedThisRound(target)) {
            turnsRemaining = 1;
        } else {
            turnsRemaining = 2;
        }
        target.applyEffect(new StunEffect(turnsRemaining));

        bc.notifyObservers(new BattleEvent(BattleEventType.EFFECT_APPLIED,
                target.getName() + " is STUNNED for "
                        + turnsRemaining + " turn(s)!"));
    }

    @Override
    public boolean needsEnemyTarget() {
        return true;
    }
}
