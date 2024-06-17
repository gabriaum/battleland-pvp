package com.gabriaum.arcade.event;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EventBuilder extends Event {

    @Getter
    private final static HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public void pulse() {
        Bukkit.getPluginManager().callEvent(this);
    }
}
