package com.gabriaum.arcade.event.type.arena;

import com.gabriaum.arcade.event.EventBuilder;
import com.gabriaum.arcade.game.list.arena.kit.type.KitType;
import com.gabriaum.arcade.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class KitEvent extends EventBuilder {

    private final User user;
    private final KitType kit;
}
