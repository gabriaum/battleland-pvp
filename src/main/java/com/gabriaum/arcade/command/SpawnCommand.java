package com.gabriaum.arcade.command;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.game.Game;
import com.gabriaum.arcade.game.type.GameType;
import com.gabriaum.arcade.user.User;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(label = "spawn")
public class SpawnCommand extends BaseCommand {
    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;
        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());

        if (user == null)
            return true;

        if (user.isProtect() && user.getGame().getType().equals(GameType.ARENA)) {
            player.sendMessage("§8§lPROTEÇÃO§f Você já está no §7§lSPAWN");
            return true;
        }

        Game game = ArcadeMain.getPlugin().getGameManager().get(GameType.ARENA);

        user.setGame(game);
        game.onJoin(player);

        player.sendMessage("§b§lWARP§f Você foi teleportado para o §3§lSPAWN");
        return false;
    }
}
