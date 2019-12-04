package fr.cocoraid.prodigyserver.minigame;


import fr.cocoraid.prodigyserver.ProdigyServer;
import fr.cocoraid.prodigyserver.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public class GameWorld implements Listener {


    private double x,y,z;
    private Location spawnPoint;



    private Location[] spawn;
    private String name;


    public interface WorldRegenCallback {

        public void onRegenDone();

    }

    public GameWorld(String name,double x, double y, double z) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        Bukkit.getPluginManager().registerEvents(this,ProdigyServer.getInstance());
    }

    public void register() {
        spawnPoint = new Location(Bukkit.getWorld(name),x,y,z);
    }

    private WorldRegenCallback callback;

    public void regenWorld(final WorldRegenCallback callback) {
        if(Bukkit.getWorld(name) != null) {
            //first unload and delete world
            World w = Bukkit.getWorld(name);
            Bukkit.getOnlinePlayers().stream().filter(p -> p.getWorld().equals(w)).forEach(p -> p.teleport(ProdigyServer.getInstance().getLobbyManager().getSpawn()));
            w.setAutoSave(false);
            Bukkit.getServer().unloadWorld(w, false);
            this.callback = callback;
            //then copy template

        }
    }


    @EventHandler
    public void unload(WorldUnloadEvent e) {
        if(e.getWorld().getName().equalsIgnoreCase(name)) {
            try {
                FileUtils.deleteDirectory(new File(Bukkit.getWorldContainer(), e.getWorld().getName()));
            } catch(IOException ex) {
                ex.printStackTrace();
            }
            Utils.broadcastAdmin("§aWorld for minigame " + name + " has been unloaded !");

            copy();

            new BukkitRunnable() {
                public void run() {
                    WorldCreator wc = new WorldCreator(name);
                    Bukkit.createWorld(wc);
                    World w = Bukkit.getWorld(name);
                    w.setTime(0);
                    w.setAutoSave(false);
                    w.setDifficulty(Difficulty.PEACEFUL);
                    w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                    w.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                    w.setThundering(false);
                    w.setKeepSpawnInMemory(false);
                    w.setStorm(false);
                    if(callback != null) {
                        callback.onRegenDone();
                        callback = null;
                    }
                    Utils.broadcastAdmin("§2World for minigame " + name + " has been loaded !");
                    spawnPoint = new Location(w,x,y,z);
                }
            }.runTaskLater(ProdigyServer.getInstance(),  20L);



        }
    }


    private void copy() {

        File srcDir = new File(Bukkit.getWorldContainer().getPath().equalsIgnoreCase(".") ? "minigames" :Bukkit.getWorldContainer().getPath() +File.pathSeparator+"minigames");
        if (!srcDir.exists()) {
            Bukkit.getLogger().warning("minigames folder does not exist!");
            return;
        }
        File destDir = Bukkit.getWorldContainer();
        try {
            FileUtils.copyDirectory(srcDir, destDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }



    public Location getSpawnPoint() {
        return spawnPoint;
    }

    public String getGameName() {
        return name;
    }


}
