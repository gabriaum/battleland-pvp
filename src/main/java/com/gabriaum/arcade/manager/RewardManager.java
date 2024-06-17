package com.gabriaum.arcade.manager;

import lombok.Getter;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RewardManager {

    @Getter
    private static final Map<UUID, List<Wolf>> wolves = new HashMap<>();

    @Getter
    private static final Map<UUID, Zombie> zombieGuards = new HashMap<>();
}
