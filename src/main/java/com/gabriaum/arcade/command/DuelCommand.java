package com.gabriaum.arcade.command;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.game.Game;
import com.gabriaum.arcade.game.list.shadow.type.CombatType;
import com.gabriaum.arcade.game.type.GameType;
import com.gabriaum.arcade.manager.impl.ShadowConfiguration;
import com.gabriaum.arcade.user.User;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.lib.commons.redis.JedisManager;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Command(label = "duel", aliases = {"1v1", "1vs1"})
public class DuelCommand extends BaseCommand {

    @Getter
    private static final Cache<User, Map<CombatType, List<User>>> requests = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());

        if (user == null || !user.isProtect() || !user.getGame().getType().equals(GameType.SHADOW) || user.getOpponent() != null) {
            return false;
        }

        if (args.length < 1) {
            player.sendMessage("§3§l1V1 §fUtilize: §b§l/" + label + "§f <target>");
            return false;
        }

        if (args[0].equalsIgnoreCase("ac")) {
            return handleAcceptDuel(player, user, args);
        } else {
            return handleRequestDuel(player, user, args);
        }
    }

    private boolean handleAcceptDuel(Player player, User user, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§3§l1V1 §fUtilize: §b§l/duel§f ac <target>");
            return false;
        }

        if (!user.isProtect()) {
            player.sendMessage("§4§lERRO §fVocê não pode aceitar esse convite agora.");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("§4§lERRO §fO jogador §b§l" + args[1] + "§f não existe.");
            return true;
        }

        User targetUser = ArcadeMain.getPlugin().getUserManager().get(target.getUniqueId());
        if (targetUser == null) {
            player.sendMessage("§4§lERRO §fO jogador §b§l" + args[1] + "§f não existe.");
            return true;
        }

        if (!targetUser.isProtect()) {
            player.sendMessage("§4§lERRO §fO jogador §b§l" + args[1] + "§f não pode aceitar esse convite agora.");
            return true;
        }

        List<User> playerRequests = requests.getIfPresent(user).get(player.getItemInHand().getType().equals(Material.BLAZE_ROD) ? CombatType.NORMAL : CombatType.CUSTOM);
        List<User> targetRequests = requests.getIfPresent(targetUser).get(player.getItemInHand().getType().equals(Material.BLAZE_ROD) ? CombatType.NORMAL : CombatType.CUSTOM);

        if ((targetRequests != null && targetRequests.contains(user)) || playerRequests != null && playerRequests.contains(targetUser)) {
            clearRequestsForUser(user, targetUser, !player.getItemInHand().getType().equals(Material.BLAZE_ROD));

            if (!player.getItemInHand().getType().equals(Material.BLAZE_ROD))
                handleShadowConfiguration(player, target);

            user.setOpponent(targetUser);
            targetUser.setOpponent(user);

            user.getGame().onJoin(player);

            player.sendMessage("§aVocê aceitou o pedido de duelo de " + target.getName());
            target.sendMessage("§aO jogador " + player.getName() + " aceitou o seu pedido de duelo.");

            return true;
        }

        player.sendMessage("§4§lERRO §fVocê não recebeu um pedido de duelo de §b§l" + target.getName());
        return true;
    }

    private boolean handleRequestDuel(Player player, User user, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage("§4§lERRO §fO jogador §b§l" + args[0] + "§f não existe.");
            return true;
        }

        User targetUser = ArcadeMain.getPlugin().getUserManager().get(target.getUniqueId());
        if (targetUser.getOpponent() != null) {
            player.sendMessage("§4§lERRO §fO jogador §b§l" + args[0] + "§f já está duelando.");
            return true;
        }

        Map<CombatType, List<User>> playerRequests = DuelCommand.getRequests().getIfPresent(user);
        Map<CombatType, List<User>> targetRequests = DuelCommand.getRequests().getIfPresent(targetUser);

        if ((targetRequests != null || playerRequests != null) && (targetRequests != null && targetRequests.containsKey(CombatType.CUSTOM) && targetRequests.get(CombatType.CUSTOM).contains(user)) || (playerRequests != null && playerRequests.containsKey(CombatType.CUSTOM) && playerRequests.get(CombatType.CUSTOM).contains(targetUser))) {
            clearRequestsForUser(user, targetUser, !player.getItemInHand().getType().equals(Material.BLAZE_ROD));

            player.sendMessage("§aVocê aceitou o pedido de duelo de " + target.getName());
            target.sendMessage("§aO jogador " + player.getName() + " aceitou o seu pedido de duelo.");

            user.setOpponent(targetUser);
            targetUser.setOpponent(user);

            user.getGame().onJoin(player);
            return true;
        }

        boolean custom = args.length >= 2 && args[1].equalsIgnoreCase("custom");

        if (targetRequests == null) {
            Map<CombatType, List<User>> map = new HashMap<>();
            List<User> list = new ArrayList<>();

            list.add(user);
            map.put(custom ? CombatType.CUSTOM : CombatType.NORMAL, list);

            requests.put(targetUser, map);
        } else {
            targetRequests.put(custom ? CombatType.CUSTOM : CombatType.NORMAL, Arrays.asList(user));
        }

        player.sendMessage("§aVocê enviou um pedido de duelo " + (custom ? "customizado " : "") + "para " + target.getName());
        target.sendMessage("§eVocê recebeu um pedido de duelo " + (custom ? "customizado " : "") + "de " + player.getName());
        return true;
    }

    private void clearRequestsForUser(User user, User target, boolean custom) {
        if (!custom) {
            try (Jedis jedis = CorePlugin.getInstance().getJedisManager().getJedisPool().getResource()) {
                jedis.del("shadow:" + user.getUniqueId() + ":config:" + target.getUniqueId());
                jedis.del("shadow:" + target.getUniqueId() + ":config:" + user.getUniqueId());
            }
        }

        requests.invalidate(user);
        requests.asMap().values().forEach(list -> list.remove(user));

        ArcadeMain.getPlugin().getQueueManager().remove(user.getUniqueId());
    }

    private void handleShadowConfiguration(Player player, Player target) {
        try (Jedis jedis = CorePlugin.getInstance().getJedisManager().getJedisPool().getResource()) {
            ShadowConfiguration configuration = CorePlugin.GSON.fromJson(
                    jedis.get("shadow:" + player.getUniqueId() + ":config:" + target.getUniqueId()),
                    ShadowConfiguration.class
            );

            if (configuration != null) {
                Map<UUID, ShadowConfiguration> map = new HashMap<>();

                map.put(target.getUniqueId(), configuration);
                ArcadeMain.getPlugin().getShadowManager().put(player.getUniqueId(), map);
            }

            jedis.del("shadow:" + player.getUniqueId() + ":config:" + target.getUniqueId());
            jedis.del("shadow:" + target.getUniqueId() + ":config:" + player.getUniqueId());
        }
    }
}