package com.gabriaum.arcade.command;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.game.type.GameType;
import com.google.gson.JsonObject;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.FileWriter;
import java.util.Arrays;

@Command(label = "setlocation", permission = "arcade.pvp.location")
public class LocationCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;
        Location location = player.getLocation().clone();

        if (args.length < 1) {
            player.sendMessage("§3§lLOCATION §fUtilize: §b§l/setlocation §f<warp> <type>");
            return true;
        }

        GameType game = Arrays.stream(GameType.values()).filter(type -> type.name().equalsIgnoreCase(args[0])).findFirst().orElse(null);

        if (game == null)
            return true;

        JsonObject map = ArcadeMain.getPlugin().getMap();

        switch (game) {
            case ARENA: {
                JsonObject arenaObject = map.getAsJsonObject("arena");

                if (args.length < 2) {
                    player.sendMessage("§3§lLOCATION §fUtilize: §b§l/setlocation §f<warp> <spawn, feast-center>");
                    return true;
                }

                String type = args[1].toLowerCase();

                if (!Arrays.asList("spawn", "feast-center").contains(type)) {
                    player.sendMessage("§3§lLOCATION §fUtilize: §b§l/setlocation §f<warp> <spawn, feast-center>");
                    return true;
                }

                if (arenaObject.has(type))
                    arenaObject.remove(type);

                arenaObject.add(type, CorePlugin.GSON.toJsonTree(location));
                map.add(type, arenaObject);
                break;
            }

            case FPS: {
                JsonObject arenaObject = map.getAsJsonObject("fps");

                if (arenaObject.has("spawn"))
                    arenaObject.remove("spawn");

                arenaObject.add("spawn", CorePlugin.GSON.toJsonTree(location));
                map.add("fps", arenaObject);
                break;
            }

            case LAVA: {
                JsonObject arenaObject = map.getAsJsonObject("lava");

                if (arenaObject.has("spawn"))
                    arenaObject.remove("spawn");

                arenaObject.add("spawn", CorePlugin.GSON.toJsonTree(location));
                map.add("lava", arenaObject);
                break;
            }

            case SHADOW: {
                if (args.length < 2) {
                    player.sendMessage("§3§lLOCATION §fUtilize: §b§l/setlocation §f<warp> <spawn, red, blue>");
                    return true;
                }

                JsonObject shadowObject = map.getAsJsonObject("shadow");
                String type = args[1].toLowerCase();

                if (!Arrays.asList("spawn", "red", "blue").contains(type)) {
                    player.sendMessage("§3§lLOCATION §fUtilize: §b§l/setlocation §f<warp> <spawn, red, blue>");
                    return true;
                }

                if (shadowObject.has(type))
                    shadowObject.remove(type);

                shadowObject.add(type, CorePlugin.GSON.toJsonTree(location));
                map.add("shadow", shadowObject);
                break;
            }
        }

        try (FileWriter fileWriter = new FileWriter(ArcadeMain.getPlugin().getDataFolder() + "/map.json")) {
            fileWriter.write(CorePlugin.GSON.toJson(map));
            fileWriter.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        player.sendMessage("§3§lLOCATION §fLocalização da warp §b§l" + args[0] + " §fdefinida com sucesso.");
        return false;
    }
}
