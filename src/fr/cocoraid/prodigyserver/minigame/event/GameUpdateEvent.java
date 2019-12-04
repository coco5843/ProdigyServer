package fr.cocoraid.prodigyserver.minigame.event;

import fr.cocoraid.prodigyserver.ProdigyServer;
import fr.cocoraid.prodigyserver.minigame.MinigameManager;
import fr.cocoraid.prodigyserver.updater.UpdateType;
import fr.cocoraid.prodigyserver.updater.event.UpdaterEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GameUpdateEvent implements Listener {


    private MinigameManager mm = ProdigyServer.getInstance().getMinigameManager();

    @EventHandler
    public void update(UpdaterEvent e) {
        if(e.getType() == UpdateType.TICK) {
            if(mm.getCurrentGame() != null)
                mm.getCurrentGame().update();
        }

    }
}
