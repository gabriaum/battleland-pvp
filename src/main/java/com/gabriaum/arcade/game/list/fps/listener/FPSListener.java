package com.gabriaum.arcade.game.list.fps.listener;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.game.Game;
import com.gabriaum.arcade.game.list.fps.FPS;
import com.gabriaum.arcade.game.type.GameType;
import com.gabriaum.arcade.user.User;
import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class FPSListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void move(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());

        if (user == null)
            return;

        Game game = user.getGame();
        Location location = event.getTo().clone();
        JsonObject fpsObject = ArcadeMain.getPlugin().getMap().getAsJsonObject("fps");

        if (game.getType().equals(GameType.FPS)) {
            if (user.isProtect() && location.getY() <= fpsObject.get("minimum-y").getAsInt()) {
                user.setProtect(false);

                player.sendMessage("§8§lPROTEÇÃO §fVocê §7§lPERDEU §fsua proteção do spawn");
            }
        }
    }
}
