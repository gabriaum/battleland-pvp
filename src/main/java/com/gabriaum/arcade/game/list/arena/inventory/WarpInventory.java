package com.gabriaum.arcade.game.list.arena.inventory;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.game.Game;
import com.gabriaum.arcade.game.type.GameType;
import com.gabriaum.arcade.user.User;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class WarpInventory extends AbstractInventoryMenu {

    public WarpInventory() {
        super("§b§lWarps", 9);

        update();
    }

    @Override
    public void update() {
        inventory.clear();

        int slot = 0;

        for (GameType game : GameType.values()) {
            if (game.getIcon().equals(Material.AIR))
                continue;

            inventory.setItem(slot++, new ItemBuilder(game.getIcon())
                    .setDisplayName("§b" + game.getName())
                    .create());
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR))
            return;
        
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());

        if (user == null)
            return;

        ItemStack clickedItem = event.getCurrentItem();

        Game game = null;

        for (GameType gameType : GameType.values()) {
            if (clickedItem.getType().equals(gameType.getIcon()) && clickedItem.getItemMeta().getDisplayName().equals("§b" + gameType.getName())) {
                game = ArcadeMain.getPlugin().getGameManager().get(gameType);
                break;
            }
        }

        player.closeInventory();

        if (game != null) {
            user.setGame(game);
            game.onJoin(player);
        }
    }
}
