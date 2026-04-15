package com.arena.model.action;

//  Command interface for all combat actions (Command Pattern)

//  OCP: New action types (e.g. RangedAttack, Taunt) can be added as new
//  classes implementing this interface - BattleEngine needs no changes

//  DIP: BattleEngine calls Action.execute() through this abstraction

//  ISP: Only execute() and getName() are required by all actions
//  Actions that need target selection advertise it via needsEnemyTarget() and needsSelf()

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
