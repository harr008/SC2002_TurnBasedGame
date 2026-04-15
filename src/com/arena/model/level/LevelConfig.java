package com.arena.model.level;

import com.arena.model.combatant.Enemy;

import java.util.List;

// Immutable data class describing a level's enemy configuration

// SRP: Holds only configuration data - no game logic
// Factory pattern creates these via LevelFactory
public class LevelConfig {

    private final int levelNumber;
    private final String difficultyName;
    private final List<Enemy> initialSpawn;
    private final List<Enemy> backupSpawn; // empty list if no backup

    public LevelConfig(int levelNumber, String difficultyName,
            List<Enemy> initialSpawn, List<Enemy> backupSpawn) {
        this.levelNumber = levelNumber;
        this.difficultyName = difficultyName;
        this.initialSpawn = List.copyOf(initialSpawn);
        this.backupSpawn = List.copyOf(backupSpawn);
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public String getDifficultyName() {
        return difficultyName;
    }

    public List<Enemy> getInitialSpawn() {
        return initialSpawn;
    }

    public List<Enemy> getBackupSpawn() {
        return backupSpawn;
    }

    public boolean hasBackupSpawn() {
        return !backupSpawn.isEmpty();
    }

    @Override
    public String toString() {
        return "Level " + levelNumber + " (" + difficultyName + ")";
    }
}
