package com.gabriaum.arcade.game.list.arena.kit.type.impl;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.game.list.arena.kit.Kit;
import com.solexgames.core.util.builder.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Spider extends Kit implements Listener {
    public Spider() {
        super(
                Material.WEB,
                "Spider",
                Arrays.asList("§7Lance sua teia e prenda seus", "§7oponentes nela."),
                "arcade.pvp.spider",
                10000,
                25
        );
    }

    @Override
    public List<ItemStack> kitItems() {
        return Collections.singletonList(new ItemBuilder(Material.SNOW_BALL).setDisplayName("§aSpider").create());
    }

    @EventHandler
    public void onSpider(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction().name().contains("RIGHT_") && hasKit(player.getUniqueId()) && isKitItem(player.getItemInHand())) {
            event.setCancelled(true);
            player.updateInventory();

            if (hasCooldown(player))
                return;
            else
                addCooldown(player.getUniqueId());

            Snowball snowball = player.launchProjectile(Snowball.class);

            snowball.setMetadata("Spiderball", new FixedMetadataValue(ArcadeMain.getPlugin(), true));
            snowball.setVelocity(player.getLocation().getDirection().multiply(1.5));
        }
    }

    @EventHandler
    public void onSpiderTarget(ProjectileHitEvent event) {
        if (event.getEntity().hasMetadata("Spiderball")) {
            List<Block> webs = new ArrayList<>();
            Location location = event.getEntity().getLocation();

            int x = new Random().nextInt(2) - 1;
             int z = new Random().nextInt(2) - 1;

            for (int y = 0; y < 2; ++y) {
                for (int xx = 0; xx < 2; ++xx) {
                    for (int zz = 0; zz < 2; ++zz) {
                        Block block = location.clone().add((double)(x + xx), (double)y, (double)(z + zz)).getBlock();
                        if (block.getType() == Material.AIR) {
                            block.setType(Material.WEB);

                            webs.add(block);
                        }
                    }
                }
            }

            event.getEntity().remove();

            new BukkitRunnable() {
                public void run() {
                    for (final Block web : webs) {
                        web.setType(Material.AIR);
                    }
                }
            }.runTaskLater(ArcadeMain.getPlugin(), 200L);
        }
    }
}
