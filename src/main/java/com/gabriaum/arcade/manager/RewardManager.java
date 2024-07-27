package com.gabriaum.arcade.manager;

import com.gabriaum.arcade.user.User;
import lombok.Getter;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RewardManager {

    @Getter
    private static final Map<UUID, List<Wolf>> wolves = new HashMap<>();

    @Getter
    private static final Map<UUID, Zombie> zombieGuards = new HashMap<>();

    @Getter
    private static final Map<UUID, AtomicInteger> nucks = new HashMap<>();
}
