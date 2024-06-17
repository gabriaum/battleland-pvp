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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Command(label = "duel", aliases = {"1v1", "1vs1"})
public class DuelCommand extends BaseCommand {

    protected final Cache<User, List<User>> requests = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;
        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());

        if (user == null || !user.isProtect() || !user.getGame().getType().equals(GameType.SHADOW) || user.getOpponent() != null)
            return false;

        Game game = user.getGame();

        if (args.length < 1) {
            player.sendMessage("§3§l1V1 §fUtilize: §b§l/" + label + "§f <target>");
            return false;
        }

        if (args[0].equalsIgnoreCase("ac")) {
            if (args.length < 2) {
                player.sendMessage("§3§l1V1 §fUtilize: §b§l/" + label + "§f ac <target>");
                return false;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                player.sendMessage("§c§lERRO §fO jogador §b§l" + args[1] + "§f não existe.");
                return true;
            }

            User targetUser = ArcadeMain.getPlugin().getUserManager().get(target.getUniqueId());

            if (targetUser == null) {
                player.sendMessage("§c§lERRO §fO jogador §b§l" + args[1] + "§f não existe.");
                return true;
            }

            JedisManager jedisManager = CorePlugin.getInstance().getJedisManager();

            if (requests.getIfPresent(user) != null && !requests.getIfPresent(user).isEmpty() && requests.getIfPresent(user).contains(targetUser)) {
                requests.getIfPresent(user).remove(targetUser);

                if (requests.getIfPresent(targetUser) != null)
                    requests.getIfPresent(targetUser).remove(user);

                user.setOpponent(targetUser);
                targetUser.setOpponent(user);

                try (Jedis jedis = jedisManager.getJedisPool().getResource()) {
                    ShadowConfiguration configuration = CorePlugin.GSON.fromJson(jedis.get("shadow:config:" + target.getUniqueId()), ShadowConfiguration.class);

                    if (configuration != null) {
                        jedis.del("shadow:config:" + target.getUniqueId());

                        jedis.setex("shadow:config:" + player.getUniqueId(), 5, CorePlugin.GSON.toJson(configuration));
                        jedis.setex("shadow:config:" + target.getUniqueId(), 5, CorePlugin.GSON.toJson(configuration));
                    }
                }

                game.onJoin(player);

                player.sendMessage("§aVocê aceitou o pedido de duelo de " + target.getName());
                target.sendMessage("§aO jogador " + player.getName() + " aceitou o seu pedido de duelo.");

                return true;
            }

            player.sendMessage("§c§lERRO §fVocê não recebeu um pedido de duelo de §b§l" + target.getName());
            return true;
        }

        Player target = player.getServer().getPlayer(args[0]);

        if (target == null) {
            player.sendMessage("§c§lERRO §fO jogador §b§l" + args[0] + "§f não existe.");
            return true;
        }

        User targetUser = ArcadeMain.getPlugin().getUserManager().get(target.getUniqueId());

        if (targetUser.getOpponent() != null) {
            player.sendMessage("§c§lERRO §fO jogador §b§l" + args[0] + "§f já está duelando.");
            return true;
        }

        if (requests.getIfPresent(targetUser) != null && !requests.getIfPresent(targetUser).isEmpty() && requests.getIfPresent(targetUser).contains(user)) {
            player.sendMessage("§c§lERRO §fVocê já enviou um pedido de duelo para §b§l" + target.getName());
            return true;
        }

        boolean custom = args.length >= 2 && args[1].equalsIgnoreCase("custom");

        if (!custom) {
            if (requests.getIfPresent(targetUser) != null && !requests.getIfPresent(targetUser).isEmpty() && requests.getIfPresent(targetUser).contains(targetUser)) {
                requests.getIfPresent(targetUser).remove(user);

                if (requests.getIfPresent(user) != null)
                    requests.getIfPresent(user).remove(targetUser);

                user.setOpponent(targetUser);
                targetUser.setOpponent(user);

                game.onJoin(player);

                player.sendMessage("§aVocê aceitou o pedido de duelo de " + target.getName());
                target.sendMessage("§aO jogador " + player.getName() + " aceitou o seu pedido de duelo.");
                return true;
            }
        }

        if (requests.getIfPresent(target) == null)
            requests.put(targetUser, new ArrayList<>(Arrays.asList(user)));
        else {
            List<User> list = requests.getIfPresent(targetUser);

            list.add(user);

            requests.asMap().replace(targetUser, list);
        }

        TextComponent accept = new TextComponent("§eClique §b§lAQUI§e para aceitar!");

        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel ac " + player.getName()));

        player.sendMessage("§aVocê enviou um pedido de duelo " + (custom ? "customizado " : "" ) + "para " + target.getName());
        target.sendMessage("§eVocê recebeu um pedido de duelo " + (custom ? "customizado " : "" ) + "de " + player.getName());
        target.spigot().sendMessage(accept);
        return false;
    }
}
