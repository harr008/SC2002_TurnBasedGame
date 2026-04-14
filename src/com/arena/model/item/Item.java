package com.arena.model.item;

import com.arena.model.action.ActionContext;
=
public interface Item {
    // Display name of the item
    String getName();

    // Short description of the item's effect
    String getDescription();

    // Executes the item's effect
    // Called by ItemAction after the player selects this item
    // The ActionContext contains the actor and resolved target
    void use(ActionContext context);
}
