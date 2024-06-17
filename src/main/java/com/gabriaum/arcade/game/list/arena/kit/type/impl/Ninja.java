package com.gabriaum.arcade.game.list.arena.kit.type.impl;

import com.gabriaum.arcade.game.list.arena.kit.Kit;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Ninja extends Kit implements Listener {

    private final Map<UUID, NinjaHit> ninjaHitMap;

    public Ninja() {
        super(
                Material.EMERALD,
                "Ninja",
                Collections.singletonList("§7Teletransporte-se para as costas do seu inimigo."),
                "arcade.pvp.ninja",
                14000,
                5
        );

        ninjaHitMap = new HashMap<>();
    }

    @Override
    public List<ItemStack> kitItems() {
        return Collections.emptyList();
    }

    @EventHandler
    public void onNinja(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        if (hasKit(damager.getUniqueId())) {
            NinjaHit hit = ninjaHitMap.get(damager.getUniqueId());

            if (event.getFinalDamage() >= player.getHealth() && hit != null) {
                ninjaHitMap.remove(damager.getUniqueId());
                return;
            }

            if (hit == null) {
                hit = new NinjaHit(player);

                ninjaHitMap.put(damager.getUniqueId(), hit);
            } else
                hit.update(player);
        }
    }

    @EventHandler
    public void onNinjaUse(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (event.isSneaking() && hasKit(player.getUniqueId()) && ninjaHitMap.containsKey(player.getUniqueId())) {
            NinjaHit hit = ninjaHitMap.get(player.getUniqueId());

            Player attacked = hit.getAttacked();

            if (attacked.isDead()) return;

            if (hit.getDuration() < System.currentTimeMillis())
                return;

            if (player.getLocation().distance(attacked.getLocation()) > 30) {
                player.sendMessage("§cO alvo está muito longe!");
                return;
            }

            if (hasCooldown(player))
                return;
            else
                addCooldown(player.getUniqueId());

            player.teleport(attacked.getLocation());
            player.sendMessage("§a§lNINJA §fTeleportado para §e" + attacked.getName() + "§f!");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ninjaHitMap.remove(event.getPlayer().getUniqueId());
    }

    @Getter
    public static class NinjaHit {

        private Player attacked;

        private long duration;

        public NinjaHit(Player attacked) {
            this.attacked = attacked;

            this.duration = System.currentTimeMillis() + 15000L;
        }

        public void update(Player attacked) {
            this.attacked = attacked;

            this.duration = System.currentTimeMillis() + 20000L;
        }
    }
}
