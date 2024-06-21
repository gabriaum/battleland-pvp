package com.gabriaum.arcade.game.list.arena.kit.type.impl;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.game.list.arena.kit.Kit;
import com.gabriaum.arcade.game.list.arena.kit.type.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Hulk extends Kit implements Listener {
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
        Player player = event.getPlayer();
        Player target = (Player) event.getRightClicked();

        if (target == null)
            return;

        if (hasKit(player.getUniqueId()) && (player.getItemInHand().getType().equals(Material.AIR) || player.getItemInHand().getType() == null)) {
            if (hasCooldown(player))
                return;
            else
                addCooldown(player.getUniqueId());

            player.setPassenger(target);
        }
    }

    @EventHandler
    public void noHulkMor(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player) event.getEntity();
            Player hulk = (Player) event.getDamager();

            if (hulk.getPassenger() != null && hulk.getPassenger() == player && hasKit(hulk.getUniqueId()) && hulk.getPassenger() == player) {
                event.setCancelled(true);
                player.setSneaking(true);

                Vector v = player.getEyeLocation().getDirection().multiply(1.5F);
                v.setY(0.6D);
                player.setVelocity(v);

                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ArcadeMain.getPlugin(), () -> player.setSneaking(false), 10L);
            }
        }
    }
}
