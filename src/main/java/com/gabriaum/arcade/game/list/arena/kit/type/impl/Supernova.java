package com.gabriaum.arcade.game.list.arena.kit.type.impl;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.event.type.UpdateEvent;
import com.gabriaum.arcade.game.list.arena.kit.Kit;
import com.solexgames.core.util.builder.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class Supernova extends Kit implements Listener {

    private ArrayList<ArrowDirection> directions;
    private HashMap<Arrow, Vector> arrows;
    private Set<UUID> damaged;

    public Supernova() {
        super(
                Material.ARROW,
                "Supernova",
                Arrays.asList("§7Invoque flechas ao seu redor e", "§7cause dano em seus inimigos."),
                "arcade.pvp.supernova",
                15000,
                20
        );

        directions = new ArrayList<>();
        ArrayList<Double> list = new ArrayList<>();
        damaged = new HashSet<>();

        list.add(0.0);
        list.add(22.5);
        list.add(45.0);
        list.add(67.5);
        list.add(90.0);
        list.add(112.5);
        list.add(135.0);
        list.add(157.5);
        list.add(180.0);
        list.add(202.5);
        list.add(225.0);
        list.add(247.5);
        list.add(270.0);
        list.add(292.5);
        list.add(315.0);
        list.add(337.5);

        for (double i : list) {
            directions.add(new ArrowDirection(i, 67.5));
            directions.add(new ArrowDirection(i, 45.0));
            directions.add(new ArrowDirection(i, 22.5));
            directions.add(new ArrowDirection(i, 0.0));
            directions.add(new ArrowDirection(i, -22.5));
            directions.add(new ArrowDirection(i, -45));
            directions.add(new ArrowDirection(i, -67.5));
        }

        directions.add(new ArrowDirection(90.0, 0.0));
        directions.add(new ArrowDirection(-90.0, 0.0));
        directions.add(new ArrowDirection(0.0, 90.0));
        directions.add(new ArrowDirection(0.0, -90.0));

        list.clear();

        arrows = new HashMap<>();
    }

    @Override
    public List<ItemStack> kitItems() {
        return Collections.singletonList(new ItemBuilder(Material.CLAY_BALL).setDisplayName("§aSupernova").create());
    }

    @EventHandler
    public void onSupernovaUpdate(UpdateEvent event) {
        Iterator<Map.Entry<Arrow, Vector>> entrys = arrows.entrySet().iterator();

        while (entrys.hasNext()) {
            Map.Entry<Arrow, Vector> entry = entrys.next();
            Arrow arrow = entry.getKey();
            Vector vector = entry.getValue();

            if (!arrow.isDead()) {
                arrow.setVelocity(vector.normalize().multiply(vector.lengthSquared() / 4));

                if (arrow.isOnGround() || arrow.getTicksLived() >= 100)
                    arrow.remove();
            } else {
                entrys.remove();
            }
        }
    }

    @EventHandler
    public void onSupernova(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (hasKit(player.getUniqueId()) && isKitItem(player.getItemInHand()) && event.getAction().name().contains("RIGHT_")) {
            if (hasCooldown(player))
                return;
            else
                addCooldown(player.getUniqueId());

            Location location = player.getLocation();

            for (ArrowDirection direction : directions) {
                synchronized (this) {
                    final Arrow arrow = location.getWorld().spawn(location.clone().add(0, 1, 0), Arrow.class);

                    arrow.setMetadata("supernova", new FixedMetadataValue(ArcadeMain.getPlugin(), player.getUniqueId()));

                    double pitch = ((direction.pitch + 90) * Math.PI) / 180;
                    double yaw = ((direction.yaw + 90) * Math.PI) / 180;
                    double x = Math.sin(pitch) * Math.cos(yaw);
                    double y = Math.sin(pitch) * Math.sin(yaw);
                    double z = Math.cos(pitch);

                    Vector vec = new Vector(x, z, y);

                    arrow.setShooter(player);
                    arrow.setVelocity(vec.multiply(2));
                    arrows.put(arrow, vec);
                }
            }

            player.playSound(player.getLocation(), Sound.SHOOT_ARROW, 0.5F, 1.0F);
        }
    }

    @EventHandler
    public void onSupernovaDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().hasMetadata("Supernova")) {
            if (event.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) event.getDamager();

                if (arrow.getShooter() instanceof Player) {
                    Player shooter = (Player) arrow.getShooter();
                    if (event.getEntity() instanceof Player) {
                        Player player = (Player) event.getEntity();

                        if (shooter.getUniqueId() == player.getUniqueId()) {
                            event.setCancelled(true);
                            return;
                        }

                        if (damaged.contains(player.getUniqueId())) {
                            event.setCancelled(true);
                            return;
                        }
                    }

                    event.setDamage(10.0D);

                    if (event.getEntity() instanceof Player) {
                        Player player = (Player) event.getEntity();

                        damaged.add(player.getUniqueId());

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                damaged.remove(player.getUniqueId());
                            }
                        }.runTaskLater(ArcadeMain.getPlugin(), 10);
                    }
                }
            }
        }
    }

    @RequiredArgsConstructor
    protected static class ArrowDirection {

        private final double pitch;
        private final double yaw;
    }
}
