package fr.cocoraid.prodigyserver.minigame.games;

import fr.cocoraid.prodigyserver.ProdigyServer;
import fr.cocoraid.prodigyserver.lobby.LobbyManager;
import fr.cocoraid.prodigyserver.minigame.GameWorld;
import fr.cocoraid.prodigyserver.minigame.MinigameManager;
import fr.cocoraid.prodigyserver.minigame.event.GameEndedEvent;
import fr.cocoraid.prodigyserver.minigame.event.PlayerGameDieEvent;
import fr.cocoraid.prodigyserver.utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class MiniGame implements Listener {

    private MinigameManager mm = ProdigyServer.getInstance().getMinigameManager();
    private LobbyManager lm = ProdigyServer.getInstance().getLobbyManager();

    protected List<Player> players = new ArrayList<>();
    protected LinkedList<Player> spectators = new LinkedList<>();

    protected String name;

    protected boolean allowMoving = true;
    protected boolean preventFoodLost = true;

    protected GameWorld gameWorld;

    private boolean started = false;
    private boolean finished = false;
    private boolean fallgame = false;
    protected boolean needRegeneration = true;

    public MiniGame(String name) {
        this.gameWorld = mm.getGameWorld(getClass().getSimpleName().toLowerCase().replace("game",""));
        this.name = name;
        Bukkit.getPluginManager().registerEvents(this,ProdigyServer.getInstance());
    }



    public void startCooldown(int seconds) {
        new BukkitRunnable() {
            int time = seconds;
            @Override
            public void run() {

                if(time % 10 == 0) {
                    TextComponent msg = new TextComponent("§aA " + name + " §agame will start in §6" + time + " §aseconds ! §2[JOIN QUEUE]");
                    msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aClick here to join the queue").create()));
                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/game join"));
                    Bukkit.getOnlinePlayers().forEach(cur -> {
                        if(mm.getQueue().contains(cur))
                            cur.sendMessage("§aThe game " + name + "§a will start in " + time + " seconds");
                        else
                            cur.spigot().sendMessage(msg);
                    });
                }

                if(time < 10) {
                    Bukkit.getOnlinePlayers().stream().filter(cur -> mm.getQueue().contains(cur)).forEach(cur -> {
                        cur.playSound(cur.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
                        cur.sendMessage("§aThe game " + name + " §a will start in " + time + (time == 1 ? " second" : " seconds"));
                    });
                }
                time--;
                if (time <= 0) {
                    this.cancel();
                    loadGame();
                }
            }
        }.runTaskTimer(ProdigyServer.getInstance(), 20, 20);
    }

    public void loadPlayer(Player player) {
        player.teleport(gameWorld.getSpawnPoint());
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();


    }

    public void loadGame() {
        mm.getQueue().stream().filter(cur -> cur.isOnline()).forEach(cur -> players.add(cur));
        mm.getQueue().clear();
        for (Player player : players) {
            loadPlayer(player);
        }
        new BukkitRunnable() {
            int time = 10;
            @Override
            public void run() {

                players.forEach(cur -> {
                    sendBarMessage(cur,"§bGame start in " + time);
                    cur.playSound(cur.getLocation(), Sound.BLOCK_BEACON_ACTIVATE,0.1F,0F);
                });
                time--;
                if (time <= 0) {
                    this.cancel();
                    players.forEach(cur -> {
                        cur.playSound(cur.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1F, 2F);
                    });
                    Bukkit.broadcastMessage("§3The game is now started, you can no longer join it !");
                    start();
                }
            }
        }.runTaskTimer(ProdigyServer.getInstance(), 20, 20);
    }

    protected int step = 0;

    public void update() {

        if(step % 20 * 2 == 0) {
            //check spectator distance
            //or cheater
            players.forEach(cur -> {
                if (!isStarted()) return;
                if (cur.getLocation().distance(gameWorld.getSpawnPoint()) > 300) {
                    if (players.contains(cur)) {
                        //well big cheater ?
                        cur.teleport(getGameWorld().getSpawnPoint());
                        cur.sendMessage("§4[warning] Cheat Client is not allowed here ! A message has been sent to the administrator...");
                        Utils.broadcastAdmin("§4[alert] Player " + cur.getName() + " is probably cheating ! Fly detected...");
                    } else if (spectators.contains(cur)) {
                        cur.teleport(getGameWorld().getSpawnPoint());
                    } else {
                        cur.teleport(ProdigyServer.getInstance().getLobbyManager().getSpawn());
                    }
                }

            });

            if (fallgame && started) {

                Iterator<Player> i = players.iterator();
                while (i.hasNext()) {
                    Player cur = i.next();

                    if (cur.getLocation().getY() <= 0) {
                        if (started) {
                            i.remove();
                            diePlayer(cur);
                        } else {
                            cur.teleport(gameWorld.getSpawnPoint());
                        }
                    }
                }
            }
        }


        step++;
        if(step >= Integer.MAX_VALUE)
            step = 0;




    }


    public void diePlayer(Player p) {
        setSpectator(p);
        p.sendMessage("§cYou die !");
        PlayerGameDieEvent event = new PlayerGameDieEvent(p);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void setSpectator(Player p) {
        p.sendRawMessage("§aYou can now spectate the game !");
        p.teleport(gameWorld.getSpawnPoint());
        p.getInventory().clear();
        p.setGameMode(GameMode.SPECTATOR);
        p.setExp(0);
        spectators.add(p);
    }

    @EventHandler
    public void playerLost(PlayerGameDieEvent e) {
        if(isStarted())
            winnerResolver();
    }

    public void resetPlayer(Player p) {
        p.getInventory().clear();
        p.setGameMode(GameMode.SURVIVAL);
        p.setExp(0);
        p.setTotalExperience(0);
        p.setLevel(0);
        p.getInventory().setArmorContents(new ItemStack[] {null,null,null,null});
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setGlowing(false);
    }

    public void winnerResolver() {

        if(players.size() > 1) return;

        stop();
        //debugger, if the winner die
        Player winner = players.get(0);
        if (winner == null)
            winner = spectators.getLast();

        winner.setGameMode(GameMode.SPECTATOR);

        spectators.forEach(cur -> {
            cur.playSound(cur.getLocation(),Sound.BLOCK_END_PORTAL_SPAWN,1,1);
        });
        Bukkit.broadcastMessage("§6" + winner.getName() + " won the game !");
        winner.playSound(winner.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1,1);

    }

    public void start() {
        started = true;
        allowMoving = true;
    }


    /**
     * This method will stop for a while, to announce the winner and spawn some fireworks
     */
    public void stop() {
        started = false;
        finished = true;

        new BukkitRunnable() {
            public void run() {
                end();
            }
        }.runTaskLater(ProdigyServer.getInstance(), 20 * 10);
    }

    public void end() {
        started = false;
        players.forEach(cur -> {
            resetPlayer(cur);
            cur.teleport(lm.getSpawn());
            //tp all players in lobby
        });
        spectators.forEach(cur -> {
            resetPlayer(cur);
            cur.teleport(lm.getSpawn());
        });
        HandlerList.unregisterAll(this);

        GameEndedEvent event = new GameEndedEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        players.clear();
        spectators.clear();

        if(needRegeneration) {
            gameWorld.regenWorld(new GameWorld.WorldRegenCallback() {
                @Override
                public void onRegenDone() {
                    mm.setCurrentGame(null);
                }
            });

        } else
            mm.setCurrentGame(null);
    }


    protected void setFallgame() {
        this.fallgame = true;
    }

    @EventHandler
    public void move(PlayerMoveEvent e) {
        if(players.contains(e.getPlayer())) {
            if(!allowMoving) {
                if (((e.getFrom().getX() != e.getTo().getX()) || (e.getFrom().getZ() != e.getTo().getZ()))) {
                    Location newLoc = e.getFrom();
                    if (newLoc.getBlock().getRelative(0, -1, 0).getType() == Material.AIR) {
                        newLoc.setY(newLoc.getY() - 1);
                    }
                    newLoc.setY(Math.floor(newLoc.getY()));

                    e.getPlayer().teleport(newLoc);
                }
            }

        }
    }

    @EventHandler
    public void left(PlayerQuitEvent e) {
        if(!players.contains(e.getPlayer())) return;
        resetPlayer(e.getPlayer());
        players.remove(e.getPlayer());
        PlayerGameDieEvent event = new PlayerGameDieEvent(e.getPlayer());
        Bukkit.getPluginManager().callEvent(event);
    }


    @EventHandler
    public void blockdamage(BlockDamageEvent e) {
        if(!players.contains(e.getPlayer())) return;

        if(!started)
            e.setCancelled(true);
    }

    @EventHandler
    public void blockplace(BlockPlaceEvent e) {
        if(!players.contains(e.getPlayer())) return;

        if(!started)
            e.setCancelled(true);
    }

    @EventHandler
    public void damage(EntityDamageEvent e) {
        if(!players.contains(e.getEntity())) return;

        if(!started)
            e.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if(!players.contains(e.getEntity())) return;
        if(!started || preventFoodLost)
            e.setCancelled(true);
    }



    protected void sendBarMessage(Player p, String msg) {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
    }

    protected void sendGlobalMessage(String msg) {
        spectators.forEach(s -> {
            s.sendMessage(msg);
        });

        players.forEach(cur -> cur.sendMessage(msg));
    }


    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public LinkedList<Player> getSpectators() {
        return spectators;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isStarted() {
        return started;
    }
}
