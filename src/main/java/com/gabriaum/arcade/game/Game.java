package com.gabriaum.arcade.game;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.game.type.GameType;
import com.gabriaum.arcade.manager.RewardManager;
import com.gabriaum.arcade.user.User;
import com.gabriaum.arcade.util.extra.StringScroller;
import com.gabriaum.arcade.util.Util;
import com.google.gson.JsonObject;
import com.solexgames.core.CorePlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

@Getter
@RequiredArgsConstructor
public abstract class Game {

    private final GameType type;
    private final StringScroller scroller;

    public abstract void handle(Player player);

    public abstract void handleScoreboard(Player player);
    public abstract void sendKit(Player player);

    public void onJoin(Player player) {
        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());
        JsonObject game = ArcadeMain.getPlugin().getMap().getAsJsonObject(type.name().toLowerCase());
        Location location = !game.get("spawn").getAsString().isEmpty() ? CorePlugin.GSON.fromJson(game.get("spawn"), Location.class) : player.getLocation().clone();

        Util.refresh(player);

        if (user.getOpponent() == null) {
            user.setProtect(true);
            player.teleport(location);

            handle(player);
        }

        if (user.getOpponent() != null && type.equals(GameType.SHADOW)) {
            User target = user.getOpponent();
            Player targetPlayer = Bukkit.getPlayer(target.getUniqueId());

            Util.refresh(targetPlayer);

            user.setProtect(false);
            target.setProtect(false);

            Location red = CorePlugin.GSON.fromJson(game.get("red"), Location.class);
            Location blue = CorePlugin.GSON.fromJson(game.get("blue"), Location.class);

            player.teleport(red);
            targetPlayer.teleport(blue);

            handle(player);
            handle(targetPlayer);
        } else {
            user.setProtect(true);
            player.teleport(location);

            ArcadeMain.getPlugin().getQueueManager().remove(player.getUniqueId());
            handle(player);
        }

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
