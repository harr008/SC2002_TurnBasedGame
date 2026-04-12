package com.arena.strategy;

import com.arena.model.combatant.Combatant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;

//  Sorts combatants by speed descending
//  Ties are broken randomly (shuffle equal-speed group before merging)
//  Only living combatants are included in the result

public class SpeedBasedTurnOrder implements TurnOrderStrategy {

    @Override
    public List<Combatant> determineTurnOrder(List<Combatant> combatants) {

        List<Combatant> living = new ArrayList<>();

        for (int i = 0; i < combatants.size(); i++) {
            Combatant c = combatants.get(i);
            if (c.isAlive()) {
                living.add(c);
            }
        }

        // Shuffle first to randomise tie positions, then sort by speed descending
        Collections.shuffle(living);

        living.sort(Comparator.comparingInt(Combatant::getSpeed).reversed());

        return living;
    }
}
