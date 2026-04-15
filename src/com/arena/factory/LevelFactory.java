package com.arena.factory;

import com.arena.model.combatant.Enemy;
import com.arena.model.level.LevelConfig;

import java.util.List;
import java.util.ArrayList;


public class LevelFactory {

    private LevelFactory() {
    }

    public static LevelConfig createLevel(int levelNumber) {
        if (levelNumber == 1) {
            return createLevel1();
        } else if (levelNumber == 2) {
            return createLevel2();
        } else if (levelNumber == 3) {
            return createLevel3();
        } else {
            throw new IllegalArgumentException(
                    "Invalid level number: " + levelNumber);
        }
    }

    // Level 1 - Easy: 3 Goblins, backup is empty
    private static LevelConfig createLevel1() {
        List<Enemy> initial = new ArrayList<>();
        initial.add(CombatantFactory.createGoblin("Goblin A"));
        initial.add(CombatantFactory.createGoblin("Goblin B"));
        initial.add(CombatantFactory.createGoblin("Goblin C"));
        List<Enemy> backup = new ArrayList<>();
        return new LevelConfig(1, "Easy", initial, backup);
    }

    // Level 2 - Medium: 1 Goblin + 1 Wolf; backup 2 Wolves
    private static LevelConfig createLevel2() {
        List<Enemy> initial = new ArrayList<>();
        initial.add(CombatantFactory.createGoblin("Goblin"));
        initial.add(CombatantFactory.createWolf("Wolf"));
        List<Enemy> backup = new ArrayList<>();
        backup.add(CombatantFactory.createWolf("Wolf A"));
        backup.add(CombatantFactory.createWolf("Wolf B"));
        return new LevelConfig(2, "Medium", initial, backup);
    }

    // Level 3 - Hard: 2 Goblins; backup 1 Goblin + 2 Wolves
    private static LevelConfig createLevel3() {
        List<Enemy> initial = new ArrayList<>();
        initial.add(CombatantFactory.createGoblin("Goblin A"));
        initial.add(CombatantFactory.createGoblin("Goblin B"));
        List<Enemy> backup = new ArrayList<>();
        backup.add(CombatantFactory.createGoblin("Goblin C"));
        backup.add(CombatantFactory.createWolf("Wolf A"));
        backup.add(CombatantFactory.createWolf("Wolf B"));
        return new LevelConfig(3, "Hard", initial, backup);
    }
}
