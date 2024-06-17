package com.gabriaum.arcade.user;

import com.gabriaum.arcade.game.Game;
import com.gabriaum.arcade.game.list.arena.kit.type.KitType;
import com.solexgames.core.board.ScoreBoard;
import com.solexgames.core.member.PvPMember;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Zombie;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class User {

    private final UUID uniqueId;
    private final PvPMember member;

    private Game game;
    private ScoreBoard scoreBoard;

    private KitType kit = KitType.NONE;
    private User opponent;
    private Zombie zombie;

    private boolean protect = true;
}
