package com.arena.model.action;

public interface Action {
    // Human-readable name shown in menus and battle log
    String getName();

    // Executes the action
    // All side effects (damage, healing, effect application) happen here
    // Notifications to BattleObservers are fired via BattleContext

    void execute(ActionContext context);

    // True if the UI should prompt the player to select an enemy target
    boolean needsEnemyTarget();

    // True if the action targets the actor themselves (e.g. Defend, Potion)
    boolean needsSelf();
}
