package com.gabriaum.arcade.object;

import com.gabriaum.arcade.manager.impl.ShadowConfiguration;
import com.gabriaum.arcade.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class Invite {

    private User sender;

    private ShadowConfiguration configuration;
    private boolean custom;
}
