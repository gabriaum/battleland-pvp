package com.gabriaum.arcade.game.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

@Getter
@RequiredArgsConstructor
public enum GameType {

    ARENA("Arena", Material.AIR),
    FPS("FPS", Material.GLASS),
    SHADOW("1v1", Material.BLAZE_ROD),
    LAVA("Lava", Material.LAVA_BUCKET),;

    private final String name;
    private final Material icon;
}
