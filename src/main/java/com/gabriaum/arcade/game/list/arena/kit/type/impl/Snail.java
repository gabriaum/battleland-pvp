package com.gabriaum.arcade.game.list.arena.kit.type.impl;

import com.gabriaum.arcade.game.list.arena.kit.Kit;
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

public class Snail extends Kit implements Listener {

    public Snail() {
        super(
                Material.WEB,
                "Snail",
                Collections.singletonList("§7Deixe seus inimigos mais lentos ao encosta-los"),
                "arcade.pvp.snail",
                7500,
                0
        );
    }

    @Override
    public List<ItemStack> kitItems() {
        return Collections.emptyList();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSnail(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        if (hasKit(event.getDamager().getUniqueId()) && new Random().nextInt(4) == 0) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 0));
        }
    }
}
