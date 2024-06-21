package com.gabriaum.arcade.listener.impl.game.combat;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.event.type.arena.RewardEvent;
import com.gabriaum.arcade.game.Game;
import com.gabriaum.arcade.game.list.arena.kit.type.KitType;
import com.gabriaum.arcade.game.type.GameType;
import com.gabriaum.arcade.user.User;
import com.solexgames.core.member.PvPMember;
import com.solexgames.core.member.stats.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CombatListener implements Listener {

    @EventHandler
    public void death(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        event.getDrops().clear();

        Player player = event.getEntity();
        Player target = event.getEntity().getKiller();

        ArcadeMain.getPlugin().getShadowManager().remove(player.getUniqueId());
        ArcadeMain.getPlugin().getShadowManager().remove(target.getUniqueId());

        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());

        if (user == null)
            return;

        PvPMember member = user.getMember();
        Arena arena = member.getArena();
        Game game = user.getGame();

        user.setOpponent(null);

        if (arena.getKillstreak() >= 5)
            Bukkit.broadcastMessage("§4§lKILLSTREAK §c§l" + player.getName() + "§f perdeu seu §6§lKILLSTREAK DE " + arena.getKillstreak() + (target != null ? " para §c§l" + target.getName() : ""));

        arena.addDeath();

        if (target != null) {
            User targetUser = ArcadeMain.getPlugin().getUserManager().get(target.getUniqueId());
            PvPMember targetMember = targetUser.getMember();
            Arena targetArena = targetMember.getArena();

            if (targetUser.getOpponent() != null) {
                user.setOpponent(null);
                targetUser.setOpponent(null);

                game.onJoin(target);
            }

            targetArena.addKill();
            targetMember.addCoins(6);
            targetMember.addXP(9);

            if (targetUser.getGame().getType().equals(GameType.FPS)) {
                PlayerInventory inventory = target.getInventory();

                inventory.setHelmet(new ItemStack(Material.IRON_HELMET));
                inventory.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                inventory.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                inventory.setBoots(new ItemStack(Material.IRON_BOOTS));
            }

            target.sendMessage("§e§lKILL §fVocê matou §e§l" + player.getName());
            target.sendMessage("§6§lMONEY §fVocê recebeu §6§l6 MOEDAS");
            target.sendMessage("§9§lXP §fVocê recebeu §9§l9 XPs");

            if (String.valueOf(targetArena.getKillstreak()).endsWith("0") || String.valueOf(targetArena.getKillstreak()).endsWith("5")) {
                Bukkit.broadcastMessage("§4§lKILLSTREAK §c§l" + target.getName() + "§f conseguiu um §6§lKILLSTREAK DE " + targetArena.getKillstreak());
                new RewardEvent(targetUser, targetArena.getKillstreak()).pulse();
            }

            new RewardEvent(targetUser, targetArena.getKillstreak()).pulse();
            targetMember.save("arena");
        }

        if (!user.getGame().getType().equals(GameType.SHADOW)) {
            List<ItemStack> items = new ArrayList<>();

            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null || item.getType().equals(Material.AIR))
                    continue;

                if (user.getKit().getKit().isKitItem(item) || item.getType().equals(Material.COMPASS) || item.getType().name().contains("_SWORD"))
                    continue;

                items.add(item);
            }

            if (!items.isEmpty()) {
                items.forEach(i -> {
                    Item item = player.getWorld().dropItem(player.getLocation(), i);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            item.remove();
                            cancel();
                        }
                    }.runTaskLater(ArcadeMain.getPlugin(), 20 * 3);
                });
            }
        }

        player.sendMessage("§c§lMORTE §fVocê morreu" + (target != null ? " para §e§l" + target.getName() : ""));
        player.sendMessage("§6§lRESPAWN §fVocê morreu e renasceu na Warp §e" + game.getType().getName() + "§f.");

        member.save("arena");

        new BukkitRunnable() {
            @Override
            public void run() {
                player.spigot().respawn();
                game.onJoin(player);
                cancel();
            }
        }.runTaskLater(ArcadeMain.getPlugin(), 1);
    }

    @EventHandler
    public void deathByTarget(EntityDamageByEntityEvent event) {
        if ((!(event.getEntity() instanceof Player)))
            return;

        Player player = (Player) event.getEntity();
        Player target = null;

        if (event.getDamager() instanceof Wolf) {
            Wolf wolf = (Wolf) event.getDamager();

            if (wolf.getOwner() instanceof Player)
                target = (Player) wolf.getOwner();
        } else if (event.getDamager() instanceof Zombie) {
            Zombie zombie = (Zombie) event.getDamager();

            if (zombie.hasMetadata("owner")) {
                String owner = zombie.getMetadata("owner").get(0).asString();

                target = Bukkit.getPlayer(owner);
            }
        }

        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());

        if (user == null)
            return;

        if (target != null && (event.getFinalDamage() >= player.getHealth())) {
            event.setDamage(0);

            List<ItemStack> items = new ArrayList<>();

            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null || item.getType().equals(Material.AIR))
                    continue;

                if (user.getKit().getKit().isKitItem(item) || item.getType().equals(Material.COMPASS) || item.getType().name().contains("_SWORD"))
                    continue;

                items.add(item);
            }

            if (!items.isEmpty()) {
                items.forEach(i -> {
                    Item item = player.getWorld().dropItem(player.getLocation(), i);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            item.remove();
                            cancel();
                        }
                    }.runTaskLater(ArcadeMain.getPlugin(), 20 * 3);
                });
            }

            PvPMember member = user.getMember();
            Game game = user.getGame();
            Arena arena = member.getArena();

            user.setOpponent(null);

            if (arena.getKillstreak() >= 5)
                Bukkit.broadcastMessage("§4§lKILLSTREAK §c§l" + player.getName() + "§f perdeu seu §6§lKILLSTREAK DE " + arena.getKillstreak() + (target != null ? " para §c§l" + target.getName() : ""));

            arena.addDeath();

            User targetUser = ArcadeMain.getPlugin().getUserManager().get(target.getUniqueId());
            PvPMember targetMember = targetUser.getMember();
            Arena targetArena = targetMember.getArena();

            if (targetUser.getOpponent() != null) {
                user.setOpponent(null);
                targetUser.setOpponent(null);

                game.onJoin(target);
            }

            targetArena.addKill();
            targetMember.addCoins(6);
            targetMember.addXP(9);

            if (targetUser.getGame().getType().equals(GameType.FPS)) {
                PlayerInventory inventory = target.getInventory();

                inventory.setHelmet(new ItemStack(Material.IRON_HELMET));
                inventory.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                inventory.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                inventory.setBoots(new ItemStack(Material.IRON_BOOTS));
            }

            target.sendMessage("§e§lKILL §fVocê matou §e§l" + player.getName());
            target.sendMessage("§6§lMONEY §fVocê recebeu §6§l6 MOEDAS");
            target.sendMessage("§9§lXP §fVocê recebeu §9§l9 XPs");

            player.sendMessage("§c§lMORTE §fVocê morreu" + (target != null ? " para §e§l" + target.getName() : ""));
            player.sendMessage("§6§lRESPAWN §fVocê morreu e renasceu na Warp §e" + game.getType().getName() + "§f.");

            if (String.valueOf(targetArena.getKillstreak()).endsWith("0") || String.valueOf(targetArena.getKillstreak()).endsWith("5")) {
                Bukkit.broadcastMessage("§4§lKILLSTREAK §c§l" + target.getName() + "§f conseguiu um §6§lKILLSTREAK DE " + targetArena.getKillstreak());
                new RewardEvent(targetUser, targetArena.getKillstreak()).pulse();
            }

            new RewardEvent(targetUser, targetArena.getKillstreak()).pulse();
            targetMember.save("arena");
            member.save("arena");

            new BukkitRunnable() {
                @Override
                public void run() {
                    game.onJoin(player);
                    cancel();
                }
            }.runTaskTimer(ArcadeMain.getPlugin(), 1, 1);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteractMushroomSoup(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());

        if (player.getItemInHand().getType() == null || !player.getItemInHand().getType().equals(Material.MUSHROOM_SOUP))
            return;

        if (event.getAction().name().contains("RIGHT_")) {
            if (player.getHealth() < (player).getMaxHealth()) {
                event.setCancelled(true);

                int restores = 7;

                if (player.getHealth() + restores <= player.getMaxHealth())
                    player.setHealth(player.getHealth() + restores);
                else
                    player.setHealth(player.getMaxHealth());

                player.setItemInHand(new ItemStack(Material.BOWL));

                if (user.getKit().equals(KitType.QUICKDROP))
                    player.setItemInHand(new ItemStack(Material.AIR));
            }
        }
    }
}