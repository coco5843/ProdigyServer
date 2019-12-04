package fr.cocoraid.prodigyserver.minigame.event;

import fr.cocoraid.prodigyserver.minigame.games.MiniGame;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameEndedEvent extends Event {



    private MiniGame game;

    public GameEndedEvent(MiniGame game) {
        this.game = game;

    }

    public MiniGame getGame() {
        return game;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}