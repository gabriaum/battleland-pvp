package com.gabriaum.arcade;

import com.gabriaum.arcade.command.*;
import com.gabriaum.arcade.event.type.UpdateEvent;
import com.gabriaum.arcade.listener.ListenerLoader;
import com.gabriaum.arcade.manager.CooldownManager;
import com.gabriaum.arcade.manager.GameManager;
import com.gabriaum.arcade.manager.QueueManager;
import com.gabriaum.arcade.manager.UserManager;
import com.gabriaum.arcade.user.User;
import com.gabriaum.arcade.util.structure.type.Feast;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;

@Getter
public class ArcadeMain extends JavaPlugin {
    @Getter
    private static ArcadeMain plugin;

    private JsonObject map;
    private Feast feast;

    private GameManager gameManager;
    private UserManager userManager;
    private QueueManager queueManager;
    private CooldownManager cooldownManager;

    @Setter
    private User topKillStreak;

    @Override
    public void onLoad() {
        plugin = this;
        saveResource("map.json", false);

        try {
            map = new JsonParser().parse(new FileReader(new File(getDataFolder(), "map.json"))).getAsJsonObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        feast = new Feast();
        ListenerLoader listener = new ListenerLoader(this);

        listener.load();

        gameManager = new GameManager();
        userManager = new UserManager();
        queueManager = new QueueManager();
        cooldownManager = new CooldownManager();

        gameManager.load();

        new LocationCommand();
        new SpawnCommand();
        new DuelCommand();
        new LeagueCommand();

        getServer().getScheduler().runTaskTimer(this, () -> new UpdateEvent().pulse(), 1, 20);
    }
}
