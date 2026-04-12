package com.arena.observer;

//  Represents a battle event with a type and descriptive message
//  Used by BattleEngine to notify BattleObservers

public class BattleEvent {
    private final BattleEventType type;
    private final String message;

    public BattleEvent(BattleEventType type, String message) {
        this.type = type;
        this.message = message;
    }

    public BattleEventType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "[" + type + "] " + message;
    }
}
