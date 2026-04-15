package com.arena.model.effect;

import com.arena.model.combatant.Combatant;

// Defend effect: grants +10 defense for the current round and the next

// Duration is round-based: tick() is called at the end of each round
// isStackable() returns false - re-applying simply resets the duration to 2
// without adding an additional +10 bonus

public class DefendEffect implements StatusEffect {

    private int roundsRemaining;
    private static final int DEFENSE_BONUS = 10;

    public DefendEffect() {
        this.roundsRemaining = 2;
    }

    @Override
    public String getName() {
        return "DEFEND";
    }

    @Override
    public int getAttackModifier() {
        return 0;
    }

    @Override
    public int getDefenseModifier() {
        return DEFENSE_BONUS;
    }

    @Override
    public boolean isStun() {
        return false;
    }

    @Override
    public boolean isInvulnerable() {
        return false;
    }

    @Override
    public boolean isExpired() {
        return roundsRemaining <= 0;
    }

    @Override
    public boolean isStackable() {
        return false;
    }

    @Override
    public void tick() {
        roundsRemaining--;
    }

    @Override
    public void onApply(Combatant t) {
        // modifier read dynamically
    }

    @Override
    public void onRemove(Combatant t) {
        // modifier removed dynamically
    }

    // Resets the duration to 2 without re-applying the stat modifier
    public void resetDuration() {
        this.roundsRemaining = 2;
    }

    public int getRoundsRemaining() {
        return roundsRemaining;
    }
}
