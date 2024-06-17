package com.gabriaum.arcade.game.list.arena.kit.type.impl;

import com.gabriaum.arcade.game.list.arena.kit.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class Boxer extends Kit implements Listener {

    public Boxer() {
        super(
                Material.NETHER_STALK,
                "Boxer",
                Collections.singletonList("§7Dê mais dano com as mãos."),
                "arcade.kit.boxer",
                9500,
                0
        );
    }

    @Override
    public List<ItemStack> kitItems() {
        return Collections.emptyList();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDamagePlayer(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player))
            return;

        Player player = (Player) event.getDamager();

        if ((event.getEntity() instanceof Player && hasKit(event.getEntity().getUniqueId())) && event.getDamage() > 1.0D)
            event.setDamage(event.getDamage() - 0.25D);

        if (hasKit(player.getUniqueId()) && player.getItemInHand().getType() == Material.AIR) {
            event.setDamage(event.getDamage() + 2.0D);
            return;
        }

        if (hasKit(player.getUniqueId()) && player.getItemInHand().getType() != Material.AIR)
            event.setDamage(event.getDamage() + 0.25D);
    }
}
