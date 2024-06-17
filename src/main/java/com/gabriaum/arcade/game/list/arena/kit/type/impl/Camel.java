package com.gabriaum.arcade.game.list.arena.kit.type.impl;

import com.gabriaum.arcade.game.list.arena.kit.Kit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;

public class Camel extends Kit implements Listener {

    public Camel() {
        super(
                Material.SAND,
                "Camel",
                Collections.singletonList("§7Torne-se mais rápido nas areias."),
               "arcade.kit.camel",
                0,
                0
        );
    }

    @Override
    public List<ItemStack> kitItems() {
        return Collections.emptyList();
    }

    @EventHandler
    public void onCamel(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Block block = event.getFrom().getBlock().getRelative(BlockFace.DOWN);

        if (hasKit(player.getUniqueId()) && block.getType().equals(Material.SAND)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 0));
        }
    }
}
