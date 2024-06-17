package com.gabriaum.arcade.game.list.arena.kit.type.impl;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.game.list.arena.kit.Kit;
import com.solexgames.core.util.builder.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Phantom extends Kit implements Listener {

    private final ArrayList<UUID> flying = new ArrayList<>();
    private final HashMap<UUID, ItemStack[]> previous = new HashMap<>();

    public Phantom() {
        super(
                Material.FEATHER,
                "Phantom",
                Arrays.asList("§7Tenha a habilidade de voar."),
                "arcade.pvp.phantom",
                15000,
                25
        );
    }

    @Override
    public List<ItemStack> kitItems() {
        return Collections.singletonList(new ItemBuilder(Material.FEATHER).setDisplayName("§aPhantom").create());
    }

    @EventHandler
    public void onPhantom(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (hasKit(player.getUniqueId()) && isKitItem(player.getItemInHand()) && event.getAction().name().contains("RIGHT")) {
            event.setCancelled(true);

            if (!this.flying.contains(player.getUniqueId())) {
                if (hasCooldown(player))
                    return;
                else
                    addCooldown(player.getUniqueId());
                previous.put(player.getUniqueId(), player.getInventory().getArmorContents());
                flying.add(player.getUniqueId());

                player.setAllowFlight(true);
                player.setFlying(true);

                player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setColor(Color.AQUA).create());
                player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).setColor(Color.AQUA).create());
                player.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).setColor(Color.AQUA).create());
                player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setColor(Color.AQUA).create());

                player.updateInventory();
                for (int i = 0; i < 6; ++i) {
                    final int current = i;
                    new BukkitRunnable() {
                        public void run() {
                            if (current == 5) {
                                if (flying.contains(player.getUniqueId())) {
                                    flying.remove(player.getUniqueId());
                                    player.sendMessage("§5§lPHANTON§f Acabou o tempo de §9§lVÔO");
                                }

                                player.setAllowFlight(false);
                                player.setFlying(false);

                                if (previous.containsKey(player.getUniqueId())) {
                                    player.getInventory().setArmorContents(previous.get(player.getUniqueId()));
                                    player.updateInventory();

                                    previous.remove(player.getUniqueId());
                                }
                            } else if (flying.contains(player.getUniqueId())) {
                                player.sendMessage("§b§lPHANTOM§f Você não voará mais em §9§l" + convert(current) + " SEGUNDOS...");
                            }
                        }
                    }.runTaskLater(ArcadeMain.getPlugin(), (long) (i * 20));
                }
            }
        }
    }
    public int convert(final int a) {
        if (a == 0)
            return 5;

        if (a == 1)
            return 4;

        if (a == 2)
            return 3;

        if (a == 3)
            return 2;

        if (a == 4)
            return 1;

        return a;
    }
}
