package com.gabriaum.arcade.game.list.arena.kit.type.impl;

import com.gabriaum.arcade.game.list.arena.kit.Kit;
import org.bukkit.Effect;
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

public class Viper extends Kit implements Listener {

    public Viper() {
        super(
                Material.SPIDER_EYE,
                "Viper",
                Collections.singletonList("§7Envene os seus inimigos."),
                "arcade.pvp.viper",
                0,
                0
        );
    }

    @Override
    public List<ItemStack> kitItems() {
        return Collections.emptyList();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onViper(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        if (hasKit(player.getUniqueId())) return;

        if (hasKit(damager.getUniqueId())) {
            if (new Random().nextInt(5) == 2) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 300, 2));
                player.getLocation().getWorld().playEffect(player.getLocation().clone().add(0.0, 0.4, 0.0), Effect.STEP_SOUND, 159, 13);
            }
        }
    }
}