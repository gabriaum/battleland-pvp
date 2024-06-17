package com.gabriaum.arcade.game.list.arena.kit.type.impl;

import com.gabriaum.arcade.game.list.arena.kit.Kit;
import com.solexgames.core.util.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Thor extends Kit implements Listener {
    public Thor() {
        super(
                Material.GOLD_AXE,
                "Thor",
                Arrays.asList("§7Tenha o próprio mjolnir de Thor", "§7em sua versão mais poderosa."),
                "arcade.pvp.thor",
                10000,
                10
        );
    }

    @Override
    public List<ItemStack> kitItems() {
        return Collections.singletonList(new ItemBuilder(Material.GOLD_AXE).setDisplayName("§aThor").create());
    }

    @EventHandler
    public void onThor(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (hasKit(player.getUniqueId()) && isKitItem(player.getItemInHand())) {
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                Block block = event.getClickedBlock();

                if (block != null) {
                    if (hasCooldown(player))
                        return;
                    else
                        addCooldown(player.getUniqueId());

                    block.getWorld().strikeLightning(block.getLocation());
                }
            }
        }
    }
}

