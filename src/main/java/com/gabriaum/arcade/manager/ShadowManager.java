package com.gabriaum.arcade.manager;

import com.gabriaum.arcade.manager.impl.ShadowConfiguration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShadowManager extends HashMap<UUID, ShadowConfiguration> {

    public ShadowConfiguration getShadowConfiguration(UUID uniqueId) {
        if (containsKey(uniqueId))
            return get(uniqueId);

        return null;
    }

    public void removeAll(UUID uniqueId) {
        if (containsKey(uniqueId)) {
            remove(uniqueId);
            return;
        }
    }
}
