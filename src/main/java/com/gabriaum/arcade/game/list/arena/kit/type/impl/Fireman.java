package com.gabriaum.arcade.game.list.arena.kit.type.impl;

import com.gabriaum.arcade.game.list.arena.kit.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class Fireman extends Kit implements Listener {

    public Fireman() {
        super(
                Material.LAVA_BUCKET,
                "Fireman",
                Collections.singletonList("§7Não tome dano para fogo, nem lava."),
                "arcade.pvp.fireman",
                6500,
                0
        );
    }

    @Override
    public List<ItemStack> kitItems() {
        return Collections.emptyList();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFireman(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (hasKit(player.getUniqueId()) && (event.getCause().equals(EntityDamageEvent.DamageCause.LAVA) || event.getCause().name().contains("FIRE")))
                event.setCancelled(true);
        }
    }
}
