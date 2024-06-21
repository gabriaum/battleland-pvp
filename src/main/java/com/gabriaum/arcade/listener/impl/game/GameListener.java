package com.gabriaum.arcade.listener.impl.game;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.game.Game;
import com.gabriaum.arcade.game.type.GameType;
import com.gabriaum.arcade.user.User;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class GameListener implements Listener {

    @EventHandler
    public void connect(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());

        if (user == null)
            return;

        Game game = user.getGame();

        game.onJoin(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void damage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());

        if (user == null)
            return;

        event.setCancelled(user.isProtect());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void damageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());
        User userDamager = ArcadeMain.getPlugin().getUserManager().get(damager.getUniqueId());

        if (user == null || userDamager == null) {
            event.setCancelled(true);
            return;
        }

        if (userDamager.getOpponent() != null && !userDamager.getOpponent().equals(user)) {
            event.setCancelled(true);
            return;
        } else if  (user.getOpponent() != null && !user.getOpponent().equals(userDamager)) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(user.isProtect() || userDamager.isProtect());
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

        event.setCancelled(user.isProtect() || user.getKit().getKit().isKitItem(item.getItemStack()) || item.getItemStack().getType().name().contains("_SWORD") || item.getItemStack().getType().equals(Material.INK_SACK));

        if (!event.isCancelled()) {
            if (user.getGame().getType().equals(GameType.SHADOW)) {
                item.remove();
                return;
            }

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
    public void pickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());

        if (user == null)
            return;

        event.setCancelled(user.isProtect());
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
