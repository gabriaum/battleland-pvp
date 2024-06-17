package com.gabriaum.arcade.game.list.arena.kit.type.impl;

import com.gabriaum.arcade.game.list.arena.kit.Kit;
import com.solexgames.core.util.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Reaper extends Kit implements Listener {

    public Reaper() {
        super(
                Material.WOOD_HOE,
                "Reaper",
                Collections.singletonList("§7Ceife as almas dos seus inimigos."),
                "arcade.pvp.reaper",
                0,
                0
        );
    }

    @Override
    public List<ItemStack> kitItems() {
        return Collections.singletonList(new ItemBuilder(Material.WOOD_HOE).setDisplayName("§aReaper").create());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onReaper(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        if (hasKit(damager.getUniqueId()) && isKitItem(damager.getItemInHand()) && new Random().nextInt(4) == 0) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 4 * 20, 0));
        }
    }
}
