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

public class Ajnin extends Kit implements Listener {

    private final Map<UUID, Ninja.NinjaHit> ajninHitMap;

    public Ajnin() {
        super(
                Material.NETHER_STAR,
                "Ajnin",
                Collections.singletonList("§7Teletransporte seus inimigos para você."),
                "arcade.pvp.ajnin",
                14000,
                5
        );

        ajninHitMap = new HashMap<>();
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
            Ninja.NinjaHit hit = ajninHitMap.get(damager.getUniqueId());

            if (event.getFinalDamage() >= player.getHealth() && hit != null) {
                ajninHitMap.remove(damager.getUniqueId());
                return;
            }

            if (hit == null) {
                hit = new Ninja.NinjaHit(player);

                ajninHitMap.put(damager.getUniqueId(), hit);
            } else
                hit.update(player);
        }
    }

    @EventHandler
    public void onAjninUse(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (event.isSneaking() && hasKit(player.getUniqueId()) && ajninHitMap.containsKey(player.getUniqueId())) {
            Ninja.NinjaHit hit = ajninHitMap.get(player.getUniqueId());
            Player attacked = hit.getAttacked();

            if (attacked.isDead())
                return;

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

            attacked.teleport(player.getLocation());

            player.sendMessage("§a§lAJNIN §fVocê teleportou o jogador §e" + attacked.getName() + " §fpara você!");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ajninHitMap.remove(event.getPlayer().getUniqueId());
    }
}
