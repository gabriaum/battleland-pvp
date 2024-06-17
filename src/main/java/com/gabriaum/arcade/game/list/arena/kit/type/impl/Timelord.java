package com.gabriaum.arcade.game.list.arena.kit.type.impl;

import com.gabriaum.arcade.game.list.arena.kit.Kit;
import com.gabriaum.arcade.user.User;
import com.solexgames.core.util.builder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Timelord extends Kit implements Listener {

    private final Map<UUID, Long> timeLordMap = new HashMap<>();

    public Timelord() {
        super(
                Material.WATCH,
                "Timelord",
                Collections.singletonList("§7Pare o tempo com o seu relógio."),
                "arcade.pvp.stomper",
                11700,
                20
        );
    }

    @Override
    public List<ItemStack> kitItems() {
        return Collections.singletonList(new ItemBuilder(Material.WATCH).setDisplayName("§aTimelord").create());
    }

    @EventHandler
    public void onTimelord(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (hasKit(player.getUniqueId()) && isKitItem(player.getItemInHand())) {
            event.setCancelled(true);

            if (hasCooldown(player))
                return;
            else
                addCooldown(player.getUniqueId());

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.equals(player))
                    continue;

                User user = getInstance().getUserManager().get(onlinePlayer.getUniqueId());

                if (user == null || user.isProtect())
                    continue;

                double distance = player.getLocation().distance(onlinePlayer.getLocation());

                if (distance <= 5)
                    timeLordMap.put(onlinePlayer.getUniqueId(), System.currentTimeMillis() + 4000L);
            }

            player.getLocation().getWorld().playEffect(player.getLocation(), Effect.LARGE_SMOKE, 30, 30);
            player.playSound(player.getLocation(), Sound.WITHER_SHOOT, 1.0F, 1.0F);

            player.sendMessage("§e§lTIMELORD §fVocê parou o tempo por 4 segundos!");
        }
    }

    @EventHandler
    public void onTimelordMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (timeLordMap.containsKey(player.getUniqueId())) {
            if (timeLordMap.get(player.getUniqueId()) > System.currentTimeMillis())
                event.setCancelled(true);
            else
                timeLordMap.remove(player.getUniqueId());
        }
    }
}
