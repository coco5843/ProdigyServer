package fr.cocoraid.prodigyserver.minigame.games.battleship;

import fr.cocoraid.prodigyserver.nms.PacketArmorStand;
import fr.cocoraid.prodigyserver.utils.UtilMath;
import net.minecraft.server.v1_13_R2.Vector3f;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Bullet {

    private ArmorStand heart;
    private Location center;
    private List<PacketArmorStand> composit = new ArrayList<>();

    private int maxMove = 20*6;


    public Bullet(Location l) {

        center = l.clone().subtract(0,0.8,0);
        heart = center.getWorld().spawn(center, ArmorStand.class );
        heart.setSmall(true);
        heart.setVisible(false);
        heart.setHelmet(new ItemStack(Material.STONE_BRICKS));
        heart.setGravity(true);
        for (int k = 0; k < 4; k++) {
            int oy = UtilMath.randomRange(-360, 360);
            int ox = UtilMath.randomRange(-360, 360);
            int oz = UtilMath.randomRange(-360, 360);
            PacketArmorStand a = new PacketArmorStand(center);
            a.getArmorStand().setSmall(true);
            a.setAsItemDisplayer(new ItemStack(Material.STONE_BRICKS));
            a.getArmorStand().setHeadPose(new Vector3f(ox, oy, oz));
            a.spawn();
            composit.add(a);
        }


        heart.setVelocity(center.getDirection().multiply(10).add(new Vector(0,0.2,0)));
    }

    public void follow() {
        composit.forEach(c -> {
            c.teleport(heart.getLocation());
        });
    }


    public ArmorStand getHeartEntity() {
        return heart;
    }

    public void remove() {
        composit.forEach(c -> c.remove());
        heart.remove();
        composit.clear();
    }

    public Location getHeart() {
        return heart.getLocation().add(0,0.8,0);
    }

    public List<PacketArmorStand> getComposit() {
        return this.composit;
    }

    public int getMaxMove() {
        return maxMove;
    }

    public void subMax() {
        maxMove--;
    }
}