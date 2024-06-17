package com.gabriaum.arcade.game.list.arena.kit;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.game.list.arena.kit.type.KitType;
import com.gabriaum.arcade.game.type.GameType;
import com.gabriaum.arcade.manager.CooldownManager;
import com.gabriaum.arcade.manager.impl.Cooldown;
import com.gabriaum.arcade.user.User;
import com.solexgames.core.util.builder.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public abstract class Kit {

    private final ArcadeMain instance = ArcadeMain.getPlugin();

    private final Material icon;
    private final String name;

    private final List<String> description;

    private final String permission;
    private final int price;

    private final int cooldownTime;

    public abstract List<ItemStack> kitItems();

    public boolean isFree() {
        return price == 0 || permission.isEmpty();
    }

    public boolean hasKitItems() {
        return !kitItems().isEmpty();
    }

    public boolean hasKit(UUID uniqueId) {
        User user = getInstance().getUserManager().get(uniqueId);

        if (user == null || !user.getGame().getType().equals(GameType.ARENA))
            return false;

        return user.getKit().getKit().getName().equalsIgnoreCase(getName());
    }

    public boolean isKitItem(ItemStack stack) {
        if (kitItems() == null || kitItems().isEmpty())
            return false;

        for (ItemStack special : kitItems()) {
            if (special.isSimilar(stack))
                return true;
        }

        return false;
    }

    public boolean hasCooldown(Player player) {
        String name = getName().toUpperCase();

        CooldownManager cooldownManager = ArcadeMain.getPlugin().getCooldownManager();

        if (cooldownManager.hasCooldown(player, "kit-" + name)) {
            Cooldown cooldown = cooldownManager.getCooldown(player, "kit-" + name);

            if (cooldown == null) return false;

            player.sendMessage("§cAguarde " + new DecimalFormat("#.#").format(cooldown.getRemaining())
                    + "s para usar o kit novamente!");

            return true;
        }

        return false;
    }

    public void addCooldown(UUID uniqueId) {
        ArcadeMain.getPlugin().getCooldownManager().addCooldown(uniqueId, "kit-" + name.toLowerCase(), getCooldownTime());
    }

    public void addCooldown(UUID uniqueId, long cooldown) {
        ArcadeMain.getPlugin().getCooldownManager().addCooldown(uniqueId, "kit-" + name.toLowerCase(), cooldown);
    }

    public boolean isUsing(KitType kit, Player player) {
        User user = instance.getUserManager().get(player.getUniqueId());

        if (user == null)
            return false;

        return user.getKit().getKit().getName().equalsIgnoreCase(kit.getName());
    }

    public void sendNeoMessage(Player player) {
        player.sendMessage("§cEste player está usando Neo!");
    }
}
