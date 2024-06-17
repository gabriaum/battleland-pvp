package com.gabriaum.arcade.game.list.arena.listener;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.event.type.arena.RewardEvent;
import com.gabriaum.arcade.game.Game;
import com.gabriaum.arcade.game.type.GameType;
import com.gabriaum.arcade.manager.RewardManager;
import com.gabriaum.arcade.user.User;
import com.gabriaum.arcade.util.Util;
import com.solexgames.core.util.builder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class RewardListener implements Listener {

    @EventHandler
    public void reward(RewardEvent event) {
        User user = event.getUser();
        Player player = Bukkit.getPlayer(user.getUniqueId());
        PlayerInventory inventory = player.getInventory();
        Game game = user.getGame();

        if (!game.getType().equals(GameType.ARENA))
            return;

        switch (event.getStreak()) {
            case 10: {
                inventory.addItem(new ItemStack(Material.GOLDEN_APPLE, 10));
                break;
            }

            case 20: {
                inventory.addItem(new ItemBuilder(Material.MUSHROOM_SOUP).setAmount(2)
                        .setDisplayName("§bSopa Encantada")
                        .create());

                break;
            }

            case 30: {
                inventory.addItem(new ItemBuilder(Material.MONSTER_EGG, 95)
                        .setDisplayName("§cCão de Ataque")
                        .create());

                break;
            }

            case 40: {
                Zombie zombie = player.getWorld().spawn(player.getLocation(), Zombie.class);

                ItemStack attack = new ItemBuilder(Material.STICK).setEnchant(Enchantment.DAMAGE_ALL, 1).create();
                ItemStack head = new ItemBuilder(Material.SKULL_ITEM, 3).setOwner(player.getName()).create();

                zombie.setCustomName("§bSegurança: " + player.getName());
                zombie.setMaxHealth(200D);
                zombie.setHealth(200);
                zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3, true, false));
                zombie.setMetadata("owner", new FixedMetadataValue(ArcadeMain.getPlugin(), player.getName()));

                zombie.getEquipment().setHelmet(head);
                zombie.getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
                zombie.getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
                zombie.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
                zombie.getEquipment().setItemInHand(attack);

                RewardManager.getZombieGuards().put(player.getUniqueId(), zombie);
                break;
            }

            case 50: {
                Zombie zombie = RewardManager.getZombieGuards().get(player.getUniqueId());

                if (zombie == null || zombie.isDead())
                    return;

                ItemStack attack = new ItemBuilder(Material.STICK).setEnchant(Enchantment.DAMAGE_ALL, 2).create();

                zombie.getEquipment().setItemInHand(attack);
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void interact(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null)
            return;

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (item.hasItemMeta()) {
                if (item.getType().equals(Material.MUSHROOM_SOUP) && item.getItemMeta().getDisplayName().equalsIgnoreCase("§bSopa Encantada")) {
                    event.setCancelled(true);

                    player.setHealth(20.0);
                    player.getInventory().remove(item);
                    return;
                }

                if (item.getType().equals(Material.MONSTER_EGG) && item.getItemMeta().getDisplayName().equalsIgnoreCase("§cCão de Ataque")) {
                    event.setCancelled(true);

                    List<Wolf> wolves = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {
                        Wolf wolf = player.getWorld().spawn(player.getLocation(), Wolf.class);

                        wolf.setCustomName("§c§lCão de Ataque");
                        wolf.setCustomNameVisible(true);
                        wolf.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, true, false));
                        wolf.setOwner(player);
                        wolf.setTamed(true);

                        wolves.add(wolf);
                    }

                    RewardManager.getWolves().put(player.getUniqueId(), wolves);
                    player.getInventory().remove(item);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void damage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Zombie && event.getDamager() instanceof Player) {
            Zombie zombie = (Zombie) event.getEntity();
            Player player = (Player) event.getDamager();

            if (zombie.hasMetadata("owner")) {
                String owner = zombie.getMetadata("owner").get(0).asString();

                if (player.getName().equals(owner))
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void guardian(EntityTargetLivingEntityEvent event) {
        if (event.getEntity() instanceof Zombie) {
            Zombie zombie = (Zombie) event.getEntity();
            String owner = zombie.getMetadata("owner").get(0).asString();

            if (event.getTarget() instanceof Player) {
                Player target = (Player) event.getTarget();
                Player near = Util.findNearestPlayer(target, zombie);

                if (target.getName().equals(owner))
                    event.setCancelled(true);

                if (near != null)
                    zombie.setTarget(near);
            }
        }

        if (event.getEntity() instanceof Wolf) {
            Wolf wolf = (Wolf) event.getEntity();
            Player owner = (Player) wolf.getOwner();

            if (event.getTarget() instanceof Player) {
                Player target = (Player) event.getTarget();
                Player near = Util.findNearestPlayer(target, wolf);

                if (target.getName().equals(owner.getName()))
                    event.setCancelled(true);

                if (near != null)
                    wolf.setTarget(near);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void move(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (RewardManager.getZombieGuards().containsKey(player.getUniqueId())) {
            Zombie zombie = RewardManager.getZombieGuards().get(player.getUniqueId());

            if (zombie == null || zombie.isDead()) {
                RewardManager.getZombieGuards().remove(player.getUniqueId());
                return;
            }

            if (player.getLocation().distance(zombie.getLocation()) > 10)
                zombie.teleport(player.getLocation());
        }
    }

    @EventHandler
    public void guardDead(EntityDeathEvent event) {
        if (event.getEntity() instanceof Zombie) {
            Zombie zombie = (Zombie) event.getEntity();

            if (zombie.hasMetadata("owner")) {
                String owner = zombie.getMetadata("owner").get(0).asString();
                Player player = Bukkit.getPlayer(owner);

                if (player != null)
                    RewardManager.getZombieGuards().remove(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void death(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (event.getFinalDamage() >= player.getHealth()) {
                if (RewardManager.getZombieGuards().containsKey(player.getUniqueId())) {
                    Zombie zombie = RewardManager.getZombieGuards().get(player.getUniqueId());

                    if (zombie != null && !zombie.isDead())
                        zombie.remove();

                    RewardManager.getZombieGuards().remove(player.getUniqueId());
                }

                if (RewardManager.getWolves().containsKey(player.getUniqueId())) {
                    for (Wolf wolf : RewardManager.getWolves().get(player.getUniqueId())) {
                        if (wolf != null && !wolf.isDead())
                            wolf.remove();
                    }

                    RewardManager.getWolves().remove(player.getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void disconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(null);

        if (RewardManager.getZombieGuards().containsKey(player.getUniqueId())) {
            Zombie zombie = RewardManager.getZombieGuards().get(player.getUniqueId());

            if (zombie != null && !zombie.isDead())
                zombie.remove();

            RewardManager.getZombieGuards().remove(player.getUniqueId());
        }

        if (RewardManager.getWolves().containsKey(player.getUniqueId())) {
            for (Wolf wolf : RewardManager.getWolves().get(player.getUniqueId())) {
                if (wolf != null && !wolf.isDead())
                    wolf.remove();
            }

            RewardManager.getWolves().remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void disconnect(PlayerKickEvent event) {
        Player player = event.getPlayer();

        if (RewardManager.getZombieGuards().containsKey(player.getUniqueId())) {
            Zombie zombie = RewardManager.getZombieGuards().get(player.getUniqueId());

            if (zombie != null && !zombie.isDead())
                zombie.remove();

            RewardManager.getZombieGuards().remove(player.getUniqueId());
        }

        if (RewardManager.getWolves().containsKey(player.getUniqueId())) {
            for (Wolf wolf : RewardManager.getWolves().get(player.getUniqueId())) {
                if (wolf != null && !wolf.isDead())
                    wolf.remove();
            }

            RewardManager.getWolves().remove(player.getUniqueId());
        }
    }
}
