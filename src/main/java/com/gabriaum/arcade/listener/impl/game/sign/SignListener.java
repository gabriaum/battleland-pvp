package com.gabriaum.arcade.listener.impl.game.sign;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SignListener implements Listener {

    @EventHandler
    public void sign(SignChangeEvent event) {
        if (event.getLine(0).equals("facil")){
            event.setLine(0, "§6§m>-----<");
            event.setLine(1, "§6§lBattleLand");
            event.setLine(2, "§a§lFácil");
            event.setLine(3, "§6§m>-----<");
        } else if (event.getLine(0).contains("medio")){
            event.setLine(0, "§6§m>-----<");
            event.setLine(1, "§6§lBattleLand");
            event.setLine(2, "§e§lMédio");
            event.setLine(3, "§6§m>-----<");
        } else if (event.getLine(0).contains("dificil")){
            event.setLine(0, "§6§m>-----<");
            event.setLine(1, "§6§lBattleLand");
            event.setLine(2, "§c§lDifícil");
            event.setLine(3, "§6§m>-----<");
        } else if (event.getLine(0).contains("extremo")){
            event.setLine(0, "§6§m>-----<");
            event.setLine(1, "§6§lBattleLand");
            event.setLine(2, "§4§lExtremo");
            event.setLine(3, "§6§m>-----<");
        }
    }

    @EventHandler
    public void refill(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        /* frame */
        if (entity instanceof ItemFrame) {
            ItemFrame itemFrame = (ItemFrame) entity;
            ItemStack item = itemFrame.getItem();

            switch (item.getType()) {
                case MUSHROOM_SOUP: {
                    event.setCancelled(true);

                    Inventory inventory = Bukkit.createInventory(player, 9 * 6, "§b§lSopa");

                    for (int i = 0; i < (9 * 6); i++)
                        inventory.setItem(i, new ItemStack(Material.MUSHROOM_SOUP));

                    player.openInventory(inventory);
                    break;
                }

                case BOWL: {
                    event.setCancelled(true);

                    Inventory inventory = Bukkit.createInventory(player, 9 * 6, "§b§lRecraft");

                    for (int i = 0; i < (9 * 6); i++)
                        inventory.setItem(i, new ItemStack(Material.BOWL, 64));

                    player.openInventory(inventory);
                    break;
                }

                case RED_MUSHROOM: {
                    event.setCancelled(true);

                    Inventory inventory = Bukkit.createInventory(player, 9 * 6, "§b§lRecraft");

                    for (int i = 0; i < (9 * 6); i++)
                        inventory.setItem(i, new ItemStack(Material.RED_MUSHROOM, 64));

                    player.openInventory(inventory);
                    break;
                }

                case BROWN_MUSHROOM: {
                    event.setCancelled(true);

                    Inventory inventory = Bukkit.createInventory(player, 9 * 6, "§b§lRecraft");

                    for (int i = 0; i < (9 * 6); i++)
                        inventory.setItem(i, new ItemStack(Material.BROWN_MUSHROOM, 64));

                    player.openInventory(inventory);
                    break;
                }
            }
        }
    }
}
