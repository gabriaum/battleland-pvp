package com.gabriaum.arcade.util;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

public class Util {

    public static void refresh(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20D);
        player.setLevel(0);
        player.setExhaustion(0);
        player.setFireTicks(0);
        player.setExp(0);
    }

    public static Player findNearestPlayer(Player bypass, Entity entity) {
        Player nearestPlayer = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Player player : entity.getWorld().getPlayers()) {
            if (player.equals(bypass))
                continue;

            double distance = player.getLocation().distance(entity.getLocation());

            if (distance < nearestDistance && distance <= 6) {
                nearestDistance = distance;
                nearestPlayer = player;
            }
        }

        return nearestPlayer;
    }
}
