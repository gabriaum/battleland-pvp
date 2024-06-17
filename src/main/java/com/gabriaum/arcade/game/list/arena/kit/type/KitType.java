package com.gabriaum.arcade.game.list.arena.kit.type;

import com.gabriaum.arcade.game.list.arena.kit.Kit;
import com.gabriaum.arcade.game.list.arena.kit.type.impl.*;
import com.gabriaum.arcade.user.User;
import com.solexgames.core.member.PvPMember;
import com.solexgames.core.util.builder.ItemBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public enum KitType {

    NONE(new Kit(Material.AIR, "Nenhum", new ArrayList<>(), "", 0, 0) {
        @Override
        public List<ItemStack> kitItems() {
            return new ArrayList<>();
        }
    }),

    ANCHOR(new Anchor()),
    BOXER(new Boxer()),
    CAMEL(new Camel()),
    FIREMAN(new Fireman()),
    HULK(new Hulk()),
    KANGAROO(new Kangaroo()),
    MONK(new Monk()),
    NINJA(new Ninja()),
    REAPER(new Reaper()),
    SNAIL(new Snail()),
    STOMPER(new Stomper()),
    THOR(new Thor()),
    TIMELORD(new Timelord()),
    VIPER(new Viper()),
    AJNIN(new Ajnin()),
    CRITICAL(new Critical()),
    FISHERMAN(new Fisherman()),
    FLASH(new Flash()),
    QUICKDROP(new Quickdrop()),
    SPIDER(new Spider()),
    PHANTOM(new Phantom()),
    SUPERNOVA(new Supernova());

    private final Kit kit;

    @Getter
    private static final List<KitType> kits = Arrays.asList(values());

    public static KitType read(String name) {
        return kits.stream().filter(kitType -> kitType.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public boolean hasPermission(UUID uniqueId) {
        Player player = Bukkit.getPlayer(uniqueId);
        User user = kit.getInstance().getUserManager().get(uniqueId);

        if (user == null)
            return false;

        PvPMember member = user.getMember();

        return player.isOp() || kit.getPermission().isEmpty() || member.hasPermission(kit.getPermission());
    }

    public Material getIcon() {
        return kit.getIcon();
    }

    public String getName() {
        return kit.getName();
    }

    public List<String> getDescription() {
        return kit.getDescription();
    }

    public int getPrice() {
        return kit.getPrice();
    }

    public int getCooldownTime() {
        return kit.getCooldownTime();
    }

}
