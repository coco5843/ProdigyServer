package fr.cocoraid.prodigyserver.minigame.games.battleship;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.mysql.jdbc.Util;
import fr.cocoraid.prodigyserver.ProdigyServer;
import fr.cocoraid.prodigyserver.nms.PacketArmorStand;
import fr.cocoraid.prodigyserver.utils.ArmorstandSchematic;
import fr.cocoraid.prodigyserver.utils.UtilItem;
import fr.cocoraid.prodigyserver.utils.UtilMath;
import fr.cocoraid.prodigyserver.utils.Utils;
import net.minecraft.server.v1_13_R2.IRegistry;
import net.minecraft.server.v1_13_R2.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_13_R2.ParticleParam;
import net.minecraft.server.v1_13_R2.ParticleType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Cannon {

    /**
     * Ability:
     *
     * Have life: 3 hit = dead
     * if no block under = dead
     * cancel other player from breaking under
     *
     * charge = 10 gun powder, right click to charge
     * leftclick to fire, if already charge, send message to tell left click
     *
     * When bullet hit = explosion
     *
     */

    private PacketArmorStand indicator;
    private ArmorstandSchematic schematic;

    private Location center;
    private Location top;
    private Location torch;

    public Cannon(Location center) {
        this.center = center.add(0.5,0,0.5);
        this.top = center.clone().add(center.getDirection().multiply(2.3)).add(0,1.5,0);
        this.torch = center.clone().add(center.getDirection().multiply(-1)).subtract(0,0.5,0);

        this.schematic = new ArmorstandSchematic("Cannon");
    }

    public void spawn() {
        indicator = new PacketArmorStand(torch);
        indicator.setAsNameDisplayer("§a█");
        indicator.removeName();
        indicator.spawn();
        schematic.paste(center,180);
        schematic.setAction(new ArmorstandSchematic.SchematicClick() {
            @Override
            public void run(Player player, EnumWrappers.EntityUseAction click) {
                if(player.getGameMode() == GameMode.SPECTATOR) return;
                if(shooting || bullet != null) {
                    player.playSound(player.getLocation(), Sound.ENTITY_TURTLE_EGG_CRACK, 1, 0);
                    return;
                }


                if(click == EnumWrappers.EntityUseAction.INTERACT_AT || click == EnumWrappers.EntityUseAction.ATTACK) {
                    if(!player.getInventory().contains(Material.GUNPOWDER)) {
                        player.sendMessage("§cYou need gunpowder to use the cannon !");
                        player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, 1, 0);
                        return;
                    }
                    player.playSound(player.getLocation(),Sound.ITEM_BOTTLE_FILL_DRAGONBREATH,1,0);
                    shoot();


                    UtilItem.removeItemAmount(player,1,Material.GUNPOWDER);
                    player.playSound(player.getLocation(),Sound.ITEM_BOTTLE_FILL_DRAGONBREATH,1,0);
                }

            }
        });
    }



    private Bullet bullet;
    private boolean shooting = false;
    private void shoot() {
        if(top == null) return;
        if(shooting) return;

        indicator.updateName("§4█");
        shooting = true;
        center.getWorld().playSound(center, Sound.ENTITY_CREEPER_PRIMED,2,1);
        center.getWorld().spawnParticle(Particle.CLOUD,top,10,0.1,0.1,0.1,0.05F);

        new BukkitRunnable() {
            public void run() {
                center.getWorld().spawnParticle(Particle.EXPLOSION_HUGE,top,1,0,0,0,0F);
                center.getWorld().spawnParticle(Particle.LAVA,top,4,0.2,0.2,0.2,0.01F);
                center.getWorld().spawnParticle(Particle.SMOKE_LARGE,top,10,0.1,0.1,0.1,0.05F);
                center.getWorld().playSound(top,Sound.ENTITY_GENERIC_EXPLODE,2,0);

                Vector toadd = center.toVector().subtract(top.toVector()).setY(0).multiply(0.4);
                schematic.getArmorStands().forEach(as -> {
                    if(as.getHead().getType() != Material.OAK_STAIRS && as.getHead().getType() != Material.OAK_SLAB)
                        as.fakeTeleport(as.getLocation().clone().add(toadd));
                });

                bullet = new Bullet(top);
                //here send bullet
            }
        }.runTaskLater(ProdigyServer.getInstance(), 15);

        new BukkitRunnable() {
            public void run() {
                center.getWorld().playSound(top,Sound.BLOCK_ANVIL_LAND,2,0);
                schematic.getArmorStands().forEach(as -> {
                    if(as.getHead().getType() != Material.OAK_STAIRS && as.getHead().getType() != Material.OAK_SLAB)
                        as.fakeTeleport(as.getLocation());
                });

            }
        }.runTaskLater(ProdigyServer.getInstance(), 20);

        new BukkitRunnable() {
            public void run() {
                shooting = false;
                indicator.removeName();

            }
        }.runTaskLater(ProdigyServer.getInstance(), 20 * 10);


    }

    public void destroyCannon() {
        schematic.clear();
        schematic.setAction(null);
        //not really realistic but must be removed
        if(bullet != null)
            bullet.remove();
        indicator.remove();
    }


    public void moveBullet() {
        if(shooting && bullet != null) {
            Location heart = bullet.getHeart();
            if(bullet.getMaxMove() < 115 &&  UtilMath.getInRadius(heart,3.5).stream().filter(b -> b.getType() != Material.AIR && b.getType() != Material.BARRIER).findAny().isPresent()) {
                bullet.remove();
                center.getWorld().spawnParticle(Particle.EXPLOSION_HUGE,heart,10,0,0,0,0F);
                center.getWorld().spawnParticle(Particle.LAVA,heart,4,0.2,0.2,0.2,0.01F);
                center.getWorld().spawnParticle(Particle.SMOKE_LARGE,heart,10,0.1,0.1,0.1,0.05F);
                center.getWorld().spawnParticle(Particle.FLAME,heart,10,0.1,0.1,0.1,0.05F);
                center.getWorld().playSound(heart,Sound.ENTITY_GENERIC_EXPLODE,5,0);


                double radius = UtilMath.randomRange(3.5,6);
                UtilMath.getClosestPlayersFromLocation(bullet.getHeart(),radius).stream().filter(cur -> cur.getGameMode() != GameMode.SPECTATOR).forEach(cur -> {
                    UtilMath.bumpEntity(cur,bullet.getHeart(),radius, 0.5);
                    cur.damage(4);
                    EntityDamageEvent event = new EntityDamageEvent(cur, EntityDamageEvent.DamageCause.CUSTOM,4);
                    Bukkit.getPluginManager().callEvent(event);
                });


                UtilMath.getInRadius(heart,radius).stream().filter(b -> b.getType() != Material.AIR && b.getType() != Material.BARRIER && b.getType() != Material.BLACK_BANNER &&  b.getType() != Material.RED_BANNER  && b.getType() != Material.BEDROCK).forEach(b -> {
                    if(UtilMath.randomRange(0,2) == 0) {
                        FallingBlock fb = b.getWorld().spawnFallingBlock(b.getLocation(), b.getBlockData());
                        fb.setDropItem(false);
                        fb.setVelocity(heart.getDirection().multiply(-1).add(new Vector(Math.toRadians(UtilMath.randomRange(-90,90)), Math.toRadians(UtilMath.randomRange(-90,90)),Math.toRadians(UtilMath.randomRange(-90,90)))));
                    }
                    if(UtilMath.randomRange(0,10) == 0) {
                        if(b.getRelative(BlockFace.DOWN).getType() != Material.AIR && b.getRelative(BlockFace.DOWN).getType().isSolid())
                            Utils.setBlockFast(Material.FIRE,b.getLocation(),false);
                    } else
                        Utils.setBlockFast(Material.AIR,b.getLocation(),false);
                    if(b.getRelative(BlockFace.UP).getType() == Material.FIRE)
                        b.getRelative(BlockFace.UP).setType(Material.AIR);

                });
                bullet = null;
            } else {
                center.getWorld().spawnParticle(Particle.EXPLOSION_LARGE,heart.clone().add(heart.getDirection().multiply(-1.5)),1,0,0,0,0F);
                center.getWorld().spawnParticle(Particle.LAVA,heart.clone().add(heart.getDirection().multiply(-1.5)),1,0,0,0,0F);
                bullet.follow();
                bullet.subMax();
                if(bullet.getMaxMove() <= 0) {
                    bullet.remove();
                    shooting = false;
                    bullet = null;
                }
            }
        }
    }

    public Location getCenter() {
        return center;
    }
}
