package com.gabriaum.arcade.game.list.arena.kit.type.impl;

import com.gabriaum.arcade.game.list.arena.kit.Kit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class Quickdrop extends Kit implements Listener {
    public Quickdrop() {
        super(
                Material.BOWL,
                "Quickdrop",
                Collections.singletonList("§7Drope automaticamente suas pots."),
                "",
                0,
                0
        );
    }

    @Override
    public List<ItemStack> kitItems() {
        return Collections.emptyList();
    }
}
