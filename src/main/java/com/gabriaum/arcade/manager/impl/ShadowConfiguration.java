package com.gabriaum.arcade.manager.impl;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

@Getter
@Builder
public class ShadowConfiguration {

    private String swordMaterialName;
    private String armorMaterialName;
    private boolean sharpness;
    private boolean onlyHotbar;
    private boolean mushroomRecraft;
}
