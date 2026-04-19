package com.arena.model.action;

import com.arena.engine.BattleContext;
import com.arena.model.combatant.Combatant;

import java.util.List;

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
