package fr.cocoraid.prodigyserver.minigame.games.gladiator;

import fr.cocoraid.prodigyserver.ProdigyServer;
import fr.cocoraid.prodigyserver.minigame.games.MiniGame;
import fr.cocoraid.prodigyserver.utils.UtilItem;
import fr.cocoraid.prodigyserver.utils.UtilMath;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;

public class GladiatorGame extends MiniGame {


    private Map<Player,PlayerData> dataMap = new HashMap<>();
    private class PlayerData {

        private boolean comeback = false;
        private Location hammerDirection;
        private ArmorStand hammer;
        private int life = 100;
        private boolean doubleJump = false;
        private List<UUID> tempVictims = new ArrayList<>();

        public PlayerData() {

        }

        public int getLife() {
            return life;
        }

        public void subLife(int i) {
            life-=i;
        }

        public boolean isDoubleJump() {
            return doubleJump;
        }

        public List<UUID> getTempVictims() {
            return tempVictims;
        }

        public void setHammer(ArmorStand hammer) {
            this.hammer = hammer;
        }

        public ArmorStand getHammer() {
            return hammer;
        }

        public Location getHammerDirection() {
            return hammerDirection;
        }

        public void setHammerDirection(Location hammerDirection) {
            this.hammerDirection = hammerDirection;
        }

        public boolean isComeback() {
            return comeback;
        }
    }


    public GladiatorGame() {
        super("§6Gladiator");
        setFallgame();
    }

    @Override
    public void start() {
        super.start();

        players.forEach(player -> {
            dataMap.put(player,new PlayerData());
            player.setAllowFlight(true);
            player.setWalkSpeed(0.4F);
            player.setLevel(100);
            player.getInventory().setItem(0, new UtilItem(player.hasPermission("ps.admin") ? Material.GOLDEN_AXE : Material.STONE_AXE,"§6§lThor Hammer §3[DROP TO LAUNCH]").build());
            player.getInventory().setBoots(UtilItem.getColorArmor(Material.LEATHER_BOOTS, Color.MAROON));
            player.getInventory().setChestplate(UtilItem.getColorArmor(Material.LEATHER_CHESTPLATE, Color.MAROON));
            player.getInventory().setLeggings(UtilItem.getColorArmor(Material.LEATHER_LEGGINGS, Color.MAROON));
        });
    }

    @Override
    public void update() {
        super.update();

        if(!isStarted()) return;


        /**
         * Hammer
         */

        dataMap.keySet().forEach(player -> {

            PlayerData data = dataMap.get(player);
            ArmorStand h = data.getHammer();
            if(h != null) {
                double x = h.getRightArmPose().getX() + Math.toRadians(40);
                double y = h.getRightArmPose().getY();
                double z = h.getRightArmPose().getZ();
                h.setRightArmPose(new EulerAngle(x, y, z));
                if (step % 2 == 0) {
                    h.getWorld().playSound(h.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 0.5f, 0.5f);
                    h.getWorld().spawnParticle(Particle.CRIT, h.getLocation().add(0, 1, 0), 1, 0.3f, 0.3f, 0.3f, 0.05f);
                }

                if (h.getLocation().distance(data.getHammerDirection()) > 1) {
                    if(!data.isComeback()) {

                        UtilMath.pullEntity(h, data.getHammerDirection(), 1.3D);
                    }
                } else {
                    if(!data.isComeback())
                        data.comeback = true;
                }

                if(data.isComeback()) {

                    UtilMath.pullEntity(h, player.getLocation(), 1.4D);
                    if(h.getLocation().distance(player.getLocation()) <= 1) {
                        h.remove();
                        data.hammer = null;
                        data.comeback = false;

                        player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_CLOSE, 1f, 0.5f);
                        player.getInventory().setItem(0, new UtilItem(player.hasPermission("ps.admin") ? Material.GOLDEN_AXE : Material.STONE_AXE,"§6§lThor Hammer §3[DROP TO LAUNCH]").build());
                        player.updateInventory();
                        return;
                    }
                }

                if (h.getLocation().getBlock().getType() != Material.AIR) {
                    if (h.getLocation().getBlock().getType() != Material.BARRIER) {
                        destroyBlocks(h.getLocation().add(0, 1, 0), 2D);
                       /* Vector center = h.getLocation().toVector();
                        for (double a = 0.; a < Math.PI * 2.; a += Math.PI / 45.) {
                            Vector v = center.clone().add(new Vector(Math.cos(a), Math.sin(a), 0).multiply(3));
                            v = UtilMath.rotate(v.subtract(h.getLocation().toVector()), player.getLocation().getYaw(), 0).add(h.getLocation().toVector());
                            h.getWorld().spawnParticle(Particle.FLAME, v.toLocation(h.getWorld()), 1, 0.5f, 0.5f, 0.5f, 0.05f);
                        }*/
                    }
                }

                for (Player cur : UtilMath.getClosestPlayersFromLocation(h.getLocation().add(0, 1, 0), 3)) {
                    if(!cur.equals(player)) {
                        if (players.contains(cur)) {
                            hammerHit(h, player, cur);

                        }
                    }
                }
            }

        });

    }


    @Override
    public void loadPlayer(Player player) {
        super.loadPlayer(player);

    }



    @Override
    public void resetPlayer(Player p) {
        super.resetPlayer(p);
        p.setWalkSpeed(0.2F);
        dataMap.remove(p);
        p.setAllowFlight(false);
    }


    /**
     * Hammer
     */



    public void hammerHit(ArmorStand hammer, Player owner, Player victim) {
        PlayerData data = dataMap.get(owner);
        List<UUID> tempVictims = data.getTempVictims();
        if(!tempVictims.contains(victim.getUniqueId())) {
            owner.playSound(owner.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            victim.playSound(victim.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 0);
            victim.playSound(victim.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);
            victim.setVelocity(hammer.getVelocity().multiply(1.3f).setY(0.2f));
            UtilMath.bumpEntity(victim, hammer.getLocation(), 2.8F,0.6F);
            tempVictims.add(victim.getUniqueId());

            PlayerData damaged = dataMap.get(victim);
            damaged.subLife(15);
            victim.setLevel(victim.getLevel() - 15);
            victim.damage(0);

            if (damaged.getLife() <= 0) {
                players.remove(victim);
                diePlayer(victim);
            } else victim.setHealth(20 * damaged.getLife() / 100);

            new BukkitRunnable() {
                public void run() {
                    tempVictims.remove(victim.getUniqueId());
                }
            }.runTaskLater(ProdigyServer.getInstance(),  2 * 20L);
        }
    }


    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if(!isStarted()) return;
        Player p = e.getPlayer();
        if(e.getItemDrop().getItemStack().getType() == Material.GOLDEN_AXE || e.getItemDrop().getItemStack().getType() == Material.STONE_AXE) {
            e.setCancelled(false);
            spawnHammer(e);
            p.getInventory().remove(p.getInventory().getItemInMainHand());

        }
    }


    public void spawnHammer(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        Location l = player.getLocation();
        PlayerData data = dataMap.get(player);
        e.getItemDrop().remove();
        ArmorStand stand = player.getWorld().spawn(player.getLocation().add(0,1.3f,0), ArmorStand.class);
        stand.setSmall(false);
        stand.setVisible(false);
        stand.setArms(true);
        stand.setCanPickupItems(false);
        stand.setGravity(true);
        stand.setItemInHand( new ItemStack(player.hasPermission("ps.admin") ? Material.GOLDEN_AXE : Material.STONE_AXE));
        stand.setMetadata("hammer", new FixedMetadataValue(ProdigyServer.getInstance(), "hammer"));
        stand.setRightArmPose(new EulerAngle(0, 0,Math.toRadians(UtilMath.randomRange(-20, 20))));
        ((CraftEntity)stand).getHandle().noclip = true;
        Location loc = player.getEyeLocation().toVector().add(l.getDirection().multiply(30)).toLocation(player.getWorld(), l.getYaw(),l.getPitch());
        data.setHammer(stand);
        data.setHammerDirection(loc);
        player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_CLOSE, 1f, 0f);
        player.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL,player.getEyeLocation().add(l.getDirection().multiply(1.8)),0, player.getVelocity().getX(),player.getVelocity().getY(),player.getVelocity().getZ(),0.05F);
    }



    private static void destroyBlocks(Location l, double radius) {
        for (Block b : UtilMath.getInRadius(l, radius)) {
            if (b.getType() != Material.AIR) {

                int r = UtilMath.randomRange(0, 4);
                if (r == 0) {
                    b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getType());
                    b.getWorld().playSound(b.getLocation(),
                            Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 0.3f, 1);
                }
                b.setType(Material.AIR);
            }
        }
    }


    /**
     * events
     */

    @EventHandler
    public void doubleJump(PlayerToggleFlightEvent e) {
        if(!isStarted()) return;

        Player p = e.getPlayer();
        if(!players.contains(p)) return;
        PlayerData data = dataMap.get(p);
        if(e.isFlying() && p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR) {
            if(data.doubleJump) {
                e.setCancelled(true);
                return;
            }
            //play particle here
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 0);
            p.setVelocity(p.getLocation().getDirection().multiply(1.4).add(new Vector(0,0.2,0)));
            p.setAllowFlight(false);
            data.doubleJump = true;
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void playerMove(PlayerMoveEvent e) {
        if(!isStarted()) return;
        Player p = e.getPlayer();
        if(!players.contains(p)) return;
        PlayerData data = dataMap.get(p);
        if(data == null) return;
        if(data.isDoubleJump()) {
            if(p.isOnGround()) {
                data.doubleJump = false;
                p.setAllowFlight(true);
            }
        }
    }

    @EventHandler
    public void breakblock(BlockBreakEvent e) {
        if (!players.contains(e.getPlayer())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void health(EntityRegainHealthEvent e) {
        if(players.contains(e.getEntity())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void pvp(EntityDamageByEntityEvent e) {
        if(!isStarted()) return;
        if(players.contains(e.getEntity()) && players.contains(e.getDamager())) {
            Player cur = (Player) e.getEntity();
            e.setDamage(0);
            PlayerData damaged = dataMap.get(e.getEntity());
            damaged.subLife(5);
            cur.setLevel(cur.getLevel() - 5);

            if(damaged.getLife() <= 0) {
                players.remove(cur);
                diePlayer(cur);
            } else cur.setHealth(20 * damaged.getLife() / 100);

        }

    }



    @EventHandler
    public void itemdamage(PlayerItemDamageEvent e) {
        if(!players.contains(e.getPlayer())) return;
        e.setCancelled(true);

    }


    @EventHandler
    public void damageEvent(EntityDamageEvent e) {
        if (!players.contains(e.getEntity())) return;
        if(e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK && e.getCause() != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)
            e.setCancelled(true);

    }
}
