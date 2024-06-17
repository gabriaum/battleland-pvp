package com.gabriaum.arcade.util.structure;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public abstract class Structure {

    public abstract void generateChest();

    public abstract List<ItemStack> getItems();
}