package com.gabriaum.arcade.game.list.arena.kit.type.impl;

import com.gabriaum.arcade.game.list.arena.kit.Kit;
import com.solexgames.core.util.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class Flash extends Kit implements Listener {
    public Flash() {
        super(
                Material.REDSTONE_TORCH_ON,
                "Flash",
                Arrays.asList("§7Mire para um lugar com seu item", "§7e clique para teleportar-se."),
                "arcade.pvp.flash",
                10000,
                25
        );
    }

    @Override
    public List<ItemStack> kitItems() {
        return Collections.singletonList(new ItemBuilder(Material.REDSTONE_TORCH_ON).setDisplayName("§aFlash").create());
    }

    @EventHandler
    public void onFlash(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction().name().contains("RIGHT_")) {
            if (hasKit(player.getUniqueId()) && isKitItem(player.getItemInHand())) {
                Block block = player.getTargetBlock((HashSet<Byte>) null, 100);

                if (block == null || block.getType().equals(Material.AIR))
                    return;

                if (!hasCooldown(player))
                    addCooldown(player.getUniqueId());
                else
                    return;

                player.teleport(block.getLocation().add(0.5, 1, 0.5));
                player.getWorld().strikeLightningEffect(block.getLocation());
            }
        }
    }
}
