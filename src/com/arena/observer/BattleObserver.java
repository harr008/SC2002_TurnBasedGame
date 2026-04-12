package com.arena.observer;

//  Any component interested in battle events implements this
//  BattleEngine notifies all registered observers when events occur

public interface BattleObserver {
    void onBattleEvent(BattleEvent event);
}
