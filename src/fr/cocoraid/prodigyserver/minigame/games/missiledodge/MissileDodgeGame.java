package fr.cocoraid.prodigyserver.minigame.games.missiledodge;

import fr.cocoraid.prodigyserver.ProdigyServer;
import fr.cocoraid.prodigyserver.minigame.games.MiniGame;
import fr.cocoraid.prodigyserver.utils.UtilMath;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;

public class MissileDodgeGame extends MiniGame {

    private enum Phase {

        EASY_PHASE("§aEasy Phase", 1, 1.3f, 1.2f, 1),
        SOFT_PHASE("§2Soft Phase", 3, 1.4f, 1.4f, 3),
        MEDIUM_PHASE("§6Medium Phase", 5, 1.5f, 1.6f, 5),
        DIFFICULT_PHASE("§cDifficult Phase", 5, 1.8f, 1.3f, 6),
        HARD_PHASE("§4Last phase hard", 6, 1.9f, 2f, 10);

        private String name;
        private float speed;
        private float pushDistance;
        private double damage;
        private int spawnPerSec;

        Phase(String name, double damage, float speed, float pushDistance, int spawnPerSec) {
            this.name = name;
            this.damage = damage;
            this.speed = speed;
            this.pushDistance = pushDistance;
            this.spawnPerSec = spawnPerSec;
        }

        public String getName() {
            return this.name;
        }

        public float getSpeed() {
            return this.speed;
        }

        public float getPushDistance() {
            return this.pushDistance;
        }

        public double getDamage() {
            return this.damage;
        }

        public int getSpawnPerSec() {
            return spawnPerSec;
        }
    }


    public MissileDodgeGame() {
        super("§4Missile§7Dodge");
        setFallgame();
        needRegeneration = false;
    }


    @Override
    public void start() {
        super.start();
        this.currentPhase = Phase.EASY_PHASE;
        nextPhase(60, Phase.SOFT_PHASE);
        nextPhase(2 * 60, Phase.MEDIUM_PHASE);
        nextPhase(3 * 60, Phase.DIFFICULT_PHASE);
        nextPhase(4 * 60, Phase.HARD_PHASE);
    }

    @Override
    public void update() {
        super.update();

        if(!isStarted()) return;


        Iterator<Meteor> i = missiles.keySet().iterator();
        while (i.hasNext()) {
            Meteor missile = i.next();
            Location end = missiles.get(missile);
            ArmorStand h = missile.getHeart();
            Phase p = currentPhase;
            if(end.distance(h.getLocation()) <= 2) {
                i.remove();
                missile.remove();
            } else {

                Vector dir = missile.getHeart().getLocation().toVector().subtract(end.toVector()).normalize().multiply(-1.5);
                Location loc = missile.getHeart().getLocation().clone().add(0,1.7,0).add(dir);
                loc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE,loc,1,0,0,0,0);
                loc.getWorld().spawnParticle(Particle.FLAME,loc,1,0.3,0.3,0.3,0.05f);
                loc.getWorld().spawnParticle(Particle.SMOKE_LARGE,loc,1,0.3,0.3,0.3,0.05f);

                if(step % 7 == 0)
                    loc.getWorld().playSound(missile.getHeart().getLocation(), Sound.ENTITY_PHANTOM_FLAP,2,0);

                missile.getComposit().forEach(c ->
                        UtilMath.pullEntity(c, end, p.getSpeed()));
                Vector particle = h.getLocation().add(0, 1.4, 0).toVector().add(h.getVelocity().multiply(-2f));


                UtilMath.getClosestPlayersFromLocation(h.getLocation().add(0, 1, 0), 2.5).stream().filter(cur -> players.contains(cur)).findFirst().ifPresent(cur -> {

                    cur.damage(p.getDamage());
                    UtilMath.bumpEntity(cur, h.getLocation(), p.getPushDistance(), 0.5);
                    loc.getWorld().spawnParticle(Particle.DRAGON_BREATH,loc,0, particle.getX(),particle.getY(),particle.getZ(),0.1f);
                    i.remove();
                    missile.remove();
                });
            }
        }

        if(step % 20 == 0) {
            spawnMissile();
        }
    }


    public void spawnMissile() {
        for(int i = 0 ; i < currentPhase.getSpawnPerSec() ; i++) {
            Location spawn = new Location(gameWorld.getSpawnPoint().getWorld(),-5,51,-12);
            spawn.setY(51);
            double x = Math.cos(UtilMath.randomRange(-180, 180)) * 150;
            double y = UtilMath.randomRange(0, 3);
            double z = Math.sin(UtilMath.randomRange(-180, 180)) * 150;
            spawn.add(x,y,z);
            Meteor m = new Meteor(spawn);
            Vector direction = gameWorld.getSpawnPoint().clone().add(0, y, 0).toVector().subtract(spawn.toVector()).normalize();
            spawn.setDirection(direction);
            Location endLocation = spawn.toVector().add(spawn.getDirection().multiply(300)).toLocation(spawn.getWorld(),
                    spawn.getYaw(),0);
            endLocation.setY(51 + y);
            missiles.put(m, endLocation);
        }

    }

    private void nextPhase(int time, Phase phase) {
        new BukkitRunnable() {
            public void run() {

                if(!isFinished()) {
                    currentPhase = phase;
                    Bukkit.broadcastMessage(phase.name + " §b has started !");
                }
            }
        }.runTaskLater(ProdigyServer.getInstance(),  20L * time);
    }

    private Map<Meteor, Location> missiles = new HashMap<>();
    private Phase currentPhase;

    public static class Meteor {

        private ArmorStand heart;
        private Location center;
        private List<ArmorStand> composit = new ArrayList<>();


        public Meteor(Location l) {

            center = l.subtract(0, 2, 0);
            heart = center.getWorld().spawn(center, ArmorStand.class);
            heart.setVisible(false);
            heart.setGravity(true);
            heart.setHelmet(new ItemStack(Material.BEDROCK));
            ((CraftEntity) heart).getHandle().noclip = true;
            composit.add(heart);

            for (int k = 0; k < 8; k++) {
                int oy = UtilMath.randomRange(-360, 360);
                int ox = UtilMath.randomRange(-30, 30);
                int oz = UtilMath.randomRange(-30, 30);
                ArmorStand a = center.getWorld().spawn(center, ArmorStand.class);
                a.setVisible(false);
                a.setGravity(true);
                a.setHelmet(new ItemStack(Material.BEDROCK));
                a.setHeadPose(new EulerAngle(Math.toDegrees(ox), Math.toDegrees(oy), Math.toDegrees(oz)));
                ((CraftEntity) a).getHandle().noclip = true;
                composit.add(a);
            }
        }

        public void remove() {
            composit.forEach(Entity::remove);
            composit.clear();
        }

        public ArmorStand getHeart() {
            return this.heart;
        }

        public List<ArmorStand> getComposit() {
            return this.composit;
        }
    }

    /**
     * events
     */

    @EventHandler
    public void breakblock(BlockBreakEvent e) {
        if (!players.contains(e.getPlayer())) return;
        e.setCancelled(true);
    }


    @EventHandler
    public void damageEvent(EntityDamageEvent e) {
        if (!players.contains(e.getEntity())) return;
        if(!isStarted())
            e.setCancelled(true);
        else {
            if(e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK)
                e.setCancelled(true);

            if(e.getDamage() >= ((Player)e.getEntity()).getHealth()) {
                players.remove(e.getEntity());
                diePlayer(((Player) e.getEntity()));
                e.setCancelled(true);
            }
        }
    }
}
