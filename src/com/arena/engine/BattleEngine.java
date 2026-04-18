package com.arena.engine;

import com.arena.model.action.Action;
import com.arena.model.action.ActionContext;
import com.arena.model.action.TargetedAction;
import com.arena.model.combatant.Combatant;
import com.arena.model.combatant.Enemy;
import com.arena.model.combatant.Player;
import com.arena.model.effect.StatusEffect;
import com.arena.model.item.Item;
import com.arena.model.level.LevelConfig;
import com.arena.observer.BattleEvent;
import com.arena.observer.BattleEventType;
import com.arena.observer.BattleObserver;
import com.arena.strategy.TurnOrderStrategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// BattleEngine orchestrates the complete battle loop
// Responsibilities (SRP per method):
// - runBattle(): top-level round loop
// - processRound(): single round: determine order -> process each turn
// - processTurn(): one combatant's turn slot
// - checkBackupSpawn(): triggered when all initial enemies die mid-round
// - endOfRound(): tick round-based effects, display status

// Implements BattleContext to expose a controlled interface to Actions/Skills

public class BattleEngine implements BattleContext {

    // Dependencies (injected, all abstractions)
    private final TurnOrderStrategy turnOrderStrategy;
    private final PlayerInputProvider inputProvider;
    private final List<BattleObserver> observers;

    // Battle state
    private final Player player;
    private final LevelConfig levelConfig;
    private final List<Enemy> activeEnemies;
    private boolean backupSpawned = false;
    private int currentRound = 0;
    private final Set<Combatant> actedThisRound = new HashSet<>();

    // Statistics
    private int totalRounds = 0;

    public BattleEngine(Player player,
            LevelConfig levelConfig,
            TurnOrderStrategy turnOrderStrategy,
            PlayerInputProvider inputProvider) {
        this.player = player;
        this.levelConfig = levelConfig;
        this.turnOrderStrategy = turnOrderStrategy;
        this.inputProvider = inputProvider;
        this.observers = new ArrayList<>();
        this.activeEnemies = new ArrayList<>(levelConfig.getInitialSpawn());
    }

    // BattleContext implementation

    @Override
    public List<Combatant> getLivingEnemies() {
        List<Combatant> aliveEnemies = new ArrayList<>();
        for (int i = 0; i < activeEnemies.size(); i++) {
            Enemy e = activeEnemies.get(i);
            if (e.isAlive()) {
                aliveEnemies.add(e);
            }
        }
        return aliveEnemies;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public int getCurrentRound() {
        return currentRound;
    }

    @Override
    public boolean hasActedThisRound(Combatant c) {
        return actedThisRound.contains(c);
    }

    @Override
    public void notifyObservers(BattleEvent event) {
        for (int i = 0; i < observers.size(); i++) {
            BattleObserver o = observers.get(i);
            o.onBattleEvent(event);
        }
    }

    @Override
    public void addObserver(BattleObserver observer) {
        observers.add(observer);
    }

    // Main battle loop

    // Runs the complete battle and returns the final result
    // Called by GameSession after setup

    public BattleResult runBattle() {
        BattleResult result = BattleResult.IN_PROGRESS;

        while (result == BattleResult.IN_PROGRESS) {
            currentRound++;
            totalRounds++;
            actedThisRound.clear();

            notifyObservers(new BattleEvent(BattleEventType.ROUND_START,
                    "ROUND " + currentRound));

            result = processRound();

            if (result == BattleResult.IN_PROGRESS) {
                endOfRound();
            }
        }

        if (result == BattleResult.PLAYER_VICTORY) {
            notifyObservers(new BattleEvent(BattleEventType.BATTLE_END,
                    "VICTORY! All enemies defeated!"));
        } else {
            notifyObservers(new BattleEvent(BattleEventType.BATTLE_END,
                    "DEFEAT! You have been defeated..."));
        }

        return result;
    }

    // Round processing

    private BattleResult processRound() {
        // Build turn order from ALL living combatants
        List<Combatant> allCombatants = new ArrayList<>();
        allCombatants.add(player);
        allCombatants.addAll(getLivingEnemies());

        List<Combatant> turnOrder = turnOrderStrategy.determineTurnOrder(allCombatants);

        for (int i = 0; i < turnOrder.size(); i++) {
            Combatant combatant = turnOrder.get(i);
            // Re-check alive status - may have been defeated earlier this round
            if (!combatant.isAlive()) {
                continue;
            }

            BattleResult turnResult = processTurn(combatant);
            if (turnResult != BattleResult.IN_PROGRESS) {
                return turnResult;
            }

            // After each turn, check if backup should spawn
            checkBackupSpawn();

            BattleResult afterSpawnCheck = checkEndCondition();
            if (afterSpawnCheck != BattleResult.IN_PROGRESS) {
                return afterSpawnCheck;
            }
        }

        return checkEndCondition();
    }

    // Turn processing

    private BattleResult processTurn(Combatant combatant) {
        notifyObservers(new BattleEvent(BattleEventType.TURN_START,
                combatant.getName() + "'s turn"));

        // Handle stun
        if (combatant.isStunned()) {
            notifyObservers(new BattleEvent(BattleEventType.TURN_SKIPPED,
                    combatant.getName() + " is STUNNED - turn skipped!"));
            combatant.tickStunEffect();
            actedThisRound.add(combatant);
            return BattleResult.IN_PROGRESS;
        }

        Action action;
        Combatant target;
        List<Combatant> targets = List.of();

        if (combatant instanceof Player p) {
            // Player turn - ask UI for action
            action = inputProvider.getPlayerAction(p, this);

            // Resolve target for actions that need one (already embedded in
            // ItemAction/SpecialSkillAction)
            target = resolvePlayerTarget(action);
        } else {
            // Enemy turn
            Enemy enemy = (Enemy) combatant;
            action = enemy.getActionStrategy().selectAction(enemy, this);
            target = player; // enemies always attack the player
        }

        if (target != null) {
            targets = List.of(target);
        }

        ActionContext ctx = new ActionContext(combatant, targets, this);
        action.execute(ctx);
        actedThisRound.add(combatant);

        return checkEndCondition();
    }

    // For player actions that target an enemy, the target was already embedded into
    // the action by CLIGameUI
    // For BasicAttack/SpecialSkillAction we return null here and let the action's
    // execute() handle it via context

    private Combatant resolvePlayerTarget(Action action) {
        if (action instanceof TargetedAction ta)
            return ta.getTarget();
        return null;
    }

    // Backup spawn

    private void checkBackupSpawn() {
        if (backupSpawned) {
            return;
        }
        if (!levelConfig.hasBackupSpawn()) {
            return;
        }

        // check if initial spawn is entirely defeated
        boolean initialAllDead = true;
        for (int i = 0; i < activeEnemies.size(); i++) {
            Enemy e = activeEnemies.get(i);
            if (levelConfig.getInitialSpawn().contains(e) && e.isAlive()) {
                initialAllDead = false;
                break;
            }
        }

        if (initialAllDead) {
            backupSpawned = true;
            List<Enemy> backup = new ArrayList<>(levelConfig.getBackupSpawn());
            activeEnemies.addAll(backup);

            StringBuilder names = new StringBuilder();
            for (Enemy e : backup)
                names.append(e.getName()).append(" ");

            notifyObservers(new BattleEvent(BattleEventType.BACKUP_SPAWN,
                    "BACKUP SPAWN! " + names.toString().trim() + " enter the battle!"));
        }
    }

    // End condition

    private BattleResult checkEndCondition() {
        if (!player.isAlive())
            return BattleResult.PLAYER_DEFEAT;

        boolean allEnemiesDead = true;
        for (int i = 0; i < activeEnemies.size(); i++) {
            Enemy e = activeEnemies.get(i);
            if (e.isAlive()) {
                allEnemiesDead = false;
                break;
            }
        }

        // Only victory if no backup is pending
        boolean backupPending = false;
        if (!backupSpawned && levelConfig.hasBackupSpawn()) {
            backupPending = true;
            for (int i = 0; i < activeEnemies.size(); i++) {
                Enemy e = activeEnemies.get(i);
                if (levelConfig.getInitialSpawn().contains(e) && e.isAlive()) {
                    backupPending = false;
                    break;
                }
            }
        }

        if (allEnemiesDead && !backupPending)
            return BattleResult.PLAYER_VICTORY;

        return BattleResult.IN_PROGRESS;
    }

    // End of round

    private void endOfRound() {
        // Tick round-based effects on all combatants
        List<Combatant> all = new ArrayList<>();
        all.add(player);
        all.addAll(activeEnemies);

        for (int i = 0; i < all.size(); i++) {
            Combatant c = all.get(i);
            List<StatusEffect> beforeTick = new ArrayList<>(c.getActiveEffects());
            c.tickRoundEffects();
            // Notify about expired effects
            for (StatusEffect e : beforeTick) {
                if (e.isExpired()) {
                    notifyObservers(new BattleEvent(BattleEventType.EFFECT_EXPIRED,
                            c.getName() + "'s " + e.getName() + " effect expired."));
                }
            }
        }

        // Display end-of-round status
        displayEndOfRoundStatus();
    }

    private void displayEndOfRoundStatus() {
        StringBuilder sb = new StringBuilder("End of Round " + currentRound + " | ");
        sb.append(player.getName()).append(" HP: ").append(player.getHpDisplay())
                .append(player.getStatusTag());

        for (int i = 0; i < activeEnemies.size(); i++) {
            Enemy e = activeEnemies.get(i);
            sb.append(" | ").append(e.getName()).append(" HP: ");
            if (e.isAlive()) {
                sb.append(e.getHpDisplay()).append(e.getStatusTag());
            } else {
                sb.append("X");
            }
        }

        boolean hasItems = player.hasItems();

        if (hasItems) {
            List<Item> inv = player.getInventory();

            for (int i = 0; i < inv.size(); i++) {
                Item item = inv.get(i);
                sb.append(" | ").append(item.getName()).append(": ").append(1);
            }
        }

        notifyObservers(new BattleEvent(BattleEventType.ROUND_END, sb.toString()));
    }

    // Statistics

    public int getTotalRounds() {
        return totalRounds;
    }

    public List<Enemy> getActiveEnemies() {
        return activeEnemies;
    }
}
