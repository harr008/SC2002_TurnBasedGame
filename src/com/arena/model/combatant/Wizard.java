package com.arena.model.combatant;

import com.arena.model.skill.ArcaneBlast;
import com.arena.model.skill.SpecialSkill;

// Stats: HP 200 | ATK 50 | DEF 10 | SPD 20
// Special Skill: Arcane Blast - deals damage to ALL enemies currently in combat
// Each enemy defeated by Arcane Blast (including Power Stone trigger) adds +10 to the Wizard's attack, lasting until end of the level

public class Wizard extends Player {

    private static final int BASE_HP = 200;
    private static final int BASE_ATTACK = 50;
    private static final int BASE_DEFENSE = 10;
    private static final int BASE_SPEED = 20;

    private int arcaneBlastBonus = 0; // cumulative per-level ATK bonus
    private final ArcaneBlast arcaneBlast;

    public Wizard(String name) {
        super(name, BASE_HP, BASE_ATTACK, BASE_DEFENSE, BASE_SPEED);
        this.arcaneBlast = new ArcaneBlast(this);
    }

    // Adds +10 ATK per enemy killed by Arcane Blast.
    public void addArcaneBlastBonus(int amount) {
        this.arcaneBlastBonus += amount;
    }

    // Resets the bonus at the start of a new level.
    public void resetArcaneBlastBonus() {
        this.arcaneBlastBonus = 0;
    }

    public int getArcaneBlastBonus() {
        return arcaneBlastBonus;
    }

    // Overrides to include the Arcane Blast cumulative bonus on top of
    // base attack and any effect modifiers.

    @Override
    public int getEffectiveAttack() {
        return super.getEffectiveAttack() + arcaneBlastBonus;
    }

    @Override
    public SpecialSkill getSpecialSkill() {
        return arcaneBlast;
    }

    @Override
    public String toString() {
        return name;
    }
}
