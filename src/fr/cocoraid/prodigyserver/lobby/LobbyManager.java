package fr.cocoraid.prodigyserver.lobby;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LobbyManager {

    private Location spawn;

    public LobbyManager() {
        spawn = new Location(Bukkit.getWorld("lobby"),300 + 0.5,50,-1171 + 0.5);
    }

    public Location getSpawn() {
        return spawn;
    }

    public World getWorld() {
        return spawn.getWorld();
    }
}
