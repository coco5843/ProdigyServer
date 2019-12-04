package fr.cocoraid.prodigyserver.command;


import fr.cocoraid.prodigyserver.ProdigyServer;
import fr.cocoraid.prodigyserver.ezcommand.CommandInfo;
import fr.cocoraid.prodigyserver.ezcommand.EzCommand;
import fr.cocoraid.prodigyserver.minigame.MinigameManager;
import fr.cocoraid.prodigyserver.minigame.games.MiniGame;
import fr.cocoraid.prodigyserver.minigame.games.battleship.BattleshipGame;
import fr.cocoraid.prodigyserver.minigame.games.gladiator.GladiatorGame;
import fr.cocoraid.prodigyserver.minigame.games.missiledodge.MissileDodgeGame;
import fr.cocoraid.prodigyserver.minigame.games.pizzaspleef.PizzaSpleefGame;
import fr.cocoraid.prodigyserver.utils.GlowEntity;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(name = "game", desc = "Start/Stop a game", perm = "",aliases = {"g"})
public class GameCMD extends EzCommand {

    private MinigameManager game = ProdigyServer.getInstance().getMinigameManager();
    private String availableGames = "pizzaspleef, missiledodge, gladiator, battleship";

    private void startGame(String arg, int time, Player player) {
        MiniGame g = null;
        if(arg.equalsIgnoreCase("pizzaspleef")) {
            g = new PizzaSpleefGame();
        } else   if(arg.equalsIgnoreCase("missiledodge")) {
            g = new MissileDodgeGame();
        } else if(arg.equalsIgnoreCase("gladiator")) {
            g = new GladiatorGame();
        } else if(arg.equalsIgnoreCase("battleship")) {
            g = new BattleshipGame();
        }
        else {
            player.sendMessage("§cGame name not found: " + availableGames);
        }
        if(g != null) {
            g.startCooldown(time);
            game.setCurrentGame(g);
        }
    }


    public void onCommand(Player p, String[] args) {
        if(args.length == 0) {

            p.sendMessage("lol");

            GlowEntity.setGlow(new ArrayList<>(Bukkit.getOnlinePlayers()),p, ChatColor.RED);

            p.sendMessage("§3Join the queue to play the current game !");
            p.sendMessage("§b/game join");
            p.sendMessage("Join and spectate the game !");
            p.sendMessage("§b/game spectate");

            if(p.hasPermission("ps.admin"))
                p.sendMessage("§3/game start <game> <cooldown>");
        } else if(args.length == 1) {
            if (args[0].equalsIgnoreCase("join")) {
                if (game.isCurrentGameRunning()) {
                    p.sendMessage("§cA game is already running you can spectate it !");
                    p.sendMessage("§c/game spectate");
                } else {
                    if (game.getQueue().contains(p)) {
                        p.sendMessage("§cYou are no longer queued for the game !");
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,0);
                        game.getQueue().remove(p);
                    } else {
                        p.sendMessage("§aYou joined the queue !");
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
                        game.getQueue().add(p);
                    }
                }
            } if (args[0].equalsIgnoreCase("spectate")) {
                if(game.isCurrentGameRunning()) {
                    if(game.getCurrentGame().getSpectators().contains(p)) {
                        p.teleport(ProdigyServer.getInstance().getLobbyManager().getSpawn());
                        p.setGameMode(GameMode.SURVIVAL);
                        p.sendMessage("§cYou are no longer spectator !");
                    } else {
                        p.teleport(game.getCurrentGame().getGameWorld().getSpawnPoint());
                        game.getCurrentGame().setSpectator(p);
                    }
                }

            }
        } else if(args.length == 3) {
            if(p.hasPermission("ps.admin") && args[0].equalsIgnoreCase("start")) {
                if(StringUtils.isNumeric(args[2])) {
                    if(game.isCurrentGameRunning()) {
                        p.sendMessage("§cA game is already running, or schematic is loading... Please wait !");
                        return;
                    }
                    startGame(args[1],Integer.valueOf(args[2]),p);
                } else {
                    p.sendMessage("§cCooldown must be a number !");
                    p.sendMessage("§3/game start <game> <cooldown>");
                }
            }

        }


    }

    public List<String> onTabComplete(Player p, String[] args) {
        return null;
    }

}