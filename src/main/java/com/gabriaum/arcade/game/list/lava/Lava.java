package com.gabriaum.arcade.game.list.lava;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.game.Game;
import com.gabriaum.arcade.game.type.GameType;
import com.gabriaum.arcade.user.User;
import com.gabriaum.arcade.util.extra.StringScroller;
import com.solexgames.core.board.ScoreBoard;
import com.solexgames.core.member.PvPMember;
import com.solexgames.core.member.league.LeagueType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.List;

public class Lava extends Game {
    public Lava() {
        super(
                GameType.LAVA,
                new StringScroller("BattleLand - LavaChallenge -", 12, 1)
        );
    }

    @Override
    public void handle(Player player) {
        handleScoreboard(player);
        sendKit(player);
    }

    @Override
    public void handleScoreboard(Player player) {
        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());
        PvPMember member = user.getMember();

        if (member == null)
            return;

        LeagueType league = member.getLeague();

        ScoreBoard scoreBoard = new ScoreBoard(player) {
            @Override
            public List<String> getLines() {
                return Arrays.asList(
                        "",
                        "§7XP: §b" + member.getXp(),
                        "§7Liga: " + league.getColor() + league.getSymbol() + " " + league.getName(),
                        "",
                        "§6www.battle.land"
                );
            }

            @Override
            public String getTitle() {
                return getScroller().next();
            }
        };
    }

    @Override
    public void sendKit(Player player) {
        PlayerInventory inventory = player.getInventory();

        inventory.clear();
        inventory.setArmorContents(null);

        for (int i = 0; i < (9 * 4); i++)
            inventory.addItem(new ItemStack(Material.MUSHROOM_SOUP));
    }
}
