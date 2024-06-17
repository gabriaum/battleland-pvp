package com.gabriaum.arcade.manager;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.game.Game;
import com.gabriaum.arcade.game.type.GameType;
import com.solexgames.core.util.loader.ClassLoader;

import java.util.HashMap;

public class GameManager extends HashMap<GameType, Game> {

    public void load() {
        for (Class<?> clazz : ClassLoader.getClassesForPackage(ArcadeMain.getPlugin(), "com.gabriaum.arcade.game.list")) {
            try {
                if (Game.class.isAssignableFrom(clazz)) {
                    Game game = (Game) clazz.newInstance();

                    put(game.getType(), game);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
