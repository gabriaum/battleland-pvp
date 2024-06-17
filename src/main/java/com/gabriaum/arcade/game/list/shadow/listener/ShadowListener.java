package com.gabriaum.arcade.game.list.shadow.listener;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.game.list.arena.inventory.KitInventory;
import com.gabriaum.arcade.game.list.arena.inventory.WarpInventory;
import com.gabriaum.arcade.game.list.shadow.inventory.CustomInventory;
import com.gabriaum.arcade.manager.QueueManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShadowListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void duel(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (event.getRightClicked() instanceof Player) {
            Player target = (Player) event.getRightClicked();

            if (target != null) {
                if (player.getItemInHand().getType().equals(Material.BLAZE_ROD)) {
                    player.chat("/duel " + target.getName());
                    return;
                }

                if (player.getItemInHand().getType().equals(Material.IRON_FENCE)) {
                    new CustomInventory(player, target).open(player);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void interact(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction().name().contains("RIGHT_")) {
            ItemStack item = event.getItem();

            if (item == null)
                return;

            ItemMeta meta = item.getItemMeta();

            if (meta != null && meta.hasDisplayName()) {
                if (meta.getDisplayName().contains("Batalha rápida")) {
                    QueueManager queue = ArcadeMain.getPlugin().getQueueManager();

                    if (queue.contains(player.getUniqueId())) {
                        meta.setDisplayName("§b§lBatalha rápida §7(§c§lOFF§7)");
                        item.setDurability((short) 8);

                        queue.remove(player.getUniqueId());
                    } else {
                        meta.setDisplayName("§b§lBatalha rápida §7(§a§lON§7)");
                        item.setDurability((short) 10);

                        queue.add(player.getUniqueId());
                    }

                    item.setItemMeta(meta);
                    player.setItemInHand(item);
                    return;
                }
            }
        }
    }
}
