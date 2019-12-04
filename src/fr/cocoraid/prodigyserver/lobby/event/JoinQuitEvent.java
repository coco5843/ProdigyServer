package fr.cocoraid.prodigyserver.lobby.event;

import fr.cocoraid.prodigyserver.ProdigyServer;
import fr.cocoraid.prodigyserver.minigame.event.GameEndedEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitEvent implements Listener {

    @EventHandler
    public void join(PlayerJoinEvent e) {
        e.getPlayer().teleport(ProdigyServer.getInstance().getLobbyManager().getSpawn());
        e.setJoinMessage("§3[ProdigyServer] §b" + e.getPlayer().getName() + " §3joins the server");
        if(ProdigyServer.getInstance().getMinigameManager().isCurrentGameRunning()) {
            e.getPlayer().sendMessage("§bA game is already running, we will send you a sound notification after ended");
        }
    }

    @EventHandler
    public void gameEnded(GameEndedEvent e) {
        Bukkit.getOnlinePlayers().stream().filter(cur -> !e.getGame().getPlayers().contains(cur) && !e.getGame().getSpectators().contains(cur)).forEach(cur -> {
            cur.playSound(cur.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
            cur.sendMessage("§aA game has just ended ! Be prepared the admin will probably start a new one !");
        });

    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {

    }
}
