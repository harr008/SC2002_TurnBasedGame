package com.arena.model.effect;

import com.arena.model.combatant.Combatant;

// Stun effect applied by Shield Bash

// Duration is turn-based: tick() is called each time the stunned combatant's turn slot is processed and skipped

// Duration rules (set by ShieldBash at time of application):
//   - Target has NOT yet acted this round → turnsRemaining = 2
//     (skips remaining slot this round + next round's slot)
//   - Target HAS already acted this round → turnsRemaining = 1
//     (skips only next round's slot)

public class StunEffect implements StatusEffect {

  private int turnsRemaining;

  public StunEffect(int turnsRemaining) {
    this.turnsRemaining = turnsRemaining;
  }

  @Override
  public String getName() {
    return "STUN";
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
    return true;
  }

  @Override
  public boolean isInvulnerable() {
    return false;
  }

  @Override
  public boolean isExpired() {
    return turnsRemaining <= 0;
  }

  @Override
  public boolean isStackable() {
    return false;
  }

  @Override
  public void tick() {
    turnsRemaining--;
  }

  @Override
  public void onApply(Combatant t) {
    // no stat mutation needed
  }

  @Override
  public void onRemove(Combatant t) {
    // no stat mutation needed
  }

  public int getTurnsRemaining() {
    return turnsRemaining;
  }
}
