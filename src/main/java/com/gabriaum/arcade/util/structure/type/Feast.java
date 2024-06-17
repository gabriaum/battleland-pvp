package com.gabriaum.arcade.util.structure.type;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.util.structure.Structure;
import com.google.gson.JsonObject;
import com.solexgames.core.CorePlugin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter
@RequiredArgsConstructor
public class Feast extends Structure {

    protected final Location location;
    protected final List<Chest> chests = new ArrayList<>();

    public Feast() {
        JsonObject arenaObject = ArcadeMain.getPlugin().getMap().getAsJsonObject("arena");

        if (arenaObject.has("feast-center"))
            location = CorePlugin.GSON.fromJson(arenaObject.get("feast-center"), Location.class);
        else
            location = null;
    }

    @Override
    public void generateChest() {
        if (location == null) {
            System.out.println("Feast location is null!");
            return;
        }

        Location enchantment = location.clone().add(0, 1, 0);

        enchantment.getBlock().setType(Material.ENCHANTMENT_TABLE);
        enchantment.getWorld().strikeLightningEffect(enchantment);

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (!(x == 0 && z == 0) && ((x % 2 == 0 && z % 2 == 0) || (x % 2 != 0 && z % 2 != 0))) {
                    Location loc = location.clone().add(x, 1, z);

                    loc.getBlock().setType(Material.CHEST);
                    chests.add((Chest) loc.getBlock().getState());
                }
            }
        }

        Iterator<ItemStack> iterator = getItems().iterator();
        Random random = new Random();
        int chestNumber = 0;

        while (iterator.hasNext() && !chests.isEmpty()) {
            if (chestNumber >= chests.size())
                chestNumber = 0;

            Chest chest = chests.get(chestNumber);

            if (chest == null) {
                chestNumber = 0;
                continue;
            }

            Inventory inv = chest.getBlockInventory();

            if (inv.firstEmpty() == -1) {
                chests.remove(chestNumber);
                continue;
            }

            ItemStack item = iterator.next();
            iterator.remove();

            int amount = item.getAmount();
            while (amount > 0 && !chests.isEmpty()) {
                if (chestNumber >= chests.size())
                    chestNumber = 0;

                chest = chests.get(chestNumber);
                inv = chest.getBlockInventory();

                if (inv.firstEmpty() == -1) {
                    chests.remove(chestNumber);
                    continue;
                }

                int slot;
                do {
                    slot = random.nextInt(inv.getSize());
                } while (inv.getItem(slot) != null);

                ItemStack singleItemStack = item.clone();
                singleItemStack.setAmount(1);
                inv.setItem(slot, singleItemStack);
                amount--;

                chest.update();
                ++chestNumber;
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Chest chest : chests) {
                    chest.getBlockInventory().clear();
                    chest.getInventory().clear();

                    chest.getBlock().setType(Material.AIR);
                    enchantment.getBlock().setType(Material.AIR);
                }

                Bukkit.broadcastMessage("§e§lFEAST§f O feast desapareceu!");
                chests.clear();
                cancel();
            }
        }.runTaskLater(ArcadeMain.getPlugin(), 20 * 30);
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();

        items.addAll(addItem(Material.IRON_SWORD, 0, 2));
        items.addAll(addItem(Material.LEATHER_HELMET, 0, 1));
        items.addAll(addItem(Material.LEATHER_CHESTPLATE, 0, 1));
        items.addAll(addItem(Material.MUSHROOM_SOUP, 1, 2));
        items.addAll(addItem(Material.ENDER_PEARL, 2, 4));
        items.addAll(addItem(Material.FISHING_ROD, 0, 1));
        items.addAll(addItem(Material.SNOW_BALL, 6, 10));
        items.addAll(addItem(Material.ARROW, 12, 36));
        items.addAll(addItem(Material.BOW, 1, 2));
        items.addAll(addItem(Material.EXP_BOTTLE, 2, 4));
        items.addAll(addItem(Material.GOLDEN_APPLE, 2, 5));
        items.addAll(addItem(Material.POTION, (short) 16385, 0, 2));
        items.addAll(addItem(Material.POTION, (short) 16389, 0, 2));

        Collections.shuffle(items);

        return items;
    }

    protected List<ItemStack> addItem(Material mat, int min, int max) {
        return addItem(mat, (short) 0, min, max);
    }

    protected List<ItemStack> addItem(Material mat, short durability, int min, int max) {
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i <= min + new Random().nextInt(max - min); i++)
            items.add(new ItemStack(mat, 1, durability));

        return items;
    }

    @Getter
    @AllArgsConstructor
    public static class Item {
        private Material material;
        private short data;

        private int min;
        private int max;

        private int chance;
    }
}
