package com.gabriaum.arcade.game.list.arena.kit.type.impl;

import com.gabriaum.arcade.game.list.arena.kit.Kit;
import com.gabriaum.arcade.user.User;
import com.solexgames.core.util.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Monk extends Kit implements Listener {

    public Monk() {
        super(
                Material.BLAZE_ROD,
                "Monk",
                Collections.singletonList("§7Bagunçe o inventário de seus inimigos."),
                "arcade.pvp.monk",
                8500,
                15
        );
    }

    @Override
    public List<ItemStack> kitItems() {
        return Collections.singletonList(new ItemBuilder(Material.BLAZE_ROD).setDisplayName("§aMonk").create());
    }

    @EventHandler
    public void onMonk(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {
            Player player = event.getPlayer(), clicked = (Player) event.getRightClicked();
            User clickedUser = getInstance().getUserManager().get(clicked.getUniqueId());

            if (clickedUser == null || clickedUser.isProtect())
                return;

            PlayerInventory inventory = clicked.getInventory();
            ItemStack item = player.getItemInHand();

            if (hasKit(player.getUniqueId()) && isKitItem(item)) {
                if (hasCooldown(player))
                    return;
                else
                    addCooldown(player.getUniqueId());

                int randomSlot = new Random().nextInt(36);

                ItemStack currentItem = clicked.getItemInHand() != null ? clicked.getItemInHand().clone() : null;
                ItemStack randomItem = inventory.getItem(randomSlot) != null ? inventory.getItem(randomSlot) : null;

                inventory.setItem(randomSlot, currentItem);
                inventory.setItemInHand(randomItem);

                player.sendMessage("§9Monk > §eVocê bagunçou o inventário de §b" + clicked.getName() + "§e!");
                clicked.sendMessage("§9Monk > §b" + player.getName() + "§e bagunçou o seu inventário!");

                clicked.playSound(clicked.getLocation(), Sound.WOLF_HURT, 0.15f, 1.0f);
            }
        }
    }
}
