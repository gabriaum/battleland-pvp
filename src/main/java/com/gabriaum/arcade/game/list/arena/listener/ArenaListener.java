package com.gabriaum.arcade.game.list.arena.listener;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.event.type.arena.KitEvent;
import com.gabriaum.arcade.game.Game;
import com.gabriaum.arcade.game.list.arena.inventory.KitInventory;
import com.gabriaum.arcade.game.list.arena.inventory.ShopInventory;
import com.gabriaum.arcade.game.list.arena.inventory.WarpInventory;
import com.gabriaum.arcade.game.list.arena.kit.type.KitType;
import com.gabriaum.arcade.game.type.GameType;
import com.gabriaum.arcade.user.User;
import com.google.gson.JsonObject;
import com.solexgames.core.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ArenaListener implements Listener {

    @EventHandler
    public void move(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());

        if (user == null)
            return;

        Game game = user.getGame();

        if (!game.getType().equals(GameType.ARENA))
            return;

        JsonObject arenaObject = ArcadeMain.getPlugin().getMap().getAsJsonObject("arena");

        Location location = player.getLocation();
        Location spawn = CorePlugin.GSON.fromJson(arenaObject.get("spawn"), Location.class);

        if (spawn == null)
            return;

        if (user.isProtect() && location.distance(spawn) >= 28) {
            user.setProtect(false);
            game.sendKit(player);

            player.sendMessage("§8§lPROTEÇÃO §fVocê §7§lPERDEU §fsua proteção do spawn");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void selector(KitEvent event) {
        User user = event.getUser();
        Player player = Bukkit.getPlayer(user.getUniqueId());
        KitType kit = event.getKit();

        user.setKit(kit);
        player.sendMessage("§e§lKIT §fVocê selecionou o kit §e" + kit.getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void interact(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction().name().contains("RIGHT_")) {
            ItemStack item = event.getItem();

            if (item == null)
                return;

            ItemMeta meta = item.getItemMeta();

            if (meta != null && meta.hasDisplayName()) {
                if (meta.getDisplayName().contains("Kits")) {
                    new KitInventory(player).open(player);
                    return;
                }

                if (meta.getDisplayName().contains("Warps")) {
                    new WarpInventory().open(player);
                    return;
                }

                if (meta.getDisplayName().contains("Loja")) {
                    new ShopInventory(player).open(player);
                    return;
                }

                if (meta.getDisplayName().contains("Evento")) {
                    player.sendMessage("§4§lERRO §fEm desenvolvimento!");
                    return;
                }
            }
        }
    }
}
