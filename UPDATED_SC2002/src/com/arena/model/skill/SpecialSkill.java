package com.arena.model.skill;

import com.arena.model.action.ActionContext;

//  Represents a player's class-specific special skill

//  New skill types (e.g. a Rogue's Backstab) can be added without modifying those classes

public interface SpecialSkill {
    // Display name shown in the action menu
    String getName();

    // Description of the skill's effect shown to the player
    String getDescription();

    // Executes the skill's effect
    // For ShieldBash: deals damage + stuns the selected target
    // For ArcaneBlast: deals damage to all enemies, buffs Wizard ATK per kill

    // context ActionContext containing actor, resolved targets, and
    // BattleContext

    void executeSkill(ActionContext context);

    // Whether this skill requires the player to select a single enemy target.
    // Used by CLIGameUI to decide whether to prompt for target selection.

    // ShieldBash -> true
    // ArcaneBlast -> false (hits all enemies automatically)

    boolean needsEnemyTarget();
}
