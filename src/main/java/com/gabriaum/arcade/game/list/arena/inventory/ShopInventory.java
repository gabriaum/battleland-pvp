package com.gabriaum.arcade.game.list.arena.inventory;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.event.type.arena.KitEvent;
import com.gabriaum.arcade.game.list.arena.kit.type.KitType;
import com.gabriaum.arcade.user.User;
import com.solexgames.core.member.PvPMember;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.util.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;
import java.util.List;

public class ShopInventory extends AbstractInventoryMenu {
    private final Player player;

    private int currentPage;
    private int totalPages;

    public ShopInventory(Player player) {
        super("§b§lLoja", 9 * 5);

        this.player = player;
        this.currentPage = 0;
        this.totalPages = (int) Math.ceil((double) KitType.values().length / 21);

        update();
    }

    @Override
    public void update() {
        inventory.clear();

        int slot = 10;
        int last = slot;

        KitType[] kits = getAvailableKits();
        int start = currentPage * 21;
        int end = Math.min(start + 21, kits.length);

        for (int i = start; i < end; i++) {
            KitType kit = kits[i];

            if (kit.equals(KitType.NONE) || kit.hasPermission(player.getUniqueId()))
                continue;

            ItemBuilder item = new ItemBuilder(kit.getIcon())
                    .setDisplayName("§a" + kit.getName())
                    .addLore(kit.getDescription());

            inventory.setItem(slot++, item.create());

            if (slot == (last + 7)) {
                slot += 2;
                last = slot;
            }
        }

        if (inventory.getContents().length == 0) {
            inventory.setItem(4 * 9, new ItemBuilder(Material.WEB).setDisplayName("§cVocê já tem todos os kits!").create());
            return;
        }

        if (currentPage > 0)
            inventory.setItem(4 * 9, new ItemBuilder(Material.ARROW).setDisplayName("§aPágina Anterior").create());

        if (currentPage < totalPages - 1)
            inventory.setItem((4 * 9) + 8, new ItemBuilder(Material.ARROW).setDisplayName("§aPróxima Página").create());
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR))
            return;
        
        event.setCancelled(true);

        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());
        PvPMember member = user.getMember();

        if (member == null)
            return;

        int slot = event.getSlot();

        if (slot == (4 * 9) && currentPage > 0) {
            currentPage--;
            update();
            return;
        }

        if (slot == (4 * 9) + 8 && currentPage < totalPages - 1) {
            currentPage++;
            update();
            return;
        }

        KitType kit = getKitBySlot(slot);

        if (kit == null || kit.hasPermission(player.getUniqueId()))
            return;

        if (member.getCoins() < kit.getPrice()) {
            player.sendMessage("§4§lERRO§f Você não tem §e§lCOINS§f o suficiente!");
            player.closeInventory();
            return;
        }

        member.removeCoins(kit.getPrice());
        member.addPermission(kit.getKit().getPermission());

        player.sendMessage("§2§lKIT §fVocê comprou o kit " + kit.getName() + " por §e§l" + kit.getPrice() + " COINS§f!");
        player.closeInventory();
    }

    private KitType getKitBySlot(int slot) {
        if (slot < 10 || (slot >= 17 && slot <= 18) || (slot >= 26 && slot <= 27) || (slot >= 35 && slot <= 36) || slot >= 44)
            return null;

        int relativeSlot = slot;
        if (slot > 16) relativeSlot -= 2;
        if (slot > 25) relativeSlot -= 2;
        if (slot > 34) relativeSlot -= 2;

        int start = currentPage * 21;
        int index = start + (relativeSlot - 10);

        KitType[] kits = getAvailableKits();

        if (index >= 0 && index < kits.length) {
            return kits[index];
        }
        return null;
    }


    private int getKitCount() {
        return (int) Arrays.stream(KitType.values()).filter(kit -> !kit.equals(KitType.NONE)).count();
    }

    private KitType[] getAvailableKits() {
        return Arrays.stream(KitType.values()).filter(kit -> !kit.equals(KitType.NONE)).toArray(KitType[]::new);
    }
}