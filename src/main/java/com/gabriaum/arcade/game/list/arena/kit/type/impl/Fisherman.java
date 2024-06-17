package com.gabriaum.arcade.game.list.arena.kit.type.impl;

import com.gabriaum.arcade.game.list.arena.kit.Kit;
import com.gabriaum.arcade.user.User;
import com.solexgames.core.util.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Fisherman extends Kit implements Listener {
    public Fisherman() {
        super(
                Material.FISHING_ROD,
                "Fisherman",
                Collections.singletonList("§7Pesque seus oponentes."),
                "arcade.pvp.fisherman",
                11000,
                0
        );
    }

    @Override
    public List<ItemStack> kitItems() {
        return Collections.singletonList(new ItemBuilder(Material.FISHING_ROD).setDisplayName("§aFisherman").create());
    }

    @EventHandler
    public void onFisherman(PlayerFishEvent event) {
        Player player = event.getPlayer();

        if (hasKit(player.getUniqueId())) {
            if (event.getCaught() instanceof Player) {
                Player caught = (Player) event.getCaught();
                User user = getInstance().getUserManager().get(caught.getUniqueId());

                if (user == null || user.isProtect()) {
                    event.setCancelled(true);
                    return;
                }

                Block block = event.getHook().getLocation().getBlock();

                if (caught != block) {
                    caught.teleport(player.getPlayer().getLocation());
                }
            }
        }
    }
}
