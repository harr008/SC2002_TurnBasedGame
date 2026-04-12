package com.arena.observer;

//  Logs battle events to the console

public class BattleLogger implements BattleObserver {

    private static final String SEPARATOR = "─────────────────────────────────────────";
    private static final String DOUBLE_SEP = "═════════════════════════════════════════";

    @Override
    public void onBattleEvent(BattleEvent event) {
        switch (event.getType()) {
            case ROUND_START -> System.out.println("\n" + DOUBLE_SEP + "\n  " + event.getMessage() + "\n" + DOUBLE_SEP);
            case ROUND_END -> System.out.println(SEPARATOR + "\n  " + event.getMessage() + "\n" + SEPARATOR);
            case TURN_START -> System.out.println("\n>> " + event.getMessage());
            case TURN_SKIPPED -> System.out.println("  (/) " + event.getMessage());
            case ATTACK -> System.out.println("  (x) " + event.getMessage());
            case DEFEND -> System.out.println("  (0) " + event.getMessage());
            case SKILL_USED -> System.out.println("   *  " + event.getMessage());
            case ITEM_USED -> System.out.println("   *  " + event.getMessage());
            case EFFECT_APPLIED -> System.out.println("   0  " + event.getMessage());
            case EFFECT_EXPIRED -> System.out.println("   O  " + event.getMessage());
            case COMBATANT_DEFEATED -> System.out.println("   X  " + event.getMessage());
            case BACKUP_SPAWN -> System.out.println("\n  (!) " + event.getMessage() + "\n");
            case BATTLE_END -> System.out.println("\n" + DOUBLE_SEP + "\n  " + event.getMessage() + "\n" + DOUBLE_SEP);
            case MESSAGE -> System.out.println("  " + event.getMessage());
        }
    }
}
