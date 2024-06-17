package com.gabriaum.arcade.game.list.arena.kit.type.impl;

import com.gabriaum.arcade.game.list.arena.kit.Kit;
import com.gabriaum.arcade.game.list.arena.kit.type.KitType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Hulk extends Kit {
    public Hulk() {
        super(
                Material.DISPENSER,
                "Hulk",
                Collections.singletonList(""),
                "arcade.pvp.hulk",
                5000,
                10
        );
    }

    @Override
    public List<ItemStack> kitItems() {
        return Collections.emptyList();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHulk(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {
            Player player = event.getPlayer();
            Player target = (Player) event.getRightClicked();

            if (hasKit(player.getUniqueId())) {
                if (hasCooldown(player))
                    return;
                else
                    addCooldown(player.getUniqueId());

                player.setPassenger(target);
            }
        }
    }
}
