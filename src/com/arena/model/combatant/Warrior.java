package com.arena.model.combatant;

import com.arena.model.skill.ShieldBash;
import com.arena.model.skill.SpecialSkill;

// Stats: HP 260 | ATK 40 | DEF 20 | SPD 30
// Special Skill: Shield Bash - deals BasicAttack damage to one enemy and stuns it for its remaining turns this round plus its next round's turn

public class Warrior extends Player {

    private static final int BASE_HP = 260;
    private static final int BASE_ATTACK = 40;
    private static final int BASE_DEFENSE = 20;
    private static final int BASE_SPEED = 30;

    private final ShieldBash shieldBash;

    public Warrior(String name) {
        super(name, BASE_HP, BASE_ATTACK, BASE_DEFENSE, BASE_SPEED);
        this.shieldBash = new ShieldBash();
    }

    @Override
    public SpecialSkill getSpecialSkill() {
        return shieldBash;
    }

    @Override
    public String toString() {
        return name;
    }
}
