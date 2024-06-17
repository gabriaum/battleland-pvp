package com.gabriaum.arcade.listener.impl.game;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.game.Game;
import com.gabriaum.arcade.user.User;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class GameListener implements Listener {

    @EventHandler
    public void connect(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());

        if (user == null)
            return;

        Game game = user.getGame();

        game.onJoin(player);
    }

    @EventHandler
    public void damage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());

        if (user == null)
            return;

        event.setCancelled(user.isProtect());
    }

    @EventHandler
    public void drop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());

        if (user == null)
            return;

        Item item = event.getItemDrop();

        if (item == null)
            return;

        System.out.println("Dropped item: " + item.getItemStack().getType().name());

        event.setCancelled(user.isProtect() || user.getKit().getKit().isKitItem(item.getItemStack()) || item.getType().equals(Material.COMPASS) || item.getType().name().contains("_SWORD"));

        if (!event.isCancelled()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (item.getItemStack().getType().equals(Material.AIR) || item.isDead()) {
                        cancel();
                        return;
                    }

                    item.getLocation().getWorld().playEffect(item.getLocation(), Effect.WITCH_MAGIC, 30, 30);
                    item.remove();
                }
            }.runTaskLater(ArcadeMain.getPlugin(), 20 * 3);
        }
    }

    @EventHandler
    public void deathDropClear(PlayerDeathEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (ItemStack item : event.getDrops())
                    item.setType(Material.AIR);
            }
        }.runTaskLater(ArcadeMain.getPlugin(), 20 * 3);
    }

    @EventHandler
    public void jump(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Block block = event.getTo().getBlock();
        Location location = block.getLocation();

        location.setY(location.getY() - 1.0);

        Block block2 = location.getBlock();

        if (block2.getType() == Material.SPONGE) {
            player.setFallDistance(-50.0f);
            player.setVelocity(new Vector(0, 5, 0));
            player.setFallDistance(-50.0f);

            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
        }
    }

    @EventHandler
    public void food(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent event) {
        event.setCancelled(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE));
    }

    @EventHandler
    public void placeBlock(BlockPlaceEvent event) {
        event.setCancelled(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE));
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM))
            event.setCancelled(true);
    }
}
