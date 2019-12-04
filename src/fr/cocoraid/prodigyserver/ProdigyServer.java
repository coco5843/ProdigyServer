package fr.cocoraid.prodigyserver;

import fr.cocoraid.prodigyserver.command.GameCMD;
import fr.cocoraid.prodigyserver.command.admin.AdminCMD;
import fr.cocoraid.prodigyserver.ezcommand.CommandRegistry;
import fr.cocoraid.prodigyserver.lobby.LobbyManager;
import fr.cocoraid.prodigyserver.lobby.event.CancelEvent;
import fr.cocoraid.prodigyserver.lobby.event.JoinQuitEvent;
import fr.cocoraid.prodigyserver.minigame.MinigameManager;
import fr.cocoraid.prodigyserver.minigame.event.GameUpdateEvent;
import fr.cocoraid.prodigyserver.protocol.InteractableSchematicProtocol;
import fr.cocoraid.prodigyserver.updater.Updater;
import fr.cocoraid.prodigyserver.utils.ArmorstandSchematic;
import fr.cocoraid.prodigyserver.utils.GlowEntity;
import fr.cocoraid.prodigyserver.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public class ProdigyServer extends JavaPlugin implements Listener {

    private static ProdigyServer instance;
    private MinigameManager minigameManager;
    private LobbyManager lobbyManager;

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Updater(this), 1L, 1L);
        Bukkit.getPluginManager().registerEvents(this,this);

        lobbyManager = new LobbyManager();
        minigameManager = new MinigameManager();
        Bukkit.getPluginManager().registerEvents(new CancelEvent(),instance);
        Bukkit.getPluginManager().registerEvents(new GameUpdateEvent(),instance);
        Bukkit.getPluginManager().registerEvents(new JoinQuitEvent(),instance);
        CommandRegistry.register(new GameCMD());
        CommandRegistry.register(new AdminCMD());

        ArmorstandSchematic.loadFile("Cannon");

        new InteractableSchematicProtocol(this);

        GlowEntity.setupColorTeams();

    }

    @Override
    public void onDisable() {
        minigameManager.getGameWorlds().forEach(g -> {
            Bukkit.getOnlinePlayers().stream().filter(cur -> cur.getWorld().equals(g.getSpawnPoint().getWorld())).forEach(cur -> cur.teleport(Bukkit.getWorlds().get(0).getSpawnLocation()));
            Bukkit.getWorld(g.getGameName()).setAutoSave(false);
            Bukkit.unloadWorld(g.getGameName(),false);
        });

        GlowEntity.clearColorTeams();
    }




    public static ProdigyServer getInstance() {
        return instance;
    }

    public MinigameManager getMinigameManager() {
        return minigameManager;
    }

    public LobbyManager getLobbyManager() {
        return lobbyManager;
    }
}
