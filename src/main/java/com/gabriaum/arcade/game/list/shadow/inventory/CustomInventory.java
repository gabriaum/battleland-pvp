package com.gabriaum.arcade.game.list.shadow.inventory;

import com.gabriaum.arcade.manager.impl.ShadowConfiguration;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.lib.commons.redis.JedisManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import redis.clients.jedis.Jedis;

public class CustomInventory extends AbstractInventoryMenu {

    protected final Player player;
    protected final Player target;

    public CustomInventory(Player player, Player target) {
        super("§b§lCUSTOMIZAR", 9 * 6);

        this.player = player;
        this.target = target;

        update();
    }

    @Override
    public void update() {
        for (int i = 0; i < 54; i++)
            inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 8).setDisplayName("§b§l-").create());

        inventory.setItem(43, new ItemBuilder(Material.WOOL, 5).setDisplayName("§a§lDesafiar Jogador").create());
        inventory.setItem(44, new ItemBuilder(Material.WOOL, 5).setDisplayName("§a§lDesafiar Jogador").create());
        inventory.setItem(52, new ItemBuilder(Material.WOOL, 5).setDisplayName("§a§lDesafiar Jogador").create());
        inventory.setItem(53, new ItemBuilder(Material.WOOL, 5).setDisplayName("§a§lDesafiar Jogador").create());

        inventory.setItem(20, new ItemBuilder(Material.WOOD_SWORD)
                .setDisplayName("§aEspada de Madeira")
                .addLore("§7Clique para alterar a espada do jogador.")
                .create());

        inventory.setItem(29, new ItemBuilder(Material.INK_SACK, 10)
                .setDisplayName("§aCom Sharpness")
                .addLore("§7Clique para alterar o encantamento da espada.")
                .create());

        inventory.setItem(21, new ItemBuilder(Material.WEB)
                .setDisplayName("§aSem Armadura")
                .addLore("§7Clique para alterar a armadura do jogador.")
                .create());

        inventory.setItem(22, new ItemBuilder(Material.MUSHROOM_SOUP)
                .setDisplayName("§aApenas hotbar")
                .addLore("§7Clique para alterar a quantidade de sopa do jogador.")
                .create());

        inventory.setItem(23, new ItemBuilder(Material.RED_MUSHROOM)
                .setDisplayName("§aRecraft de Cogumelo")
                .addLore("§7Clique para alterar o tipo de recraft do jogador.")
                .create());
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR))
            return;
        
        event.setCancelled(true);

        switch (event.getSlot()) {
            case 20: {
                Material material = event.getCurrentItem().getType();
                String name = "";

                switch (material) {
                    case WOOD_SWORD:
                        material = Material.STONE_SWORD;
                        name = "Pedra";
                        break;
                    case STONE_SWORD:
                        material = Material.IRON_SWORD;
                        name = "Ferro";
                        break;
                    case IRON_SWORD:
                        material = Material.GOLD_SWORD;
                        name = "Ouro";
                        break;
                    case GOLD_SWORD:
                        material = Material.DIAMOND_SWORD;
                        name = "Diamante";
                        break;
                    case DIAMOND_SWORD:
                        material = Material.WOOD_SWORD;
                        name = "Madeira";
                        break;
                }


                event.getInventory().setItem(20, new ItemBuilder(material)
                        .setDisplayName("§aEspada de " + name)
                        .addLore("§7Clique para alterar a espada do jogador.")
                        .create());

                player.updateInventory();
                break;
            }

            case 29: {
                String name = event.getCurrentItem().getItemMeta().getDisplayName();
                int durability = event.getCurrentItem().getDurability();

                switch (name) {
                    case "§aCom Sharpness":
                        name = "§aSem Sharpness";
                        durability = 8;
                        break;

                    case "§aSem Sharpness":
                        name = "§aCom Sharpness";
                        durability = 10;
                        break;
                }

                event.getInventory().setItem(29, new ItemBuilder(Material.INK_SACK, durability)
                        .setDisplayName(name)
                        .addLore("§7Clique para alterar o encantamento da espada.")
                        .create());

                player.updateInventory();
                break;
            }

            case 21: {
                Material material = event.getCurrentItem().getType();
                String name = "";

                switch (material) {
                    case WEB:
                        material = Material.LEATHER_CHESTPLATE;
                        name = "Couro";
                        break;
                    case LEATHER_CHESTPLATE:
                        material = Material.CHAINMAIL_CHESTPLATE;
                        name = "Corrente";
                        break;
                    case CHAINMAIL_CHESTPLATE:
                        material = Material.IRON_CHESTPLATE;
                        name = "Ferro";
                        break;
                    case IRON_CHESTPLATE:
                        material = Material.GOLD_CHESTPLATE;
                        name = "Ouro";
                        break;
                    case GOLD_CHESTPLATE:
                        material = Material.DIAMOND_CHESTPLATE;
                        name = "Diamante";
                        break;
                    case DIAMOND_CHESTPLATE:
                        material = Material.WEB;
                        name = "Sem Armadura";
                        break;
                }

                event.getInventory().setItem(21, new ItemBuilder(material)
                        .setDisplayName("§aArmadura de " + name)
                        .addLore("§7Clique para alterar a armadura do jogador.")
                        .create());

                player.updateInventory();
                break;
            }

            case 22: {
                String name = event.getCurrentItem().getItemMeta().getDisplayName();

                switch (name) {
                    case "§aApenas hotbar":
                        name = "§aInventário completo";
                        break;

                    case "§aInventário completo":
                        name = "§aApenas hotbar";
                        break;
                }

                event.getInventory().setItem(22, new ItemBuilder(Material.MUSHROOM_SOUP)
                        .setDisplayName(name)
                        .addLore("§7Clique para alterar a quantidade de sopa do jogador.")
                        .create());

                player.updateInventory();
                break;
            }

            case 23: {
                Material material = event.getCurrentItem().getType();
                String name = "";

                switch (material) {
                    case RED_MUSHROOM:
                        material = Material.INK_SACK;
                        name = "Cocoa";
                        break;

                    case INK_SACK:
                        material = Material.RED_MUSHROOM;
                        name = "Cogumelo";
                        break;
                }

                ItemBuilder item = new ItemBuilder(material)
                        .setDisplayName("§aRecraft de " + name)
                        .addLore("§7Clique para alterar o tipo de recraft do jogador.");

                if (material.equals(Material.INK_SACK))
                    item.setDurability((short) 3);

                event.getInventory().setItem(23, item.create());
                player.updateInventory();
                break;
            }
        }

        Inventory inventory = event.getInventory();
        ItemStack item = event.getCurrentItem();

        if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§a§lDesafiar Jogador")) {
            String sword = inventory.getItem(20).getItemMeta().getDisplayName().split("de ")[1];
            String armor = inventory.getItem(21).getItemMeta().getDisplayName().split("de ")[1];
            String refill = inventory.getItem(22).getItemMeta().getDisplayName();

            JedisManager manager = CorePlugin.getInstance().getJedisManager();
            ShadowConfiguration configuration = ShadowConfiguration.builder()
                    .swordMaterialName(sword)
                    .armorMaterialName(armor)
                    .onlyHotbar(refill.contains("hotbar"))
                    .mushroomRecraft(inventory.getItem(23).getType().equals(Material.RED_MUSHROOM))
                    .sharpness(inventory.getItem(29).getDurability() == 10)
                    .build();

            try (Jedis jedis = manager.getJedisPool().getResource()) {
                jedis.setex("shadow:config:" + player.getUniqueId(), 80, CorePlugin.GSON.toJson(configuration));
            }

            player.chat("/duel " + target.getName() + " custom");
            player.closeInventory();
            return;
        }
    }
}