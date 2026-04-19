package com.arena.model.combatant;

import com.arena.model.item.Item;
import com.arena.model.skill.SpecialSkill;
import java.util.ArrayList;
import java.util.List;

public abstract class Player extends Combatant {

    private final List<Item> inventory;
    private int lastSkillUsedRound; // -100 = never used (always ready initially)

    protected Player(String name, int hp, int attack, int defense, int speed) {
        super(name, hp, attack, defense, speed);
        this.inventory = new ArrayList<>();
        this.lastSkillUsedRound = -100;
    }

    // Special skill cooldown

    public boolean isSpecialSkillReady(int currentRound) {
        return currentRound >= lastSkillUsedRound + 3;
    }

    // Remaining cooldown rounds displayed to the player. 0 means available
    public int getSpecialSkillCooldown(int currentRound) {
        return Math.max(0, lastSkillUsedRound + 3 - currentRound);
    }

    // Records that the skill was used on the given round (for cooldown tracking)
    public void recordSkillUsed(int currentRound) {
        this.lastSkillUsedRound = currentRound;
    }

    // Inventory management

    public void setInventory(List<Item> items) {
        this.inventory.clear();
        this.inventory.addAll(items);
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public boolean hasItems() {
        return !inventory.isEmpty();
    }

    // Removes the first matching item from the inventory (single-use consumption)
    // No-op if the item is not present

    public void consumeItem(Item item) {
        inventory.remove(item);
    }

    // Abstract members

    // Returns the player's class-specific special skill
    public abstract SpecialSkill getSpecialSkill();
}
