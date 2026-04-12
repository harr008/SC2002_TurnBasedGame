package com.arena.engine;

import com.arena.model.action.Action;
import com.arena.model.combatant.Player;

//  Abstraction for collecting player decisions during battle

public interface PlayerInputProvider {

    // Asks the player to choose their action for this turn

    // @param player The player combatant taking their turn
    // @param context Current battle state (for displaying HP, cooldowns, etc.)
    // @return The chosen Action, ready to execute

    Action getPlayerAction(Player player, BattleContext context);
}
