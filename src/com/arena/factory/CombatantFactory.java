package com.arena.factory;

import com.arena.model.combatant.*;
import com.arena.model.item.*;

import java.util.List;
import java.util.ArrayList;


public class CombatantFactory {

    private CombatantFactory() {
    } // utility class

    // Players

    public static Warrior createWarrior(String name) {
        return new Warrior(name);
    }

    public static Wizard createWizard(String name) {
        return new Wizard(name);
    }

    // Enemies

    public static Goblin createGoblin(String name) {
        return new Goblin(name);
    }

    public static Wolf createWolf(String name) {
        return new Wolf(name);
    }

    // Items

    public static Item createItem(String itemName) {
        String name = itemName.trim().toLowerCase();
        if (name.equals("potion")) {
            return new Potion();
        } else if (name.equals("power stone")) {
            return new PowerStone();
        } else if (name.equals("smoke bomb")) {
            return new SmokeBomb();
        } else {
            throw new IllegalArgumentException("Unknown item: " + itemName);
        }
    }

    public static List<Item> createItemList(String item1, String item2) {
        List<Item> items = new ArrayList<>();
        items.add(createItem(item1));
        items.add(createItem(item2));
        return items;
    }
}
