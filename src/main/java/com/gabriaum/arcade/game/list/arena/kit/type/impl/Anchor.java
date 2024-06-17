package com.gabriaum.arcade.game.list.arena.kit.type.impl;

import com.gabriaum.arcade.game.list.arena.kit.Kit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.List;

public class Anchor extends Kit implements Listener {

    public Anchor() {
        super(
                Material.ANVIL,
                "Anchor",
                Collections.singletonList("§7Se prenda ao chão e não saia dele."),
                "arcade.kit.anchor",
                12500,
                0);
    }

    @Override
    public List<ItemStack> kitItems() {
        return Collections.emptyList();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAnchor(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        Entity damager = event.getDamager();

        if (hasKit(player.getUniqueId()) || hasKit(damager.getUniqueId())) {
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.15F, 1.0F);

            handleVelocity(player);
            handleVelocity(damager);
        }
    }

    protected void handleVelocity(Entity entity) {
        new BukkitRunnable() {
            @Override
            public void run() {
                entity.setVelocity(new Vector(0.0, -1, 0.0));
            }
        }.runTaskLater(getInstance(), 1L);
    }
}
