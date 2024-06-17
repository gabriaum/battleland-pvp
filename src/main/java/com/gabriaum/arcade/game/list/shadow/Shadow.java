package com.gabriaum.arcade.game.list.shadow;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.game.Game;
import com.gabriaum.arcade.game.type.GameType;
import com.gabriaum.arcade.manager.impl.ShadowConfiguration;
import com.gabriaum.arcade.user.User;
import com.gabriaum.arcade.util.extra.StringScroller;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.board.ScoreBoard;
import com.solexgames.core.member.PvPMember;
import com.solexgames.core.member.league.LeagueType;
import com.solexgames.core.util.builder.ItemBuilder;
import com.solexgames.lib.commons.redis.JedisManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.List;

public class Shadow extends Game {
    public Shadow() {
        super(
                GameType.SHADOW,
                new StringScroller("BattleLand - 1v1 -", 12, 1)
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
                        "§7Batalhando contra:",
                        "§b" + (user.getOpponent() != null ? user.getOpponent().getMember().getName() : "Ninguém"),
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
        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());
        PlayerInventory inventory = player.getInventory();

        inventory.clear();
        inventory.setArmorContents(null);

        if (user.isProtect()) {
            inventory.setItem(3, new ItemBuilder(Material.BLAZE_ROD)
                    .setDisplayName("§b§lDesafiar")
                    .create());

            inventory.setItem(4, new ItemBuilder(Material.IRON_FENCE)
                    .setDisplayName("§b§lCustomizar")
                    .create());

            inventory.setItem(5, new ItemBuilder(Material.INK_SACK, 8)
                    .setDisplayName("§b§lBatalha rápida §7(§c§lOFF§7)")
                    .create());

            return;
        }

        JedisManager jedisManager = CorePlugin.getInstance().getJedisManager();
        ShadowConfiguration configuration = CorePlugin.GSON.fromJson(jedisManager.getJedisPool().getResource().get("shadow:config:" + player.getUniqueId()), ShadowConfiguration.class);

        if (configuration == null) {
            inventory.setItem(0, new ItemBuilder(Material.STONE_SWORD).setEnchant(Enchantment.DAMAGE_ALL, 1).create());

            for (int i = 0; i < 8; i++)
                inventory.addItem(new ItemStack(Material.MUSHROOM_SOUP));
        } else {
            Material sword = Material.WOOD_SWORD;

            switch (configuration.getSwordMaterialName().toLowerCase()) {
                case "madeira": {
                    sword = Material.WOOD_SWORD;
                    break;
                }

                case "pedra": {
                    sword = Material.STONE_SWORD;
                    break;
                }

                case "ferro": {
                    sword = Material.IRON_SWORD;
                    break;
                }

                case "ouro": {
                    sword = Material.GOLD_SWORD;
                    break;
                }

                case "diamante": {
                    sword = Material.DIAMOND_SWORD;
                    break;
                }
            }

            Material helmet = Material.AIR;
            Material chestplate = Material.AIR;
            Material leggings = Material.AIR;
            Material boots = Material.AIR;

            switch (configuration.getArmorMaterialName().toLowerCase()) {
                case "couro": {
                    helmet = Material.LEATHER_HELMET;
                    chestplate = Material.LEATHER_CHESTPLATE;
                    leggings = Material.LEATHER_LEGGINGS;
                    boots = Material.LEATHER_BOOTS;
                    break;
                }

                case "corrente": {
                    helmet = Material.CHAINMAIL_HELMET;
                    chestplate = Material.CHAINMAIL_CHESTPLATE;
                    leggings = Material.CHAINMAIL_LEGGINGS;
                    boots = Material.CHAINMAIL_BOOTS;
                    break;
                }

                case "ferro": {
                    helmet = Material.IRON_HELMET;
                    chestplate = Material.IRON_CHESTPLATE;
                    leggings = Material.IRON_LEGGINGS;
                    boots = Material.IRON_BOOTS;
                    break;
                }

                case "ouro": {
                    helmet = Material.GOLD_HELMET;
                    chestplate = Material.GOLD_CHESTPLATE;
                    leggings = Material.GOLD_LEGGINGS;
                    boots = Material.GOLD_BOOTS;
                    break;
                }

                case "diamante": {
                    helmet = Material.DIAMOND_HELMET;
                    chestplate = Material.DIAMOND_CHESTPLATE;
                    leggings = Material.DIAMOND_LEGGINGS;
                    boots = Material.DIAMOND_BOOTS;
                    break;
                }
            }

            ItemBuilder swordEnchantment = new ItemBuilder(sword);

            if (configuration.isSharpness())
                swordEnchantment.setEnchant(Enchantment.DAMAGE_ALL, 1);

            inventory.setItem(0, swordEnchantment.create());

            if (!helmet.equals(Material.AIR)) {
                inventory.setHelmet(new ItemStack(helmet));
                inventory.setChestplate(new ItemStack(chestplate));
                inventory.setLeggings(new ItemStack(leggings));
                inventory.setBoots(new ItemStack(boots));
            }

            if (!configuration.isOnlyHotbar()) {
                inventory.setItem(13, new ItemStack(Material.BOWL, 64));

                if (configuration.isMushroomRecraft()) {
                    inventory.setItem(14, new ItemStack(Material.RED_MUSHROOM, 64));
                    inventory.setItem(15, new ItemStack(Material.BROWN_MUSHROOM, 64));
                } else {
                    inventory.setItem(14, new ItemBuilder(Material.INK_SACK, 3).setAmount(64).create());
                }
            }

            for (int i = 0; i < (configuration.isOnlyHotbar() ? 8 : 9 * 4); i++)
                inventory.addItem(new ItemStack(Material.MUSHROOM_SOUP));
        }
    }
}
