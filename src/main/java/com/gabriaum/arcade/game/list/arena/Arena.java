package com.gabriaum.arcade.game.list.arena;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.game.Game;
import com.gabriaum.arcade.game.list.arena.kit.Kit;
import com.gabriaum.arcade.game.type.GameType;
import com.gabriaum.arcade.user.User;
import com.gabriaum.arcade.util.extra.StringScroller;
import com.solexgames.core.board.ScoreBoard;
import com.solexgames.core.member.PvPMember;
import com.solexgames.core.member.league.LeagueType;
import com.solexgames.core.util.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.List;

public class Arena extends Game {
    public Arena() {
        super(
                GameType.ARENA,
                new StringScroller("BattleLand - Arena -", 12, 1)
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
        com.solexgames.core.member.stats.Arena arena = member.getArena();

        ScoreBoard scoreBoard = new ScoreBoard(player) {
            @Override
            public List<String> getLines() {
                return Arrays.asList(
                        "",
                        "§7Kills: §b" + arena.getKills(),
                        "§7Deaths: §b" + arena.getDeaths(),
                        "§7Killstreak: §b" + arena.getKillstreak(),
                        "§7XP: §b" + member.getXp(),
                        "§7Liga: " + league.getColor() + league.getSymbol() + " " + league.getName(),
                        "",
                        "§7Top KillStreak:",
                        "§b" + (ArcadeMain.getPlugin().getTopKillStreak() == null ? "Ninguém - 0" : ArcadeMain.getPlugin().getTopKillStreak().getMember().getName() + " - " + ArcadeMain.getPlugin().getTopKillStreak().getMember().getArena().getKillstreak()),
                        "",
                        "§7Kit: §e" + user.getKit().getName(),
                        "",
                        "§6www.battle.land"
                );
            }

            @Override
            public String getTitle() {
                return getScroller().next();
            }
        };

        user.setScoreBoard(scoreBoard);
    }

    @Override
    public void sendKit(Player player) {
        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());
        Kit kit = user.getKit().getKit();

        PlayerInventory inventory = player.getInventory();

        inventory.clear();
        inventory.setArmorContents(null);

        if (user.isProtect()) {
            inventory.setItem(1, new ItemBuilder(Material.ENDER_CHEST)
                    .setDisplayName("§b§lKits")
                    .create());

            inventory.setItem(2, new ItemBuilder(Material.COMPASS)
                    .setDisplayName("§b§lWarps")
                    .create());

            inventory.setItem(6, new ItemBuilder(Material.DIAMOND)
                    .setDisplayName("§b§lLoja")
                    .create());

            inventory.setItem(7, new ItemBuilder(Material.BOOK)
                    .setDisplayName("§b§lEvento")
                    .create());

            return;
        }

        inventory.setItem(0, new ItemStack(Material.STONE_SWORD));
        inventory.setItem(13, new ItemStack(Material.BOWL, 64));
        inventory.setItem(14, new ItemStack(Material.RED_MUSHROOM, 64));
        inventory.setItem(15, new ItemStack(Material.BROWN_MUSHROOM, 64));

        if (kit.hasKitItems())
            for (ItemStack item : kit.kitItems())
                inventory.setItem(1, item);

        for (int i = 0; i < (9 * 4); i++)
            inventory.addItem(new ItemStack(Material.MUSHROOM_SOUP));
    }
}
