package com.gabriaum.arcade.manager;

import com.gabriaum.arcade.manager.impl.ShadowConfiguration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShadowManager extends HashMap<UUID, Map<UUID, ShadowConfiguration>> {

    public ShadowConfiguration getShadowConfiguration(UUID uniqueId) {
        if (containsKey(uniqueId))
            return get(uniqueId).values().stream().findFirst().orElse(null);

        ShadowConfiguration configuration = null;
        Collection<Map<UUID, ShadowConfiguration>> configurations = values();

        for (Map<UUID, ShadowConfiguration> map : configurations) {
            if (map.containsKey(uniqueId)) {
                configuration = map.get(uniqueId);
                break;
            }
        }

        return configuration;
    }

    public void removeAll(UUID uniqueId) {
        if (containsKey(uniqueId)) {
            remove(uniqueId);
            return;
        }
    }
}
