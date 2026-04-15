package com.arena.model.action;

import com.arena.model.combatant.Player;
import com.arena.model.item.Item;
import com.arena.observer.BattleEvent;
import com.arena.observer.BattleEventType;

//  ItemAction wraps a chosen Item as an Action (Command Pattern)

//  The item to use is injected at construction time (chosen by the player
//  via CLIGameUI before this action is submitted to BattleEngine)

//  SRP: This class is only responsible for delegating to the item's use()
//  method and recording consumption on the Player's inventory

public class ItemAction implements Action {

    private final Item item;

    public ItemAction(Item item) {
        this.item = item;
    }

    @Override
    public String getName() {
        return "Use Item: " + item.getName();
    }

    @Override
    public void execute(ActionContext context) {
        context.getBattleContext().notifyObservers(new BattleEvent(
                BattleEventType.ITEM_USED,
                context.getActor().getName() + " uses " + item.getName() + "!"));

        item.use(context);

        // Remove from inventory after use (single-use items)
        if (context.getActor() instanceof Player player) {
            player.consumeItem(item);
        }
    }

    public Item getItem() {
        return item;
    }

    @Override
    public boolean needsEnemyTarget() {
        return false;
    }

    @Override
    public boolean needsSelf() {
        return true;
    }
}
