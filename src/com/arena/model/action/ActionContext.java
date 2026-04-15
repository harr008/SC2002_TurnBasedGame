package com.arena.model.action;

import com.arena.engine.BattleContext;
import com.arena.model.combatant.Combatant;

import java.util.List;


//  Data object passed to every Action.execute() call

//  SRP: Bundles actor, targets, and BattleContext into one parameter
//  so Action signatures stay clean and stable

//  DIP: Actions depend on BattleContext (abstraction), not BattleEngine directly

public class ActionContext {

    private final Combatant actor;
    private final List<Combatant> targets;
    private final BattleContext battleContext;

    public ActionContext(Combatant actor, List<Combatant> targets, BattleContext battleContext) {
        this.actor = actor;
        this.targets = targets;
        this.battleContext = battleContext;
    }

    public Combatant getActor() {
        return actor;
    }

    public List<Combatant> getTargets() {
        return targets;
    }

    public Combatant getPrimaryTarget() {
        if (targets.isEmpty()) {
            return null;
        } else {
            return targets.get(0);
        }
    }

    public BattleContext getBattleContext() {
        return battleContext;
    }
}
