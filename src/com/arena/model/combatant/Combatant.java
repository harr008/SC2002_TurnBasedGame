package com.arena.model.combatant;

import com.arena.model.effect.DefendEffect;
import com.arena.model.effect.StatusEffect;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Combatant {

    protected final String name;
    protected int hp;
    protected final int maxHp;
    protected int baseAttack;
    protected final int baseDefense;
    protected final int speed;
    protected final List<StatusEffect> activeEffects;

    protected Combatant(String name, int hp, int attack, int defense, int speed) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.baseAttack = attack;
        this.baseDefense = defense;
        this.speed = speed;
        this.activeEffects = new ArrayList<>();
    }

    // Returns base attack plus all active effect modifiers
    public int getEffectiveAttack() {
        return baseAttack + activeEffects.stream().mapToInt(StatusEffect::getAttackModifier).sum();
    }

    // Returns base defense plus all active effect modifiers
    public int getEffectiveDefense() {
        return baseDefense + activeEffects.stream().mapToInt(StatusEffect::getDefenseModifier).sum();
    }

    // Reduces HP by the given amount. If any active effect marks this combatant
    // as invulnerable (e.g. SmokeBombEffect), damage is set to 0
    // HP cannot go below 0

    public void takeDamage(int damage) {
        if (isInvulnerable())
            return;
        this.hp = Math.max(0, this.hp - damage);
    }

    public void heal(int amount) {
        this.hp = Math.min(maxHp, this.hp + amount);
    }

    public boolean isAlive() {
        return hp > 0;
    }

    // Status effect management

    public boolean isStunned() {
        return activeEffects.stream().anyMatch(StatusEffect::isStun);
    }

    public boolean isInvulnerable() {
        return activeEffects.stream().anyMatch(StatusEffect::isInvulnerable);
    }

    // Applies a StatusEffect
    // If the same non-stackable type is already present:
    // - DefendEffect: reset its duration (no extra +10)
    // - Others: replace with new instance

    public void applyEffect(StatusEffect effect) {
        Optional<StatusEffect> existing = activeEffects.stream().filter(e -> e.getClass()
                .equals(effect.getClass()) && !e.isStackable()).findFirst();

        if (existing.isPresent()) {
            if (existing.get() instanceof DefendEffect de) {
                de.resetDuration(); // no stacking — just reset timer
            } else {
                removeEffect(existing.get());
                effect.onApply(this);
                activeEffects.add(effect);
            }
            return;
        }

        effect.onApply(this);
        activeEffects.add(effect);
    }

    public void removeEffect(StatusEffect effect) {
        effect.onRemove(this);
        activeEffects.remove(effect);
    }

    // Ticks (decrements) the stun effect on this combatant
    // Called by BattleEngine when this combatant's turn slot is skipped

    public void tickStunEffect() {
        List<StatusEffect> expired = new ArrayList<>();
        for (StatusEffect e : activeEffects) {
            if (e.isStun()) {
                e.tick();
                if (e.isExpired())
                    expired.add(e);
            }
        }
        expired.forEach(this::removeEffect);
    }

    // Ticks all non-stun effects (Defend, SmokeBomb)
    // Called by BattleEngine at the end of each round

    public void tickRoundEffects() {
        List<StatusEffect> expired = new ArrayList<>();
        for (StatusEffect e : activeEffects) {
            if (!e.isStun()) {
                e.tick();
                if (e.isExpired())
                    expired.add(e);
            }
        }
        expired.forEach(this::removeEffect);
    }

    // Accessors

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getBaseAttack() {
        return baseAttack;
    }

    public int getBaseDefense() {
        return baseDefense;
    }

    public int getSpeed() {
        return speed;
    }

    public List<StatusEffect> getActiveEffects() {
        return activeEffects;
    }

    // Returns a formatted HP string e.g."185/260"
    public String getHpDisplay() {
        return hp + "/" + maxHp;
    }

    // Returns a short status tag for end-of-round display
    public String getStatusTag() {
        if (!isAlive())
            return "[x ELIMINATED]";
        StringBuilder sb = new StringBuilder();
        for (StatusEffect e : activeEffects) {
            sb.append(" [").append(e.getName()).append("]");
        }
        return sb.toString();
    }
}
