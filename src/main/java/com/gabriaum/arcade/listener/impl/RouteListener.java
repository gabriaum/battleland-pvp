package com.gabriaum.arcade.listener.impl;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.game.Game;
import com.gabriaum.arcade.game.type.GameType;
import com.gabriaum.arcade.user.User;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.member.PvPMember;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class RouteListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public synchronized void route(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        try {
            PvPMember member = CorePlugin.getInstance().getPvPMemberData().read(player.getUniqueId());

            if (member == null)
                member = CorePlugin.getInstance().getPvPMemberData().registry(player.getUniqueId(), player.getName());

            User user = new User(player.getUniqueId(), member);
            Game game = ArcadeMain.getPlugin().getGameManager().get(GameType.ARENA);

            if (game == null) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cOcorreu um problema ao consultar sua rota.");
                return;
            }

            user.setGame(game);
            ArcadeMain.getPlugin().getUserManager().put(player.getUniqueId(), user);
        } catch (Exception ex) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cOcorreu um problema ao consultar sua rota.");

            ex.printStackTrace();
        }
    }

    @EventHandler
    public void remove(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());

        if (user != null) {
            if (ArcadeMain.getPlugin().getTopKillStreak().equals(user))
                ArcadeMain.getPlugin().setTopKillStreak(null);

            ArcadeMain.getPlugin().getQueueManager().remove(player.getUniqueId());
            ArcadeMain.getPlugin().getUserManager().remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void remove(PlayerKickEvent event) {
        Player player = event.getPlayer();
        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());

        if (user != null) {
            if (ArcadeMain.getPlugin().getTopKillStreak().equals(user))
                ArcadeMain.getPlugin().setTopKillStreak(null);

            ArcadeMain.getPlugin().getQueueManager().remove(player.getUniqueId());
            ArcadeMain.getPlugin().getUserManager().remove(player.getUniqueId());
        }
    }
}
