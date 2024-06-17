package com.gabriaum.arcade.command;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.user.User;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.member.PvPMember;
import com.solexgames.core.member.league.LeagueType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(label = "league", aliases = {"leagues", "liga", "ligas"})
public class LeagueCommand extends BaseCommand {
    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;
        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());

        if (user == null)
            return true;

        PvPMember member = user.getMember();
        LeagueType league = member.getLeague();

        player.sendMessage("§6§lLIGA§f O BattleLand possui um sistema de liga unico que garante aos players uma competição e mais destaque no servidor.");
        player.sendMessage("§fAo matar alguém você recebe uma quantidade de XP calculada por nossa rede para upar de nível.");

        for (LeagueType l : LeagueType.values()) {
            player.sendMessage(l.getColor() + "§l" + l.getSymbol() + " " + l.name());
        }

        LeagueType next = league.next();

        player.sendMessage("§fSua liga atual é " + league.getColor() + "§l" + league.getSymbol() + " " + league.name());
        player.sendMessage("§fSeu XP atual é §4§l" + member.getXp() + "§f XPs");
        player.sendMessage("Próxima liga " + (league.equals(LeagueType.LEGENDARY) ? "§c§lVOCÊ JÁ ESTÁ NA ÚLTIMA LIGA" : next.getColor() + "§l" + next.getSymbol() + " " + next.name()));
        player.sendMessage("§fXP necessária para a próxima liga: §4§l" + (next.getXp() - member.getXp()));
        return false;
    }
}
