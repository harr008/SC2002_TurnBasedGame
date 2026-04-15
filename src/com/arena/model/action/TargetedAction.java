package com.arena.model.action;

import com.arena.model.combatant.Combatant;
import java.util.List;

//  Wraps an Action with a pre-resolved target

//  Created by CLIGameUI when the player selects an action that requires
//  a target (BasicAttack, SpecialSkillAction for ShieldBash)
//  BattleEngine extracts the target via resolvePlayerTarget()

//  SRP: Target resolution lives in the UI layer; the engine just executes

public class TargetedAction implements Action {
    private final Action wrapped;
    private final Combatant target;

    public TargetedAction(Action wrapped, Combatant target) {
        this.wrapped = wrapped;
        this.target = target;
    }

    public Combatant getTarget() {
        return target;
    }

    @Override
    public String getName() {
        return wrapped.getName();
    }

    @Override
    public boolean needsEnemyTarget() {
        return false;
    }

    @Override
    public boolean needsSelf() {
        return wrapped.needsSelf();
    }

    @Override
    public void execute(ActionContext context) {
        // Rebuild context with the correct target
        ActionContext withTarget = new ActionContext(
                context.getActor(), List.of(target), context.getBattleContext());
        wrapped.execute(withTarget);
    }
}