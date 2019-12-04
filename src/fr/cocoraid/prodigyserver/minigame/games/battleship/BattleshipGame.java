package fr.cocoraid.prodigyserver.minigame.games.battleship;

import fr.cocoraid.prodigyserver.ProdigyServer;
import fr.cocoraid.prodigyserver.minigame.games.MiniGame;
import fr.cocoraid.prodigyserver.utils.*;
import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.Blocks;
import net.minecraft.server.v1_13_R2.ChatComponentText;
import net.minecraft.server.v1_13_R2.PacketPlayOutBlockAction;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class BattleshipGame extends MiniGame {

    private static String[]story = new String[] {
            "§3Welcome Sailor, a ship is attacking us ! "+"§fLet's start your training before fighting... ",
            "§4Cannons " + "§fRight or left click with gunpowder to use cannon",
            "§7Gunpowder "+"§fA chest at the front is dropping gunpowder",
            "§6Captain "+"§fProtect your captain otherwise you will no longer respawn",
            "§6Captain "+"§fCaptain can heal you if you are close",
            "§6Captain "+"§fIf your captain die, you will no longer be able to capture the ennemy flag !",
            "§5Flag "+"§fCapture ennemy flag or kill all ennemies (wihout respawn) to win this battle !"
    };


    /***
     *
     * If player has die, he is removed from players list
     * Do not set team null, because we will still send team message
     *
     */

    private Map<BattleshipTeam,List<Cannon>> cannons = new HashMap<>();
    private Map<ArmorStand,Captain> captains = new HashMap<>();


    public static ItemStack powder = new ItemStack(Material.GUNPOWDER);
    static {
        ItemMeta meta = powder.getItemMeta();
        meta.setLocalizedName(UUID.randomUUID().toString());
        meta.setDisplayName("§cRight-Click inside a cannon to charge it !");
        meta.setLore(Arrays.asList("§bThe most you charge the most your cannon will shot the bullet far away",
                "§bYou can shift-right-click to fullfill the cannon",
                "You can only charge the cannon with a maximum of 10 gunpowders"));
        powder.setItemMeta(meta);
    }

    public static ItemStack cannon = new UtilItem(Material.ANVIL,"§cPlace a cannon").build();
    private Map<Player,PlayerData> dataMap = new HashMap<>();
    public class PlayerData {
        private BattleshipTeam team;
        //shark or player
        private String lastDamager = "";

        public PlayerData() {
        }

        public void setTeam(BattleshipTeam team) {
            this.team = team;
        }

        public BattleshipTeam getTeam() {
            return team;
        }

        public void setLastDamager(String lastDamager) {
            this.lastDamager = lastDamager;
        }


        public String getLastDamager() {
            return lastDamager;
        }
    }

    public void setupTeams() {
        int k = 0;
        for (Player player : players) {


            dataMap.put(player, new PlayerData());
            BattleshipTeam t = k % 2 == 0 ? BattleshipTeam.CONQUISTADOR : BattleshipTeam.PIRATE;
            dataMap.get(player).setTeam(t);
            player.sendMessage("§bYou joined " + t.getName() + " §bteam !");
            player.teleport(t.getSpawnPoints()[k % 2 == 0 ? k : k - 1]);
            k++;

            for (String s : story) {
                player.sendMessage(s);
            }
        }
    }


    public BattleshipGame() {
        super("§3BattleShip");
    }

    private static List<Head> heads = new ArrayList<>();
    static {
        for (Head head : Head.values()) {
            if(head.name().startsWith("BATTLESHIP"))
                heads.add(head);
        }
    }

    @Override
    public void loadGame() {
        super.loadGame();



        setupTeams();

        for (BattleshipTeam team : BattleshipTeam.values()) {
            cannons.putIfAbsent(team,new ArrayList<>());
            team.getBanner().getBlock().setType(team == BattleshipTeam.PIRATE ? Material.BLACK_BANNER : Material.ORANGE_BANNER);
            team.getPowderChest().getBlock().setType(Material.CHEST);
            team.getSpawnTeam().getBlock().getRelative(BlockFace.DOWN).setType(Material.BEDROCK);
            Captain captain = new Captain(team,dataMap);
            captains.put(captain.getCaptain(),captain);

            for (Location location : team.getCannonPoints()) {
                Cannon c = new Cannon(location.clone().add(location.getDirection().multiply(0.5)));
                c.spawn();
                cannons.get(team).add(c);
            }
            for(int k = 0; k < 20 ; k++) {
                Villager v = team.getSpawnTeam().getWorld().spawn(team.getSpawnTeam(),Villager.class);
                v.setPersistent(true);
                v.setVelocity(new Vector(Math.toRadians(UtilMath.randomRange(-180, 180)), 0.5, Math.toRadians(UtilMath.randomRange(-180, 180))).multiply(0.2));
                v.getEquipment().setHelmet(heads.get(new Random().nextInt(heads.size())).getHead());

            }

        }


        allowMoving = false;


        //We must set easy difficulty because shark can't attack
        World w = Bukkit.getWorld(gameWorld.getGameName());
        w.setDifficulty(Difficulty.EASY);
        //Spawn angry sharks
        Location l = gameWorld.getSpawnPoint();
        for(int k = 0; k < 50 ; k++) {
            CustomDolphin dolphin  = new CustomDolphin(((CraftWorld)l.getWorld()).getHandle());
            dolphin.setCustomName(new ChatComponentText("§4shark"));
            dolphin.setCustomNameVisible(true);
            dolphin.persist = true;
            dolphin.canPickUpLoot = false;
            dolphin.persistent = true;
            dolphin.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
            ((CraftWorld)l.getWorld()).getHandle().addEntity(dolphin, CreatureSpawnEvent.SpawnReason.CUSTOM);
        }
    }

    @Override
    public void start() {
        super.start();

        allowMoving = true;
        gameWorld.getSpawnPoint().getWorld().setTime(8000);


        players.forEach(player -> {
            BattleshipTeam t = dataMap.get(player).getTeam();
            if(player.hasPermission("ps.admin")) {
                Parrot parrot = player.getWorld().spawn(player.getLocation(),Parrot.class);
                parrot.setPersistent(true);
                player.setShoulderEntityLeft(parrot);
            }
            player.getInventory().setItem(0, new ItemStack(Material.STONE_AXE));
            ItemStack clone = cannon.clone();
            clone.setAmount(3);
            player.getInventory().setItem(1,clone);
            player.getInventory().addItem(new ItemStack(Material.BOW));
            player.getInventory().setItem(20,new ItemStack(Material.ARROW,2 * 64));
            player.getInventory().setBoots(UtilItem.getColorArmor(Material.LEATHER_BOOTS, t.getColor()));
            player.getInventory().setChestplate(UtilItem.getColorArmor(Material.LEATHER_CHESTPLATE, t.getColor()));
            player.getInventory().setLeggings(UtilItem.getColorArmor(Material.LEATHER_LEGGINGS, t.getColor()));
        });
    }




    private boolean giveaway = false;
    @Override
    public void update() {
        super.update();
        if(!isStarted()) return;

        if(step % (20 * 10) == 0) {
            for (BattleshipTeam battleshipTeam : BattleshipTeam.values()) {
                if(battleshipTeam.getFlagCapturedPlayer() != null) {
                    Utils.strike(battleshipTeam.getFlagCapturedPlayer().getLocation().add(0,5,0),getAllTeamMembers(battleshipTeam));
                    getAllTeamMembers(battleshipTeam).forEach(cur -> {
                        cur.playSound(cur.getLocation(),Sound.ENTITY_PARROT_IMITATE_ENDER_DRAGON,1,0);
                        cur.sendTitle("§4Your flag has been captured !","§cKill the assaillant right now...", 60,60,60);
                    });
                }
                battleshipTeam.getList().forEach(i -> i.remove());
                battleshipTeam.getList().clear();
            }
            giveaway = true;
            Bukkit.getScheduler().scheduleSyncDelayedTask(ProdigyServer.getInstance(), (Runnable)new Runnable() {
                @Override
                public void run() {
                    giveaway = false;
                    for (BattleshipTeam battleshipTeam : BattleshipTeam.values()) {
                        chest(battleshipTeam.getPowderChest(), false);

                    }
                }
            }, 20L * 3);
        }

        if(giveaway) {
            //Send open chest for spectators and players
            for (BattleshipTeam battleshipTeam : BattleshipTeam.values()) {
                chest(battleshipTeam.getPowderChest(), true);
                gameWorld.getSpawnPoint().getWorld().spawnParticle(Particle.SMOKE_LARGE,battleshipTeam.getPowderChest(),10,0.1,0.1,0.1,0.05F);

                if(battleshipTeam.getList().size() < 10) {
                    Vector v = new Vector(Math.toRadians(UtilMath.randomRange(-180, 180)), 0.5, Math.toRadians(UtilMath.randomRange(-180, 180))).multiply(0.2);
                    Item i = gameWorld.getSpawnPoint().getWorld().dropItem(battleshipTeam.getPowderChest(), powder);
                    i.setTicksLived(5000);
                    i.setVelocity(v);
                    battleshipTeam.getList().add(i);
                }
            }
        }

        //Give blocks each 2 blocks each 5 seconds, max 64
        if(step % (20*6) == 0) {
            players.forEach(cur -> {
                Material m  = dataMap.get(cur).team == BattleshipTeam.PIRATE ? Material.BLACK_WOOL : Material.ORANGE_WOOL;
                if(UtilItem.getAmount(cur,m) < 64 * 2)
                    cur.getInventory().addItem(new ItemStack(m , 4));
            });
        }

        cannons.values().forEach(list -> list.forEach(c -> {
            c.moveBullet();
        }));


        if(step % (20 * 5) == 0) {
            captains.keySet().removeIf(as -> {
                Captain c = captains.get(as);
                if(c.isDead()) return true;
                else {
                    UtilMath.getClosestPlayersFromLocation(c.getCaptain().getLocation(),5).stream().
                            filter(cur -> players.contains(cur) && dataMap.get(cur) != null && dataMap.get(cur).getTeam() == c.getTeam()).forEach(cur -> {
                        if(cur.getHealth() < 20) {
                            cur.setHealth(cur.getHealth() + 2 >= 20 ? 20 : cur.getHealth() + 2);
                            cur.playSound(cur.getLocation(),Sound.ITEM_BOTTLE_FILL_DRAGONBREATH,1,2);
                            cur.getWorld().spawnParticle(Particle.HEART,cur.getLocation(),5,0.5,0.5,0.5,0.1);
                        }
                        BattleshipTeam otherTeam = c.getTeam() == BattleshipTeam.PIRATE ? BattleshipTeam.CONQUISTADOR : BattleshipTeam.PIRATE;
                        if(otherTeam.getFlagCapturedPlayer() != null && otherTeam.getFlagCapturedPlayer().equals(cur)) {
                            cur.getInventory().setHelmet(null);
                            //won the game
                            players.forEach(player -> {
                                player.setGameMode(GameMode.SPECTATOR);
                                player.playSound(player.getLocation(),Sound.BLOCK_END_PORTAL_SPAWN,1,1);
                            });
                            sendGlobalMessage("§6" + c.getTeam() + " §6won the game !");
                            stop();

                        }
                    });
                    return false;
                }


            });
        }


        //check mat
        if(step % 20 == 0) {
            for (BattleshipTeam team : cannons.keySet()) {
                if(team.getMat().getBlock().getType() == Material.AIR) {
                    if(!team.isMatDestroyed()) {
                        team.setMatDestroyed(true);
                        destroyMat(team.getMat());
                    }

                }

                cannons.get(team).removeIf(c -> {
                    if(c.getCenter().getBlock().getType() == Material.AIR || c.getCenter().getBlock().getType() == Material.FIRE) {
                        c.getCenter().getWorld().playSound(c.getCenter(),Sound.BLOCK_ANVIL_PLACE,1,0);
                        c.destroyCannon();
                        return true;
                    } else return false;
                });
            }
        }
    }

    //max radius 90
    //147 max height 77 min

    private void destroyMat(Location l) {

        new BukkitRunnable() {
            Location center = l.clone().add(0,5,0);
            @Override
            public void run() {
                UtilMath.getInXRadius(center,90,40).stream().filter(b -> b.getType() !=  Material.AIR).forEach(b -> {
                    Utils.setBlockFast(Material.AIR,b.getLocation(),false);
                });
                center.add(0,1,0);
                if(center.getY() >= 147)
                    cancel();

            }
        }.runTaskTimer(ProdigyServer.getInstance(),10,10);


    }


    @Override
    public void loadPlayer(Player player) {
        super.loadPlayer(player);

    }


    @Override
    public void diePlayer(Player p) {

        if(players.contains(p)) {

            PlayerData data = dataMap.get(p);
            BattleshipTeam t = data.getTeam();
            BattleshipTeam ennemyTeam = t == BattleshipTeam.PIRATE ? BattleshipTeam.CONQUISTADOR : BattleshipTeam.PIRATE;
            if(ennemyTeam.getFlagCapturedPlayer() != null && ennemyTeam.getFlagCapturedPlayer().equals(p)) {
                GlowEntity.removeGlow(p);
                ennemyTeam.setFlagCapturedByPlayer(null);
                ennemyTeam.getBanner().getBlock().setType(ennemyTeam == BattleshipTeam.PIRATE ? Material.BLACK_BANNER : Material.ORANGE_BANNER);
                sendGlobalMessage(ennemyTeam.getName() + " team flag has been replaced !");
            }
            if(t.isAllowRespawn()) {
                p.getInventory().clear();
                p.getInventory().setItem(0, new ItemStack(Material.STONE_AXE));
                p.getInventory().addItem(new ItemStack(Material.BOW));
                p.getInventory().setItem(20, new ItemStack(Material.ARROW, 2 * 64));
                p.getInventory().setBoots(UtilItem.getColorArmor(Material.LEATHER_BOOTS, t.getColor()));
                p.getInventory().setChestplate(UtilItem.getColorArmor(Material.LEATHER_CHESTPLATE, t.getColor()));
                p.getInventory().setLeggings(UtilItem.getColorArmor(Material.LEATHER_LEGGINGS, t.getColor()));
                p.setHealth(20);
                p.updateInventory();
                p.teleport(t.getSpawnTeam());
                return;
            }
        }

        super.diePlayer(p);
        players.remove(p);

    }

    @EventHandler
    public void merge(ItemMergeEvent e) {
        if(e.getTarget().getItemStack().equals(powder))
            e.setCancelled(true);
    }

    @EventHandler
    public void health(EntityRegainHealthEvent e) {
        if(players.contains(e.getEntity())) {
            if(e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED)
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void fallingblocks(EntityChangeBlockEvent e) {
        if(e.getBlock().getWorld().equals(gameWorld.getSpawnPoint().getWorld()))
            if(isStarted())
                e.setCancelled(true);
    }




    @EventHandler
    public void damage(EntityDamageEvent e) {

        if(captains.containsKey(e.getEntity())) {
            if(e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK)
                e.setCancelled(true);
            return;
        }

        if(!players.contains(e.getEntity())) return;
        if(!isStarted()) return;
        Player victim = (Player) e.getEntity();
        if(victim.getHealth() - e.getDamage() <= 0) {
            e.setCancelled(true);
            String killer = "";
            if(e.getCause() == EntityDamageEvent.DamageCause.CUSTOM) killer = "§7Cannon";
            else if(e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK){
                killer =  dataMap.get(e.getEntity()).getLastDamager();
            }
            diePlayer(victim);
            if(killer.equalsIgnoreCase(""))
                sendGlobalMessage(dataMap.get(e.getEntity()).getTeam().getName() + " " + e.getEntity().getName() + " §c has been killed...");
            else
                sendGlobalMessage(dataMap.get(e.getEntity()).getTeam().getName() + " " + e.getEntity().getName() + " §c has been killed by " + killer);


        }

    }

    @EventHandler
    public void breakBlock(BlockBreakEvent e) {
        Player p = e.getPlayer();
        for (BattleshipTeam team : BattleshipTeam.values()) {
            if(team.getPowderChest().getBlock().equals(e.getBlock())) {
                e.setCancelled(true);
                break;
            }
            if(team.getBanner().getBlock().equals(e.getBlock())) {
                e.setCancelled(true);
                //If another player place a block do not think that's the banner
                if(!team.isFlagCaptured() && team.isAllowRespawn()) {
                    //check if the player is coming from another team
                    if(dataMap.containsKey(p) && dataMap.get(p).getTeam() != team) {
                        sendGlobalMessage(dataMap.get(p).getTeam().getName() + " §4" + p.getName() + " §chas captured the flag !");
                        p.sendMessage("§bYou have just captured the flag, give it to your captain and win this battle !");
                        e.getBlock().getWorld().strikeLightningEffect(e.getBlock().getLocation());

                        players.forEach(cur -> {
                            cur.playSound(cur.getLocation(),Sound.ENTITY_PARROT_IMITATE_ENDER_DRAGON,1,0);
                        });
                        getAllTeamMembers(team).forEach(cur -> {
                            cur.sendTitle("§4Your flag has been captured !","§cKill the assaillant right now...", 60,60,60);
                        });

                        //get team of assaillant:
                        BattleshipTeam assaillantTeam = dataMap.get(p).getTeam();
                        getAllTeamMembers(assaillantTeam).stream().filter(cur -> !cur.equals(p)).forEach(cur -> {
                            p.sendMessage("§bProtect your team mate §3" + p.getName() + " §bfrom ennemies !");
                        });

                        //Now glow :) we set glow of team break

                        GlowEntity.setGlow(players,p,assaillantTeam.getChatColor());

                        team.setFlagCapturedByPlayer(e.getPlayer());
                        ItemStack banner = new ItemStack(team == BattleshipTeam.PIRATE ? Material.BLACK_BANNER : Material.ORANGE_BANNER);
                        p.getInventory().setHelmet(banner);
                        e.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
    }

    @EventHandler
    public void placeBlock(BlockPlaceEvent e) {
        if(!isStarted()) return;
        if(!players.contains(e.getPlayer())) return;

        if(e.getItemInHand().getType() == cannon.getType() && e.getItemInHand().hasItemMeta() && e.getItemInHand().getItemMeta().getDisplayName().equals(cannon.getItemMeta().getDisplayName())) {
            e.setCancelled(true);
            BattleshipTeam team = dataMap.get(e.getPlayer()).getTeam();
            UtilItem.removeItemAmount(e.getPlayer(),1,cannon.getType());
            Location l = e.getBlockPlaced().getLocation().subtract(0,0.7,0);
            l.setPitch(0);
            l.setDirection(e.getPlayer().getEyeLocation().getDirection());
            Cannon c = new Cannon(l);
            c.spawn();
            cannons.get(team).add(c);
        }
    }


    @EventHandler
    public void itemdamage(PlayerItemDamageEvent e) {
        if(!players.contains(e.getPlayer())) return;
        e.setCancelled(true);

    }


    @EventHandler
    public void fire(BlockIgniteEvent e) {
        if(e.getIgnitingBlock().getWorld().equals(gameWorld.getSpawnPoint().getWorld())) {
            if (e.getCause() == BlockIgniteEvent.IgniteCause.SPREAD)
                e.setCancelled(true);
        }
    }

    @Override
    public void resetPlayer(Player p) {
        super.resetPlayer(p);
        dataMap.remove(p);
    }

    @EventHandler
    public void teamDamage(EntityDamageByEntityEvent e) {
        if(!isStarted()) return;


        if(captains.containsKey(e.getEntity())) {
            if(players.contains(e.getDamager())) {
                captains.get(e.getEntity()).damage((Player)e.getDamager());
                e.setCancelled(true);
            }
        }

        if(players.contains(e.getEntity())) {
            dataMap.get(e.getEntity()).setLastDamager(e.getDamager().getName());
            if(players.contains(e.getDamager())) {
                BattleshipTeam damagerTeam = dataMap.get(e.getDamager()).team;
                BattleshipTeam damagedTeam = dataMap.get(e.getEntity()).team;
                if (damagedTeam == damagerTeam) {
                    e.setCancelled(true);
                }
            }
        }
    }


    @Override
    public void winnerResolver() {
        int piratesNotNull = 0;
        int conquistadorsNotNull = 0;
        for (Player player : dataMap.keySet()) {
            PlayerData data = dataMap.get(player);
            if(!spectators.contains(player)) {
                if (data.getTeam() == BattleshipTeam.PIRATE) piratesNotNull++;
                else if (data.getTeam() == BattleshipTeam.CONQUISTADOR) conquistadorsNotNull++;
            }
        }

        BattleshipTeam winner = null;
        //This should be impossible
        if(piratesNotNull == 0 && conquistadorsNotNull == 0) {
            sendGlobalMessage("§4Error: Can't find a winner, you have both Pirates and Conquistadors won the game :/");
            stop();
            return;
        }
        if(piratesNotNull <= 0)
            winner = BattleshipTeam.CONQUISTADOR;
        if(conquistadorsNotNull <= 0)
            winner = BattleshipTeam.PIRATE;

        players.forEach(cur -> {
            cur.setGameMode(GameMode.SPECTATOR);
            cur.playSound(cur.getLocation(),Sound.BLOCK_END_PORTAL_SPAWN,1,1);
        });

        if(winner == null) {
            sendGlobalMessage("§cError: can't find a winner !");
            stop();
            return;
        }
        sendGlobalMessage("§6" + winner.getName() + " §6won the game !");
        stop();

    }

    private List<Player> getAllTeamMembers(BattleshipTeam team) {
        return dataMap.keySet().stream().filter(cur -> dataMap.containsKey(cur) && dataMap.get(cur).getTeam() == team).collect(Collectors.toList());

    }


    private void chest(Location l, boolean open) {
        PacketPlayOutBlockAction action = new PacketPlayOutBlockAction(new BlockPosition(l.getX(),l.getY(),l.getZ()), Blocks.CHEST,1,open ? 1 : 0);
        Bukkit.getOnlinePlayers().stream().filter(cur -> cur.getWorld().equals(gameWorld.getSpawnPoint().getWorld())).forEach(cur -> {
            ((CraftPlayer)cur).getHandle().playerConnection.sendPacket(action);
        });

    }

}
