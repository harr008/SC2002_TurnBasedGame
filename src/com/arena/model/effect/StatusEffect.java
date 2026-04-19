package com.arena.model.effect;

import com.arena.model.combatant.Combatant;

// Represents a persistent status effect on a combatant
// Effects that don't modify stats simply return 0

public interface StatusEffect {
    // Human-readable effect name for display
    String getName();

    // Additional attack damage modifier while effect is active
    int getAttackModifier();

    // Additional defense modifier while effect is active
    int getDefenseModifier();

    // True if this effect prevents the combatant from taking actions
    boolean isStun();

    // True if this effect causes the combatant to take 0 incoming damage
    // Used by SmokeBombEffect.

    boolean isInvulnerable();

    // True when the effect's duration has fully elapsed and it should be removed
    boolean isExpired();

    // Whether this effect type can stack if applied again while active
    // If false, re-applying resets the duration instead of adding a new instance
    boolean isStackable();

    // Decrements the effect's internal duration counter by one unit
    // For stun: called when the combatant's turn is skipped
    // For round-based effects: called at end of each round
    void tick();

    // Called immediately when the effect is applied to a combatant
    void onApply(Combatant target);

    // Called immediately when the effect expires or is forcibly removed
    void onRemove(Combatant target);
}
