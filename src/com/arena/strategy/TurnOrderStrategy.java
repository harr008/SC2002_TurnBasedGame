package com.arena.strategy;

import com.arena.model.combatant.Combatant;

import java.util.List;

// Returns a new ordered list of combatants defining the turn sequence
// for one round. Dead combatants are included in the input list but
// will be filtered out by the strategy 

public interface TurnOrderStrategy {

    // @param combatants All combatants currently registered in the battle
    // @return Ordered list - index 0 acts first

    List<Combatant> determineTurnOrder(List<Combatant> combatants);
}
