package com.arena.model.effect;

import com.arena.model.combatant.Combatant;

// Smoke Bomb effect: the bearer takes 0 incoming damage

// Duration is round-based (tick() at end of each round)
// Lasts for the current round and the next (duration = 2)

// isInvulnerable() returns true -> BasicAttackAction sets damage to 0
// if the target has this effect active

public class SmokeBombEffect implements StatusEffect {

    private int roundsRemaining;

    public SmokeBombEffect() {
        this.roundsRemaining = 2;
    }

    @Override
    public String getName() {
        return "SMOKE BOMB";
    }

    @Override
    public int getAttackModifier() {
        return 0;
    }

    @Override
    public int getDefenseModifier() {
        return 0;
    }

    @Override
    public boolean isStun() {
        return false;
    }

    @Override
    public boolean isInvulnerable() {
        return true;
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
        // no stat mutation
    }

    @Override
    public void onRemove(Combatant t) {
        // no stat mutation
    }

    public int getRoundsRemaining() {
        return roundsRemaining;
    }
}
