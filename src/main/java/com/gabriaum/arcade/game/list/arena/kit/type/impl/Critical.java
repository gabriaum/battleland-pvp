package com.gabriaum.arcade.game.list.arena.kit.type.impl;

import com.gabriaum.arcade.game.list.arena.kit.Kit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Critical extends Kit implements Listener {
    public Critical() {
        super(
                Material.REDSTONE_BLOCK,
                "Critical",
                Arrays.asList("§7A cada hit tenha 30% de um", "§7critical aumentado em 3x."),
                "arcade.pvp.anchor",
                12000,
                0
        );
    }

    @Override
    public List<ItemStack> kitItems() {
        return Collections.emptyList();
    }

    @EventHandler
    public void onCritical(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player entity = (Player) event.getEntity();

            if (hasKit(damager.getUniqueId())) {
                if (damager.getInventory().getItemInHand() != null) {
                    if (Math.random() <= 0.3) {
                        event.setDamage(event.getDamage() + 1.5);
                        entity.getWorld().playEffect(entity.getLocation(), Effect.STEP_SOUND, (Object) Material.REDSTONE_BLOCK, 10);

                        damager.sendMessage("§c§lCRITICAL§f Você acertou um hit crítico!");
                        entity.sendMessage("§c§lCRITICAL§f Você foi acertado por um hit crítico!");
                    }
                }
            }
        }
    }
}
