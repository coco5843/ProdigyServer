package fr.cocoraid.prodigyserver.minigame;

import fr.cocoraid.prodigyserver.ProdigyServer;
import fr.cocoraid.prodigyserver.minigame.games.MiniGame;
import fr.cocoraid.prodigyserver.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MinigameManager {


    private FileConfiguration config = ProdigyServer.getInstance().getConfig();


    //this class make players join the queues
    private List<Player> queue = new ArrayList<>();


    private LinkedList<GameWorld> gameWorlds = new LinkedList<>();
    private MiniGame currentGame;


    public MinigameManager() {


       /* File file = new File(Bukkit.getServer().getWorldContainer(), "minigame");
        boolean worldExists = file.exists();

       WorldCreator wc = new WorldCreator("minigame");
        wc.environment(World.Environment.NORMAL);
        wc.generator(new VoidGenerator());
        Bukkit.createWorld(wc);

        World w = Bukkit.getWorld("minigame");
        w.setTime(0);
        w.setDifficulty(Difficulty.PEACEFUL);
        w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        w.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        w.setThundering(false);
        w.setStorm(false);*/



        //load worlds
        gameWorlds.add(new GameWorld( "pizzaspleef", -7, 88, -19));
        gameWorlds.add(new GameWorld("missiledodge",20, 50, -3));
        gameWorlds.add(new GameWorld("gladiator",0, 56, 0));
        gameWorlds.add(new GameWorld("battleship",-29, 54, 6));


        gameWorlds.forEach(g -> {
            File file = new File(Bukkit.getServer().getWorldContainer(), g.getGameName());
            if(file.exists()) {
                try {
                    FileUtils.deleteDirectory(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

        try {
            FileUtils.copyDirectory(new File(Bukkit.getWorldContainer().getPath() + "/" + "minigames"), Bukkit.getServer().getWorldContainer());
        } catch(IOException e) {
            e.printStackTrace();
        }

        gameWorlds.forEach(g -> {
            //config.addDefault(g.getGameName(), false);
           // File file = new File(Bukkit.getServer().getWorldContainer(), g.getGameName());
            //boolean worldExists = file.exists();


            WorldCreator wc = new WorldCreator(g.getGameName());

            Bukkit.createWorld(wc);

            World w = Bukkit.getWorld(g.getGameName());
            w.setAutoSave(false);
            w.setTime(0);
            w.setKeepSpawnInMemory(false);
            w.setDifficulty(Difficulty.PEACEFUL);
            w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            w.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            w.setThundering(false);
            w.setStorm(false);

            g.register();


        });
       /* config.options().copyDefaults(true);
        ProdigyServer.getInstance().saveConfig();*/



    }


    @EventHandler
    public void initWorld(WorldLoadEvent e) {
        if(gameWorlds.stream().filter(g -> g.getGameName().equalsIgnoreCase(e.getWorld().getName())).findAny().isPresent()) {
            Utils.broadcastAdmin("§aNew world " + e.getWorld().getName() + " has been loaded !");
        }
    }

    public GameWorld getGameWorld(String game) {
        return gameWorlds.stream().filter(g -> g.getGameName().equalsIgnoreCase(game)).findAny().get();
    }

    public void setCurrentGame(MiniGame currentGame) {
        this.currentGame = currentGame;
    }

    public MiniGame getCurrentGame() {
        return currentGame;
    }

    public boolean isCurrentGameRunning() {
        return currentGame != null && currentGame.isStarted() && !currentGame.isFinished();
    }


    public LinkedList<GameWorld> getGameWorlds() {
        return gameWorlds;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public List<Player> getQueue() {
        return queue;
    }
}
