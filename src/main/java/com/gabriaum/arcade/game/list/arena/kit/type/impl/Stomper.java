package com.gabriaum.arcade.game.list.arena.kit.type.impl;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.game.list.arena.kit.Kit;
import com.gabriaum.arcade.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Stomper extends Kit implements Listener {

    public Stomper() {
        super(
                Material.IRON_BOOTS,
                "Stomper",
                Arrays.asList("§7Seja como uma grande bigorna", "§7no ar e mate seus adversários", "§7esmagados."),
                "arcade.pvp.stomper",
                12000,
                0
        );
    }

    @Override
    public List<ItemStack> kitItems() {
        return Collections.emptyList();
    }

    @EventHandler
    public void onStomper(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (hasKit(player.getUniqueId())) {
                if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                    boolean hasPlayer = false;

                    for (Entity entity : player.getNearbyEntities(6.0, 3.0, 6.0)) {
                        if (!(entity instanceof Player))
                            continue;

                        Player nearby = (Player) entity;
                        User user = ArcadeMain.getPlugin().getUserManager().get(nearby.getUniqueId());

                        if (user == null || user.isProtect())
                            continue;

                        if (nearby.isSneaking()) {
                            nearby.damage(4.0D, player);
                        } else {
                            if (nearby.getHealth() - event.getDamage() > 0.0D) {
                                nearby.damage(event.getDamage());
                            } else {
                                nearby.damage(event.getDamage() * 2.0, player);
                            }
                        }

                        player.sendMessage("§6§lSTOMPER§f Você §e§lSTOMPOU§f o §e" + nearby.getName());
                        nearby.sendMessage("§6§lSTOMPER§f Você foi §e§lSTOMPADO§f pelo §e" + player.getName());
                        hasPlayer = true;
                    }

                    event.setDamage(0);

                    if (hasPlayer)
                        player.getWorld().playSound(player.getLocation(), Sound.ANVIL_LAND, 1, 1);
                }
            }
        }
    }
}
