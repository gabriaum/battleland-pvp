package com.gabriaum.arcade.game.list.fps;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.game.Game;
import com.gabriaum.arcade.game.type.GameType;
import com.gabriaum.arcade.user.User;
import com.gabriaum.arcade.util.extra.StringScroller;
import com.solexgames.core.board.ScoreBoard;
import com.solexgames.core.member.PvPMember;
import com.solexgames.core.member.league.LeagueType;
import com.solexgames.core.util.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.List;

public class FPS extends Game {
    public FPS() {
        super(
                GameType.FPS,
                new StringScroller("BattleLand - FPS -", 12, 1)
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
                        "§7Warp: §3FPS",
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

        inventory.setHelmet(new ItemStack(Material.IRON_HELMET));
        inventory.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        inventory.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        inventory.setBoots(new ItemStack(Material.IRON_BOOTS));

        inventory.setItem(0, new ItemBuilder(Material.DIAMOND_SWORD).setEnchant(Enchantment.DAMAGE_ALL, 1).create());
        inventory.setItem(13, new ItemStack(Material.BOWL, 64));
        inventory.setItem(14, new ItemStack(Material.RED_MUSHROOM, 64));
        inventory.setItem(15, new ItemStack(Material.BROWN_MUSHROOM, 64));

        for (int i = 0; i < (9 * 4); i++)
            inventory.addItem(new ItemStack(Material.MUSHROOM_SOUP));
    }
}
