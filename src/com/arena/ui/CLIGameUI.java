package com.arena.ui;

import com.arena.engine.BattleContext;
import com.arena.engine.GameStatistics;
import com.arena.engine.PlayerInputProvider;
import com.arena.factory.CombatantFactory;
import com.arena.factory.LevelFactory;
import com.arena.model.action.*;
import com.arena.model.combatant.*;
import com.arena.model.item.Item;
import com.arena.model.item.PowerStone;
import com.arena.model.level.LevelConfig;
import java.util.List;
import java.util.Scanner;



public class CLIGameUI implements PlayerInputProvider {

    private static final String BANNER = "\n╔══════════════════════════════════════════╗\n" +
            "║          COMBAT ARENA                    ║\n" +
            "║      Turn-Based Combat Game              ║\n" +
            "╚══════════════════════════════════════════╝\n";

    private final Scanner scanner;

    public CLIGameUI() {
        this.scanner = new Scanner(System.in);
    }

    private int promptInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = scanner.nextLine().trim();
                int val = Integer.parseInt(line);
                if (val >= min && val <= max)
                    return val;
                System.out.println("  Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input. Please enter a number.");
            }
        }
    }

    public void pause() {
        System.out.print("\n  Press ENTER to continue...");
        scanner.nextLine();
    }

    // LOADING SCREEN

    public void showLoadingScreen() {
        System.out.println(BANNER);
        System.out.println("  Welcome to the Combat Arena!");
        System.out.println("  Defeat all enemies to win.\n");
    }

    // PLAYER SELECTION

    public Player selectPlayer() {
        System.out.println("\n  SELECT YOUR HERO\n");
        System.out.println("  1. Warrior");
        System.out.println("     HP: 260  ATK: 40  DEF: 20  SPD: 30");
        System.out.println("     Special: Shield Bash - Attack + Stun one enemy");
        System.out.println();
        System.out.println("  2. Wizard");
        System.out.println("     HP: 200  ATK: 50  DEF: 10  SPD: 20");
        System.out.println("     Special: Arcane Blast - Hit all enemies, +10 ATK per kill");
        System.out.println();

        int choice = promptInt("  Choose (1-2): ", 1, 2);

        if (choice == 1) {
            String name = "Warrior";
            Player player = CombatantFactory.createWarrior(name);
            System.out.println("\n  " + player + " selected!\n");
            return player;
        } else {
            String name = "Wizard";
            Player player = CombatantFactory.createWizard(name);
            System.out.println("\n  " + player + " selected!\n");
            return player;
        }
    }

    // ITEM SELECTION

    public List<Item> selectItems() {
        System.out.println("\n  SELECT TWO ITEMS  (duplicates allowed)\n");
        System.out.println("  1. Potion        - Restore 100 HP");
        System.out.println("  2. Power Stone   - Trigger special skill (no cooldown change)");
        System.out.println("  3. Smoke Bomb    -  Invulnerable to damage this round & next\n");

        int c1 = promptInt("  First item  (1-3): ", 1, 3);
        int c2 = promptInt("  Second item (1-3): ", 1, 3);

        Item item1 = itemFromChoice(c1);
        Item item2 = itemFromChoice(c2);

        System.out.println("\n  Items: " + item1.getName() + " + " + item2.getName() + "\n");
        return List.of(item1, item2);
    }

    private Item itemFromChoice(int choice) {
        return switch (choice) {
            case 1 -> CombatantFactory.createItem("potion");
            case 2 -> CombatantFactory.createItem("power stone");
            case 3 -> CombatantFactory.createItem("smoke bomb");
            default -> throw new IllegalArgumentException("Bad item choice");
        };
    }

    // DIFFICULTY LEVEL SELECTION

    public LevelConfig selectLevel() {
        System.out.println("\n  SELECT DIFFICULTY\n");
        System.out.println("  1. Easy   - 3 Goblins");
        System.out.println("             Goblin: HP 55 | ATK 35 | DEF 15 | SPD 25");
        System.out.println();
        System.out.println("  2. Medium - 1 Goblin + 1 Wolf  (Backup: 2 Wolves)");
        System.out.println("             Wolf:   HP 40 | ATK 45 | DEF  5 | SPD 35");
        System.out.println();
        System.out.println("  3. Hard   - 2 Goblins  (Backup: 1 Goblin + 2 Wolves)\n");

        int choice = promptInt("  Choose difficulty (1-3): ", 1, 3);
        LevelConfig level = LevelFactory.createLevel(choice);

        System.out.println("\n  " + level + " selected!\n");
        return level;
    }

    // PLAYER INPUT PROVIDER - called by BattleEngine each player turn

    @Override
    public Action getPlayerAction(Player player, BattleContext context) {
        displayBattleStatus(player, context);
        return promptPlayerAction(player, context);
    }

    private void displayBattleStatus(Player player, BattleContext context) {
        System.out.println();
        System.out.println("  ── Battle Status ──────────────────────────");
        System.out.printf("  %-20s HP: %s%s%n", player.getName(), player.getHpDisplay(), player.getStatusTag());

        for (var enemy : context.getLivingEnemies()) {
            System.out.printf("  %-20s HP: %s%s%n", enemy.getName(), enemy.getHpDisplay(), enemy.getStatusTag());
        }
        System.out.println("  ───────────────────────────────────────────");
    }

    private Action promptPlayerAction(Player player, BattleContext context) {
        int round = context.getCurrentRound();

        System.out.println("\n  YOUR TURN - Choose an action:\n");
        System.out.println("  1. Basic Attack");
        System.out.println("  2. Defend  (+10 DEF this round & next)");

        // Item option - only shown if items remain
        boolean hasItems = player.hasItems();
        if (hasItems) {
            System.out.print("  3. Use Item  [");
            List<Item> inv = player.getInventory();
            for (int i = 0; i < inv.size(); i++) {
                System.out.print(inv.get(i).getName());
                // Only print the comma and space if this is NOT the last item
                if (i < inv.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
        } else {
            System.out.println("  3. Use Item  [none remaining]");
        }

        // Special skill with cooldown display
        boolean skillReady = player.isSpecialSkillReady(round);
        int cooldown = player.getSpecialSkillCooldown(round);
        String skillLabel = "  4. " + player.getSpecialSkill().getName();
        if (skillReady) {
            System.out.println(skillLabel + "  [READY]");
        } else {
            System.out.println(skillLabel + "  [Cooldown: " + cooldown + " round(s)]");
        }
        System.out.println();

        while (true) {
            int choice = promptInt("  Action (1-4): ", 1, 4);

            switch (choice) {
                case 1 -> {
                    Combatant target = selectEnemyTarget(context);
                    return new TargetedAction(new BasicAttackAction(), target);
                }
                case 2 -> {
                    return new DefendAction();
                }
                case 3 -> {
                    if (!hasItems) {
                        System.out.println("  No items remaining! Choose another action.");
                        continue;
                    }
                    return buildItemAction(player, context);
                }
                case 4 -> {
                    if (!skillReady) {
                        System.out
                                .println("  Skill on cooldown (" + cooldown + " round(s) remaining)! Choose another.");
                        continue;
                    }
                    return buildSpecialSkillAction(player, context);
                }
            }
        }
    }

    // Item action builder

    private Action buildItemAction(Player player, BattleContext context) {
        List<Item> inv = player.getInventory();

        System.out.println("\n  Select item:");
        for (int i = 0; i < inv.size(); i++) {
            Item item = inv.get(i);
            System.out.println("  " + (i + 1) + ". " + item.getName() + " - " + item.getDescription());
        }

        int idx = promptInt("  Item (1-" + inv.size() + "): ", 1, inv.size()) - 1;
        Item chosen = inv.get(idx);

        // Power Stone needs to know the skill's target preference
        if (chosen instanceof PowerStone) {
            return buildPowerStoneAction(player, context, chosen);
        }

        return new ItemAction(chosen);
    }

    private Action buildPowerStoneAction(Player player, BattleContext context, Item powerStone) {
        boolean needsTarget = player.getSpecialSkill().needsEnemyTarget();
        if (needsTarget) {
            Combatant target = selectEnemyTarget(context);
            // Wrap: TargetedAction carries the target into the ItemAction's ActionContext
            return new TargetedAction(new ItemAction(powerStone), target);
        }
        return new ItemAction(powerStone);
    }

    // Special skill action builder

    private Action buildSpecialSkillAction(Player player, BattleContext context) {
        boolean needsTarget = player.getSpecialSkill().needsEnemyTarget();
        if (needsTarget) {
            Combatant target = selectEnemyTarget(context);
            return new TargetedAction(new SpecialSkillAction(), target);
        }
        return new SpecialSkillAction();
    }

    // Enemy target selection

    private Combatant selectEnemyTarget(BattleContext context) {
        List<Combatant> enemies = context.getLivingEnemies();

        if (enemies.size() == 1) {
            return enemies.get(0);
        }

        System.out.println("\n  Select target:");

        for (int i = 0; i < enemies.size(); i++) {
            Combatant e = enemies.get(i);
            System.out.printf("  %d. %-20s HP: %s%s%n", i + 1, e.getName(), e.getHpDisplay(), e.getStatusTag());
        }

        int idx = promptInt("  Target (1-" + enemies.size() + "): ", 1, enemies.size()) - 1;
        return enemies.get(idx);
    }

    // END SCREENS

    public void showVictoryScreen(GameStatistics stats) {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║            VICTORY!                      ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  Congratulations! You have defeated all your enemies.");
        System.out.println();
        System.out.println("  ── Statistics ─────────────────────────────");
        System.out.printf("  Remaining HP    : %d / %d%n", stats.getRemainingHp(), stats.getPlayer().getMaxHp());
        System.out.printf("  Total Rounds    : %d%n", stats.getTotalRounds());
        System.out.printf("  Items Remaining : %s%n", stats.itemSummary());
    }

    public void showDefeatScreen(GameStatistics stats) {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║               DEFEATED                   ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  Defeated. Don't give up - try again!");
        System.out.println();
        System.out.println("  ── Statistics ─────────────────────────────");
        System.out.printf("  Enemies Remaining : %d%n", stats.getEnemiesRemaining());
        System.out.printf("  Total Rounds      : %d%n", stats.getTotalRounds());
    }

    // POST-GAME OPTIONS
    // Returns true to replay with same settings, false to go to main menu
    // Returning false from main menu exits the game

    public int showPostGameMenu() {
        System.out.println("\n  What would you like to do?");
        System.out.println("  1. Replay with the same settings");
        System.out.println("  2. Start a new game (return to main menu)");
        System.out.println("  3. Exit");
        return promptInt("  Choice (1-3): ", 1, 3);
    }

    public void closeScanner() {
        scanner.close();
    }
}
