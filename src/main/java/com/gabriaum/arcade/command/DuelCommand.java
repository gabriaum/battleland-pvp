package com.gabriaum.arcade.command;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.game.Game;
import com.gabriaum.arcade.game.list.shadow.inventory.CustomInventory;
import com.gabriaum.arcade.game.list.shadow.type.CombatType;
import com.gabriaum.arcade.game.type.GameType;
import com.gabriaum.arcade.manager.impl.ShadowConfiguration;
import com.gabriaum.arcade.object.Invite;
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
    private static final Cache<User, List<Invite>> requests = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());

        if (user.getOpponent() != null) {
            player.sendMessage("§4§lERRO §fVocê já está duelando.");
            return false;
        }

        if (args.length == 0) {
            player.sendMessage("§3§l1V1 §fUtilize: §b§l/duel§f <target> [custom]");
            return false;
        }

        System.out.println("1");

        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("ac")) {
                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage("§4§lERRO §fO jogador §b§l" + args[1] + "§f não existe.");
                    return false;
                }

                User targetUser = ArcadeMain.getPlugin().getUserManager().get(target.getUniqueId());
                boolean custom = player.getItemInHand().getType().equals(Material.IRON_FENCE);

                System.out.println("2");

                return handleAcceptDuel(user, targetUser, custom, args);
            }
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage("§4§lERRO §fO jogador §b§l" + args[0] + "§f não existe.");
            return false;
        }

        User targetUser = ArcadeMain.getPlugin().getUserManager().get(target.getUniqueId());
        boolean custom = args.length > 1 && args[1].equalsIgnoreCase("custom");

        System.out.println("3");

        List<Invite> playerInvites = requests.getIfPresent(user);
        List<Invite> targetInvites = requests.getIfPresent(targetUser);

        System.out.println("4");

        if (playerInvites == null || playerInvites.isEmpty())
            playerInvites = new ArrayList<>();

        if (targetInvites == null || targetInvites.isEmpty())
            targetInvites = new ArrayList<>();

        if (!playerInvites.isEmpty()) {
            boolean has = false;

            for (Invite invite : playerInvites) {
                if (invite.getSender().equals(targetUser) && (invite.isCustom() == custom)) {
                    has = true;
                    break;
                }
            }

            if (has) {
                System.out.println("5");

                return handleAcceptDuel(user, targetUser, custom, args);
            }
        }

        boolean hasInvited = false;

        if (!targetInvites.isEmpty()) {
            for (Invite invite : targetInvites) {
                if (invite.getSender().equals(user) && (invite.isCustom() == custom)) {
                    hasInvited = true;
                    break;
                }
            }
        }

        if (hasInvited) {
            player.sendMessage("§4§lERRO §fVocê já enviou um pedido de duelo para §b§l" + target.getName());
            return false;
        }

        ShadowConfiguration configuration = null;

        if (custom) {
            try (Jedis jedis = CorePlugin.getInstance().getJedisManager().getJedisPool().getResource()) {
                if (jedis.exists("shadow:" + player.getUniqueId() + ":config:" + target.getUniqueId())) {
                    configuration = CorePlugin.GSON.fromJson(
                            jedis.get("shadow:" + player.getUniqueId() + ":config:" + target.getUniqueId()),
                            ShadowConfiguration.class
                    );

                    jedis.del("shadow:" + player.getUniqueId() + ":config:" + target.getUniqueId());
                }
            }
        }

        Invite invite = Invite.builder()
                .sender(user)
                .configuration(configuration)
                .custom(custom)
                .build();

        targetInvites.add(invite);
        requests.put(targetUser, targetInvites);

        player.sendMessage("§aVocê enviou um pedido de duelo " + (custom ? "customizado " : "") + "para " + target.getName());
        target.sendMessage("§eVocê recebeu um pedido de duelo " + (custom ? "customizado " : "") + "de " + player.getName());
        return true;
    }

    private boolean handleAcceptDuel(User player, User target, boolean custom, String[] args) {
        Player p = Bukkit.getPlayer(player.getUniqueId());
        Player t = Bukkit.getPlayer(target.getUniqueId());

        Invite invite = requests.getIfPresent(player).stream().filter(i -> i.getSender().equals(target) && i.isCustom() == custom).findFirst().orElse(null);

        if (invite == null)
            invite = requests.getIfPresent(target).stream().filter(i -> i.getSender().equals(player) && i.isCustom() == custom).findFirst().orElse(null);

        if (invite == null) {
            p.sendMessage("§4§lERRO §fVocê não recebeu nenhum pedido de duelo.");
            return false;
        }

        if (invite.getConfiguration() != null) {
            ArcadeMain.getPlugin().getShadowManager().put(p.getUniqueId(), invite.getConfiguration());
            ArcadeMain.getPlugin().getShadowManager().put(t.getUniqueId(), invite.getConfiguration());
        }

        requests.invalidate(player);
        requests.invalidate(target);

        player.setOpponent(target);
        target.setOpponent(player);

        player.getGame().onJoin(p);
        target.getGame().onJoin(t);
        return true;
    }
}