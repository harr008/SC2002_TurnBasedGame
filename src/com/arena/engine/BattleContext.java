package com.arena.engine;

import com.arena.model.combatant.Combatant;
import com.arena.model.combatant.Player;
import com.arena.observer.BattleEvent;
import com.arena.observer.BattleObserver;

import java.util.List;

// provides a controlled, read-only-ish view of battle state to Actions, Skills, and Items

public interface BattleContext {
    // Returns all living enemies in current combat
    List<Combatant> getLivingEnemies();

    // Returns the player combatant
    Player getPlayer();

    // Returns the current round number (1-based)
    int getCurrentRound();

    // Returns true if the given combatant has already taken its action this round
    // Used by ShieldBash to determine stun duration
    boolean hasActedThisRound(Combatant combatant);

    // Fires a BattleEvent to all registered observers
    void notifyObservers(BattleEvent event);

    // Registers an observer to receive BattleEvents
    void addObserver(BattleObserver observer);
}
