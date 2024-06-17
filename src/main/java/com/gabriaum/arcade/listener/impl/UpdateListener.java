package com.gabriaum.arcade.listener.impl;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.event.type.UpdateEvent;
import com.gabriaum.arcade.game.Game;
import com.gabriaum.arcade.manager.QueueManager;
import com.gabriaum.arcade.user.User;
import com.gabriaum.arcade.util.structure.type.Feast;
import com.solexgames.core.board.ScoreBoard;
import com.solexgames.core.member.PvPMember;
import com.solexgames.core.member.stats.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class UpdateListener implements Listener {

    private final AtomicInteger counter = new AtomicInteger(0);

    @EventHandler
    public void update(UpdateEvent event) {
        /* feast */
        Feast feast = ArcadeMain.getPlugin().getFeast();
        Location location = feast.getLocation().clone();
        int count = counter.incrementAndGet();

        if (count == 595)
            Bukkit.broadcastMessage("§e§lFEAST§f O feast irá nascer em 5 segundos! §7(" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")");
        else if (count == 596)
            Bukkit.broadcastMessage("§e§lFEAST§f O feast irá nascer em 4 segundos! §7(" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")");
        else if (count == 597)
            Bukkit.broadcastMessage("§e§lFEAST§f O feast irá nascer em 3 segundos! §7(" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")");
        else if (count == 598)
            Bukkit.broadcastMessage("§e§lFEAST§f O feast irá nascer em 2 segundos! §7(" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")");
        else if (count == 599)
            Bukkit.broadcastMessage("§e§lFEAST§f O feast irá nascer em 1 segundo! §7(" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")");

        if (count == 600) {
            feast.generateChest();
            counter.set(0);

            Bukkit.broadcastMessage("§e§lFEAST§f O feast nasceu! §7(" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")");
        }

        /* Scoreboard animation */
        for (Map.Entry<UUID, ScoreBoard> entry : ScoreBoard.getAllBoards().entrySet()) {
            User user = ArcadeMain.getPlugin().getUserManager().get(entry.getKey());

            if (user == null)
                continue;

            ScoreBoard scoreBoard = entry.getValue();

            for (Game game : ArcadeMain.getPlugin().getGameManager().values()) {
                scoreBoard.setTitle("§8§l>> §6§l" + game.getScroller().next() + " §8§l<<");
                scoreBoard.update();
            }
        }

        /* League */
        for (User user : ArcadeMain.getPlugin().getUserManager().values()) {
            PvPMember member = user.getMember();

            if (member != null)
                member.checkLeague();
        }

        /* KillStreak */
        Optional<User> killStreak = ArcadeMain.getPlugin().getUserManager().values().stream()
                .max(Comparator.comparingInt(u -> u.getMember().getArena().getKillstreak()));

        if (killStreak.isPresent()) {
            User user = killStreak.get();
            PvPMember member = user.getMember();
            Arena arena = member.getArena();

            if (arena.getKillstreak() >= 1)
                ArcadeMain.getPlugin().setTopKillStreak(user);
        }

        /* Queue system */
        QueueManager queue = ArcadeMain.getPlugin().getQueueManager();

        if (queue.size() >= 2) {
            Random random = new Random();

            int index1 = random.nextInt(queue.size());
            int index2;
            do {
                index2 = random.nextInt(queue.size());
            } while (index1 == index2);

            UUID playerId = queue.get(index1);
            UUID targetId = queue.get(index2);

            User user = ArcadeMain.getPlugin().getUserManager().get(playerId);
            User targetUser = ArcadeMain.getPlugin().getUserManager().get(targetId);

            if (user != null && targetUser != null) {
                user.setOpponent(targetUser);
                targetUser.setOpponent(user);

                Player player = Bukkit.getPlayer(playerId);
                Player target = Bukkit.getPlayer(targetId);

                if (player == null || target == null)
                    return;

                user.getGame().onJoin(player);
                queue.remove(playerId);
                queue.remove(targetId);
            }
        }
    }
}
