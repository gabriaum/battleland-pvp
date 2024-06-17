package com.gabriaum.arcade.listener;

import com.solexgames.core.util.loader.ClassLoader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@RequiredArgsConstructor
public class ListenerLoader {

    private final JavaPlugin plugin;

    public void load() {
        for (Class<?> clazz : ClassLoader.getClassesForPackage(plugin, "com.gabriaum.arcade")) {
            try {
                if (Listener.class.isAssignableFrom(clazz)) {
                    Listener listener = (Listener) clazz.newInstance();

                    plugin.getServer().getPluginManager().registerEvents(listener, plugin);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
