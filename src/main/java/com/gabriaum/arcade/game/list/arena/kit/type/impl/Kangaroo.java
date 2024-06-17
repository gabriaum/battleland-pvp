package com.gabriaum.arcade.game.list.arena.kit.type.impl;

import com.gabriaum.arcade.game.list.arena.kit.Kit;
import com.solexgames.core.util.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public class Kangaroo extends Kit implements Listener {

    private final List<UUID> jumps = new ArrayList<>();

    public Kangaroo() {
        super(
                Material.FIREWORK,
                "Kangaroo",
                Arrays.asList("§7Use seu foguete para se", "§7locomover rapidamente pelo mapa", "§7com double-jumps."),
                "",
                0,
                8
        );
    }

    @Override
    public List<ItemStack> kitItems() {
        return Collections.singletonList(new ItemBuilder(Material.FIREWORK).setDisplayName("§aKangaroo").create());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {

            if (hasKit(event.getEntity().getUniqueId()) && event.getCause().equals(EntityDamageEvent.DamageCause.FALL) && event.getDamage() > 7.0D) {
                event.setDamage(7.0D);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) && !(event.getDamager() instanceof Player))

        if (hasKit(event.getEntity().getUniqueId())) {
            addCooldown(event.getEntity().getUniqueId());
        }
    }

    @EventHandler
    public void onJump(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (hasKit(player.getUniqueId()) && isKitItem(player.getItemInHand()) && isKitItem(player.getItemInHand())) {
            event.setCancelled(true);

            if (jumps.contains(player.getUniqueId()))
                return;

            if (hasCooldown(player))
                return;

            Vector vector = player.getEyeLocation().getDirection().multiply(player.isSneaking() ? 2.3F : 0.7f).setY(player.isSneaking() ? 0.5 : 1F);

            player.setFallDistance(-1.0F);
            player.setVelocity(vector);

            jumps.add(player.getUniqueId());
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onJumpRemove(PlayerMoveEvent event) {
        if (jumps.contains(event.getPlayer().getUniqueId())
                && (event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR
                || event.getPlayer().isOnGround())) {
            jumps.remove(event.getPlayer().getUniqueId());
        }
    }
}
