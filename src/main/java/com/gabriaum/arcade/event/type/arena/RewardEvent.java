package com.gabriaum.arcade.event.type.arena;

import com.gabriaum.arcade.event.EventBuilder;
import com.gabriaum.arcade.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RewardEvent extends EventBuilder {

    private final User user;
    private final int streak;
}
