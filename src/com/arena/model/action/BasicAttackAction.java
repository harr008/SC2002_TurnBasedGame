package com.arena.model.action;

import com.arena.model.combatant.Combatant;
import com.arena.observer.BattleEvent;
import com.arena.observer.BattleEventType;

//  BasicAttack action

//  Damage = max(0, attacker.effectiveAttack - target.effectiveDefense)
//  If the target has an active invulnerability effect (SmokeBomb), damage is 0
//  HP cannot go below 0 (clamped in Combatant.takeDamage)
public class BasicAttackAction implements Action {

        @Override
        public String getName() {
                return "Basic Attack";
        }

        @Override
        public void execute(ActionContext context) {
                int damage = 0;
                String msg;
                Combatant actor = context.getActor();
                Combatant target = context.getPrimaryTarget();

                boolean invuln = target.isInvulnerable();
                if (!invuln) {
                        damage = Math.max(0, actor.getEffectiveAttack() - target.getEffectiveDefense());
                }

                target.takeDamage(damage);

                if (invuln) {
                        msg = actor.getName() + " attacks " + target.getName() + " -> 0 damage (Smoke Bomb active)";
                } else {
                        msg = actor.getName() + " attacks " + target.getName()
                                        + " -> " + damage + " dmg ("
                                        + actor.getEffectiveAttack() + "-" + target.getEffectiveDefense() + ")"
                                        + " | HP: " + (target.getHp() + damage) + " -> " + target.getHp();
                }

                context.getBattleContext().notifyObservers(
                                new BattleEvent(BattleEventType.ATTACK, msg));

                if (!target.isAlive()) {
                        context.getBattleContext().notifyObservers(
                                        new BattleEvent(BattleEventType.COMBATANT_DEFEATED,
                                                        target.getName() + " has been ELIMINATED!"));
                }
        }

        @Override
        public boolean needsEnemyTarget() {
                return true;
        }

        @Override
        public boolean needsSelf() {
                return false;
        }
}
