package com.gabriaum.arcade.command;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.game.Game;
import com.gabriaum.arcade.game.type.GameType;
import com.gabriaum.arcade.manager.impl.ShadowConfiguration;
import com.gabriaum.arcade.user.User;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.lib.commons.redis.JedisManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Command(label = "duel", aliases = {"1v1", "1vs1"})
public class DuelCommand extends BaseCommand {

    private final Cache<User, List<User>> requests = CacheBuilder.newBuilder()
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

        List<User> userRequests = requests.getIfPresent(user);
        if (userRequests != null && userRequests.contains(targetUser)) {
            clearRequestsForUser(user);
            clearRequestsForUser(targetUser);

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

        List<User> targetRequests = requests.getIfPresent(targetUser);
        if (targetRequests != null && targetRequests.contains(user)) {
            clearRequestsForUser(user);
            clearRequestsForUser(targetUser);

            player.sendMessage("§aVocê aceitou o pedido de duelo de " + target.getName());
            target.sendMessage("§aO jogador " + player.getName() + " aceitou o seu pedido de duelo.");

            user.setOpponent(targetUser);
            targetUser.setOpponent(user);

            user.getGame().onJoin(player);

            return true;
        }

        boolean custom = args.length >= 2 && args[1].equalsIgnoreCase("custom");

        if (targetRequests == null) {
            List<User> list = new ArrayList<>();

            list.add(user);

            requests.put(targetUser, list);
        } else {
            targetRequests.add(user);
        }

        TextComponent accept = new TextComponent("§eClique §b§lAQUI§e para aceitar!");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel ac " + player.getName()));

        player.sendMessage("§aVocê enviou um pedido de duelo " + (custom ? "customizado " : "") + "para " + target.getName());
        target.sendMessage("§eVocê recebeu um pedido de duelo " + (custom ? "customizado " : "") + "de " + player.getName());
        target.spigot().sendMessage(accept);

        return true;
    }

    private void clearRequestsForUser(User user) {
        requests.invalidate(user);
        requests.asMap().values().forEach(list -> list.remove(user));
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
        }
    }
}