package fr.cocoraid.prodigyserver.command.admin;

import fr.cocoraid.prodigyserver.ProdigyServer;
import fr.cocoraid.prodigyserver.ezcommand.CommandInfo;
import fr.cocoraid.prodigyserver.ezcommand.EzCommand;
import fr.cocoraid.prodigyserver.minigame.GameWorld;
import fr.cocoraid.prodigyserver.minigame.MinigameManager;
import fr.cocoraid.prodigyserver.minigame.games.battleship.BattleshipGame;
import fr.cocoraid.prodigyserver.minigame.games.battleship.Cannon;
import fr.cocoraid.prodigyserver.minigame.games.gladiator.GladiatorGame;
import fr.cocoraid.prodigyserver.minigame.worldgenerator.VoidGenerator;
import fr.cocoraid.prodigyserver.utils.ArmorstandSchematic;
import fr.cocoraid.prodigyserver.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.io.File;
import java.util.List;

@CommandInfo(name = "admin", desc = "admin commands", perm = "ps.admin",aliases = {"ad"})
public class AdminCMD extends EzCommand {

    private String worldLoaded;
    private MinigameManager game = ProdigyServer.getInstance().getMinigameManager();

    public void onCommand(Player p, String[] args) {
        if(args.length == 0) {
            p.sendMessage("§3/admin world <name>");
            p.sendMessage("§3/admin regen <name>");

            BattleshipGame g = new BattleshipGame();
            g.startCooldown(10);
            game.setCurrentGame(g);

            Bukkit.getOnlinePlayers().forEach(cur -> game.getQueue().add(cur));


        } else if(args.length == 2) {
            if(args[0].equalsIgnoreCase("world")) {
                if(Bukkit.getWorld(args[1]) == null) {
                    File file = new File(ProdigyServer.getInstance().getServer().getWorldContainer(), args[1]);
                    if(!file.exists()) {
                        p.sendMessage("§cSorry, world folder not found...");
                    } else {
                        p.sendMessage("§aWorld is loading, please wait....");
                        worldLoaded = args[1];
                        Bukkit.createWorld(WorldCreator.name(args[1]));
                    }
                } else {
                    p.setGameMode(GameMode.SPECTATOR);
                    p.teleport(Bukkit.getWorld(args[1]).getSpawnLocation());
                }
            } else if(args[0].equalsIgnoreCase("regen")) {
                if(Bukkit.getWorld(args[1]) == null) {
                  p.sendMessage("§cMinigame world not found :/");
                } else {
                    p.sendMessage("§bThe world is regenerating !");
                   ProdigyServer.getInstance().getMinigameManager().getGameWorld(args[1]).regenWorld(new GameWorld.WorldRegenCallback() {
                       @Override
                       public void onRegenDone() {
                           p.sendMessage("§3The world " + args[1] + " is now regen !");
                       }
                   });
                }
            } else if(args[0].equalsIgnoreCase("newworld")) {
                WorldCreator wc = new WorldCreator(args[1]);
                wc.generator(new VoidGenerator());
                worldLoaded = args[1];
                Bukkit.createWorld(wc);
                World w = Bukkit.getWorld(args[1]);
                w.setTime(0);
                w.setAutoSave(false);
                w.setDifficulty(Difficulty.PEACEFUL);
                w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                w.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                w.setThundering(false);
                w.setKeepSpawnInMemory(false);
                w.setStorm(false);

            }

        }


    }


    @EventHandler
    public void initWorld(WorldLoadEvent e) {
        if(worldLoaded != null && e.getWorld().getName().equalsIgnoreCase(worldLoaded)) {
            TextComponent msg = new TextComponent("§aClick to join the world ! §2[JOIN WORLD]");
            msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aClick here to join the world").create()));
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/admin world " + e.getWorld().getName()));
            Bukkit.getOnlinePlayers().stream().filter(cur -> cur.hasPermission("ps.admin")).forEach(cur -> {
                cur.sendMessage("§aNew world " + e.getWorld().getName() + " has been loaded !");
                cur.spigot().sendMessage(msg);
            });

            worldLoaded = null;
        }
    }

    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

}
